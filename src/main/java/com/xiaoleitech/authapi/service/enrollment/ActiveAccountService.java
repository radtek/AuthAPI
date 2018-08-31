package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface ActiveAccountService {
    /**
     * 激活用户 (RP)
     * get https://server/api/account_active?app_id=<app_id>&token=<token>&app_account_id=<app_account_id>
     * Remark token: md5(app_id + app_name + skey + epoch / 60) // 1minute
     *
     * @param appId:        APP 的应用ID号
     * @param token:        令牌
     * @param appAccountId: APP用户账户的ID号
     * @return {
     * error_code: errorcode,
     * error_message: error_message,
     * }
     */
    AuthAPIResponse activeAccount(int appId, String token, int appAccountId);
}
