package com.xiaoleitech.authapi.model.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterUserResponse extends AuthAPIResponse {
    private String password_salt;
    private String auth_key;
    private String user_id;
    private int user_state;
}