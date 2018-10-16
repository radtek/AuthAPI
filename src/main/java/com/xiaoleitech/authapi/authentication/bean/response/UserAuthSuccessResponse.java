package com.xiaoleitech.authapi.authentication.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthSuccessResponse extends AuthAPIResponse {
    private String verify_token;
    private java.sql.Timestamp expire_at;
}
