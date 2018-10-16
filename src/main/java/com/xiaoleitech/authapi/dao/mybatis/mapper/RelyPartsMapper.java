package com.xiaoleitech.authapi.dao.mybatis.mapper;

import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RelyPartsMapper {
    @Select("SELECT * FROM rps r WHERE r.id=#{rp_id} AND r.state>0")
    List<RelyParts> selectRelyPartsByRpId(@Param("rp_id") int rpId);

    @Select("SELECT * FROM rps r WHERE r.rp_uuid=#{rp_uuid} AND r.state>0")
    List<RelyParts> selectRelyPartsByRpUuid(@Param("rp_uuid") String rpUuid);

    @Select("SELECT * FROM rps r WHERE r.rp_name=#{rp_name} AND r.state>0")
    List<RelyParts> selectRelyPartsByRpName(@Param("rp_name") String appName);

    @Insert("INSERT INTO rps( " +
            "manager_id, rp_uuid, rp_name, rp_logo_file_url, " +
            "rp_login_redirection_url, rp_account_authorized_callback_url, " +
            "rp_account_unauthorized_callback_url, rp_account_enroll_callback_url, rp_account_unenroll_callback_url, " +
            "state, expire_at, client_type, real_name_scope, " +
            "rp_protect_methods, new_account_policy, uniq_account_name, " +
            "authorization_policy, auth_life_time, use_cert, cert_type, " +
            "caid, app_key, otp_alg, otp_digits, " +
            "inteval, strong, otp_c, otp_q, " +
            "need_active_code, created_at, updated_at, otp_key, " +
            "need_info, rp_account_exist_callback_url) " +
            "VALUES ( " +
            "#{manager_id}, #{rp_uuid}, #{rp_name}, #{rp_logo_file_url}, " +
            "#{rp_login_redirection_url}, #{rp_account_authorized_callback_url}, " +
            "#{rp_account_unauthorized_callback_url}, #{rp_account_enroll_callback_url}, #{rp_account_unenroll_callback_url}, " +
            "#{state}, #{expire_at, jdbcType=TIMESTAMP}, #{client_type}, #{real_name_scope}, " +
            "#{rp_protect_methods}, #{new_account_policy}, #{uniq_account_name}, " +
            "#{authorization_policy}, #{auth_life_time}, #{use_cert}, #{cert_type}, " +
            "#{caid}, #{app_key}, #{otp_alg}, #{otp_digits}, " +
            "#{inteval}, #{strong}, #{otp_c}, #{otp_q}, " +
            "#{need_active_code}, #{created_at, jdbcType=TIMESTAMP}, #{updated_at, jdbcType=TIMESTAMP}, #{otp_key}, " +
            "#{need_info}, #{rp_account_exist_callback_url}) ")
    int insertOneRelyPart(RelyParts relyPart);

    @Update("UPDATE rps r SET " +
            "manager_id=#{manager_id}, rp_uuid=#{rp_uuid}, rp_name=#{rp_name}, rp_logo_file_url=#{rp_logo_file_url}, " +
            "rp_login_redirection_url=#{rp_login_redirection_url}, rp_account_authorized_callback_url=#{rp_account_authorized_callback_url}, " +
            "rp_account_unauthorized_callback_url=#{rp_account_unauthorized_callback_url}, rp_account_enroll_callback_url=#{rp_account_enroll_callback_url}, " +
            "rp_account_unenroll_callback_url=#{rp_account_unenroll_callback_url}, state=#{state}, " +
            "expire_at=#{expire_at, jdbcType=TIMESTAMP}, client_type=#{client_type}, real_name_scope=#{real_name_scope}, " +
            "rp_protect_methods=#{rp_protect_methods}, new_account_policy=#{new_account_policy}, uniq_account_name=#{uniq_account_name}, " +
            "authorization_policy=#{authorization_policy}, auth_life_time=#{auth_life_time}, use_cert=#{use_cert}, cert_type=#{cert_type}, " +
            "caid=#{caid}, app_key=#{app_key}, otp_alg=#{otp_alg}, otp_digits=#{otp_digits}, " +
            "inteval=#{inteval}, strong=#{strong}, otp_c=#{otp_c}, otp_q=#{otp_q}, " +
            "need_active_code=#{need_active_code}, created_at=#{created_at, jdbcType=TIMESTAMP}, updated_at=#{updated_at, jdbcType=TIMESTAMP}, " +
            "need_info=#{need_info}, rp_account_exist_callback_url=#{rp_account_exist_callback_url} " +
            "WHERE " +
            "r.rp_uuid=#{rp_uuid}  AND r.state>0  ")
    int  updateOneRecordByUuid(RelyParts relyPart);


}
