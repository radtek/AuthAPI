package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.OtpAuthHistories;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OtpAuthHistoryMapper {
    @Select("SELECT * FROM otp_auth_histories h " +
            "WHERE h.rp_account_id=#{rp_account_id} " +
            "ORDER BY h.auth_at DESC " +
            "LIMIT 0, #{recent_count} ")
    List<OtpAuthHistories> selectRecentHistory(@Param("rp_account_id") int rpAccountId, @Param("recent_count") int count);

    @Insert("INSERT INTO otp_auth_histories (" +
            "rp_id, user_id, rp_account_id, " +
            "rp_account_name, otp, auth_at) " +
            "VALUES (" +
            "#{rp_id}, #{user_id}, #{rp_account_id}, " +
            "#{rp_account_name}, #{otp}, #{auth_at, jdbcType=TIMESTAMP})")
    int insertOneRecord(OtpAuthHistories otpAuthHistory);
}
