package com.xiaoleitech.authapi.enrollment.controller;

import com.xiaoleitech.authapi.enrollment.service.*;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enrollment.bean.request.EnrollAppRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class XL_EnrollmentAPI {
    private final EnrollmentService enrollmentService;
    private final UnenrollAppService unenrollAppService;
    private final ActiveAccountService activeAccountService;
    private final CheckAccountUniqueService checkAccountUniqueService;
    private final CheckAccountStateService checkAccountStateService;
    private final SetAccountProtectMethodsService setAccountProtectMethodsService;

    @Autowired
    public XL_EnrollmentAPI(EnrollmentService enrollmentService, UnenrollAppService unenrollAppService, ActiveAccountService activeAccountService, CheckAccountUniqueService checkAccountUniqueService, CheckAccountStateService checkAccountStateService, SetAccountProtectMethodsService setAccountProtectMethodsService) {
        this.enrollmentService = enrollmentService;
        this.unenrollAppService = unenrollAppService;
        this.activeAccountService = activeAccountService;
        this.checkAccountUniqueService = checkAccountUniqueService;
        this.checkAccountStateService = checkAccountStateService;
        this.setAccountProtectMethodsService = setAccountProtectMethodsService;
    }

    /**
     * 用户绑定应用 (APP)
     * post https://server/api/enroll
     *
     * @param enrollAppRequest form data:
     *                         app_id=<app_id>  // UUID
     *                         user_id=<user_id> // UUID
     *                         verify_token=<verify_token>
     *                         [account_name=<account_name>]
     *                         [protect_methods=<auth_methods>]
     *                         [id_code=<id_code>]
     *                         [active_code=<active_code>]
     *                         [sex=<0|1>]
     *                         [email=<email>]
     * @param bindingResult    数据绑定的结果
     * @return {
     * error_code: errercode,
     * error_message: error_message,
     * [app_account_id: app_account_id,]   // UUID
     * [account_state: account_state,] // 1: OK, 2: need to be activated
     * [authorization_policy: authorization_policy,]
     * [otp_alg: otp_alg,] // 1: google; 2: totp; 3: sm3
     * [otp_inteval: otp_inteval,] // 30 or 60
     * [otp_seed: otp_seed,] // otp seed
     * [otp_digits: otp_digits] // 6 or 8
     * }
     */
    @RequestMapping(value = "/api/enroll", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse enrollApp(@ModelAttribute @Valid EnrollAppRequest enrollAppRequest, BindingResult bindingResult) {
        return enrollmentService.enrollApp(enrollAppRequest, bindingResult);
    }

    /**
     * 用户解绑应用 (APP)
     * get https://server/api/unenroll?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param userUuid:      user_id 系统定义的用户UUID号
     * @param verifyToken: verify_token 验证令牌
     * @param appUuid:       app_id 系统定义的应用UUID号
     * @return {
     * error_code: errercode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/unenroll", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse unenrollApp(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken, @RequestParam("app_id") String appUuid) {
        return unenrollAppService.unenrollApp(userUuid, verifyToken, appUuid);
    }

    /**
     * 激活用户 (RP)
     * get https://server/api/active_account?app_id=<app_id>&token=<token>&app_account_id=<app_account_id>
     * token: md5(app_id + app_name + skey + epoch / 60) // 1minute
     *
     * @param appUuid:        APP 的应用UUID号
     * @param token:          令牌
     * @param appAccountUuid: APP用户账户的UUID号
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * }
     */
    @RequestMapping(value = "/api/active_account", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse activeAccount(@RequestParam("app_id") String appUuid,
                                  @RequestParam("token") String token,
                                  @RequestParam("app_account_id") String appAccountUuid) {
        return activeAccountService.activeAccount(appUuid, token, appAccountUuid);
    }

    /**
     * 检查用户名是否唯一 (APP)
     * get https://server/api/check_account_name?app_id=<app_id>&app_account_name=<app_account_name>
     *
     * @param appUuid:          应用UUID号 rp_uuid
     * @param appAccountName: 应用账户名 app_account_name
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/check_account_name", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse checkAccountUnique(@RequestParam("app_id")String appUuid,
                                       @RequestParam("app_account_name")String appAccountName) {
        return checkAccountUniqueService.checkAccountUnique(appUuid, appAccountName);
    }

    /**
     * 检查用户是否激活 (APP)
     * get http://server/api/check_account_state?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param userUuid:      用户UUID号 user_id
     * @param verifyToken: 验证令牌 verify_token
     * @param appUuid:       APP应用UUID号 app_id
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * [account_state: account_state] // 1: ok, 2: to be activated
     * }
     */
    @RequestMapping(value = "/api/check_account_state", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse checkAccountState(
            @RequestParam("user_id") String userUuid,
            @RequestParam("verify_token") String verifyToken,
            @RequestParam("app_id") String appUuid) {
        return checkAccountStateService.checkAccountState(userUuid, verifyToken, appUuid);
    }

    /**
     * 用户修改账号登录项设置 (APP)
     * get http://server/api/set_account_protect_methods?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>&protect_methods=<protect_methods>
     *
     * @param userUuid:         用户UUID号 user_id
     * @param verifyToken:    验证令牌 verify_token
     * @param appUuid:          APP 应用UUID app_id
     * @param protectMethods: 保护方法 protect_methods
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/set_account_protect_methods", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse setAccountProtectMethods(@RequestParam("user_id") String userUuid,
                                             @RequestParam("verify_token") String verifyToken,
                                             @RequestParam("app_id") String appUuid,
                                             @RequestParam("protect_methods") String protectMethods) {
        return setAccountProtectMethodsService.setAccountProtectMethods(userUuid, verifyToken, appUuid, protectMethods);
    }


}
