package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.helper.UsersTableHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.mapper.UsersMapper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
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
    public AuthAPIResponse registerUser(RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        ErrorCodeEnum errorCode;
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();

        // 检查通过API传入的参数是否有绑定错误，具体绑定规则的修改请转到 RegisterUserRequest 类
        // 如果有数据绑定错误，返回绑定错误原因
        if (systemErrorResponse.checkRequestParams(bindingResult, authAPIResponse) != ErrorCodeEnum.ERROR_OK) {
            return authAPIResponse;
        }

        // TODO: 手机验证

        // TODO: 后续增加更多的数据有效性验证

        // 在系统中查找待注册的用户，
        // TODO: 暂时用 getUserByPhoneNo，如果可以采用身份证判断，改成 getUserByIdNo。
        Users user = usersTableHelper.getUserByPhoneNo(registerUserRequest.getPhone_no());

        // 如果查不到该用户，则在系统中增加此用户记录
        if (user == null) {
            errorCode = addNewUserRecord(registerUserRequest);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                systemErrorResponse.fillErrorResponse(authAPIResponse, errorCode);
                return authAPIResponse;
            }
        } else {
            // 如果查到该用户，检查用户状态
            if ((user.getUser_state() != UserStateEnum.USER_NOT_REGISTERED.getState()) &&
                    (user.getUser_state() != UserStateEnum.USER_UNREGISTERED.getState())) {
                // 用户状态不是未注册和未注销，则返回用户不能重复注册的错误信息
                systemErrorResponse.fillErrorResponse(authAPIResponse, ErrorCodeEnum.ERROR_USER_REGISTERED);
                return authAPIResponse;
            }

            // 更新用户状态及相应数据（身份、地址、号码等等，以API传入的数据为准）
            errorCode = updateUserRecord(registerUserRequest, user, UserStateEnum.USER_REG_BINDING_L1);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                systemErrorResponse.fillErrorResponse(authAPIResponse, errorCode);
                return authAPIResponse;
            }
        }

        // Request 的服务成功执行，返回
        systemErrorResponse.fillErrorResponse(registerUserResponse, ErrorCodeEnum.ERROR_HTTP_SUCCESS);
        // User 使用 phone_no 来检索（有必要改成UUID检索）
        user = usersTableHelper.getUserByPhoneNo(registerUserRequest.getPhone_no());
        registerUserResponse.setPassword_salt(user.getPassword_salt());
        registerUserResponse.setAuth_key(user.getAuth_key());
        registerUserResponse.setUser_id(user.getUser_uuid());
        registerUserResponse.setUser_state(user.getUser_state());

        return registerUserResponse;
    }

    @Transactional
    @Override
    public AuthAPIResponse unregisterUser(String userUuid, String verifyToken) {
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();
        // TODO: 是否需要检查 verifyToken

        // 读取用户记录
        Users user = usersTableHelper.getUserByUuid(userUuid);

        // 系统中找不到用户，则返回错误：用户未找到 (ERROR_USER_NOT_FOUND)
        if (user == null) {
            systemErrorResponse.fillErrorResponse(authAPIResponse, ErrorCodeEnum.ERROR_USER_NOT_FOUND);
            return authAPIResponse;
        }

        // 设置用户状态（未注册，逻辑删除）和更新时间
        user.setUser_state(UserStateEnum.USER_UNREGISTERED.getState());
        user.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());

        // 更新用户记录，在目前代码逻辑中，也可以采用 updateUserStateByUserUuid
        int num = usersMapper.updateOneUserByUserUuid(user);

        // 成功更新一条记录，返回成功信息，否则返回内部错误
        systemErrorResponse.fillErrorResponse(unregisterUserResponse,
                (num == 1) ? ErrorCodeEnum.ERROR_HTTP_SUCCESS : ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        return unregisterUserResponse;
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

        // 设置用户的UUID、状态、密码盐、认证秘钥、创建时间和更新时间
        user.setUser_uuid(UUID.randomUUID().toString());
        user.setUser_state(UserStateEnum.USER_REG_BINDING_L1.getState());
        user.setPassword_salt(UUID.randomUUID().toString());
        // 认证秘钥
        user.setAuth_key(UUID.randomUUID().toString().replace("-", ""));
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        user.setCreated_at(currentTime);
        user.setUpdated_at(currentTime);

        // insertOneUser 方法返回插入的记录数量，如果成功了，则返回1 （新建记录的数量）
        int num = usersMapper.insertOneUser(user);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }

    private ErrorCodeEnum updateUserRecord(RegisterUserRequest registerUserRequest, Users user, UserStateEnum userState) {
        // user里存放了数据库中的用户旧资料，将 Post 请求中的新资料覆盖到记录中
        copyUserParamsFromRequest(registerUserRequest, user);

        // 设置用户的状态和更新时间
        user.setUser_state(userState.getState());
        user.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());

        // 更新整条用户记录，成功执行，则返回1（更新记录的数量）
        int num = usersMapper.updateOneUserByUserUuid(user);

        // 如果更新没有执行成功，则统一设置为 ERROR_CANNOT_ENROLL 错误码，后续再根据实际应用需要做错误码细化
        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }
}
