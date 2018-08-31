package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface UnenrollAppService {

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
    AuthAPIResponse unenrollApp(int userId, String verifyToken, int appId);
}
