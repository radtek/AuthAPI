package com.xiaoleitech.authapi.model.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class RegisterUserExistResponse extends AuthAPIResponse {
    private String user_id;
}
