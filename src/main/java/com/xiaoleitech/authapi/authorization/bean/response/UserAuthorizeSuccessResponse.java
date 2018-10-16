package com.xiaoleitech.authapi.authorization.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthorizeSuccessResponse extends AuthAPIResponse {
    private String account_id;
    private String authorization_token;
}
