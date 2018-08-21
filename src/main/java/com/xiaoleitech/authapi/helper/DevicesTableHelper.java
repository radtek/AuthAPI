package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.mapper.DevicesMapper;
import com.xiaoleitech.authapi.model.pojo.Devices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DevicesTableHelper {
    private final DevicesMapper devicesMapper;

    @Autowired
    public DevicesTableHelper(DevicesMapper devicesMapper) {
        this.devicesMapper = devicesMapper;
    }

    /**
     * 获取设备（指定IMEI）的UUID
     *
     * @param imei 设备的IMEI码
     * @return UUID（字符串格式）
     */
    public String getDevicesUuidByImei(String imei) {
        List<Devices> devicesList = devicesMapper.selectDevicesByIMEI(imei);
        if (devicesList.size() == 0) return "";

        Devices device = devicesList.get(0);
        return device.getDevice_uuid();
    }

    /**
     * 获取指定IMEI的设备对象
     *
     * @param imei 设备的IMEI码
     * @return Devices device (默认IMEI全局唯一)
     */
    public Devices getDeviceByImei(String imei) {
        List<Devices> devicesList = devicesMapper.selectDevicesByIMEI(imei);
        // TODO: 对系统中有多条记录的情况，需讨论处理方式
        if (devicesList.size() == 0)
            return null;
        else
            return devicesList.get(0);
    }

    /**
     * 获取指定UUID的设备对象
     *
     * @param uuid 设备在系统中的UUID
     * @return Devices device
     */
    public Devices getDeviceByUuid(String uuid) {
        List<Devices> devicesList = devicesMapper.selectDevicesByUuid(uuid);
        if (devicesList.size() == 0)
            return null;
        else
            return devicesList.get(0);
    }
}
