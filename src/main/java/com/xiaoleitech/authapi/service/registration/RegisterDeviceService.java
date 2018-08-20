package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;
import org.springframework.validation.BindingResult;

public interface RegisterDeviceService {
    /**
     * @param registerDeviceRequest Post data of register_device API.
     * @param bindingResult         Data binding result, including the validation error info if any.
     * @return Response of the register_device API.
     */
    RegisterDeviceResponse registerDevcie(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult);
}
