package com.xiaoleitech.authapi.dao.jointable;

import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.dao.mybatis.mapper.JoinDeviceUserMapper;
import com.xiaoleitech.authapi.dao.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JoinDeviceUserHelper {
    private final JoinDeviceUserMapper joinDeviceUserMapper;

    @Autowired
    public JoinDeviceUserHelper(JoinDeviceUserMapper joinDeviceUserMapper) {
        this.joinDeviceUserMapper = joinDeviceUserMapper;
    }

    /**
     * 联合查询devices和users表，找到deviceUuid关联的user
     *
     * @param deviceUuid 设备的UUID
     * @return 单条（如果找到，取首条）Users记录
     */
    public Users getUserByDeviceUuid(String deviceUuid) {
        List<Users> usersList = joinDeviceUserMapper.getUserByDeviceUuid(deviceUuid);
        return UtilsHelper.getFirstValid(usersList);
    }
}
