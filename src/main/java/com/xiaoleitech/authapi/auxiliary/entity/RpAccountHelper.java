package com.xiaoleitech.authapi.auxiliary.entity;

import com.xiaoleitech.authapi.global.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import org.springframework.stereotype.Component;

@Component
public class RpAccountHelper {
    public boolean checkAuthState(RpAccounts rpAccount, String authToken) {
        if (rpAccount.getAuthred() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState())
            return false;
        return rpAccount.getAuthorization_token().equals(authToken);
    }
}
