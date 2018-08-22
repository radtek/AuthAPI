package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.bean.RegisterUserRequest;
import org.springframework.validation.BindingResult;

public interface RegisterUserService {
    /**
     * 用户注册 (APP)
     * post https://server/api/register_user
     *
     * @param registerUserRequest form data:
     *                            phone_no=<phone_no>
     *                            password=<password>
     *                            device_id=<device_id>
     *                            [user_realname=<user_realname>]
     *                            [id_no=<id_no>]
     *                            [real_address=<real_address>]
     *                            [protect_methods=<protect_methods>]
     *                            [user_certificate_public_key=<user_certificate_public_key>]
     * @param bindingResult       数据绑定结果
     * @return {
     * error_code: errercode,
     * error_message: error_message,
     * password_salt: password_salt,
     * auth_key: auth_key,
     * user_id: user_id,
     * user_state: user_state
     * }
     * @implNote verify user's phone_no before register_user
     */
    AuthAPIResponse registerUser(RegisterUserRequest registerUserRequest, BindingResult bindingResult);

    /**
     * 用户反注册 (APP)
     * get https://server/api/deregister_user?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param userUuid    系统中用户的UUID
     * @param verifyToken 验证令牌
     * @return {
     * error_code: errercode,
     * error_message: error_message
     * }
     */
    AuthAPIResponse unregisterUser(String userUuid, String verifyToken);
}
