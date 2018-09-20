package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.model.authorization.UserAuthorizeRequest;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface UserAuthorizeService {

    AuthAPIResponse authorize(UserAuthorizeRequest userAuthorizeRequest, BindingResult bindingResult);
}
