package com.xiaoleitech.authapi.zold.Test.service;

import com.xiaoleitech.authapi.dao.pojo.Devices;

public interface DevicesService {

    Devices selectDevicesByIMEI(String imei);
}
