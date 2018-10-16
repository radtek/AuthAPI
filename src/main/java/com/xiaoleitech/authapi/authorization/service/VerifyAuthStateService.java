package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface VerifyAuthStateService {

    AuthAPIResponse verifyAuthState(String appUuid, String token, String appAccountUuid, String authorizeToken);
}
