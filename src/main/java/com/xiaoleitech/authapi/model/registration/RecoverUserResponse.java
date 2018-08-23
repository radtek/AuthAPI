package com.xiaoleitech.authapi.model.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class RecoverUserResponse extends AuthAPIResponse {
    private String user_id;
    private String password_salt;
    private String auth_key;
    private String protect_methods;
}
