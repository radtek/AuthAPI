package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import com.xiaoleitech.authapi.service.enrollment.*;
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
     *                         app_id=<app_id>
     *                         user_id=<user_id>
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
     * [app_account_id: app_account_id,]
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
     * @param userId:      user_id 用户ID号
     * @param verifyToken: verify_token 验证令牌
     * @param appId:       app_id 应用ID号
     * @return {
     * error_code: errercode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/unenroll", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse unenrollApp(@RequestParam("user_id") String userId, @RequestParam("verify_token") String verifyToken, @RequestParam("app_id") String appId) {
        return unenrollAppService.unenrollApp(userId, verifyToken, appId);
    }

    /**
     * 激活用户 (RP)
     * get https://server/api/account_active?app_id=<app_id>&token=<token>&app_account_id=<app_account_id>
     * token: md5(app_id + app_name + skey + epoch / 60) // 1minute
     *
     * @param appId:        APP 的应用ID号
     * @param token:        令牌
     * @param appAccountId: APP用户账户的ID号
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * }
     */
    @RequestMapping(value = "/api/account_active", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse activeAccount(@RequestParam("app_id") String appId, @RequestParam("token") String token, @RequestParam("app_account_id") String appAccountId) {
        return activeAccountService.activeAccount(appId, token, appAccountId);
    }

    /**
     * 检查用户名是否唯一 (APP)
     * get https://server/api/check_account_name?app_id=<app_id>&app_account_name=<app_account_name>
     *
     * @param appId:          应用ID号 app_id
     * @param appAccountName: 应用账户名 app_account_name
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/check_account_name", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse checkAccountUnique(String appId, String appAccountName) {
        return checkAccountUniqueService.checkAccountUnique(appId, appAccountName);
    }

    /**
     * 检查用户是否激活 (APP)
     * get http://server/api/check_account_state?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param userId:      用户ID号 user_id
     * @param verifyToken: 验证令牌 verify_token
     * @param appId:       APP应用ID号 app_id
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * [account_state: account_state] // 1: ok, 2: to be activated
     * }
     */
    @RequestMapping(value = "/api/check_account_state", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse checkAccountState(
            @RequestParam("user_id") String userId,
            @RequestParam("verify_token") String verifyToken,
            @RequestParam("app_id") String appId) {
        return checkAccountStateService.checkAccountState(userId, verifyToken, appId);
    }

    /**
     * 用户修改账号登录项设置 (APP)
     * get http://server/api/set_account_protect_methods?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>&protect_methods=<protect_methods>
     *
     * @param userId:         用户ID号 user_id
     * @param verifyToken:    验证令牌 verify_token
     * @param appId:          APP 应用ID app_id
     * @param protectMethods: 保护方法 protect_methods
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/set_account_protect_methods", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse setAccountProtectMethods(@RequestParam("user_id") String userId,
                                             @RequestParam("verify_token") String verifyToken,
                                             @RequestParam("app_id") String appId,
                                             @RequestParam("protect_methods") String protectMethods) {
        return setAccountProtectMethodsService.setAccountProtectMethods(userId, verifyToken, appId, protectMethods);
    }


}
