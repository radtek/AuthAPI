package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface CheckAccountUniqueService {
    /**
     * 检查用户名是否唯一 (APP)
     * get https://server/api/check_account_name?app_id=<app_id>&app_account_name=<app_account_name>
     *
     * @param appId:          应用ID号
     * @param appAccountName: 应用账户名
     * @return {
     * error_code: errorcode,
     * error_message: error_message
     * }
     */
    AuthAPIResponse checkAccountUnique(String appId, String appAccountName);
}
