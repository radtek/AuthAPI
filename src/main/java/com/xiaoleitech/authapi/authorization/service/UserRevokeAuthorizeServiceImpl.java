package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.global.cipher.symmetric.SymmetricAlgorithm;
import com.xiaoleitech.authapi.authorization.bean.response.UserRevokeAuthorizeResponse;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRevokeAuthorizeServiceImpl implements UserRevokeAuthorizeService {
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final SymmetricAlgorithm symmetricAlgorithm;
    private final UserRevokeAuthorizeResponse userRevokeAuthorizeResponse;

    @Autowired
    public UserRevokeAuthorizeServiceImpl(SystemErrorResponse systemErrorResponse, UsersTableHelper usersTableHelper, AuthenticationHelper authenticationHelper, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, SymmetricAlgorithm symmetricAlgorithm, UserRevokeAuthorizeResponse userRevokeAuthorizeResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.userRevokeAuthorizeResponse = userRevokeAuthorizeResponse;
    }

    @Override
    public AuthAPIResponse revokeAuthorize(String appUuid, String userUuid, String verifyToken) {
        // 检查参数
        if (appUuid.isEmpty() || userUuid.isEmpty() || verifyToken.isEmpty())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 读取用户记录
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 检查验证令牌
        if (user.getAuthenticated() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_AUTHENTICATED);
        if ( !authenticationHelper.isTokenVerified(verifyToken))  {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_TOKEN);
        }

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_APP_NOT_FOUND);
        }

        // 获取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getId(), user.getId());
        if (rpAccount == null) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED);
        }

        // 应用账户必须激活
        if (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState()) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_ACTIVATED);
        }

        // 清除令牌
        authenticationHelper.clearToken(verifyToken);

        // 保存未认证的状态
        rpAccount.setAuthred(UserAuthStateEnum.AUTH_STATE_NOT_AUTHED.getAuthState());
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            return systemErrorResponse.response(errorCode);
        }

        // TODO: 添加回调 rp_account_unauthorized_callback_url

        return systemErrorResponse.success();
    }
}
