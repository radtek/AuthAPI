package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserRevokeAuthorizeService {

    AuthAPIResponse revokeAuthorize(String appUuid, String userUuid, String verifyToken);
}
