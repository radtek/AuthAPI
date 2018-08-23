package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.check.PingResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PingServiceImpl implements PingService {
    private final PingResponse pingResponse;
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public PingServiceImpl(PingResponse pingResponse, SystemErrorResponse systemErrorResponse) {
        this.pingResponse = pingResponse;
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public AuthAPIResponse ping() {
        // PING Always response the success message to the caller.
        // And in this version, just response the error code & msg.
        return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_HTTP_SUCCESS);
    }

    @Override
    public String pingResponseString() {
        // For the APP need, return the formatted string
        AuthAPIResponse authAPIResponse = ping();
        return authAPIResponse.getError_code() + ": " + authAPIResponse.getError_message();
    }
}
