package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;

public interface RegisterDeviceService {
    RegisterDeviceResponse registerDevcie(RegisterDeviceRequest registerDeviceRequest);
}
