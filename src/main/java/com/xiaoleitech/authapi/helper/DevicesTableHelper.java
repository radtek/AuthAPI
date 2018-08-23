package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.mapper.DevicesMapper;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
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
     * @return device: Devices 对象  (默认IMEI全局唯一)
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
     * @return device: Devices 对象
     */
    public Devices getDeviceByUuid(String uuid) {
        List<Devices> devicesList = devicesMapper.selectDevicesByUuid(uuid);
        if (devicesList.size() == 0)
            return null;
        else
            return devicesList.get(0);
    }

    /**
     * 更新一条设备记录，并将错误信息通过 AuthAPIResponse 对象返回
     *
     * @param device: 存放设备记录
     * @return errorCode: ErrorCodeEnum
     * 更新成功返回 ErrorCodeEnum.ERROR_OK，否则返回 ErrorCodeEnum.ERROR_INTERNAL_ERROR
     */
    public ErrorCodeEnum updateOneDeviceRecord(Devices device) {
        // 设置Device的更新时间
        device.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());

        // 更新该条设备记录
        int num = devicesMapper.updateDeviceRecord(device);

        // 如果更新没有执行成功，则统一设置为 ERROR_INTERNAL_ERROR 错误码，后续再根据实际应用需要做错误码细化
        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_INTERNAL_ERROR;
    }
}
