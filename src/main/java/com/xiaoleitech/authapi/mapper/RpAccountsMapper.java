package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RpAccountsMapper {
    @Select("SELECT * FROM rpaccounts WHERE rp_id=#{rp_id} AND user_id=#{user_id}")
    List<RpAccounts> SelectRpAccountsByRpIdAndUserId(@Param("rp_id") int rpId, @Param("user_id") int userId);

    @Insert("INSERT INTO rpaccounts(" +
            "rpa_uuid, rp_id, " +
            "user_id, protect_methods, rp_account_name, " +
            "rp_account_uuid, state, authred, " +
            "authr_at, authorization_token, " +
            "otp_seed, cert_key, cert, " +
            "cert_state, created_at, updated_at, " +
            "sdk_auth_key, sdk_verify_token, auth_at) " +
            "VALUES (" +
            "#{rpa_uuid}, #{rp_id}, " +
            "#{user_id}, #{protect_methods}, #{rp_account_name}, " +
            "#{rp_account_uuid}, #{state}, #{authred}, " +
            "#{authr_at, jdbcType=TIMESTAMP }, #{authorization_token}, " +
            "#{otp_seed}, #{cert_key}, #{cert}, " +
            "#{cert_state}, #{created_at, jdbcType=TIMESTAMP}, #{updated_at, jdbcType=TIMESTAMP}, " +
            "#{sdk_auth_key}, #{sdk_verify_token}, #{auth_at, jdbcType=TIMESTAMP })")
    int insertOneRpAccount(RpAccounts rpAccount);

    @Update("UPDATE rpaccounts " +
            "SET " +
            "rp_id=#{rp_id}, user_id=#{user_id}, protect_methods=#{protect_methods}, " +
            "rp_account_name=#{rp_account_name}, rp_account_uuid=#{rp_account_uuid}, " +
            "state=#{state}, authred=#{authred}, " +
            "authr_at=#{authr_at, jdbcType=TIMESTAMP}, authorization_token=#{authorization_token}, " +
            "otp_seed=#{otp_seed}, cert_key=#{cert_key}, " +
            "cert=#{cert}, cert_state=#{cert_state}, " +
            "created_at=#{created_at, jdbcType=TIMESTAMP}, updated_at=#{updated_at, jdbcType=TIMESTAMP}, " +
            "sdk_auth_key=#{sdk_auth_key}, sdk_verify_token=#{sdk_verify_token}, " +
            "auth_at=#{auth_at, jdbcType=TIMESTAMP} " +
            "WHERE rpa_uuid=#{rpa_uuid}")
    int updateRpAccountByRpaUuid(RpAccounts rpAccount);
}
