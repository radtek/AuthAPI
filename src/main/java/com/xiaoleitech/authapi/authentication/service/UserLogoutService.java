package com.xiaoleitech.authapi.authentication.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserLogoutService {

    AuthAPIResponse logout(String userUuid, String verifyToken);
}
