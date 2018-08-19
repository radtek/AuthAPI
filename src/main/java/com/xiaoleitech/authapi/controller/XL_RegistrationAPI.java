package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;
import com.xiaoleitech.authapi.service.registration.RegisterDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class XL_RegistrationAPI {
    private final
    RegisterDeviceService registerDeviceService;

    @Autowired
    public XL_RegistrationAPI(RegisterDeviceService registerDeviceService) {
        this.registerDeviceService = registerDeviceService;
    }

    @RequestMapping(value = "/api/register_device", method = RequestMethod.POST)
    public @ResponseBody
    RegisterDeviceResponse register_device(@ModelAttribute RegisterDeviceRequest registerDeviceRequest, Model model) {
        System.out.println(registerDeviceRequest);

        return registerDeviceService.registerDevcie(registerDeviceRequest);
    }
}
