package com.xiaoleitech.authapi.auxiliary.entity;

import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.dao.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersHelper {
    private final UsersTableHelper usersTableHelper;

    @Autowired
    public UsersHelper(UsersTableHelper usersTableHelper) {
        this.usersTableHelper = usersTableHelper;
    }

    /**
     * 检查指定用户的令牌是否有效
     *
     * @param user        users表中用户的记录
     * @param verifyToken 待验证令牌
     * @return true -- 令牌验证成功
     * false -- 令牌验证失败
     * @Deprecated
     */
    static public boolean isUserVerifyToken(Users user, String verifyToken) {
        // TODO: 验证令牌方式待定，返回
//        return user.getVerify_token() == verifyToken;
        return true;
    }


}
