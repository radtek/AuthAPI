package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.model.authentication.RefreshTokenResponse;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final UsersTableHelper usersTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final AuthenticationHelper authenticationHelper;
    private final RefreshTokenResponse refreshTokenResponse;

    @Autowired
    public RefreshTokenServiceImpl(UsersTableHelper usersTableHelper, SystemErrorResponse systemErrorResponse, AuthenticationHelper authenticationHelper, RefreshTokenResponse refreshTokenResponse) {
        this.usersTableHelper = usersTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.authenticationHelper = authenticationHelper;
        this.refreshTokenResponse = refreshTokenResponse;
    }

    @Override
    public AuthAPIResponse refreshToken(String userUuid, String verifyToken) {
        // 获取指定UUID的用户记录
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_USER);

        // 验证token
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 用户不是已认证状态，返回未认证错误
        if (user.getAuthenticated() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_ACCOUNT_NOT_AUTHED);

        // 更新token，需清除旧的token
        authenticationHelper.clearToken(verifyToken);
        user.setVerify_token(authenticationHelper.generateTimingToken());

        // 设置新的认证时间
        user.setAuth_at(UtilsHelper.getCurrentSystemTimestamp());

        // 在系统中更新用户记录
        ErrorCodeEnum errorCode = usersTableHelper.updateOneUserRecord(user);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        refreshTokenResponse.setExpire_at(user.getAuth_at());
        refreshTokenResponse.setVerify_token(user.getVerify_token());
        systemErrorResponse.fillErrorResponse(refreshTokenResponse, ErrorCodeEnum.ERROR_OK);
        return refreshTokenResponse;
    }
}