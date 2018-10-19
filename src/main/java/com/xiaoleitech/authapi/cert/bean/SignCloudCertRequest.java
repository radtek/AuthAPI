package com.xiaoleitech.authapi.cert.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SignCloudCertRequest {
    private String user_id;
    private String verify_token;
    private String app_id;
    private String r;
    private String s2;
    private String s3;
}
