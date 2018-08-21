package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
}
