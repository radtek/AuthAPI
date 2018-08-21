package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.*;
import com.xiaoleitech.authapi.service.registration.RegisterDeviceService;
import com.xiaoleitech.authapi.service.registration.RegisterUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class XL_RegistrationAPI {
    private final RegisterDeviceService registerDeviceService;
    private final RegisterUserService registerUserService;

    @Autowired
    public XL_RegistrationAPI(RegisterDeviceService registerDeviceService, RegisterUserService registerUserService) {
        this.registerDeviceService = registerDeviceService;
        this.registerUserService = registerUserService;
    }

    @RequestMapping(value = "/api/register_device", method = RequestMethod.POST)
    public @ResponseBody
    RegisterDeviceResponse register_device(@ModelAttribute @Valid RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult) {
        System.out.println(registerDeviceRequest);

        return registerDeviceService.registerDevcie(registerDeviceRequest, bindingResult);
    }

    @RequestMapping(value = "/api/deregister_device", method = RequestMethod.GET)
    public @ResponseBody
    UnregisterDeviceResponse unregisterDevice(@RequestParam("device_id") String uuid) {
        return registerDeviceService.unregisterDevice(uuid);
    }

    @RequestMapping(value = "/api/register_user", method = RequestMethod.POST)
    public @ResponseBody
    RegisterUserResponse registerUser(@ModelAttribute @Valid RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        return registerUserService.registerUser(registerUserRequest, bindingResult);
    }

    @RequestMapping(value = "/api/deregister_user", method = RequestMethod.GET)
    public @ResponseBody
    UnregisterUserResponse unregisterUser(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return registerUserService.unregisterUser(userUuid, verifyToken);
    }
}
