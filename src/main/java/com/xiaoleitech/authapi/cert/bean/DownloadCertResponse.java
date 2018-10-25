package com.xiaoleitech.authapi.cert.bean;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Data
@Component
@EqualsAndHashCode(callSuper = true)
public class DownloadCertResponse extends AuthAPIResponse {
    private String  certificate;
}
