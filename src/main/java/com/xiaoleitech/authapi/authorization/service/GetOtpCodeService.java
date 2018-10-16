package com.xiaoleitech.authapi.authorization.service;

import org.springframework.stereotype.Component;

@Component
public interface GetOtpCodeService {

    String getOtpCode(String appUuid, String accountName, String nonce);
}
