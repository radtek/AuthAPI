package com.xiaoleitech.authapi.service.check;

import com.xiaoleitech.authapi.model.bean.PingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PingServiceImpl implements PingService {
    private final PingResponse pingResponse;

    @Autowired
    public PingServiceImpl(PingResponse pingResponse) {
        this.pingResponse = pingResponse;
    }

    @Override
    public PingResponse ping() {
        // PING Always response the success message to the caller.
        // And in this version, just response the error code & msg.
        pingResponse.setError_code(200);
        pingResponse.setError_message("OK");
        return pingResponse;
    }

    @Override
    public String pingResponseString() {
        // For the APP need, return the formatted string
        PingResponse pingResponse = ping();
        return pingResponse.getError_code() + ": " + pingResponse.getError_message();
    }
}
