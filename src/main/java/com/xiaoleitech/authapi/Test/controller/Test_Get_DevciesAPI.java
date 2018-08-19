package com.xiaoleitech.authapi.Test.controller;

import com.xiaoleitech.authapi.Test.mapper.FlowerMapper;
import com.xiaoleitech.authapi.Test.model.pojo.Flower;
import com.xiaoleitech.authapi.Test.service.DevicesService;
import com.xiaoleitech.authapi.model.pojo.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

//@RestController
@Controller
//@RequestMapping("/test")
public class Test_Get_DevciesAPI implements Serializable {
    //    private DevicesService devicesService;
//    @Autowired
//    DevicesMapper devicesMapper;
    private final FlowerMapper flowerMapper;
    private final
    DevicesService devicesService;

    @Autowired
    public Test_Get_DevciesAPI(FlowerMapper flowerMapper, DevicesService devicesService) {
        this.flowerMapper = flowerMapper;
        this.devicesService = devicesService;
    }

    @RequestMapping("/test/flower")
    @ResponseBody
    Flower getFlower() {
        return flowerMapper.getFlowers();
    }

    @RequestMapping(value = "/test/selectdevices/imei", method = RequestMethod.GET)
    public @ResponseBody
    Devices selectDevicesByIMEI() {
//        DevicesService devicesService = new DevicesService();
        System.out.println("--->selectDevicesByIMEI");
        return devicesService.selectDevicesByIMEI("i123");
    }
}
