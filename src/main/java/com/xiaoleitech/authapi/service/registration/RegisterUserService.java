package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.registration.RegisterUserRequest;
import com.xiaoleitech.authapi.model.registration.UpdateUserRequest;
import org.springframework.validation.BindingResult;

public interface RegisterUserService {

    AuthAPIResponse registerUser(RegisterUserRequest registerUserRequest, BindingResult bindingResult);

    AuthAPIResponse unregisterUser(String userUuid, String verifyToken);

    AuthAPIResponse updateUser(UpdateUserRequest updateUserRequest, BindingResult bindingResult);

    AuthAPIResponse recoverUser(String deviceUuid, String password, String phoneNumber);

    AuthAPIResponse getAuthKey(String appUuid, String password, String phoneNumber);
}
