package com.xiaoleitech.authapi.enquiry.controller;

import com.xiaoleitech.authapi.enquiry.service.*;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "查询接口控制器", tags = "6-Enquiry")
public class XL_EnquiryAPI {
    private final GetUserInfoService getUserInfoService;
    private final GetAppEnrollmentInfoService getAppEnrollmentInfoService;
    private final GetUserEnrollmentInfoService getUserEnrollmentInfoService;
    private final GetDeviceEnrollmentInfoService getDeviceEnrollmentInfoService;
    private final GetUserExistService getUserExistService;
    private final GetDeviceExistService getDeviceExistService;
    private final GetUserAuthConfigService getUserAuthConfigService;
    private final GetAccountRealnameInfoService getAccountRealnameInfoService;
    private final GetAccountInfoService getAccountInfoService;
    private final CheckUserIdExistService checkUserIdExistService;

    @Autowired
    public XL_EnquiryAPI(GetUserInfoService getUserInfoService, GetAppEnrollmentInfoService getAppEnrollmentInfoService, GetUserEnrollmentInfoService getUserEnrollmentInfoService, GetDeviceEnrollmentInfoService getDeviceEnrollmentInfoService, GetUserExistService getUserExistService, GetDeviceExistService getDeviceExistService, GetUserAuthConfigService getUserAuthConfigService, GetAccountRealnameInfoService getAccountRealnameInfoService, GetAccountInfoService getAccountInfoService, CheckUserIdExistService checkUserIdExistService) {
        this.getUserInfoService = getUserInfoService;
        this.getAppEnrollmentInfoService = getAppEnrollmentInfoService;
        this.getUserEnrollmentInfoService = getUserEnrollmentInfoService;
        this.getDeviceEnrollmentInfoService = getDeviceEnrollmentInfoService;
        this.getUserExistService = getUserExistService;
        this.getDeviceExistService = getDeviceExistService;
        this.getUserAuthConfigService = getUserAuthConfigService;
        this.getAccountRealnameInfoService = getAccountRealnameInfoService;
        this.getAccountInfoService = getAccountInfoService;
        this.checkUserIdExistService = checkUserIdExistService;
    }

    /**
     * 获取用户信息
     * get https://server/api/get_user_info?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param userUuid    用户UUID
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [user_realname: user_realname]
     * [phone_no: phone_no]
     * [protect_methods: protect_methods]
     * [real_address: real_address]
     * [id_no: id_no]
     * [face_enrolled: face_enrolled]
     * }
     */
    @RequestMapping(value = "/api/get_user_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getUserInfo(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return getUserInfoService.getUserInfo(userUuid, verifyToken);
    }

    /**
     * 获得应用绑定信息
     * get https://server/api/get_app_enrollment_info?app_id=<app_id>
     *
     * @param appUuid 应用APP的UUID
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * [app_name: app_name,]
     * [app_logo: app_logo,]
     * [app_protect_methods: app_protect_methods,]
     * [obtain_realname_info_scope: realname_info_scope,]
     * [need_uniq_username: need_uniq_username,] // 3:不需要用户名；2:需要用户名但不必唯一；1:需要唯一用户名
     * [new_account_policy: new_account_policy,]
     * [need_info: need_info,] // 0: not need, 1: need
     * [use_cert: use_cert,] // 0: 不需要证书，1: 需要证书，2: 可选证书
     * [cert_type: cert_type,] // 11: SM2SM3; 12: SM2SM3 CoSign; 21: RSA2048SHA256; 22: RSA2048SHA256 CoSign
     * [cert_template: cert_template]  // { <key> : <value_type["int" | "string"]>, ... }
     * // eg: {"电话号码": "string", "用户名":"string", "账号名": "string", "device_id": "string"}
     * }
     */
    @RequestMapping(value = "/api/get_app_enrollment_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAppEnrollmentInfo(@RequestParam("app_id") String appUuid) {
        return getAppEnrollmentInfoService.getAppEnrollmentInfo(appUuid);
    }

    /**
     * 获得用户所有绑定的应用
     * get https://server/api/get_user_enrollment_info?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param userUuid    用户UUID
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * app:
     * [
     * {
     * app_id: app_id,
     * app_name: app_name,
     * app_logo: app_logo,
     * obtain_realname_info_scope: realname_info_scope,
     * app_protect_methods: app_protect_methods,
     * need_uniq_username: need_uniq_username, // 3:不需要用户名；2:需要用户名但不必唯一；1:需要唯一用户名
     * new_account_policy: new_account_policy,
     * auth_life_time: auth_life_time,
     * authorization_policy: authorization_policy,
     * account_state: account_state,
     * app_account_name: app_account_name,
     * app_account_id: app_account_id,
     * app_authorization_state: app_authorization_state,
     * enrolled_time: enrolled_time,
     * app_authorized_at: app_authorized_at,
     * authorization_token: authorization_token,
     * client_type: client_type,
     * otp_alg: otp_alg, // 0: no; 1: Google; 2: custom TOTP; 3: GM SM3
     * use_cert: use_cert, // 0 : not use cert, 1: use cert
     * cert_state: cert_state, // 0 : not ok, 1: ok
     * cert_type: cert_type
     * },
     * .
     * .
     * .
     * ]
     * }
     */
    @RequestMapping(value = "/api/get_user_enrollment_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAppEnrollmentInfo(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return getUserEnrollmentInfoService.getUserEnrollInfo(userUuid, verifyToken);
    }

