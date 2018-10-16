package com.xiaoleitech.authapi.dao.mybatis.mapper;

import com.xiaoleitech.authapi.dao.pojo.Devices;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DevicesMapper {
    @Select("SELECT * FROM devices d WHERE d.imei=#{imei} AND d.state>0")
    List<Devices> selectDevicesByIMEI(@Param("imei") String imei);

    @Select("SELECT * FROM devices d WHERE d.device_uuid=#{device_uuid} AND d.state>0")
    List<Devices> selectDevicesByUuid(@Param("device_uuid") String deviceUuid);

    @Select("SELECT * FROM devices d WHERE d.id=#{device_id} AND d.state>0")
    List<Devices> selectDevicesById(@Param("device_id") int deviceId);

    @Insert("INSERT INTO devices(" +
            "device_uuid, imei, state, protect_method_capability, " +
            "device_model, device_tee, device_se, device_type, " +
            "device_token, created_at, updated_at) " +
            "VALUES (" +
            "#{device_uuid}, #{imei}, #{state}, #{protect_method_capability}, " +
            "#{device_model}, #{device_tee}, #{device_se}, " +
            "#{device_type}, #{device_token}, " +
            "#{created_at, jdbcType=TIMESTAMP }, #{updated_at, jdbcType=TIMESTAMP })")
    int insertOneDevice(Devices device);

    @Update("UPDATE devices d " +
            "SET state=#{state}, updated_at=#{updated_at, jdbcType=TIMESTAMP} " +
            "WHERE imei=#{imei}  AND d.state>-1 ")
    int updateDeviceRegisterStatusByImei(Devices device);

    @Update("UPDATE devices d " +
            "SET state=#{state}, updated_at=#{updated_at, jdbcType=TIMESTAMP} " +
            "WHERE device_uuid=#{device_uuid}  AND d.state>-1 " )
    int updateDeviceRegisterStatusByUuid(Devices device);

    @Update("UPDATE devices d " +
            "SET " +
            "state=#{state}, " +
            "protect_method_capability=#{protect_method_capability}, " +
            "device_model=#{device_model}, " +
            "device_tee=#{device_tee}, " +
            "device_se=#{device_se}, " +
            "device_type=#{device_type}, " +
            "device_token=#{device_token}, " +
            "updated_at=#{updated_at, jdbcType=TIMESTAMP} " +
            "WHERE device_uuid=#{device_uuid} AND d.state>-1 ")
    int updateDeviceRecord(Devices device);

}
