package com.xiaoleitech.authapi.cert.bean;

import com.xiaoleitech.authapi.global.bean.AppAccountRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class DownloadCertRequest extends AppAccountRequest {
}
