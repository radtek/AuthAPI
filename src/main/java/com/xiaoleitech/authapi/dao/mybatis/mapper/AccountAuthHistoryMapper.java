package com.xiaoleitech.authapi.dao.mybatis.mapper;

import com.xiaoleitech.authapi.dao.pojo.AccountAuthHistories;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AccountAuthHistoryMapper {
    @Select("SELECT * FROM auth_histories h " +
            "WHERE h.user_id=#{user_id} AND h.rp_id=#{rp_id} ")
    List<AccountAuthHistories> selectAuthHistoryByUserIdAndRpId(@Param("user_id")int userId, @Param("rp_id") int relyPartId);

    @Insert("INSERT INTO auth_histories( " +
            "user_id, rp_id, protect_method, " +
            "auth_ip, auth_latitude, auth_longitude, " +
            "auth_at, created_at, updated_at) " +
            "VALUES ( " +
            "#{user_id}, #{rp_id}, #{protect_method}, " +
            "#{auth_ip}, #{auth_latitude}, #{auth_longitude}, " +
            "#{auth_at, jdbcType=TIMESTAMP}, #{created_at, jdbcType=TIMESTAMP}, #{updated_at, jdbcType=TIMESTAMP}) " )
    int insertOneHistory(AccountAuthHistories accountAuthHistorie);
}
