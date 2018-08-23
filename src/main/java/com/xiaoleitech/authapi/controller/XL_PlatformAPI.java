package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.service.platform.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
// TODO: How to use the same parent-request name, then the sub-request name just using the function name
//@RequestMapping("/api")
public class XL_PlatformAPI {
    private final
    PlatformService platformService;

    @Autowired
    public XL_PlatformAPI(PlatformService platformService) {
        this.platformService = platformService;
    }

    /**
     * 获得平台设置
     * get https://server/api/get_platform_setting
     *
     * @return {
     * error_code: errorCode,
     * error_message: errorMessage,
     * platform_name: platformName,
     * platform_logo_url: platform_logo_url,
     * use_ssl: use_ssl // 0: not use ssl, 1: RSA2048/SHA256, 2: SM2/SM3
     * }
     */
    //    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    @RequestMapping(value = "/api/get_platform_setting", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse get_platform_setting() {
        return platformService.getPlatformSetting();
    }
}
