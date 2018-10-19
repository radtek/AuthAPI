package com.xiaoleitech.authapi.authentication.service;

import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.authentication.bean.response.UserAuthStateResponse;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAuthStateServiceImpl implements  UserAuthStateService {
    private final UsersTableHelper usersTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final UserAuthStateResponse userAuthStateResponse;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserAuthStateServiceImpl(UsersTableHelper usersTableHelper, SystemErrorResponse systemErrorResponse, UserAuthStateResponse userAuthStateResponse, AuthenticationHelper authenticationHelper) {
        this.usersTableHelper = usersTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.userAuthStateResponse = userAuthStateResponse;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public AuthAPIResponse getUserAuthState(String userUuid, String verifyToken) {
        // 获取指定UUID的用户记录
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_USER);

        // 验证token
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 获取用户认证状态
        if (user.getAuthenticated() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_ACCOUNT_NOT_AUTHED);

        // 返回过期时间
        userAuthStateResponse.setExpire_at(authenticationHelper.getUserAuthExpireTime(user));
        systemErrorResponse.fill(userAuthStateResponse, ErrorCodeEnum.ERROR_OK);

        return userAuthStateResponse;
    }
}
