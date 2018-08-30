package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.model.pojo.Users;
import org.springframework.stereotype.Component;

@Component
public class UsersHelper {

    static public boolean isUserVerifyToken(Users user, String verifyToken) {
        // TODO: 验证令牌方式待定，返回
//        return user.getVerify_token() == verifyToken;
        return true;
    }
}
