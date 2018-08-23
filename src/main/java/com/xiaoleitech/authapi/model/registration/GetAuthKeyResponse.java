package com.xiaoleitech.authapi.model.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class GetAuthKeyResponse extends AuthAPIResponse {
    private String user_id;
    private String protect_methods;
    private String device_id;
    private String password_salt;
    private String sdk_auth_key;
}
