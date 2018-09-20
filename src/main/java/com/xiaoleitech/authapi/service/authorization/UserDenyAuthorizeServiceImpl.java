package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDenyAuthorizeServiceImpl implements UserDenyAuthorizeService{
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public UserDenyAuthorizeServiceImpl(SystemErrorResponse systemErrorResponse) {
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public AuthAPIResponse denyAuthorize(String appUuid, String userUuid, String verifyToken) {
        return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
    }
}
