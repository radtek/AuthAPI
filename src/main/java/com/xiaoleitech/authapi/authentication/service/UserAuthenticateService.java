package com.xiaoleitech.authapi.authentication.service;

import com.xiaoleitech.authapi.authentication.bean.request.UserAuthRequest;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface UserAuthenticateService {

    AuthAPIResponse userAuthenticate(UserAuthRequest userAuthRequest, BindingResult bindingResult);
}
