package com.xiaoleitech.authapi.model.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class RegisterDeviceResponse extends AuthAPIResponse {
    private String device_id;
}
