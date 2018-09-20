package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAuthorizeStateService {

    AuthAPIResponse getAuthorizeState(String userUuid, String verifyToken, String appUuid);
}
