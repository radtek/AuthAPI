package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetOtpCodeService {

    String getOtpCode(String appUuid, String accountName, String nonce);
}
