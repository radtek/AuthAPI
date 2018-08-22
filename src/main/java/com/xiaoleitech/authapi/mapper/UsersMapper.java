package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsersMapper {
    // 根据用户UUID查找用户记录
    @Select("SELECT * FROM users WHERE user_uuid=#{user_uuid}")
    List<Users> selectUsersByUuid(@Param("user_uuid") String uuid);

    // 根据电话号码查找用户记录
    @Select("SELECT * FROM users WHERE phone_no=#{phone_no}")
    List<Users> selectUsersByPhoneNo(@Param("phone_no") String phoneNo);

    // 根据身份证号查找用户记录
    @Select("SELECT * FROM users WHERE id_no=#{id_no}")
    List<Users> selectUsersByIdNo(@Param("id_no") String idNo);

    @Insert("INSERT INTO users( " +
            "device_id, user_uuid, real_name, phone_no, " +
            "sex, birthday, hukou_address, real_address, " +
            "id_no, id_expire_at, protect_methods, password, " +
            "password_salt, second_factor_attempt_fail_count, " +
            "password_attempt_fail_count, second_factor_lock_to, " +
            "password_lock_to, authenticated, protect_method, " +
            "auth_latitude, auth_longitude, auth_at, verify_token, " +
            "face_enrolled, user_state, created_at, updated_at) " +
            "VALUES ( " +
            "#{device_id}, #{user_uuid}, #{real_name}, #{phone_no}, " +
            "#{sex}, #{birthday, jdbcType=TIMESTAMP}, #{hukou_address}, #{real_address}, " +
            "#{id_no}, #{id_expire_at, jdbcType=TIMESTAMP}, #{protect_methods}, #{password}, " +
            "#{password_salt}, #{second_factor_attempt_fail_count}, " +
            "#{password_attempt_fail_count}, #{second_factor_lock_to, jdbcType=TIMESTAMP}, " +
            "#{password_lock_to, jdbcType=TIMESTAMP}, #{authenticated}, #{protect_method}, " +
            "#{auth_latitude}, #{auth_longitude}, #{auth_at, jdbcType=TIMESTAMP}, #{verify_token}, " +
            "#{face_enrolled}, #{user_state}, #{created_at, jdbcType=TIMESTAMP}, #{updated_at, jdbcType=TIMESTAMP}) ")
    int insertOneUser(Users user);

    @Update("UPDATE users SET( " +
            "device_id=#{device_id}, real_name=#{real_name}, phone_no=#{phone_no}, " +
            "sex=#{sex}, birthday=#{birthday, jdbcType=TIMESTAMP}, hukou_address=#{hukou_address}, " +
            "real_address=#{real_address}, id_no=#{id_no}, id_expire_at=#{id_expire_at, jdbcType=TIMESTAMP}, " +
            "protect_methods=#{protect_methods}, password=#{password}, password_salt=#{password_salt}, " +
            "second_factor_attempt_fail_count=#{second_factor_attempt_fail_count}, " +
            "password_attempt_fail_count=#{password_attempt_fail_count}, second_factor_lock_to=#{second_factor_lock_to, jdbcType=TIMESTAMP}, " +
            "password_lock_to=#{password_lock_to, jdbcType=TIMESTAMP}, authenticated=#{authenticated}, protect_method=#{protect_method}, " +
            "auth_latitude=#{auth_latitude}, auth_longitude=#{auth_longitude}, auth_at=#{auth_at, jdbcType=TIMESTAMP}, " +
            "verify_token=#{verify_token}, face_enrolled=#{face_enrolled}, user_state=#{user_state}, " +
            "created_at=#{created_at, jdbcType=TIMESTAMP}, updated_at=#{updated_at, jdbcType=TIMESTAMP}) " +
            "WHERE " +
            "user_uuid=#{user_uuid}")
    int updateOneUserByUserUuid(Users user);

    // TODO: 实际应用中，可能需要更新状态及少数几个字段，另外再开方法实现
    @Update("UPDATE users SET( " +
            "user_state=#{user_state}, updated_at=#{updated_at, jdbcType=TIMESTAMP}) " +
            "WHERE " +
            "user_uuid=#{user_uuid}")
    int updateUserStateByUserUuid(Users user);

}
