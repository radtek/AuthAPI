package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.*;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.mapper.RpAccountsMapper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppResponse;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.AuthorizationPolicyEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UniqueAccountNameEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.UUID;

@Component
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollCommon enrollCommon;
    private final SystemErrorResponse systemErrorResponse;
    private final EnrollAppResponse enrollAppResponse;
    private final UsersTableHelper usersTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsMapper rpAccountsMapper;
    private final JoinRelyPartAccountHelper joinRelyPartAccountHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public EnrollmentServiceImpl(EnrollCommon enrollCommon,
                                 SystemErrorResponse systemErrorResponse,
                                 EnrollAppResponse enrollAppResponse,
                                 UsersTableHelper usersTableHelper,
                                 RpAccountsTableHelper rpAccountsTableHelper,
                                 RelyPartsTableHelper relyPartsTableHelper,
                                 RpAccountsMapper rpAccountsMapper,
                                 JoinRelyPartAccountHelper joinRelyPartAccountHelper,
                                 AuthenticationHelper authenticationHelper) {
        this.enrollCommon = enrollCommon;
        this.systemErrorResponse = systemErrorResponse;
        this.enrollAppResponse = enrollAppResponse;
        this.usersTableHelper = usersTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsMapper = rpAccountsMapper;
        this.joinRelyPartAccountHelper = joinRelyPartAccountHelper;
        this.authenticationHelper = authenticationHelper;
    }

    private void setAccountState(RpAccounts rpAccount, RelyParts relyPart) {
        // 根据应用方的 authorization_policy 来定 enroll 后用户的状态
        // 如AHAPP已登录则授权
        if (relyPart.getAuthorization_policy() == AuthorizationPolicyEnum.AUTH_NEED_LOGIN.getAuthPolicy())
            rpAccount.setState(AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState());
        else
            rpAccount.setState(AccountStateEnum.ACCOUNT_STATE_INACTIVE.getState());
    }

    @Override
    public AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult) {
//        // 检查请求参数中指定的 app 和 user
//        ErrorCodeEnum errorCode = enrollCommon.checkAppAndUser( enrollAppRequest.getApp_id(), enrollAppRequest.getUser_id() );
//        if (errorCode != ErrorCodeEnum.ERROR_OK)
//            return systemErrorResponse.getGeneralResponse(errorCode);
//
//        // 验证令牌
//        if (!AuthenticationHelper.isTokenVerified(enrollAppRequest.getVerify_token()))
//            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);
//
//        // 如果要求昵称唯一，检查对应的应用账户表，昵称是否唯一。
//        if (relyPart.getUniq_account_name() == UniqueAccountNameEnum.USING_UNIQUE_NICK_NAME.getUniqueAccountName()) {
//
//        }
//
//        // 根据指定的 [app_uuid, user_uuid, account_name] 获取联合查询结果
//        AppUsers appUser = joinRelyPartAccountHelper.getAppUserAccount(
//                enrollAppRequest.getApp_id(),
//                enrollAppRequest.getUser_id(),
//                enrollAppRequest.getAccount_name(),
//                AccountStateEnum.ACCOUNT_STATE_UNKNOWN.getState());
//        if (appUser != null) {
//
//        }

        ErrorCodeEnum errorCode;

        // 用户必须存在
        Users user = usersTableHelper.getUserByUserUuid(enrollAppRequest.getUser_id());
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 通过app_id查找 rps(依赖方) 表中的记录，找不到记录则不能进行登记操作
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(enrollAppRequest.getApp_id());
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(enrollAppRequest.getVerify_token()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 查找 rpaccounts 中的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(
                relyPart.getId(), user.getId());

        // 如果要求昵称唯一，检查对应的应用账户表，昵称是否唯一。
        if (relyPart.getUniq_account_name() == UniqueAccountNameEnum.USING_UNIQUE_NICK_NAME.getUniqueAccountName()) {
            // 要求昵称唯一时，请求参数中的账户名称不能为空
            if (enrollAppRequest.getAccount_name().isEmpty()) {
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_PARAMETER);
            }
            // 如果在系统中查到重复的 account_name，则返回用户名已经占有
            if (enrollCommon.isExistAccountName(  enrollAppRequest.getApp_id(),
                                                    enrollAppRequest.getAccount_name())) {
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USERNAME_USED);
            }
        }

        // 活跃状态的账户记录找不到，则查找是否有逻辑删除的记录
        rpAccount = rpAccountsTableHelper.getExistRpAccountByRpIdAndUserId(
                relyPart.getId(), user.getId());

        if (rpAccount == null) {
            // 新建一条记录
            errorCode = addNewRpAccountRecord(enrollAppRequest, user, relyPart);
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return systemErrorResponse.getGeneralResponse(errorCode);
        } else {
            // 检查当前注册应用的state，如果是已注册(不管是否激活)，返回已注册的错误信息
            if ( (rpAccount.getState() == AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState()) ||
                    (rpAccount.getState() == AccountStateEnum.ACCOUNT_STATE_INACTIVE.getState())) {
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_ALREADY_ENROLLED);
            }

            // 用请求的参数更新这条记录
            errorCode = copyEnrollAppRequestParams(enrollAppRequest, rpAccount, user, relyPart);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                return systemErrorResponse.getGeneralResponse(errorCode);
            }

            // 根据应用的 authorization_policy 来设置应用注册状态为：注册即激活或注册未激活
            setAccountState(rpAccount, relyPart);
            errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                return systemErrorResponse.getGeneralResponse(errorCode);
            }
        }

        // 重新获取这条记录，执行到此可以获取记录，获取不到说明有未知错误
        rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getId(), user.getId());
        if (rpAccount == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        // 返回成功
        systemErrorResponse.fillErrorResponse(enrollAppResponse, ErrorCodeEnum.ERROR_OK);
        // 填写RelyPart的资料
        enrollAppResponse.setApp_account_id(rpAccount.getRp_account_uuid());
        enrollAppResponse.setAccount_state(rpAccount.getState());
        enrollAppResponse.setAuthorization_policy(relyPart.getAuthorization_policy());
        enrollAppResponse.setOtp_alg(relyPart.getOtp_alg());
        enrollAppResponse.setOtp_inteval(relyPart.getInteval());
        // TODO: otp_key是otp_seed ?
        enrollAppResponse.setOtp_seed(relyPart.getOtp_key());
        enrollAppResponse.setOtp_digits(relyPart.getOtp_digits());

        return enrollAppResponse;
    }

    private ErrorCodeEnum copyEnrollAppRequestParams(EnrollAppRequest enrollAppRequest,
                                            RpAccounts rpAccount,
                                            Users user,
                                            RelyParts relyPart) {
        rpAccount.setRp_id(relyPart.getId());
        rpAccount.setUser_id(user.getId());
        rpAccount.setProtect_methods(enrollAppRequest.getProtect_methods());

        // 账户昵称，检查relypart的昵称属性配置
        if (relyPart.getUniq_account_name() == UniqueAccountNameEnum.NOT_USING_NICK_NAME.getUniqueAccountName()) {
            // 用户UUID作为昵称
            rpAccount.setRp_account_name(user.getUser_uuid());
        } else if (relyPart.getUniq_account_name() == UniqueAccountNameEnum.USING_NICK_NAME.getUniqueAccountName()) {
            // 不需要检查昵称的唯一性
            rpAccount.setRp_account_name(enrollAppRequest.getAccount_name());
        } else if (relyPart.getUniq_account_name() == UniqueAccountNameEnum.USING_UNIQUE_NICK_NAME.getUniqueAccountName()) {
            // 要求昵称唯一性
            // 昵称参数不能为空
            if (enrollAppRequest.getAccount_name().isEmpty())
                return ErrorCodeEnum.ERROR_PARAMETER;
            rpAccount.setRp_account_name(enrollAppRequest.getAccount_name());
        } else {
            // 未知属性，也采用用户UUID作为昵称
            rpAccount.setRp_account_name(user.getUser_uuid());
        }

        // TODO: EnrollAppRequest中的 id_code / active_code / EMail 在哪里处理
        // sex 送到Users表处理

        return ErrorCodeEnum.ERROR_OK;
    }
    private ErrorCodeEnum addNewRpAccountRecord(EnrollAppRequest enrollAppRequest,
                                                Users user,
                                                RelyParts relyPart) {
        RpAccounts rpAccount = new RpAccounts();

        // 使用请求参数填充 RpAccounts 记录
        ErrorCodeEnum errorCode = copyEnrollAppRequestParams(enrollAppRequest, rpAccount, user, relyPart);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return errorCode;

        // 填充 UUID、注册状态、创建和更新时间
        rpAccount.setRp_account_uuid(UtilsHelper.generateUuid());
        // 根据应用的 authorization_policy 来设置应用注册状态为：注册即激活或注册未激活
        setAccountState(rpAccount, relyPart);
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        rpAccount.setCreated_at(currentTime);
        rpAccount.setUpdated_at(currentTime);

        // 在表中创建一条新记录
        int num = rpAccountsMapper.insertOneRpAccount(rpAccount);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_INTERNAL_ERROR;
    }
}
