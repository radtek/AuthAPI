package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.helper.*;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.cache.RedisCacheConfiguration;
import com.xiaoleitech.authapi.helper.jointable.JoinDeviceUserHelper;
import com.xiaoleitech.authapi.helper.table.DevicesTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.mapper.RpAccountsMapper;
import com.xiaoleitech.authapi.mapper.UsersMapper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.DeviceStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserStateEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.model.registration.*;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Component
public class RegisterUserServiceImpl implements RegisterUserService {
    private Logger logger = LoggerFactory.getLogger(RegisterUserServiceImpl.class);

    private final RegisterUserResponse registerUserResponse;
    private final UnregisterUserResponse unregisterUserResponse;
    private final UsersMapper usersMapper;
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;
    private final UpdateUserResponse updateUserResponse;
    private final RecoverUserResponse recoverUserResponse;
    private final GetAuthKeyResponse getAuthKeyResponse;
    private final RpAccountsMapper rpAccountsMapper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final DevicesTableHelper devicesTableHelper;
    private final JoinDeviceUserHelper joinDeviceUserHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public RegisterUserServiceImpl(RegisterUserResponse registerUserResponse,
                                   UnregisterUserResponse unregisterUserResponse,
                                   UsersMapper usersMapper,
                                   SystemErrorResponse systemErrorResponse,
                                   UsersTableHelper usersTableHelper,
                                   UpdateUserResponse updateUserResponse,
                                   RecoverUserResponse recoverUserResponse,
                                   GetAuthKeyResponse getAuthKeyResponse,
                                   RpAccountsMapper rpAccountsMapper,
                                   RpAccountsTableHelper rpAccountsTableHelper,
                                   DevicesTableHelper devicesTableHelper,
                                   JoinDeviceUserHelper joinDeviceUserHelper,
                                   AuthenticationHelper authenticationHelper) {
        this.registerUserResponse = registerUserResponse;
        this.unregisterUserResponse = unregisterUserResponse;
        this.usersMapper = usersMapper;
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
        this.updateUserResponse = updateUserResponse;
        this.recoverUserResponse = recoverUserResponse;
        this.getAuthKeyResponse = getAuthKeyResponse;
        this.rpAccountsMapper = rpAccountsMapper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.devicesTableHelper = devicesTableHelper;
        this.joinDeviceUserHelper = joinDeviceUserHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @Transactional
    @Override
    public AuthAPIResponse registerUser(RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        ErrorCodeEnum errorCode;
        AuthAPIResponse authAPIResponse = new AuthAPIResponse();

        logger.info("---> User Password" + registerUserRequest.getPassword());

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
                return systemErrorResponse.getGeneralResponse(errorCode);
            }
        } else {
            // 如果查到该用户，检查用户状态
            if ((user.getUser_state() != UserStateEnum.USER_NOT_REGISTERED.getState()) &&
                    (user.getUser_state() != UserStateEnum.USER_UNREGISTERED.getState())) {
                // 用户状态不是未注册和未注销，则返回用户不能重复注册的错误信息
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_REGISTERED);
            }

