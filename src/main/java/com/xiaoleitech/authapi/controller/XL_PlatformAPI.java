package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.PlatformSettingResponse;
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

    //    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    @RequestMapping(value = "/api/get_platform_setting", method = RequestMethod.GET)
    public @ResponseBody
    PlatformSettingResponse get_platform_setting() {
        return platformService.getPlatformSetting();
    }
}
