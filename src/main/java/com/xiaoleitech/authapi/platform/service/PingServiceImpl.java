package com.xiaoleitech.authapi.platform.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.platform.bean.response.PingResponse;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
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
        return systemErrorResponse.getSuccessResponse();
    }

    @Override
    public String pingResponseString() {
        // For the APP need, return the formatted string
        AuthAPIResponse authAPIResponse = ping();
        return authAPIResponse.getError_code() + ": " + authAPIResponse.getError_message();
    }
}
