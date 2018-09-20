package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.CheckAccountStateResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.AppUsers;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckAccountStateServiceImpl implements CheckAccountStateService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final JoinRelyPartAccountHelper joinRelyPartAccountHelper;
    private final CheckAccountStateResponse checkAccountStateResponse;

    @Autowired
    public CheckAccountStateServiceImpl(SystemErrorResponse systemErrorResponse,
                                        RpAccountsTableHelper rpAccountsTableHelper,
                                        JoinRelyPartAccountHelper joinRelyPartAccountHelper,
                                        CheckAccountStateResponse checkAccountStateResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.joinRelyPartAccountHelper = joinRelyPartAccountHelper;
        this.checkAccountStateResponse = checkAccountStateResponse;
    }

    @Override
    public AuthAPIResponse checkAccountState(String userUuid, String verifyToken, String appUuid) {
        // 联合查询用户注册记录和用户信息
        AppUsers appUser = joinRelyPartAccountHelper.getAppUserAccount(appUuid, userUuid, "");
        if (appUser == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND );

        systemErrorResponse.fillErrorResponse(checkAccountStateResponse, ErrorCodeEnum.ERROR_OK);
        checkAccountStateResponse.setAccount_state(appUser.getAccount_state());
        return checkAccountStateResponse;
    }
}
