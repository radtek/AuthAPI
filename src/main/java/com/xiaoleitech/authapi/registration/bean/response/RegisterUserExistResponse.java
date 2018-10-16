package com.xiaoleitech.authapi.registration.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class RegisterUserExistResponse extends AuthAPIResponse {
    private String user_id;
}
