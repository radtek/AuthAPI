package com.xiaoleitech.authapi.model.bean;

import org.springframework.stereotype.Component;

@Component
public class RegisterDeviceResponse extends AuthAPIResponse {

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    private String device_id;
}
