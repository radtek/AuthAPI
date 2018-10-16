package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.AppUsers;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetAccountProtectMethodsServiceImpl implements SetAccountProtectMethodsService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final JoinRelyPartAccountHelper joinRelyPartAccountHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public SetAccountProtectMethodsServiceImpl(SystemErrorResponse systemErrorResponse,
                                               RpAccountsTableHelper rpAccountsTableHelper,
                                               JoinRelyPartAccountHelper joinRelyPartAccountHelper,
                                               AuthenticationHelper authenticationHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.joinRelyPartAccountHelper = joinRelyPartAccountHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public AuthAPIResponse setAccountProtectMethods(String userUuid, String verifyToken, String appUuid, String protectMethods) {
        // 联合查询应用账户表的注册记录
        AppUsers appUser = joinRelyPartAccountHelper.getAppUserAccount(appUuid, userUuid, "");
        if (appUser == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 从 rpaccounts 表中查找用户注册的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(appUser.getRp_id(), appUser.getUser_id());
        if (rpAccount == null )
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND );

        // 检查令牌 verifyToken
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 设置保护方式
        rpAccount.setProtect_methods(protectMethods);
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode == ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getSuccessResponse();
        else
            return systemErrorResponse.getGeneralResponse(errorCode);
    }
}
