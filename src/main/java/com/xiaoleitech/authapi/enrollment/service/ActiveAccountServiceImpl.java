package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.auxiliary.entity.RelyPartHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActiveAccountServiceImpl implements ActiveAccountService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final UsersTableHelper usersTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RelyPartHelper relyPartHelper;

    @Autowired
    public ActiveAccountServiceImpl(SystemErrorResponse systemErrorResponse,
                                    RpAccountsTableHelper rpAccountsTableHelper,
                                    UsersTableHelper usersTableHelper,
                                    RelyPartsTableHelper relyPartsTableHelper,
                                    AuthenticationHelper authenticationHelper, RelyPartHelper relyPartHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.usersTableHelper = usersTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartHelper = relyPartHelper;
    }

    @Override
    public AuthAPIResponse activeAccount(String appUuid, String token, String appAccountUuid) {
        // 通过 appUuid 查找 rps(依赖方) 表中的记录，找不到记录则不能进行解绑操作
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_APP);

        // 从 rpaccounts 表中查找用户注册的记录，appAccountId 即 userId
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(appAccountUuid);
        if (rpAccount == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED);

        // 如果不是待激活或已激活状态，则返回错误提示（允许多次重复激活）
        if (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_INACTIVE.getState() &&
                (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState())) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED);
        }

        // 校验 token
        if (!relyPartHelper.verifyToken(relyPart, token))
            return systemErrorResponse.invalidToken();
//        if (!authenticationHelper.isTokenVerified(token))
//            return systemErrorResponse.invalidToken();

        // 将用户应用绑定记录设置为激活状态，然后更新记录
        rpAccount.setState(AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState());
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            return systemErrorResponse.response(errorCode);
        }

        return systemErrorResponse.success();
    }
}
