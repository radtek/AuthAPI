package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.helper.RelyPartHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppLogoutAccountServiceImpl implements AppLogoutAccountService {
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RelyPartHelper relyPartHelper;

    @Autowired
    public AppLogoutAccountServiceImpl(SystemErrorResponse systemErrorResponse, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, AuthenticationHelper authenticationHelper, RelyPartHelper relyPartHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartHelper = relyPartHelper;
    }

    @Override
    public AuthAPIResponse logoutAccount(String appUuid, String token, String appAccountUuid) {

        // 检查参数
        if (appUuid.isEmpty() || appAccountUuid.isEmpty() || token.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 读取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(appAccountUuid);
        if (rpAccount == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        // 校验 token
        if (!relyPartHelper.verifyToken(relyPart, token))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 设置未认证状态，并保存账户记录
        rpAccount.setAuthred(UserAuthStateEnum.AUTH_STATE_NOT_AUTHED.getAuthState());
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            return systemErrorResponse.getGeneralResponse(errorCode);
        }

        // TODO: 添加回调 rp_account_unauthorized_callback_url

        return systemErrorResponse.getSuccessResponse();
    }
}