package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.service.check.GetIDPInfoService;
import com.xiaoleitech.authapi.service.check.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XL_CheckAPI {

    private final PingService pingService;
    private final
    GetIDPInfoService getIDPInfoService;

    @Autowired
    public XL_CheckAPI(PingService pingService, GetIDPInfoService getIDPInfoService) {
        this.pingService = pingService;
        this.getIDPInfoService = getIDPInfoService;
    }

    @RequestMapping(value = "/api/ping", method = RequestMethod.GET)
    public String ping() {
        return pingService.pingResponseString();
    }

    @RequestMapping(value = "/api/get_idp_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse get_idp_info() {
        return getIDPInfoService.getIDPInfo();
    }
}
