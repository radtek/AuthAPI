package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.model.authentication.UserAuthRequest;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface UserAuthenticateService {

    AuthAPIResponse userAuthenticate(UserAuthRequest userAuthRequest, BindingResult bindingResult);
}
