package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserLogoutService {

    AuthAPIResponse logout(String userUuid, String verifyToken);
}
