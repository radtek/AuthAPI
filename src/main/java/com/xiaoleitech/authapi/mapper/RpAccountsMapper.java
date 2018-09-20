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

    @Select("SELECT * FROM rpaccounts r WHERE r.user_id=#{user_id} AND r.state>0")
    List<RpAccounts> selectRpAccountsByUserId(@Param("user_id") int userId);

    @Select("SELECT * FROM rpaccounts r WHERE r.rp_id=#{rp_id} AND r.user_id=#{user_id} AND r.state>0")
    List<RpAccounts> selectRpAccountsByRpIdAndUserId(@Param("rp_id") int rpId, @Param("user_id") int userId);

    @Select("SELECT * FROM rpaccounts r WHERE r.rp_id=#{rp_id} AND r.user_id=#{user_id} ")
    List<RpAccounts> selectAllRpAccountsByRpIdAndUserId(@Param("rp_id") int rpId, @Param("user_id") int userId);

    @Select("SELECT * FROM rpaccounts r WHERE r.rp_id=#{rp_id} AND r.rp_account_name=#{rp_account_name} AND r.state>0")
    List<RpAccounts> selectRpAccountsByRpIdAndAccountName(@Param("rp_id") int rpId, @Param("rp_account_name") String accountName);

    @Select("SELECT * FROM rpaccounts r WHERE r.rp_account_uuid=#{rp_account_uuid}  AND r.state>0")
    List<RpAccounts> selectRpAccountsByRpAccountUuid(@Param("rp_account_uuid") String rpAccountUuid);

    @Select("SELECT * " +
            "FROM rpaccounts a " +
            "LEFT JOIN rps r " +
                "ON a.rp_id=r.rp_id " +
            "LEFT JOIN  users u " +
                "ON a.user_id=u.user_id " +
            "WHERE " +
                "r.rp_uuid=#{rp_uuid} " +
                "AND u.user_uuid=#{user_uuid} " +
                "AND a.state>0  ")
    List<RpAccounts> selectRpAccountsByRpUuidAndUserUuid(@Param("rp_uuid") String rpUuid, @Param("user_uuid") String userUuid);

    @Insert("INSERT INTO rpaccounts(" +
            "rp_id, " +
            "user_id, protect_methods, rp_account_name, " +
            "rp_account_uuid, state, authred, " +
            "authr_at, authorization_token, " +
            "otp_seed, cert_key, cert, " +
            "cert_state, created_at, updated_at, " +
            "sdk_auth_key, sdk_verify_token, auth_at) " +
            "VALUES (" +
            "#{rp_id}, " +
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
            "rp_account_name=#{rp_account_name}, " +
            "state=#{state}, authred=#{authred}, " +
            "authr_at=#{authr_at, jdbcType=TIMESTAMP}, authorization_token=#{authorization_token}, " +
            "otp_seed=#{otp_seed}, cert_key=#{cert_key}, " +
            "cert=#{cert}, cert_state=#{cert_state}, " +
            "created_at=#{created_at, jdbcType=TIMESTAMP}, updated_at=#{updated_at, jdbcType=TIMESTAMP}, " +
            "sdk_auth_key=#{sdk_auth_key}, sdk_verify_token=#{sdk_verify_token}, " +
            "auth_at=#{auth_at, jdbcType=TIMESTAMP} " +
            "WHERE rp_account_uuid=#{rp_account_uuid}")
    int updateRpAccountByRpaUuid(RpAccounts rpAccount);
}
