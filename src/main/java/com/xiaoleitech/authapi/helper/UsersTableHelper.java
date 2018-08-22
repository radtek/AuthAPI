package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.mapper.UsersMapper;
import com.xiaoleitech.authapi.model.pojo.Users;
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

    public Users getUserByPhoneNo(String phoneNo) {
        List<Users> usersList = usersMapper.selectUsersByPhoneNo(phoneNo);
        return (usersList.size() == 0) ? null : usersList.get(0);
    }

    public Users getUserByIdNo(String idNo) {
        List<Users> usersList = usersMapper.selectUsersByIdNo(idNo);
        return (usersList.size() == 0) ? null : usersList.get(0);
    }

    public Users getUserByUuid(String uuid) {
        List<Users> usersList = usersMapper.selectUsersByUuid(uuid);
        return (usersList.size() == 0) ? null : usersList.get(0);
    }
}
