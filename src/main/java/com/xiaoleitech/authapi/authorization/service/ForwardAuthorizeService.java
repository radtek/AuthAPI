package com.xiaoleitech.authapi.authorization.service;

import org.springframework.stereotype.Component;

@Component
public interface ForwardAuthorizeService {

    String forwardAuthorize(String appUuid, String token, String accountName, String nonce);
}
