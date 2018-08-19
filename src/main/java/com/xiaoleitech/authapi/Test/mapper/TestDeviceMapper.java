package com.xiaoleitech.authapi.Test.mapper;

import com.xiaoleitech.authapi.model.pojo.Devices;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface TestDeviceMapper {
    @Select("SELECT * FROM devices")
    Devices selectDevicesByIMEI(String imei);
}

