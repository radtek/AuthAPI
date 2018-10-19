package com.xiaoleitech.authapi.cert.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VerifySignatureRequest {
    private String signed_value;
    private int alg;
    private String hash;
    private String message;
    private String certificate;
    private String cacert;
}
