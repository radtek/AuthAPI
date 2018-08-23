package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface EnrollmentService {
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
    AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult);
}
