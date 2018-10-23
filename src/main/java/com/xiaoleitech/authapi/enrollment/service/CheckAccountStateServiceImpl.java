package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enrollment.bean.response.CheckAccountStateResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.AppUsers;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
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
            return systemErrorResponse.userNotFound();

        systemErrorResponse.fill(checkAccountStateResponse, ErrorCodeEnum.ERROR_OK);
        checkAccountStateResponse.setAccount_state(appUser.getAccount_state());
        return checkAccountStateResponse;
    }
}
