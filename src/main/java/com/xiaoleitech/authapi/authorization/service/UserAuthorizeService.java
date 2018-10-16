package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.authorization.bean.request.UserAuthorizeRequest;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface UserAuthorizeService {

    AuthAPIResponse authorize(UserAuthorizeRequest userAuthorizeRequest, BindingResult bindingResult);
}
