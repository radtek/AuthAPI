package com.xiaoleitech.authapi.controller;


import com.xiaoleitech.authapi.model.authorization.UserAuthorizeRequest;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.authorization.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class XL_AuthorizationAPI {
    private final UserAuthorizeService userAuthorizeService;
    private final UserRevokeAuthorizeService userRevokeAuthorizeService;
    private final UserDenyAuthorizeService userDenyAuthorizeService;
    private final AppLogoutAccountService appLogoutAccountService;
    private final VerifyAuthStateService verifyAuthStateService;
    private final GetAuthorizeStateService getAuthorizeStateService;
    private final ForwardAuthorizeService forwardAuthorizeService;
    private final GetOtpCodeService getOtpCodeService;
    private final OtpAuthorizeService otpAuthorizeService;

    @Autowired
    public XL_AuthorizationAPI(UserAuthorizeService userAuthorizeService,
                               UserRevokeAuthorizeService userRevokeAuthorizeService,
                               UserDenyAuthorizeService userDenyAuthorizeService,
                               AppLogoutAccountService appLogoutAccountService,
                               VerifyAuthStateService verifyAuthStateService,
                               GetAuthorizeStateService getAuthorizeStateService,
                               ForwardAuthorizeService forwardAuthorizeService,
                               GetOtpCodeService getOtpCodeService,
                               OtpAuthorizeService otpAuthorizeService) {
        this.userAuthorizeService = userAuthorizeService;
        this.userRevokeAuthorizeService = userRevokeAuthorizeService;
        this.userDenyAuthorizeService = userDenyAuthorizeService;
        this.appLogoutAccountService = appLogoutAccountService;
        this.verifyAuthStateService = verifyAuthStateService;
        this.getAuthorizeStateService = getAuthorizeStateService;
        this.forwardAuthorizeService = forwardAuthorizeService;
        this.getOtpCodeService = getOtpCodeService;
        this.otpAuthorizeService = otpAuthorizeService;
    }

    /** 用户授权应用 (APP)
     * post https://server/api/authorize
     * 		form data:
     * 			app_id=<app_id>  // UUID
     * 			user_id=<user_id>  // UUID
     * 			verify_token=<verify_token>
     * 			nonce=<nonce>
     * 			client_type=<client_type> // 1 iOS APP; 2 android APP
     * 			response=<reponse>  // response = AES256.ECB(key:verify_token, data:P5Padding(nonce))
     * 								// or response = sm2sign(verify_token)
     *
     * @param userAuthorizeRequest  用户授权请求的表单数据
     * @param bindingResult  表单数据的绑定结果
     * @return
     * 			{
     * 				error_code: errercode,
     * 				error_message: error_message,
     * 				account_id: account_id,
     * 				[authorization_token: authorization_token]
     * 			}
     */
    @RequestMapping(value = "/api/authorize", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse authorize(@ModelAttribute UserAuthorizeRequest userAuthorizeRequest, BindingResult bindingResult) {
        return userAuthorizeService.authorize(userAuthorizeRequest, bindingResult);
    }

    /** 用户取消授权应用 (APP)
     * get https://server/api/unauthorize?app_id=<app_id>&user_id=<user_id>&verify_token=<verify_token>
     *
     * @param appUuid   应用UUID
     * @param userUuid   用户UUID
     * @param verifyToken  验证令牌
     * @return
     * 			{
     * 				error_code: errercode,
     * 				error_message: error_message
     * 			}
     */
    @RequestMapping(value = "/api/unauthorize", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse revokeAuthorize(@RequestParam("app_id") String appUuid,
                                    @RequestParam("user_id") String userUuid,
                                    @RequestParam("verify_token") String verifyToken) {
        return userRevokeAuthorizeService.revokeAuthorize(appUuid, userUuid, verifyToken);
    }

    /** 用户拒绝授权应用 (APP)
     * get https://server/api/deny_authorize?app_id=<app_id>&user_id=<user_id>&verify_token=<verify_token>
     *
     * @param appUuid   应用UUID
     * @param userUuid   用户UUID
     * @param verifyToken  验证令牌
     * @return
     * 			{
     * 				error_code: errercode,
     * 				error_message: error_message
     * 			}
     */
    @RequestMapping(value = "/api/deny_authorize", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse userDenyAuthorize(@RequestParam("app_id") String appUuid,
                                      @RequestParam("user_id") String userUuid,
                                      @RequestParam("verify_token") String verifyToken) {
        return userDenyAuthorizeService.denyAuthorize(appUuid, userUuid, verifyToken);
    }

    /** 应用取消授权应用 (RP)
     * get https://server/api/logout_account?app_id=<app_id>&token=<token>&app_account_id=<app_account_id>
     *
     * @param appUuid   应用UUID
     * @param token   验证令牌
     * @param appAccountUuid  应用账户UUID
     * @return
     * 			{
     * 				error_code: errorcode,
     * 				error_message: error_message
     * 			}
     */
    @RequestMapping(value = "/api/logout_account", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse logoutAccount(@RequestParam("app_id") String appUuid,
                                  @RequestParam("token") String token,
                                  @RequestParam("app_account_id") String appAccountUuid) {
            return appLogoutAccountService.logoutAccount(appUuid, token, appAccountUuid);
    }

    /** 验证用户登录状态 (RP)
     * get https://server/api/verify_auth_state?app_id=<app_id>&token=<token>&app_account_id=<app_account_id>&authorization_token=<authorization_token>
     *
     * @param appUuid   应用UUID
     * @param token   验证令牌
     * @param appAccountUuid  应用账户UUID
     * @param authorizeToken  授权令牌
     * @return
     * 			{
     * 				error_code: errercode,
     * 				error_message: error_message,
     * 				[auth_life_time: auth_life_time,]
     * 				[expire_at: expire_at] // 失效时间
     * 			}
     */
    @RequestMapping(value = "/api/verify_auth_state", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse verifyAuthorizeState(@RequestParam("app_id") String appUuid,
                                         @RequestParam("token") String token,
                                         @RequestParam("app_account_id") String appAccountUuid,
                                         @RequestParam("authorization_token") String authorizeToken) {
        return verifyAuthStateService.verifyAuthState(appUuid, token, appAccountUuid, authorizeToken);
    }

    /** 获得应用授权状态（APP）
     * get https://server/api/get_authorize_state?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param userUuid  用户UUID
     * @param verifyToken  验证令牌
     * @param appUuid  应用UUID
     * @return
     * 			{
     * 				error_code: errorcode,
     * 				error_message: error_message,
     * 				[auth_state: auth_state,] // 0: not; 1: authorized
     * 				[auth_at: auth_at,] // authorize time at
     * 				[auth_life_time: auth_life_time,]
     * 				[authorization_token: authorization_token]
     * 			}
     */
    @RequestMapping(value = "/api/get_authorize_state", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAuthorizeState(@RequestParam("user_id") String userUuid,
                                      @RequestParam("verify_token") String verifyToken,
                                      @RequestParam("app_id") String appUuid) {
        return getAuthorizeStateService.getAuthorizeState(userUuid, verifyToken, appUuid);
    }

    /** 请求推送认证（RPWebJS）
     * get https://server/api/web_auth_req?app_id=app_id&account_name=account_name&nonce=nonce
     *
     * @param appUuid  应用UUID
     * @param accountName 客户名
     * @param nonce 一次性数字码
     * @return
     * 			{
     * 				"0" | error_message // "0": 成功发送推送；others: error message
     * 			}
     */
    @RequestMapping(value = "/api/web_auth_req", method = RequestMethod.GET)
    public @ResponseBody
    String webAuthRequest(@RequestParam("app_id") String appUuid, @RequestParam("account_name")String accountName, @RequestParam("nonce")String nonce) {
        return forwardAuthorizeService.forwardAuthorize(appUuid, accountName, nonce);
    }

    /** 获取OTP码（RPWeb）
     * get https://server/api/get_otp_code?app_id=app_id&account_name=account_name&nonce=nonce
     *
     * @param appUuid  应用UUID
     * @param accountName 客户名
     * @param nonce nonce
     * @return
     * 			{
     * 				"0XXXXXX" | error_message
     * 			}
     */
    @RequestMapping(value = "/api/get_otp_code", method = RequestMethod.GET)
    public @ResponseBody
    String getOtpCode(@RequestParam("app_id") String appUuid,
                               @RequestParam("account_name") String accountName,
                               @RequestParam("nonce") String nonce) {
        return Integer.toString(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getCode());
//        return getOtpCodeService.getOtpCode(appUuid, accountName, nonce);
    }

     /** OTP码授权（RP）
     * get https://server/api/otp_auth?app_id=<app_id>&token=<token>&account_id=<account_id>&otp=<otp>
     *     // otp码对应的nonce由4.5.7设置，并在1分钟内有效。
     *
     * @param appUuid 应用UUID
     * @param token 令牌
     * @param accountUuid 应用账户UUID
     * @param otp  动态口令
     * @return
     * 			{
     * 				error_code: errorcode,
     * 				error_message: error_message
     * 			}
     */
     @RequestMapping(value = "/api/otp_auth", method = RequestMethod.GET)
     public @ResponseBody
     AuthAPIResponse otpAuthorize(@RequestParam("app_id") String appUuid,
                                  @RequestParam("token") String token,
                                  @RequestParam("account_id") String accountUuid,
                                  @RequestParam("otp") String otp) {
         return otpAuthorizeService.otpAuthorize(appUuid, token, accountUuid, otp);
     }


}
