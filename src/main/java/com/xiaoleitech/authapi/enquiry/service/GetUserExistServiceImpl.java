package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.UserExistResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.UserStateEnum;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.stereotype.Component;

@Component
public class GetUserExistServiceImpl implements GetUserExistService {
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final UserExistResponse userExistResponse;

    public GetUserExistServiceImpl(SystemErrorResponse systemErrorResponse, UsersTableHelper usersTableHelper, RelyPartsTableHelper relyPartsTableHelper, UserExistResponse userExistResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.userExistResponse = userExistResponse;
    }

    @Override
    public AuthAPIResponse getUserExist(String phoneNo, String appUuid) {
        System.out.println("------>   getUserExist");

        // 检查请求参数
        if (phoneNo.isEmpty())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 按电话号码查询 user 记录
        Users user = usersTableHelper.getUserByPhoneNo(phoneNo);
        if (user == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_FOUND);
        if ( (user.getUser_state() == UserStateEnum.USER_NOT_REGISTERED.getState()) ||
                (user.getUser_state() == UserStateEnum.USER_UNREGISTERED.getState()))
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 如果没有指定应用UUID，返回依据电话号码在本系统中获取 user 信息
        if ((appUuid == null) || (appUuid.isEmpty())) {
            userExistResponse.setUser_id(user.getUser_uuid());
            userExistResponse.setId_no(user.getId_no());
            userExistResponse.setUser_realname(user.getReal_name());
            systemErrorResponse.fill(userExistResponse, ErrorCodeEnum.ERROR_USER_REGISTERED);
            return userExistResponse;
        }

        // TODO: 如果指定了应用UUID，则通过应用方的回调接口获取用户信息
        return systemErrorResponse.notImplemented();
    }
}
