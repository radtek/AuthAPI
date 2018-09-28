package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.EnrollDeviceInfo;
import com.xiaoleitech.authapi.model.pojo.EnrollUserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JoinRelyPartAccountMapper {
    @Select("SELECT " +
                "r.rp_uuid	app_id, " +
                "r.rp_name app_name, " +
                "r.rp_logo_file_url app_logo, " +
                "r.real_name_scope obtain_realname_info_scope, " +
                "r.rp_protect_methods app_protect_methods, " +
                "r.uniq_account_name need_uniq_username, " +
                "r.new_account_policy new_account_policy, " +
                "r.auth_life_time auth_life_time, " +
                "r.authorization_policy authorization_policy, " +
                "a.state account_state, " +
                "a.rp_account_name app_account_name, " +
                "a.rp_account_uuid app_account_id, " +
                "a.authred app_authorization_state, " +
                "a.created_at enrolled_time, " +
                "a.authr_at app_authorized_at, " +
                "a.authorization_token authorization_token, " +
                "r.client_type client_type, " +
                "r.otp_alg otp_alg, " +
                "r.use_cert use_cert, " +
                "a.cert_state cert_state, " +
                "r.cert_type cert_type " +
            "FROM rpaccounts a " +
            "LEFT JOIN rps r " +
                "ON a.rp_id=r.id " +
            "LEFT JOIN  users u " +
                "ON a.user_id=u.id " +
            "WHERE " +
                "u.user_uuid=#{user_uuid} " +
                "AND a.state>0 " +
                "AND r.state>0 " +
                "AND u.user_state>0  ")
    List<EnrollUserInfo> selectEnrollUserInfo(@Param("user_uuid") String userUuid);

    @Select("SELECT " +
                "r.rp_name app_name, " +
                "r.rp_logo_file_url app_logo, " +
                "r.real_name_scope obtain_realname_info_scope, " +
                "r.rp_protect_methods app_protect_methods, " +
                "r.uniq_account_name need_uniq_username, " +
                "r.new_account_policy new_account_policy, " +
                "r.auth_life_time auth_life_time, " +
                "r.authorization_policy authorization_policy, " +
                "a.state account_state, " +
                "a.rp_account_name app_account_name, " +
                "a.rp_account_uuid app_account_id, " +
                "a.authred app_authorization_state, " +
                "a.created_at enrolled_time, " +
                "a.authr_at app_authorized_at, " +
                "a.authorization_token authorization_token, " +
                "r.client_type client_type, " +
                "r.use_cert use_cert, " +
                "a.cert_state cert_state " +
            "FROM rpaccounts a " +
            "LEFT JOIN rps r " +
                "ON a.rp_id=r.id " +
            "LEFT JOIN  users u " +
                "ON a.user_id=u.id " +
            "LEFT JOIN  devices d " +
                "ON d.id=u.device_id " +
            "WHERE " +
                "u.user_uuid=#{user_uuid} " +
                "AND d.device_uuid=#{device_uuid} " +
                "AND r.rp_uuid=#{rp_uuid} " +
                "AND a.state>0 " +
                "AND r.state>0 " +
                "AND u.user_state>0  ")
    List<EnrollDeviceInfo> selectEnrollDeviceInfo(@Param("user_uuid") String userUuid,
                                                  @Param("device_uuid") String deviceUuid,
                                                  @Param("rp_uuid") String appUuid);
}
