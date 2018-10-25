package com.xiaoleitech.authapi.cert.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class SignCloudCertRequest extends AppAccountRequest{
    private String r;
    private String s2;
    private String s3;
}