    /**
     * 获得设备绑定的应用信息
     * get https://server/api/get_device_enrollment_info?user_id=<user_id>&device_id=<device_id>&app_id=<app_id>
     *
     * @param userUuid   用户UUID
     * @param deviceUuid 设备UUID
     * @param appUuid    应用UUID
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * app_name: app_name,
     * app_logo: app_logo,
     * obtain_realname_info_scope: realname_info_scope,
     * app_protect_methods: app_protect_methods,
     * need_uniq_username: need_uniq_username, // 3:不需要用户名；2:需要用户名但不必唯一；1:需要唯一用户名
     * new_account_policy: new_account_policy,
     * auth_life_time: auth_life_time,
     * authorization_policy: authorization_policy,
     * account_state: account_state,
     * app_account_name: app_account_name,
     * app_account_id: app_account_id,
     * app_authorization_state: app_authorization_state,
     * enrolled_time: enrolled_time,
     * app_authorized_at: app_authorized_at,
     * authorization_token: authorization_token,
     * client_type,
     * use_cert, // 0: not use cert | 1: use cert | 2: optional
     * cert_state // 0: no cert; 1/2: pending; 3: OK
     * }
     */
    @RequestMapping(value = "/api/get_device_enrollment_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getDeviceEnrollmentInfo(@RequestParam("user_id") String userUuid, @RequestParam("device_id") String deviceUuid, @RequestParam("app_id") String appUuid) {
        return getDeviceEnrollmentInfoService.getDeviceEnrollmentInfo(userUuid, deviceUuid, appUuid);
    }

    /**
     * 用户是否注册
     * get https://server/api/user_exist?phone_no=<phone_no>&app_id=<app_id>
     * *note: app_id is optional.*
     *
     * @param phoneNo 用户在系统中绑定的手机号
     * @param appUuid 应用方UUID
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [user_id: user_id]
     * [account_name: account_name]
     * [id_no: id_no]
     * [user_realname: user_realname]
     * }
     */
    @RequestMapping(value = "/api/user_exist", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getUserExist(@RequestParam("phone_no") String phoneNo,
                                 @RequestParam(value = "app_id", required = false, defaultValue = "") String appUuid) {
        return getUserExistService.getUserExist(phoneNo, appUuid);
    }

    /**
     * 设备是否注册 (APP)
     * get https://server/api/device_exist?imei=<imei>
     *
     * @param imei 手机识别码
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [device_id: device_id]
     * }
     */
    @RequestMapping(value = "/api/device_exist", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getDeviceExist(@RequestParam("imei") String imei) {
        return getDeviceExistService.getDeviceExist(imei);
    }

    /**
     * 获得账号实名信息（RP）
     * get https://server/api/get_account_realname_info?app_id=<app_id>&token=<token>account_id=<account_id>
     *
     * @param appUuid     应用UUID
     * @param token       验证令牌
     * @param accountUuid 账户UUID
     * @return {
     * error_code: errorcode,
     * error_message: error_message, // 以下信息根据应用授权范围提供
     * [user_realname: user_realname]
     * [phone_no: phone_no]
     * [id_no: id_no]
     * [id_expire_at: yyyy/mm/dd]
     * }
     */
    @RequestMapping(value = "/api/get_account_realname_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAccountRealNameInfo(@RequestParam("app_id") String appUuid, @RequestParam("token") String token, @RequestParam("account_id") String accountUuid) {
        return getAccountRealnameInfoService.getRealnameInfo(appUuid, token, accountUuid);
    }

    /**
     * 获得账号信息（RP）
     * get https://server/api/get_account_info?app_id=<app_id>&token=<token>&account_id=<account_id>&account_name=<account_name>
     * account_id 和 account_name 必选其一
     *
     * @param appUuid     应用UUID
     * @param token       验证令牌
     * @param accountUuid 账户UUID
     * @param accountName 账户名
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [account_id: account_id,]
     * [account_name: account_name,]
     * [authorized: authorized,] // 1: true, 0: false
     * [authorized_at: authorized_at]
     * }
     */
    @RequestMapping(value = "/api/get_account_info", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAccountInfo(@RequestParam("app_id") String appUuid,
                                   @RequestParam("token") String token,
                                   @RequestParam(value = "account_id", required = false, defaultValue = "") String accountUuid,
                                   @RequestParam(value = "account_name", required = false, defaultValue = "") String accountName) {
        return getAccountInfoService.getAccountInfo(appUuid, token, accountUuid, accountName);
    }

    /**
     * 根据用户ID检查用户是否注册（APP）
     * get https://server/api/userid_exist?user_id=<user_id>
     *
     * @param userUuid 用户UUID
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/userid_exist", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse checkUserIdExist(@RequestParam("user_id") String userUuid) {
        return checkUserIdExistService.isUserIdExist(userUuid);
    }

    /**
     * 获取用户登录设置
     * get https://server/api/get_user_auth_configure?user_id=<user_id>&device_id=<device_id>
     *
     * @param userUuid   用户UUID
     * @param deviceUuid 设备UUID
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [protect_methods: protect_methods,]
     * [face_enrolled: face_enrolled] // 0: not enrolled; 1: enrolled
     * }
     */
    @RequestMapping(value = "/api/get_user_auth_configure", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getUserAuthConfig(@RequestParam("user_id") String userUuid, @RequestParam("device_id") String deviceUuid) {
        return getUserAuthConfigService.getUserAuthConfig(userUuid, deviceUuid);
    }
}
