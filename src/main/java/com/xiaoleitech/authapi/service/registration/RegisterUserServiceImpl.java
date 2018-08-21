package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.helper.UsersTableHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.mapper.UsersMapper;
import com.xiaoleitech.authapi.model.bean.RegisterUserRequest;
import com.xiaoleitech.authapi.model.bean.RegisterUserResponse;
import com.xiaoleitech.authapi.model.bean.UnregisterUserResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserStateEnum;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.UUID;

@Component
public class RegisterUserServiceImpl implements RegisterUserService {
    private final RegisterUserResponse registerUserResponse;
    private final UnregisterUserResponse unregisterUserResponse;
    private final UsersMapper usersMapper;
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;

    @Autowired
    public RegisterUserServiceImpl(RegisterUserResponse registerUserResponse, UnregisterUserResponse unregisterUserResponse, UsersMapper usersMapper, SystemErrorResponse systemErrorResponse, UsersTableHelper usersTableHelper) {
        this.registerUserResponse = registerUserResponse;
        this.unregisterUserResponse = unregisterUserResponse;
        this.usersMapper = usersMapper;
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
    }

    @Transactional
    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        ErrorCodeEnum errorCode;

        // 检查通过API传入的参数是否有绑定错误，具体绑定规则的修改请转到 RegisterUserRequest 类
        // 如果有数据绑定错误，返回绑定错误原因
        if (systemErrorResponse.checkRequestParams(bindingResult, registerUserResponse) != ErrorCodeEnum.ERROR_OK) {
            return registerUserResponse;
        }

        // TODO: 后续增加更多的数据有效性验证

        // 在系统中查找待注册的用户，
        // TODO: 暂时用 getUserByPhoneNo，如果可以采用身份证判断，改成 getUserByIdNo。
        Users user = usersTableHelper.getUserByPhoneNo(registerUserRequest.getPhone_no());

        // 如果查不到该用户，则在系统中增加此用户记录
        if (user == null) {
            errorCode = addNewUserRecord(registerUserRequest);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                systemErrorResponse.fillErrorResponse(registerUserResponse, errorCode);
                return registerUserResponse;
            }
        } else {
            // 如果查到该用户，检查用户状态
            if ((user.getUser_state() != UserStateEnum.USER_NOT_REGISTERED.getState()) ||
                    (user.getUser_state() != UserStateEnum.USER_UNREGISTERED.getState())) {
                // 用户状态不是未注册和未注销，则返回用户不能重复注册的错误信息
                systemErrorResponse.fillErrorResponse(registerUserResponse, ErrorCodeEnum.ERROR_USER_REGISTERED);
                return registerUserResponse;
            }

            // 更新用户状态及相应数据（身份、地址、号码等等，以API传入的数据为准）
            errorCode = updateUserRecord(registerUserRequest, user, UserStateEnum.USER_REG_BINDING_L1);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                systemErrorResponse.fillErrorResponse(registerUserResponse, errorCode);
                return registerUserResponse;
            }
        }

        return null;
    }

    @Transactional
    @Override
    public UnregisterUserResponse unregisterUser(String userUuid, String verifyToken) {
        return null;
    }

    private ErrorCodeEnum copyUserParamsFromRequest(RegisterUserRequest registerUserRequest, Users user) {
        // 使用 Request 数据填充 user
        user.setPhone_no(registerUserRequest.getPhone_no());
        user.setPassword(registerUserRequest.getPassword());
        user.setDevice_id(registerUserRequest.getDevice_id());
        user.setReal_name(registerUserRequest.getUser_realname());
        user.setId_no(registerUserRequest.getId_no());
        user.setReal_address(registerUserRequest.getReal_address());
        user.setProtect_methods(registerUserRequest.getProtect_methods());
        // TODO: 表中的 AuthKey 是否和 User_certificate_public_key 含义一致
        user.setAuth_key(registerUserRequest.getUser_certificate_public_key());

        return ErrorCodeEnum.ERROR_OK;
    }

    private ErrorCodeEnum addNewUserRecord(RegisterUserRequest registerUserRequest) {
        // 准备插入记录的数据，从Request中复制用户信息
        Users user = new Users();
        copyUserParamsFromRequest(registerUserRequest, user);

        // 设置用户的UUID、状态、创建时间和更新时间
        user.setUser_uuid(UUID.randomUUID().toString());
        user.setUser_state(UserStateEnum.USER_REG_BINDING_L1.getState());
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        user.setCreated_at(currentTime);
        user.setUpdated_at(currentTime);

        // insertOneUser 方法返回插入的记录数量，如果成功了，则返回1
        int num = usersMapper.insertOneUser(user);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }

    private ErrorCodeEnum updateUserRecord(RegisterUserRequest registerUserRequest, Users user, UserStateEnum userState) {
        return ErrorCodeEnum.ERROR_OK;
    }
}
