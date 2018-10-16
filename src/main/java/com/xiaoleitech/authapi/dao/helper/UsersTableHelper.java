package com.xiaoleitech.authapi.dao.helper;

import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.dao.mybatis.mapper.UsersMapper;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersTableHelper {
    private final UsersMapper usersMapper;

    @Autowired
    public UsersTableHelper(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    /** 检查系统中是否存在指定ID的用户，不管该用户处于何种状态
     *
     * @param userUuid 用户UUID
     * @return
     *      true: 找到该用户；false: 找不到该用户
     */
    public boolean isValidUserUuid(String userUuid) {
        Users user = getUserByUserUuid(userUuid);
        return user != null;
    }


    /**
     * 按指定电话号码查找用户，返回用户记录数据
     *
     * @param phoneNo: 用户绑定的电话号码
     * @return user: Users 对象
     */
    public Users getUserByPhoneNo(String phoneNo) {
        List<Users> usersList = usersMapper.selectUsersByPhoneNo(phoneNo);
        return UtilsHelper.getFirstValid(usersList);
    }

    /**
     * 按指定身份证号码查找用户，并返回用户记录数据
     *
     * @param idNo: 用户注册时绑定的身份证号码
     * @return user: Users 对象
     */
    public Users getUserByIdNo(String idNo) {
        List<Users> usersList = usersMapper.selectUsersByIdNo(idNo);
        return UtilsHelper.getFirstValid(usersList);
    }

    /**
     * 按系统中用户的UUID查找用户，并返回用户记录数据
     *
     * @param uuid: 系统中用户记录创建时分配的唯一UUID
     * @return user: Users 对象
     */
    public Users getUserByUserUuid(String uuid) {
        List<Users> usersList = usersMapper.selectUsersByUserUuid(uuid);
        return UtilsHelper.getFirstValid(usersList);
    }

//    /**
//     * 按用户关联设备的ID查找用户，并返回用户记录数据
//     *
//     * @param deviceUuid: 用户关联或绑定的设备 UUID
//     * @return user: Users 对象
//     */
//    public Users getUserByDeviceUuid(String deviceUuid) {
//        List<Users> usersList = usersMapper.selectUsersByDeviceUuid(deviceUuid);
//        return (usersList.size() == 0) ? null : usersList.get(0);
//    }

    /** 获取指定user_id的用户记录
     *
     * @param userId: 用户id (integer)
     * @return Users记录
     */
    public  Users getUserByUserId(int userId) {
        List<Users> usersList = usersMapper.selectUsersByUserId(userId);
        return UtilsHelper.getFirstValid(usersList);
    }

    /**
     * 使用传入的user对象数据，更新该用户记录
     *
     * @param user: Users对象
     * @return errorCode: ErrorCodeEnum
     * 更新成功返回 ErrorCodeEnum.ERROR_OK，否则返回 ErrorCodeEnum.ERROR_INTERNAL_ERROR
     */
    public ErrorCodeEnum updateOneUserRecord(Users user) {
        // 设置用户记录的更新时间
        user.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());

        // 更新整条用户记录，成功执行，则返回1（更新记录的数量）
        int num = usersMapper.updateOneUserByUserUuid(user);

        // 如果更新没有执行成功，则统一设置为 ERROR_INTERNAL_ERROR 错误码，后续再根据实际应用需要做错误码细化
        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_INTERNAL_ERROR;
    }
}
