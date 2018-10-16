package com.xiaoleitech.authapi.authentication.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface RefreshTokenService {

    AuthAPIResponse refreshToken(String userUuid, String verifyToken);
}
