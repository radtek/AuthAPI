package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.UserInfoResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetUserInfoServiceImpl implements GetUserInfoService{
    private final UsersTableHelper usersTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final UserInfoResponse userInfoResponse;

    @Autowired
    public GetUserInfoServiceImpl(UsersTableHelper usersTableHelper, SystemErrorResponse systemErrorResponse, UserInfoResponse userInfoResponse) {
        this.usersTableHelper = usersTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.userInfoResponse = userInfoResponse;
    }

    @Override
    public AuthAPIResponse getUserInfo(String userUuid, String verifyToken) {
        // 获取指定UUID的 user 记录
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_USER);

        userInfoResponse.setUser_realname(user.getReal_name());
        userInfoResponse.setPhone_no(user.getPhone_no());
        userInfoResponse.setProtect_methods(user.getProtect_methods());
        userInfoResponse.setReal_address(user.getReal_address());
        userInfoResponse.setId_no(user.getId_no());
        userInfoResponse.setFace_enrolled(user.getFace_enrolled());

        systemErrorResponse.fillErrorResponse(userInfoResponse, ErrorCodeEnum.ERROR_OK);

        return userInfoResponse;
    }
}
