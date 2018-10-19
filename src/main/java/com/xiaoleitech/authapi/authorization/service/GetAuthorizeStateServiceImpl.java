package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.authorization.bean.response.AuthorizeStateResponse;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAuthorizeStateServiceImpl implements GetAuthorizeStateService {
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final UsersTableHelper usersTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final AuthorizeStateResponse authorizeStateResponse;

    @Autowired
    public GetAuthorizeStateServiceImpl(SystemErrorResponse systemErrorResponse, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, UsersTableHelper usersTableHelper, AuthenticationHelper authenticationHelper, AuthorizeStateResponse authorizeStateResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.usersTableHelper = usersTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.authorizeStateResponse = authorizeStateResponse;
    }

    @Override
    public AuthAPIResponse getAuthorizeState(String userUuid, String verifyToken, String appUuid) {
        // 检查参数
        if (userUuid.isEmpty() || verifyToken.isEmpty() || appUuid.isEmpty())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 读取用户记录
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 校验 verifyToken
        if (!authenticationHelper.isValidVerifyToken(verifyToken))
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 读取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getId(), user.getId());
        if (rpAccount == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        authorizeStateResponse.setAuth_state(rpAccount.getAuthred());
        if (rpAccount.getAuthred() == UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState()) {
            authorizeStateResponse.setAuth_at(rpAccount.getAuthr_at());
            authorizeStateResponse.setAuth_life_time(relyPart.getAuth_life_time());
            authorizeStateResponse.setAuthorization_token(rpAccount.getAuthorization_token());
        }
        systemErrorResponse.fill(authorizeStateResponse, ErrorCodeEnum.ERROR_OK);

        return authorizeStateResponse;
    }
}
