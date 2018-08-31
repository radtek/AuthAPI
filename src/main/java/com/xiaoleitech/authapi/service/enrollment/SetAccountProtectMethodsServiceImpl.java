package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetAccountProtectMethodsServiceImpl implements SetAccountProtectMethodsService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;

    @Autowired
    public SetAccountProtectMethodsServiceImpl(SystemErrorResponse systemErrorResponse,
                                               RpAccountsTableHelper rpAccountsTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
    }

    @Override
    public AuthAPIResponse setAccountProtectMethods(int userId, String verifyToken, int appId, String protectMethods) {
        // 从 rpaccounts 表中查找用户注册的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(appId, userId);
        if (rpAccount == null )
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED );

        // 检查令牌 verifyToken
        if (!AuthenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 获取用户注册状态
        AccountStateEnum accountState = AccountStateEnum.ACCOUNT_STATE_UNBINDING;
        accountState.setState(rpAccount.getState());

        // 检查用户是否绑定应用
        if (accountState == AccountStateEnum.ACCOUNT_STATE_UNBINDING)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED );

        // 设置保护方式
        rpAccount.setProtect_methods(protectMethods);
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode == ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getHttpSuccessResponse();
        else
            return systemErrorResponse.getGeneralResponse(errorCode);
    }
}
