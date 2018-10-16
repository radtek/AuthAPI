package com.xiaoleitech.authapi.platform.controller;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.platform.service.GetIDPInfoService;
import com.xiaoleitech.authapi.platform.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XL_CheckAPI {

    private final PingService pingService;
    private final GetIDPInfoService getIDPInfoService;

    @Autowired
    public XL_CheckAPI(PingService pingService, GetIDPInfoService getIDPInfoService) {
        this.pingService = pingService;
        this.getIDPInfoService = getIDPInfoService;
    }

    /**
     * PING (RP/APP)
     * get http://server/api/ping
     *
     * @return 200: OK
     */
    @RequestMapping(value = "/api/ping", method = RequestMethod.GET)
    public String ping() {
        return pingService.pingResponseString();
    }

    /**
     * 获取IDP资料
     * get https://server/api/get_idp_info
     *
     * @return {
     * error_code: errorCode,
     * error_message: errorMessage,
     * version: version,
     * supported_ios_app_version: version,
     * supported_android_app_version: version
     * }
     */
    @RequestMapping(value = "/api/get_idp_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse get_idp_info() {
        return getIDPInfoService.getIDPInfo();
    }
}
