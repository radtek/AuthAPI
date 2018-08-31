package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.AppStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnenrollAppServiceImpl implements UnenrollAppService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;

    @Autowired
    public UnenrollAppServiceImpl(
            SystemErrorResponse systemErrorResponse,
            RpAccountsTableHelper rpAccountsTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
    }

    @Override
    public AuthAPIResponse unenrollApp(int userId, String verifyToken, int appId) {
        // 从 rpaccounts 表中查找用户注册的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(appId, userId);
        if (rpAccount == null )
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED );

        // 检查令牌 verifyToken
        if (!AuthenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 将用户应用绑定记录设置为未注册状态
        rpAccount.setState(AppStateEnum.APP_NOT_REGISTER.getApp_state());
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            return systemErrorResponse.getGeneralResponse(errorCode);
        }

        return systemErrorResponse.getHttpSuccessResponse();
    }
}
