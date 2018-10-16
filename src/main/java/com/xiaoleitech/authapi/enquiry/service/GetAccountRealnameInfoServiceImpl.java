package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.auxiliary.entity.RelyPartHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.AccountRealNameInfoResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.RealNameScopeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAccountRealnameInfoServiceImpl implements GetAccountRealnameInfoService {
    private final SystemErrorResponse systemErrorResponse;
    private final AccountRealNameInfoResponse accountRealNameInfoResponse;
    private final UsersTableHelper usersTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RelyPartHelper relyPartHelper;

    @Autowired
    public GetAccountRealnameInfoServiceImpl(SystemErrorResponse systemErrorResponse, AccountRealNameInfoResponse accountRealNameInfoResponse, UsersTableHelper usersTableHelper, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, AuthenticationHelper authenticationHelper, RelyPartHelper relyPartHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.accountRealNameInfoResponse = accountRealNameInfoResponse;
        this.usersTableHelper = usersTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartHelper = relyPartHelper;
    }

    private boolean isAdmitPhoneNo(String properties) {
        return UtilsHelper.isPropertyExist(properties, RealNameScopeEnum.REALNAME_AUTH_PHONE_NUMBER.getRealNameScope());
    }

    private boolean isAdmitIdNo(String properties) {
        return UtilsHelper.isPropertyExist(properties, RealNameScopeEnum.REALNAME_AUTH_ID_NUMBER.getRealNameScope());
    }

    private boolean isAdmitRealName(String properties) {
        return UtilsHelper.isPropertyExist(properties, RealNameScopeEnum.REALNAME_AUTH_NAME.getRealNameScope());
    }

    @Override
    public AuthAPIResponse getRealnameInfo(String appUuid, String token, String accountUuid) {
        // 检查参数
        if (appUuid.isEmpty() || token.isEmpty() || accountUuid.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 检查应用是否有效
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 检查账户是否有效
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(accountUuid);
        if (rpAccount == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        // 校验 token
        if (!relyPartHelper.verifyToken(relyPart, token))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 读取用户记录
        Users user = usersTableHelper.getUserByUserId(rpAccount.getUser_id());
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        String properties = relyPart.getReal_name_scope();
        if (isAdmitRealName(properties)) {
            accountRealNameInfoResponse.setUser_realname(user.getReal_name());
        } else {
            accountRealNameInfoResponse.setUser_realname("");
        }
        if (isAdmitPhoneNo(properties)) {
            accountRealNameInfoResponse.setPhone_no(user.getPhone_no());
        } else {
            accountRealNameInfoResponse.setPhone_no("");
        }
        if (isAdmitIdNo(properties)) {
            accountRealNameInfoResponse.setId_no(user.getId_no());
            accountRealNameInfoResponse.setId_expire_at(user.getId_expire_at());
        } else {
            accountRealNameInfoResponse.setId_no("");
            accountRealNameInfoResponse.setId_expire_at(new java.sql.Timestamp(0));
        }
        systemErrorResponse.fillErrorResponse(accountRealNameInfoResponse, ErrorCodeEnum.ERROR_OK);

        return accountRealNameInfoResponse;
    }
}
