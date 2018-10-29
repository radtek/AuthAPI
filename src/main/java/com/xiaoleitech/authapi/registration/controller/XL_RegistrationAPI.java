package com.xiaoleitech.authapi.registration.controller;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.registration.bean.request.RegisterDeviceRequest;
import com.xiaoleitech.authapi.registration.bean.request.RegisterUserRequest;
import com.xiaoleitech.authapi.registration.bean.request.UpdateUserRequest;
import com.xiaoleitech.authapi.registration.service.RegisterDeviceService;
import com.xiaoleitech.authapi.registration.service.RegisterUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@Api(value = "注册接口控制器", tags = "2-Registration")
public class XL_RegistrationAPI {
    private final RegisterDeviceService registerDeviceService;
    private final RegisterUserService registerUserService;

    @Autowired
    public XL_RegistrationAPI(RegisterDeviceService registerDeviceService, RegisterUserService registerUserService) {
        this.registerDeviceService = registerDeviceService;
        this.registerUserService = registerUserService;
    }

    /**
     * 注册设备
     * post https://server/api/register_device
     *
     * @param registerDeviceRequest Post data of register_device API.
     *                              form data:
     *                              imei=<imei>
     *                              protect_method_capability=<protect_method_capability>
     *                              device_type=<device_type>
     *                              device_model=<device_model>
     *                              device_tee=<device_tee>
     *                              device_se=<device_se>
     *                              device_token=<device_token>
     * @param bindingResult         Data binding result, including the validation error info if any.
     * @return Response of the register_device API.
     * return:
     * {
     * error_code: errorCode,
     * error_message: errorMessage,
     * [device_id: device_id]  // if errorCode == 0, UUID类型
     * }
     */
    //    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @RequestMapping(value = "/api/register_device", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse registerDevice(@ModelAttribute @Valid RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult) {
        System.out.println(registerDeviceRequest);

        return registerDeviceService.registerDevice(registerDeviceRequest, bindingResult);
    }

    /**
     * 设备反注册 (APP)
     * get https://server/api/deregister_device?device_id=<device_id>
     *
     * @param deviceId 系统定义的device_id（UUID类型），存于devices主表中
     * @return {
     * error_code: errorCode,
     * error_message: errorMessage
     * }
     */
    @RequestMapping(value = "/api/deregister_device", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse unregisterDevice(@RequestParam("device_id") String deviceId) {
        return registerDeviceService.unregisterDevice(deviceId);
    }


    /**
     * 用户注册 (APP)
     * post https://server/api/register_user
     *
     * @param registerUserRequest form data:
     *                            phone_no=<phone_no>
     *                            password=<password>
     *                            device_id=<device_id> (UUID)
     *                            [user_realname=<user_realname>]
     *                            [id_no=<id_no>]
     *                            [real_address=<real_address>]
     *                            [protect_methods=<protect_methods>]
     *                            [user_certificate_public_key=<user_certificate_public_key>]
     * @param bindingResult       数据绑定结果
     * @return {
     * error_code: errorCode,
     * error_message: error_message,
     * password_salt: password_salt,
     * auth_key: auth_key,
     * user_id: user_id, (UUID)
     * user_state: user_state
     * }
     * @implNote verify user's phone_no before register_user
     */
    @RequestMapping(value = "/api/register_user", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse registerUser(@ModelAttribute @Valid RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        return registerUserService.registerUser(registerUserRequest, bindingResult);
    }

    /**
     * 用户反注册 (APP)
     * get https://server/api/deregister_user?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param userUuid    系统中用户的UUID
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errorCode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/deregister_user", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse unregisterUser(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return registerUserService.unregisterUser(userUuid, verifyToken);
    }

    /**
     * 用户更新 （APP）
     * post https://server/api/update_user?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param updateUserRequest form data:
     *                          [password=<password>]
     *                          [user_realname=<user_realname>]
     *                          [id_no=<id_no>]
     *                          [id_expire_at=<yyyy/mm/dd>]
     *                          [protect_methods=<protect_methods>]
     *                          [face_enrolled=<face_enrolled>] // 0: not enrolled; 1: enrolled
     * @param userUuid          = <user_id>
     * @param verifyToken       = <verify_token>
     * @param bindingResult     数据绑定结果
     * @return {
     * error_code: errercode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/update_user", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse updateUser(@ModelAttribute UpdateUserRequest updateUserRequest, @RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken, BindingResult bindingResult) {
        updateUserRequest.setUser_id(userUuid);
        updateUserRequest.setVerify_token(verifyToken);
        return registerUserService.updateUser(updateUserRequest, bindingResult);
    }

    /**
     * 恢复用户状态
     * get https://server/api/recover_user?phone_no=<phone_no>&password=<password>&device_id=<device_id>
     *
     * @param deviceUuid:  用户注册时，系统分配的设备 UUID
     * @param password:    用户密码
     * @param phoneNumber: 用户绑定的手机号
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [user_id: user_id,] (UUID字符串)
     * [password_salt: password_salt,]
     * [auth_key: auth_key,]
     * [protect_methods: protect_methods]
     * }
     */
    @RequestMapping(value = "/api/recover_user", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse recoverUser(@RequestParam("device_id") String deviceUuid, @RequestParam("password") String password, @RequestParam("phone_no") String phoneNumber) {
        return registerUserService.recoverUser(deviceUuid, password, phoneNumber);
    }

    /**
     * 获得用户信息（客户SDK需求，实际是获取用户认证信息）
     * get https://server/api/get_sdk_auth_key?phone_no=<phone_no>&password=<password>&app_id=<app_id>
     *
     * @param appUuid:     APP UUID
     * @param password:    用户密码
     * @param phoneNumber: 用户绑定的手机号码
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [user_id: user_id,]  UUID
     * [protect_methods: protect_methods,]
     * [device_id: device_id,] UUID
     * [password_salt: password_salt,]
     * [sdk_auth_key: sdk_auth_key,]
     * }
     */
    @RequestMapping(value = "/api/get_sdk_auth_key", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAuthKey(@RequestParam("app_id") String appUuid, @RequestParam("password") String password, @RequestParam("phone_no") String phoneNumber) {
        return registerUserService.getAuthKey(appUuid, password, phoneNumber);
    }
}
