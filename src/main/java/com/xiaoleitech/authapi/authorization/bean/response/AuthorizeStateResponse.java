package com.xiaoleitech.authapi.authorization.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorizeStateResponse extends AuthAPIResponse {
    private int auth_state;
    private java.sql.Timestamp auth_at;
    private int auth_life_time;
    private String authorization_token;
}
