package com.xiaoleitech.authapi.model.authorization;

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
