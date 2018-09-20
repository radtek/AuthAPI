package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface VerifyAuthStateService {

    AuthAPIResponse verifyAuthState(String appUuid, String token, String appAccountUuid, String authorizeToken);
}
