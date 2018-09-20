package com.xiaoleitech.authapi.model.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
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
