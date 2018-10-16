package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.auxiliary.entity.RelyPartHelper;
import com.xiaoleitech.authapi.auxiliary.entity.RpAccountHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.authorization.bean.response.VerifyAuthStateResponse;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyAuthStateServiceImpl implements VerifyAuthStateService {
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RelyPartHelper relyPartHelper;
    private final RpAccountHelper rpAccountHelper;
    private final VerifyAuthStateResponse verifyAuthStateResponse;

    @Autowired
    public VerifyAuthStateServiceImpl(SystemErrorResponse systemErrorResponse, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, AuthenticationHelper authenticationHelper, RelyPartHelper relyPartHelper, RpAccountHelper rpAccountHelper, VerifyAuthStateResponse verifyAuthStateResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartHelper = relyPartHelper;
        this.rpAccountHelper = rpAccountHelper;
        this.verifyAuthStateResponse = verifyAuthStateResponse;
    }

    @Override
    public AuthAPIResponse verifyAuthState(String appUuid, String token, String appAccountUuid, String authorizeToken) {
        // 检查参数
        if (appUuid.isEmpty() || token.isEmpty() || appAccountUuid.isEmpty() || authorizeToken.isEmpty())
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

        // 校验认证状态和 authorization_token
        if (!rpAccountHelper.checkAuthState(rpAccount, authorizeToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_ACCOUNT_NOT_AUTHED);

        systemErrorResponse.fillErrorResponse(verifyAuthStateResponse, ErrorCodeEnum.ERROR_OK);
        verifyAuthStateResponse.setAuth_life_time(relyPart.getAuth_life_time());
        verifyAuthStateResponse.setExpire_at(relyPart.getExpire_at());
        return verifyAuthStateResponse;
    }
}
