package com.xiaoleitech.authapi.cert.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SignMessageRequest {
    private String user_id;
    private String verify_token;
    private String app_id;
    private int alg;
    private String hash;
    private String message;
}
