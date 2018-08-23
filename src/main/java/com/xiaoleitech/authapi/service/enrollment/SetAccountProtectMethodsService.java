package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface SetAccountProtectMethodsService {
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
    AuthAPIResponse setAccountProtectMethods(String userId, String verifyToken, String appId, String protectMethods);
}
