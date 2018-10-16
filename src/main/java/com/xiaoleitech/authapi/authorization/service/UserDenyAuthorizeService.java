package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserDenyAuthorizeService {

    AuthAPIResponse denyAuthorize(String appUuid, String userUuid, String verifyToken);
}
