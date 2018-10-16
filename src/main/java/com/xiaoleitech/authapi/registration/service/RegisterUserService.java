package com.xiaoleitech.authapi.registration.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.registration.bean.request.RegisterUserRequest;
import com.xiaoleitech.authapi.registration.bean.request.UpdateUserRequest;
import org.springframework.validation.BindingResult;

public interface RegisterUserService {

    AuthAPIResponse registerUser(RegisterUserRequest registerUserRequest, BindingResult bindingResult);

    AuthAPIResponse unregisterUser(String userUuid, String verifyToken);

    AuthAPIResponse updateUser(UpdateUserRequest updateUserRequest, BindingResult bindingResult);

    AuthAPIResponse recoverUser(String deviceUuid, String password, String phoneNumber);

    AuthAPIResponse getAuthKey(String appUuid, String password, String phoneNumber);
}
