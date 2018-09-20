package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.AppUsers;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JoinAppUsersMapper {
    @Select("SELECT " +
                "a.rp_id rp_id, " +
                "r.rp_uuid	rp_uuid, " +
                "r.rp_name rp_name, " +
                "u.user_id user_id, " +
                "u.user_uuid user_uuid, " +
                "u.real_name user_name, " +
                "d.device_id device_id, " +
                "d.device_uuid device_uuid, " +
                "a.rpaccount_id account_id, " +
                "a.rp_account_uuid account_uuid, " +
                "a.rp_account_name account_name, " +
                "a.state account_state " +
            "FROM rpaccounts a " +
            "LEFT JOIN rps r " +
                "ON a.rp_id=r.rp_id " +
            "LEFT JOIN  users u " +
                "ON a.user_id=u.user_id " +
            "LEFT JOIN devices d " +
                "ON d.device_id=u.device_id " +
            "WHERE " +
                "r.rp_uuid LIKE '%${rp_uuid}%' " +
                "AND u.user_uuid LIKE '%${user_uuid}%' " +
                "AND a.rp_account_name LIKE '%${account_name}%' " +
                "AND a.state>0  ")
    /**
     * 联合查询用户在指定 app （Rely-Part）下的账户(account)信息
     *  查询数据参见 AppUsers POJO类
     * @param appUuid app或rely-part的UUID。如果为空字符串，表示不限制 app_uuid (或rp_uuid)。
     * @param userUuid 用户的UUID。如果为空字符串，表示不限制 user_uuid。
     * @param accountName 账户名称。如果为空字符串，表示不限制 account_name。
     */
    List<AppUsers> selectAccount(
            @Param("rp_uuid") String appUuid,
            @Param("user_uuid") String userUuid,
            @Param("account_name") String accountName);

    @Select("SELECT " +
                "a.rp_id rp_id, " +
                "r.rp_uuid	rp_uuid, " +
                "r.rp_name rp_name, " +
                "u.user_id user_id, " +
                "u.user_uuid user_uuid, " +
                "u.real_name user_name, " +
                "d.device_id device_id, " +
                "d.device_uuid device_uuid, " +
                "a.rpaccount_id account_id, " +
                "a.rp_account_uuid account_uuid, " +
                "a.rp_account_name account_name, " +
                "a.state account_state " +
            "FROM rpaccounts a " +
            "LEFT JOIN rps r " +
                "ON a.rp_id=r.rp_id " +
            "LEFT JOIN  users u " +
                "ON a.user_id=u.user_id " +
            "LEFT JOIN devices d " +
                "ON d.device_id=u.device_id " +
            "WHERE " +
                "r.rp_uuid LIKE '%${rp_uuid}%' " +
                "AND u.user_uuid != #{user_uuid}" +
                "AND a.rp_account_name LIKE '%${account_name}%' " +
                "AND a.state>0 ")
    /**
     * @param appUuid 指定查询范围的APP UUID，如果为空，则全局查询
     * @param userSelfUuid 查询时要排除的用户UUID，表示自己的记录不用查，不能为空
     * @param accountName 要查询的账户名称，不为空
     */
    List<AppUsers> findOtherAppAccountWithSameName(
            @Param("rp_uuid") String appUuid,
            @Param("user_uuid") String userSelfUuid,
            @Param("account_name") String accountName);
}
