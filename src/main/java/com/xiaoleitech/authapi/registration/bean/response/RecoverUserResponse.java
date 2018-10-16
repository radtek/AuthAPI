package com.xiaoleitech.authapi.registration.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class RecoverUserResponse extends AuthAPIResponse {
    private String user_id; // UUID
    private String password_salt;
    private String auth_key;
    private String protect_methods;
}
