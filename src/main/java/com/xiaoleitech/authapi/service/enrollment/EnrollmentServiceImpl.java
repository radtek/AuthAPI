package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.*;
import com.xiaoleitech.authapi.mapper.RpAccountsMapper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppResponse;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.AppStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
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
    private final SystemErrorResponse systemErrorResponse;
    private final EnrollAppResponse enrollAppResponse;
    private final UsersTableHelper usersTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsMapper rpAccountsMapper;

    @Autowired
    public EnrollmentServiceImpl(SystemErrorResponse systemErrorResponse,
                                 EnrollAppResponse enrollAppResponse,
                                 UsersTableHelper usersTableHelper,
                                 RpAccountsTableHelper rpAccountsTableHelper,
                                 RelyPartsTableHelper relyPartsTableHelper, RpAccountsMapper rpAccountsMapper) {
        this.systemErrorResponse = systemErrorResponse;
        this.enrollAppResponse = enrollAppResponse;
        this.usersTableHelper = usersTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsMapper = rpAccountsMapper;
    }

    @Override
    public AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult) {
        // 用户必须存在
        Users user = usersTableHelper.getUserByUserId(enrollAppRequest.getUser_id());
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 验证令牌
        if (!UsersHelper.isUserVerifyToken(user, enrollAppRequest.getVerify_token()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 通过app_id查找 rps(依赖方) 表中的记录，找不到记录则不能进行登记操作
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpId(enrollAppRequest.getApp_id());
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);

        // 查找 rpaccounts 中的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(
                enrollAppRequest.getApp_id(), enrollAppRequest.getUser_id());
        if (rpAccount == null) {
            // rpaccounts 没有这条记录，新建一条记录
            ErrorCodeEnum errorCode = addNewRpAccountRecord(enrollAppRequest);
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return systemErrorResponse.getGeneralResponse(errorCode);
        } else {
            // TODO: 检查当前注册应用的state，如果是已注册，返回已注册的错误信息
            // 用请求的参数更新这条记录
            copyEnrollAppRequestParams(enrollAppRequest, rpAccount);
            // 设置应用注册状态为注册未激活
            rpAccount.setState(AppStateEnum.APP_REGISTER_NOT_ACTIVE.getApp_state());
            ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                return systemErrorResponse.getGeneralResponse(errorCode);
            }
        }

        // 返回成功
        systemErrorResponse.fillErrorResponse(enrollAppResponse, ErrorCodeEnum.ERROR_HTTP_SUCCESS);
        // 填写RelyPart的资料
        // TODO: app_account_id 从哪里取
        enrollAppResponse.setAccount_state(AccountStateEnum.ACCOUNT_STATE_INACTIVE.getState());
        enrollAppResponse.setAuthorization_policy(relyPart.getAuthorization_policy());
        enrollAppResponse.setOtp_alg(relyPart.getOtp_alg());
        enrollAppResponse.setOtp_inteval(relyPart.getInteval());
        // TODO: otp_key是otp_seed ?
        enrollAppResponse.setOtp_seed(relyPart.getOtp_key());
        enrollAppResponse.setOtp_digits(relyPart.getOtp_digits());

        return enrollAppResponse;
    }

    private void copyEnrollAppRequestParams(EnrollAppRequest enrollAppRequest, RpAccounts rpAccount) {
        rpAccount.setRp_id(enrollAppRequest.getApp_id());
        rpAccount.setUser_id(enrollAppRequest.getUser_id());
        rpAccount.setRp_account_name(enrollAppRequest.getAccount_name());
        rpAccount.setProtect_methods(enrollAppRequest.getProtect_methods());
        // TODO: EnrollAppRequest中的 id_code / active_code / EMail 在哪里处理
        // sex 送到Users表处理
    }
    private ErrorCodeEnum addNewRpAccountRecord(EnrollAppRequest enrollAppRequest) {
        RpAccounts rpAccount = new RpAccounts();

        // 使用请求参数填充 RpAccounts 记录
        copyEnrollAppRequestParams(enrollAppRequest, rpAccount);

        // 填充 UUID、注册状态、创建和更新时间
        rpAccount.setRpa_uuid(UUID.randomUUID().toString());
        rpAccount.setState(AppStateEnum.APP_REGISTER_NOT_ACTIVE.getApp_state());
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        rpAccount.setCreated_at(currentTime);
        rpAccount.setUpdated_at(currentTime);

        // 在表中创建一条新记录
        int num = rpAccountsMapper.insertOneRpAccount(rpAccount);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_INTERNAL_ERROR;
    }
}
