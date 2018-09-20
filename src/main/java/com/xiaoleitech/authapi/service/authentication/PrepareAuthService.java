package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface PrepareAuthService {

    AuthAPIResponse prepareAuthenticate(String userUuid);
}
