package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnenrollAppServiceImpl implements UnenrollAppService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final UsersTableHelper usersTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UnenrollAppServiceImpl(
            SystemErrorResponse systemErrorResponse,
            RpAccountsTableHelper rpAccountsTableHelper,
            UsersTableHelper usersTableHelper,
            RelyPartsTableHelper relyPartsTableHelper,
            AuthenticationHelper authenticationHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.usersTableHelper = usersTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    public AuthAPIResponse unenrollApp(String userUuid, String verifyToken, String appUuid) {
        // 用户必须存在
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 通过 appUuid 查找 rps(依赖方) 表中的记录，找不到记录则不能进行解绑操作
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);

        // 从 rpaccounts 表中查找用户注册的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(
                relyPart.getId(), user.getId());
        if (rpAccount == null )
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED );

        // 如果已经处于解绑状态，则返回错误提示
        if ((rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState()) &&
                (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_INACTIVE.getState())) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED );
        }

        // 逻辑删除此条账户记录
        rpAccount.setState(AccountStateEnum.ACCOUNT_LOGICAL_DELETE.getState());
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            return systemErrorResponse.getGeneralResponse(errorCode);
        }

        return systemErrorResponse.getSuccessResponse();
    }
}
