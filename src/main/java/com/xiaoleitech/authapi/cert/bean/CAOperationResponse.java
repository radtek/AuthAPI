package com.xiaoleitech.authapi.cert.bean;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class CAOperationResponse extends AuthAPIResponse {
    private int ca_id;
}
