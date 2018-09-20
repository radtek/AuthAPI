package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.registration.RegisterDeviceRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

public interface RegisterDeviceService {

    AuthAPIResponse registerDevice(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult);

    AuthAPIResponse unregisterDevice(String deviceId);

}
