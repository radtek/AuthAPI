package com.xiaoleitech.authapi.zold.Test.controller;

import com.xiaoleitech.authapi.zold.Test.mapper.FlowerMapper;
import com.xiaoleitech.authapi.zold.Test.model.pojo.Flower;
import com.xiaoleitech.authapi.zold.Test.service.DevicesService;
import com.xiaoleitech.authapi.dao.pojo.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

//@RestController
@Controller
//@RequestMapping("/ztest")
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

    @RequestMapping("/ztest/flower")
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
