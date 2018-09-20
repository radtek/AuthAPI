package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckAccountUniqueServiceImpl implements CheckAccountUniqueService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final EnrollCommon enrollCommon;

    @Autowired
    public CheckAccountUniqueServiceImpl(SystemErrorResponse systemErrorResponse,
                                         RpAccountsTableHelper rpAccountsTableHelper,
                                         EnrollCommon enrollCommon) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.enrollCommon = enrollCommon;
    }

    @Override
    public AuthAPIResponse checkAccountUnique(String appUuid, String appAccountName) {
        boolean existAccount = enrollCommon.isExistAccountName(appUuid, appAccountName);
        if (existAccount)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USERNAME_USED);
        else
            return systemErrorResponse.getSuccessResponse();
    }
}
