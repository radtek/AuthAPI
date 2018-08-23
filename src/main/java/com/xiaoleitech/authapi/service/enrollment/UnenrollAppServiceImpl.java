package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnenrollAppServiceImpl implements UnenrollAppService {
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public UnenrollAppServiceImpl(SystemErrorResponse systemErrorResponse) {
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public AuthAPIResponse unenrollApp(String userId, String verifyToken, String appId) {
        return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
    }
}