            // 验证密码
            if (!authenticationHelper.isValidPassword(
                    registerUserRequest.getPassword(),
                    user.getPassword(),
                    user.getPassword_salt()))
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_PASSWORD);

            // 更新用户状态及相应数据（身份、地址、号码等等，以API传入的数据为准）
            errorCode = updateRegisterUserRecord(registerUserRequest, user, UserStateEnum.USER_REG_BINDING_L1);
            if (errorCode != ErrorCodeEnum.ERROR_OK) {
                return systemErrorResponse.getGeneralResponse(errorCode);
            }
        }

        // User 使用 phone_no 来检索（有必要改成UUID检索）
        user = usersTableHelper.getUserByPhoneNo(registerUserRequest.getPhone_no());
        registerUserResponse.setPassword_salt(user.getPassword_salt());
        registerUserResponse.setAuth_key(user.getAuth_key());
        registerUserResponse.setUser_id(user.getUser_uuid());
        registerUserResponse.setUser_state(user.getUser_state());

        // Request 的服务成功执行，返回
        systemErrorResponse.fillErrorResponse(registerUserResponse, ErrorCodeEnum.ERROR_OK);

        return registerUserResponse;
    }

    @Transactional
    @Override
    public AuthAPIResponse unregisterUser(String userUuid, String verifyToken) {
        // TODO: 是否需要检查 verifyToken

        // 读取用户记录
        // 系统中找不到用户，则返回错误：用户未找到 (ERROR_USER_NOT_FOUND)
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if (user == null) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);
        }

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 如果用户已经是注销状态，则返回找不到用户
        if (user.getUser_state() == UserStateEnum.USER_UNREGISTERED.getState()) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);
        }

        ErrorCodeEnum errorCode;

        // 逻辑删除关联设备记录
        Devices device = devicesTableHelper.getDeviceById(user.getDevice_id());
        if (device != null){
            device.setState(DeviceStateEnum.DEV_LOGICAL_DELETE.getState());
            errorCode = devicesTableHelper.updateOneDeviceRecord(device);
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return systemErrorResponse.getGeneralResponse(errorCode);
        }

        // 逻辑删除账户记录
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByUserId(user.getUser_id());
        for (int i=0; i<rpAccountsList.size(); i++) {
            RpAccounts rpAccount = rpAccountsList.get(i);
            rpAccount.setState(AccountStateEnum.ACCOUNT_LOGICAL_DELETE.getState());
            errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return systemErrorResponse.getGeneralResponse(errorCode);
        }

        // 设置用户状态（未注册，逻辑删除）
        user.setUser_state(UserStateEnum.USER_LOGICAL_DELETE.getState());
        errorCode = usersTableHelper.updateOneUserRecord(user);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);


        return systemErrorResponse.getSuccessResponse();
    }

    @Transactional
    @Override
    public AuthAPIResponse updateUser(UpdateUserRequest updateUserRequest, BindingResult bindingResult) {
        // 读取用户记录
        Users user = usersTableHelper.getUserByUserUuid(updateUserRequest.getUser_id());

        // 系统中找不到用户，则返回错误：用户未找到 (ERROR_USER_NOT_FOUND)
        if (user == null) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);
        }

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(updateUserRequest.getVerify_token()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 验证密码
        if (!authenticationHelper.isValidPassword(updateUserRequest.getPassword(), user.getPassword(), user.getPassword_salt()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_PASSWORD);

        // 更新用户记录，只更新请求中包含的字段
        // TODO: 是否有更好的方法简化下面代码。(查阅lombok的 @NonNull，set null时会抛出异常)
        if (updateUserRequest.getVerify_token() != null)
            user.setVerify_token(updateUserRequest.getVerify_token());
        if (updateUserRequest.getUser_realname() != null)
            user.setReal_name(updateUserRequest.getUser_realname());
        if (updateUserRequest.getId_no() != null)
            user.setId_no(updateUserRequest.getId_no());
        // POST请求中到期时间为<yyyy/mm/dd>格式字符串，需要转换成时间格式
        if (updateUserRequest.getId_expire_at() != null) {
            Timestamp timestamp = UtilsHelper.parseExpireTimeFromString(updateUserRequest.getId_expire_at(), '/');
            user.setId_expire_at(timestamp);
        }
        if (updateUserRequest.getProtect_methods() != null)
            user.setProtect_methods(updateUserRequest.getProtect_methods());
        // TODO: 如果Request中没有 face_enrolled 参数，则使用默认值 0。各种状态值需要Review，避免用0表示某个明确的状态值
        user.setFace_enrolled(updateUserRequest.getFace_enrolled());

        // 成功更新一条记录，并返回执行结果 (Response)
        return systemErrorResponse.getGeneralResponse(usersTableHelper.updateOneUserRecord(user));
    }

    @Transactional
    @Override
    public AuthAPIResponse recoverUser(String deviceUuid, String password, String phoneNumber) {
        //检查参数，电话号码必须
        if (phoneNumber.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 先使用电话号码读取用户记录。
        Users user = usersTableHelper.getUserByPhoneNo(phoneNumber);
        if (user == null && !deviceUuid.isEmpty()) {
            // 如果电话号码找不到，再使用设备UUID来查找。这段没有实际意义，可以考虑删除
            user = joinDeviceUserHelper.getUserByDeviceUuid(deviceUuid);
        }

        // 系统中找不到用户，则返回错误：用户未找到 (ERROR_USER_NOT_FOUND)
        if (user == null) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);
        }

        // 检查手机号码，验证密码
        if (!user.getPhone_no().equals(phoneNumber))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_DEVICE);
        if (!authenticationHelper.isValidPassword(password, user.getPassword(), user.getPassword_salt()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_PASSWORD);

        // 设置用户状态（已注册，已绑定设备、手机号，已激活）
        user.setUser_state(UserStateEnum.USER_REG_BINDING_L1.getState());

        // 更新用户记录，如果失败则返回处理的错误码 (Response)
        ErrorCodeEnum errorCode = usersTableHelper.updateOneUserRecord(user);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // 填充响应数据：用户UUID，密码盐，认证秘钥，保护方法
        recoverUserResponse.setUser_id(user.getUser_uuid());
        recoverUserResponse.setPassword_salt(user.getPassword_salt());
        recoverUserResponse.setAuth_key(user.getAuth_key());
        recoverUserResponse.setProtect_methods(user.getProtect_methods());
        systemErrorResponse.fillErrorResponse(recoverUserResponse, ErrorCodeEnum.ERROR_OK);

        return recoverUserResponse;
    }

    @Transactional
    @Override
    public AuthAPIResponse getAuthKey(String appUuid, String password, String phoneNumber) {

        // 按照phone number 从users表中读取用户
        Users user = usersTableHelper.getUserByPhoneNo(phoneNumber);
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

//        // 从RPAccounts表中查询指定AppID的账户记录
//        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(appId, user.getUsers_id());
//        if (rpAccount == null)
//            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        //  验证密码
        if (!authenticationHelper.isValidPassword(password, user.getPassword(), user.getPassword_salt()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_PASSWORD);

        // 计算 sdk_auth_key
        String base64SdkAuthKey = authenticationHelper.getSdkAuthKey(appUuid, user.getAuth_key());

        // 填充响应数据
        getAuthKeyResponse.setUser_id(user.getUser_uuid());
        getAuthKeyResponse.setProtect_methods(user.getProtect_methods());
        getAuthKeyResponse.setDevice_id(devicesTableHelper.getDeviceById(user.getDevice_id()).getDevice_uuid());
        getAuthKeyResponse.setPassword_salt(user.getPassword_salt());
        getAuthKeyResponse.setSdk_auth_key(base64SdkAuthKey);

        // Request 的服务成功执行，返回
        systemErrorResponse.fillErrorResponse(getAuthKeyResponse, ErrorCodeEnum.ERROR_OK);

        return getAuthKeyResponse;
    }

    private ErrorCodeEnum copyUserParamsFromRequest(RegisterUserRequest registerUserRequest, Users user) {
        // Users表中device_id存放的是Devices的主键 device_id
        Devices device = devicesTableHelper.getDeviceByUuid(registerUserRequest.getDevice_id());
        if (device == null)
            return ErrorCodeEnum.ERROR_INVALID_DEVICE;
        user.setDevice_id(device.getDevice_id());

        // 使用 Request 数据填充 user
        user.setPhone_no(registerUserRequest.getPhone_no());
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
        BeanUtils.copyProperties(registerUserRequest, user);
        // Users表中device_id存放的是Devices的主键 device_id
        Devices device = devicesTableHelper.getDeviceByUuid(registerUserRequest.getDevice_id());
        if (device == null)
            return ErrorCodeEnum.ERROR_INVALID_DEVICE;
        user.setDevice_id(device.getDevice_id());
        user.setReal_name(registerUserRequest.getUser_realname());
        user.setAuth_key(registerUserRequest.getUser_certificate_public_key());

//        copyUserParamsFromRequest(registerUserRequest, user);

        // 认证秘钥
        user.setAuth_key(authenticationHelper.generateAuthKey());

        // 设置用户的UUID、状态、密码盐、认证秘钥、创建时间和更新时间
        user.setUser_uuid(UUID.randomUUID().toString());
        user.setUser_state(UserStateEnum.USER_REG_BINDING_L1.getState());
        user.setPassword_salt(UUID.randomUUID().toString());
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        user.setCreated_at(currentTime);
        user.setUpdated_at(currentTime);

        // 传入的用户密码已散列
        user.setPassword(registerUserRequest.getPassword());

        // insertOneUser 方法返回插入的记录数量，如果成功了，则返回1 （新建记录的数量）
        int num = usersMapper.insertOneUser(user);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_CANNOT_ENROLL;
    }

    private ErrorCodeEnum updateRegisterUserRecord(RegisterUserRequest registerUserRequest, Users user, UserStateEnum userState) {
        // user里存放了数据库中的用户旧资料，将 Post 请求中的新资料覆盖到记录中
        BeanUtils.copyProperties(registerUserRequest, user);
        // Users表中device_id存放的是Devices的主键 device_id
        Devices device = devicesTableHelper.getDeviceByUuid(registerUserRequest.getDevice_id());
        if (device == null)
            return ErrorCodeEnum.ERROR_INVALID_DEVICE;
        user.setDevice_id(device.getDevice_id());
        user.setReal_name(registerUserRequest.getUser_realname());
        user.setAuth_key(registerUserRequest.getUser_certificate_public_key());
//        copyUserParamsFromRequest(registerUserRequest, user);

        // 设置用户的状态
        user.setUser_state(userState.getState());

        return usersTableHelper.updateOneUserRecord(user);
    }
}
