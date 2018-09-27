package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.model.authorization.AuthorizeStateResponse;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
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
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 读取用户记录
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 校验 verifyToken
        if (!authenticationHelper.isValidVerifyToken(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 读取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getRp_id(), user.getUser_id());
        if (rpAccount == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        authorizeStateResponse.setAuth_state(rpAccount.getAuthred());
        authorizeStateResponse.setAuth_at(rpAccount.getAuth_at());
        authorizeStateResponse.setAuth_life_time(relyPart.getAuth_life_time());
        authorizeStateResponse.setAuthorization_token(rpAccount.getAuthorization_token());
        systemErrorResponse.fillErrorResponse(authorizeStateResponse, ErrorCodeEnum.ERROR_OK);

        return authorizeStateResponse;
    }
}