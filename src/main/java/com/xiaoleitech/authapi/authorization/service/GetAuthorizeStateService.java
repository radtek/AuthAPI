package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAuthorizeStateService {

    AuthAPIResponse getAuthorizeState(String userUuid, String verifyToken, String appUuid);
}
