package com.xiaoleitech.authapi.registration.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
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
    private String sdk_auth_key; // base64编码
}
