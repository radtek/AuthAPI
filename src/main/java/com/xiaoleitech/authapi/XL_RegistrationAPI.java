package com.xiaoleitech.authapi;

import com.xiaoleitech.authapi.pojo.ErrorInfo;
import com.xiaoleitech.authapi.pojo.RegisterDeviceRequest;
import com.xiaoleitech.authapi.pojo.RegisterDeviceResponse;
import com.xiaoleitech.authapi.pojo.TEST_RegisterDevice;
import org.springframework.web.bind.annotation.*;

import java.net.PortUnreachableException;

@RestController
public class XL_RegistrationAPI {
    @RequestMapping(value = "/api/register_device", method = RequestMethod.POST)
    public @ResponseBody
    RegisterDeviceResponse register_device(@ModelAttribute RegisterDeviceRequest registerDeviceRequest) {
        System.out.println(registerDeviceRequest);

        RegisterDeviceResponse registerDeviceResponse = new RegisterDeviceResponse();
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setError_code(11);
        errorInfo.setError_message("failed");
        registerDeviceResponse.setDevice_id("DEV_100101");
//        registerDeviceResponse.setErrorInfo(errorInfo);

        return registerDeviceResponse;
    }
}
