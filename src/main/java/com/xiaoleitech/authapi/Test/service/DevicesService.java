package com.xiaoleitech.authapi.Test.service;

import com.xiaoleitech.authapi.model.pojo.Devices;

public interface DevicesService {

    Devices selectDevicesByIMEI(String imei);
}
