package com.xiaoleitech.authapi.authorization.bean.request;

import com.xiaoleitech.authapi.global.bean.AppAccountRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthorizeRequest extends AppAccountRequest {
    private String nonce;
    private int client_type;
    private String response;
}
