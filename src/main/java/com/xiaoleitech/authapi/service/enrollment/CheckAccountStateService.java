package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface CheckAccountStateService {
    /**
     * 检查用户是否激活 (APP)
     * get http://server/api/check_account_state?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param userId:      用户ID号
     * @param verifyToken: 验证令牌
     * @param appId:       APP应用ID号
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * [account_state: account_state] // 1: ok, 2: to be activated
     * }
     */
    AuthAPIResponse checkAccountState(int userId, String verifyToken, int appId);
}
