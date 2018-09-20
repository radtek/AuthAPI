package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserLogoutServiceImpl implements UserLogoutService {
    private final UsersTableHelper usersTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserLogoutServiceImpl(UsersTableHelper usersTableHelper, SystemErrorResponse systemErrorResponse, AuthenticationHelper authenticationHelper) {
        this.usersTableHelper = usersTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public AuthAPIResponse logout(String userUuid, String verifyToken) {
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

        // 清除验证令牌
        authenticationHelper.clearToken(verifyToken);

        // 设置用户登出状态
        user.setAuthenticated(UserAuthStateEnum.AUTH_STATE_NOT_AUTHED.getAuthState());
        user.setVerify_token(verifyToken);

        // 在系统中更新用户记录
        ErrorCodeEnum errorCode = usersTableHelper.updateOneUserRecord(user);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        return systemErrorResponse.getSuccessResponse();
    }
}
