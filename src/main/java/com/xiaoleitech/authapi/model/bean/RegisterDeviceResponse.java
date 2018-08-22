package com.xiaoleitech.authapi.model.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDeviceResponse extends AuthAPIResponse {
    private String device_id;
}
