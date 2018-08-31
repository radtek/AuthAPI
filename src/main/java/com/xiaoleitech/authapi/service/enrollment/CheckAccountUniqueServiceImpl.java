package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckAccountUniqueServiceImpl implements CheckAccountUniqueService {
    private final SystemErrorResponse systemErrorResponse;
    private final RpAccountsTableHelper rpAccountsTableHelper;

    @Autowired
    public CheckAccountUniqueServiceImpl(SystemErrorResponse systemErrorResponse,
                                         RpAccountsTableHelper rpAccountsTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
    }

    @Override
    public AuthAPIResponse checkAccountUnique(int appId, String appAccountName) {
        int count = rpAccountsTableHelper.getRpAccountCountByRpIdAndUserName(appId, appAccountName);

        switch (count) {
            case 1:
                // 绑定指定APP的用户只有一个
                return systemErrorResponse.getHttpSuccessResponse();
            case 0:
                // 找不到绑定指定APP的用户
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);
            default:
                // 其他情况，包括可能出现的负数及大于等于2的情况，目前版本使用内部错误
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }
    }
}
