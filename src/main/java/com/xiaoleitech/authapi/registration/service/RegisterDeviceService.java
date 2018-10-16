package com.xiaoleitech.authapi.registration.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.registration.bean.request.RegisterDeviceRequest;
import org.springframework.validation.BindingResult;

public interface RegisterDeviceService {

    AuthAPIResponse registerDevice(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult);

    AuthAPIResponse unregisterDevice(String deviceId);

}
