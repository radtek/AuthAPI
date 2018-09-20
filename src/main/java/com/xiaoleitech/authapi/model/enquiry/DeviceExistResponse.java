package com.xiaoleitech.authapi.model.enquiry;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceExistResponse extends AuthAPIResponse {
    private String device_id;
}
