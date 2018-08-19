package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.Devices;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DevicesMapper {
    @Select("SELECT * FROM devices WHERE imei=#{imei}")
    List<Devices> selectDevicesByIMEI(@Param("imei") String imei);

    @Insert("INSERT INTO devices(" +
            "device_uuid, imei, state, protect_method_capability, " +
            "device_model, device_tee, device_se, device_type, " +
            "device_token)" +//, created_at, updated_at) " +
            "VALUES (" +
            "#{device_uuid}, #{imei}, #{state}, #{protect_method_capability}, " +
            "#{device_model}, #{device_tee}, #{device_se}, " +
            "#{device_type}, #{device_token})")
//, #{created_at}, #{updated_at})")
    int insertDevice(Devices device);

}
