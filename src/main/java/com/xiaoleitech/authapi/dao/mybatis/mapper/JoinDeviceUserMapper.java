package com.xiaoleitech.authapi.dao.mybatis.mapper;

import com.xiaoleitech.authapi.dao.pojo.Users;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JoinDeviceUserMapper {
    @Select("SELECT * FROM users u " +
            "LEFT JOIN devices d " +
            "ON u.device_id=d.id " +
            "WHERE d.device_uuid=#{device_uuid} " +
            "AND d.state>0 AND u.user_state>0")
    List<Users> getUserByDeviceUuid(@Param("device_uuid") String deviceUuid);
}
