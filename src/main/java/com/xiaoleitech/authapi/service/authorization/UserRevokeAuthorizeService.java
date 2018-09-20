package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserRevokeAuthorizeService {

    AuthAPIResponse revokeAuthorize(String appUuid, String userUuid, String verifyToken);
}
