package com.xiaoleitech.authapi;

import com.xiaoleitech.authapi.pojo.PlatformSettingResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.management.counter.perf.PerfLongArrayCounter;

@RestController
// TODO: How to use the same parent-request name, then the sub-request name just using the function name
//@RequestMapping("/api")
public class XL_PlatformAPI {
    //    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    @RequestMapping(value = "/api/get_platform_setting", method = RequestMethod.GET)
    public @ResponseBody
    PlatformSettingResponse get_platform_setting() {
        PlatformSettingResponse platformSettingResponse = new PlatformSettingResponse();
        platformSettingResponse.setError_code(400);
        platformSettingResponse.setError_message("Success");
        platformSettingResponse.setPlatform_name("IOS");
        platformSettingResponse.setPlatform_logo_url("www.google.com");
        platformSettingResponse.setUse_ssl(0);
        return platformSettingResponse;
    }

    // ========================================================================
    // Just for Test
    @RequestMapping(value = "/api/get_platform_setting2", method = RequestMethod.GET)
    public String get_platform_setting2() {
        return "123";
    }
}
