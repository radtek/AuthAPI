package com.xiaoleitech.authapi.Test.service;

import com.xiaoleitech.authapi.Test.mapper.TestDeviceMapper;
import com.xiaoleitech.authapi.model.pojo.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DevicesServiceImpl implements DevicesService {
    private final
    TestDeviceMapper devicesMapper;

    @Autowired
    public DevicesServiceImpl(TestDeviceMapper devicesMapper) {
        this.devicesMapper = devicesMapper;
    }

    @Override
    public Devices selectDevicesByIMEI(String imei) {
        return devicesMapper.selectDevicesByIMEI(imei);
    }
}
