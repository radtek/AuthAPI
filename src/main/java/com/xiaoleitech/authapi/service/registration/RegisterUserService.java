package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.registration.RegisterUserRequest;
import com.xiaoleitech.authapi.model.registration.UpdateUserRequest;
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
     * error_code: errorCode,
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
     * error_code: errorCode,
     * error_message: error_message
     * }
     */
    AuthAPIResponse unregisterUser(String userUuid, String verifyToken);

    /** 用户更新 （APP）
     * post https://server/api/update_user?user_id=<user_id>&verify_token=<verify_token>
     *
     * @param updateUserRequest
     *      request parameters: string user_id, string verify_token
     * 		plus: form data:
     * 			[password=<password>]
     * 			[user_realname=<user_realname>]
     * 			[id_no=<id_no>]
     * 			[id_expire_at=<yyyy/mm/dd>]
     * 			[protect_methods=<protect_methods>]
     * 			[face_enrolled=<face_enrolled>] // 0: not enrolled; 1: enrolled
     * @param bindingResult  数据绑定结果
     * @return
     * 			{
     * 				error_code: errercode,
     * 				error_message: error_message
     * 			}
     */
    AuthAPIResponse updateUser(UpdateUserRequest updateUserRequest, BindingResult bindingResult);

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
     * [user_id: user_id,]
     * [password_salt: password_salt,]
     * [auth_key: auth_key,]
     * [protect_methods: protect_methods]
     * }
     */
    AuthAPIResponse recoverUser(String deviceUuid, String password, String phoneNumber);

    /**
     * 获得用户信息（客户SDK需求，实际是获取用户认证信息）
     * get https://server/api/get_sdk_auth_key?phone_no=<phone_no>&password=<password>&app_id=<app_id>
     *
     * @param appId:       APP ID
     * @param password:    用户密码
     * @param phoneNumber: 用户绑定的手机号码
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * [user_id: user_id,]
     * [protect_methods: protect_methods,]
     * [device_id: device_id,]
     * [password_salt: password_salt,]
     * [sdk_auth_key: sdk_auth_key,]
     * }
     */
    AuthAPIResponse getAuthKey(int appId, String password, String phoneNumber);
}
