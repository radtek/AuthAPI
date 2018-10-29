package com.xiaoleitech.authapi.auxiliary.entity;

import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.bean.AppAccountRequest;
import com.xiaoleitech.authapi.global.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppAccountHelper {
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public RelyParts getRelyPart() {
        return relyPart;
    }

    public void setRelyPart(RelyParts relyPart) {
        this.relyPart = relyPart;
    }

    public RpAccounts getRpAccount() {
        return rpAccount;
    }

    public void setRpAccount(RpAccounts rpAccount) {
        this.rpAccount = rpAccount;
    }

    private Users user;
    private RelyParts relyPart;
    private RpAccounts rpAccount;
    private final UsersTableHelper usersTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AppAccountHelper(UsersTableHelper usersTableHelper, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, AuthenticationHelper authenticationHelper) {
        this.usersTableHelper = usersTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.authenticationHelper = authenticationHelper;
    }

    public ErrorCodeEnum getAppAccount(AppAccountRequest appAccountRequest) {
        String userUuid = appAccountRequest.getUser_id();
        String appUuid = appAccountRequest.getApp_id();
        String verifyToken = appAccountRequest.getVerify_token();

        // 检查参数
        if (userUuid.isEmpty() || appUuid.isEmpty() || verifyToken.isEmpty())
            return ErrorCodeEnum.ERROR_NEED_PARAMETER;

        // 取应用账户记录（同时也获取了user和rp记录）
        ErrorCodeEnum errorCode = fetchAppAccount(appUuid, userUuid);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return errorCode;

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return ErrorCodeEnum.ERROR_INVALID_TOKEN;

        // 应用账户必须激活
        if (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState()) {
            return ErrorCodeEnum.ERROR_USER_NOT_ACTIVATED;
        }

        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum fetchAppAccount(String appUuid, String userUuid) {
        // 获取用户记录，失败则返回 ERROR_USER_NOT_FOUND
        user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return ErrorCodeEnum.ERROR_USER_NOT_FOUND;

        // 获取应用记录，失败则返回 ERROR_APP_NOT_FOUND
        relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return ErrorCodeEnum.ERROR_APP_NOT_FOUND;

        // 通过 relypart.id 和 user.id 获取账户记录，失败则返回 ERROR_INVALID_ACCOUNT
        rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getId(), user.getId());
        if (rpAccount == null)
            return ErrorCodeEnum.ERROR_INVALID_ACCOUNT;

        return ErrorCodeEnum.ERROR_OK;
    }
}
