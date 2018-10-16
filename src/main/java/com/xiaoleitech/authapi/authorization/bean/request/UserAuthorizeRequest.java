package com.xiaoleitech.authapi.authorization.bean.request;

import lombok.Data;

@Data
public class UserAuthorizeRequest {
    private String  app_id;
    private String  user_id;
    private String verify_token;
    private String nonce;
    private int client_type;
    private String response;
}
