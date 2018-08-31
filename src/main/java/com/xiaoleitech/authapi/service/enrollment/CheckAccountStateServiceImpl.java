package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.CheckAccountStateResponse;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckAccountStateServiceImpl implements CheckAccountStateService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;

    @Autowired
    public CheckAccountStateServiceImpl(SystemErrorResponse systemErrorResponse,
                                        RpAccountsTableHelper rpAccountsTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
    }

    @Override
    public AuthAPIResponse checkAccountState(int userId, String verifyToken, int appId) {
        // 从 rpaccounts 表中查找用户注册的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(appId, userId);
        if (rpAccount == null )
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED );

        // 检查令牌 verifyToken
        if (!AuthenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 获取用户注册状态
        AccountStateEnum accountState = AccountStateEnum.ACCOUNT_STATE_UNBINDING;
        accountState.setState(rpAccount.getState());

        CheckAccountStateResponse checkAccountStateResponse = new CheckAccountStateResponse();
        switch (accountState) {
            case ACCOUNT_STATE_UNBINDING:
                // 用户未绑定指定应用
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED);
            case ACCOUNT_STATE_ACTIVE:
            case ACCOUNT_STATE_INACTIVE:
                // 用户绑定指定应用时，返回用户激活状态
                systemErrorResponse.fillErrorResponse(checkAccountStateResponse, ErrorCodeEnum.ERROR_HTTP_SUCCESS);
                checkAccountStateResponse.setAccount_state(accountState.getState());
                return checkAccountStateResponse;
            default:
                // 其他情况，返回内部错误
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }
    }
}
