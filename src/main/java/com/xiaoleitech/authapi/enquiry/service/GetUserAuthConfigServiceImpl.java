package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.dao.helper.DevicesTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.UserAuthConfigResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.Devices;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetUserAuthConfigServiceImpl implements GetUserAuthConfigService {
    private final SystemErrorResponse systemErrorResponse;
    private final UserAuthConfigResponse userAuthConfigResponse;
    private final UsersTableHelper usersTableHelper;
    private final DevicesTableHelper devicesTableHelper;

    @Autowired
    public GetUserAuthConfigServiceImpl(SystemErrorResponse systemErrorResponse, UserAuthConfigResponse userAuthConfigResponse, UsersTableHelper usersTableHelper, DevicesTableHelper devicesTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.userAuthConfigResponse = userAuthConfigResponse;
        this.usersTableHelper = usersTableHelper;
        this.devicesTableHelper = devicesTableHelper;
    }

    @Override
    public AuthAPIResponse getUserAuthConfig(String userUuid, String deviceUuid) {
        // 检查参数
        if (userUuid.isEmpty() || deviceUuid.isEmpty())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 检查用户是否存在
        Users  user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 检查绑定设备
        Devices device = devicesTableHelper.getDeviceByUuid(deviceUuid);
        if (device == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_DEVICE_NOT_FOUND);

        // 获取用户认证参数
        userAuthConfigResponse.setProtect_methods(user.getProtect_methods());
        userAuthConfigResponse.setFace_enrolled(user.getFace_enrolled());
        systemErrorResponse.fill(userAuthConfigResponse, ErrorCodeEnum.ERROR_OK);
        return userAuthConfigResponse;
    }
}
