package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface ForwardAuthorizeService {

    String forwardAuthorize(String appUuid, String token, String accountName, String nonce);
}
