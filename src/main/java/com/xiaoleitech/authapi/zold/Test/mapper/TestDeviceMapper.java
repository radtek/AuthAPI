package com.xiaoleitech.authapi.zold.Test.mapper;

import com.xiaoleitech.authapi.dao.pojo.Devices;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface TestDeviceMapper {
    @Select("SELECT * FROM devices")
    Devices selectDevicesByIMEI(String imei);
}

