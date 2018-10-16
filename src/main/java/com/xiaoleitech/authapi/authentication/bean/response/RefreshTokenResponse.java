package com.xiaoleitech.authapi.authentication.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshTokenResponse extends AuthAPIResponse {
    private java.sql.Timestamp expire_at;
    private String verify_token;
}
