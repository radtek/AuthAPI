package com.xiaoleitech.authapi.authentication.controller;

import com.xiaoleitech.authapi.authentication.service.*;
import com.xiaoleitech.authapi.authentication.bean.request.UserAuthRequest;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "认证接口控制器", tags = "4-Authentication")
public class XL_AuthenticationAPI {
    private final PrepareAuthService prepareAuthService;
    private final UserAuthenticateService userAuthenticateService;
    private final UserAuthStateService userAuthStateService;
    private final RefreshTokenService refreshTokenService;
    private final UserLogoutService userLogoutService;

    @Autowired
    public XL_AuthenticationAPI(PrepareAuthService prepareAuthService,
                                UserAuthenticateService userAuthenticateService,
                                UserAuthStateService userAuthStateService,
                                RefreshTokenService refreshTokenService, UserLogoutService userLogoutService) {
        this.prepareAuthService = prepareAuthService;
        this.userAuthenticateService = userAuthenticateService;
        this.userAuthStateService = userAuthStateService;
        this.refreshTokenService = refreshTokenService;
        this.userLogoutService = userLogoutService;
    }

    /**
     * 准备认证
     * get https://server/api/pre_auth?user_id=<user_id>
     *
     * @param userUuid 用户UUID
     * @return {
     * error_code: errercode,
     * error_message: error_message,
     * challenge: challenge // 1分钟内有效
     * }
     */
    @RequestMapping(value = "/api/pre_auth", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse prepareAuth(@RequestParam("user_id") String userUuid) {
        return prepareAuthService.prepareAuthenticate(userUuid);
    }

    /**
     * 用户身份认证 (APP)
     * post https://server/api/authenticate
     * form data:
     * [app_id=<app_id>] 应用的UUID ，可选
     * user_id=<user_id> 用户的UUID user_uuid
     * protect_method=<auth_method>
     * latitude=<latitude>
     * longitude=<longitude>
     * response=<response> 调用者返回的待验证数据，计算方式如下：
     * // hotp/push: 使用 auth_key 对 challenge 做 iHMAC 的结果
     * // password: 使用 challenge 对 password + password_salt 做 iHMAC 的结果
     * // iHMAC 定义如下：(具体用哪些数据根据参数和系统定义 protect_method 来定)
     * iHMAC = HASH(challenge + user.password + user.password_salt + user.auth_key)
     * or
     * iHMAC = HASH(challenge + user.password + user.password_salt + rp.sdk_auth_key)
     *
     * @param userAuthRequest POST请求的表单数据
     * @param bindingResult   POST表单数据绑定结果
     * @return {
     * error_code: errercode,
     * error_message: error_message,
     * [verify_token: verify_token,]
     * [expire_at: expire_at,]
     * [remain_retry_count: remain_retry_count] // if failed
     * }
     */
    @RequestMapping(value = "/api/authenticate", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse userAuthenticate(@ModelAttribute UserAuthRequest userAuthRequest, BindingResult bindingResult) {
        return userAuthenticateService.userAuthenticate(userAuthRequest, bindingResult);
    }

    /**
     * 验证用户身份认证状态 (APP)
     * get https://server/api/verify_user_auth_state?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param userUuid    用户UUID（UUID类型）
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [expire_at: expire_at]
     * }
     */

    @RequestMapping(value = "/api/verify_user_auth_state", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse userAuthState(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return userAuthStateService.getUserAuthState(userUuid, verifyToken);
    }

    /**
     * 用户登出 (APP)
     * get https://server/api/logout?user_id=user_id&verify_token=<verify_token>
     *
     * @param userUuid    用户UUID
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errercode,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/logout", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse userLogout(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return userLogoutService.logout(userUuid, verifyToken);
    }

    /**
     * 用户登录状态延续 (APP)
     * get https://server/api/refresh_token?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param userUuid    用户UUID
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errercode,
     * error_message: error_message,
     * [expire_at: expire_at,] // 失效时间
     * [verify_token: verify_token]
     * }
     */
    @RequestMapping(value = "/api/refresh_token", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse refreshToken(@RequestParam("user_id") String userUuid, @RequestParam("verify_token") String verifyToken) {
        return refreshTokenService.refreshToken(userUuid, verifyToken);
    }
}
