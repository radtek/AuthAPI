package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterUserRequest;
import com.xiaoleitech.authapi.service.registration.RegisterDeviceService;
import com.xiaoleitech.authapi.service.registration.RegisterUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class XL_RegistrationAPI {
    private final RegisterDeviceService registerDeviceService;
    private final RegisterUserService registerUserService;

    @Autowired
    public XL_RegistrationAPI(RegisterDeviceService registerDeviceService, RegisterUserService registerUserService) {
        this.registerDeviceService = registerDeviceService;
        this.registerUserService = registerUserService;
    }

    //    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @RequestMapping(value = "/api/register_device", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse register_device(@ModelAttribute @Valid RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult) {
        System.out.println(registerDeviceRequest);

        return registerDeviceService.registerDevcie(registerDeviceRequest, bindingResult);
    }

    @RequestMapping(value = "/api/deregister_device", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse unregisterDevice(@RequestParam("device_id") String uuid) {
        return registerDeviceService.unregisterDevice(uuid);
    }


    @RequestMapping(value = "/api/register_user", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse registerUser(@ModelAttribute @Valid RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        return registerUserService.registerUser(registerUserRequest, bindingResult);
    }

    @RequestMapping(value = "/api/deregister_user", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse unregisterUser(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return registerUserService.unregisterUser(userUuid, verifyToken);
    }
}
