package com.xiaoleitech.authapi.enrollment.service;

import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.dao.jointable.JoinRelyPartAccountHelper;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.AppUsers;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnrollCommon {
    private final UsersTableHelper usersTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final JoinRelyPartAccountHelper joinRelyPartAccountHelper;

    @Autowired
    public EnrollCommon(UsersTableHelper usersTableHelper,
                        RelyPartsTableHelper relyPartsTableHelper,
                        JoinRelyPartAccountHelper joinRelyPartAccountHelper) {
        this.usersTableHelper = usersTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.joinRelyPartAccountHelper = joinRelyPartAccountHelper;
    }

    public ErrorCodeEnum checkAppAndUser(String appUuid, String userUuid) {
        // 如果 appUuid 不为空串，检查 rely-part 表，是否存在此记录
        if (!appUuid.isEmpty()) {
            RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
            if (relyPart == null)
                return ErrorCodeEnum.ERROR_INVALID_APP;
        }

        // 如果 userUuid 不为空串，检查 users 表，是否存在此记录
        if (! userUuid.isEmpty()) {
            Users user = usersTableHelper.getUserByUserUuid(userUuid);
            if (user == null)
                return ErrorCodeEnum.ERROR_USER_NOT_FOUND;
        }

        return ErrorCodeEnum.ERROR_OK;
    }

    public boolean isExistAccountName_old(String appUuid, String userUuid, String accountName) {
        AppUsers appUser = joinRelyPartAccountHelper.getAppUserWithSameName(appUuid, userUuid, accountName);
        return (appUser != null);
    }

    public boolean isExistAccountName(String appUuid, String accountName) {
        AppUsers appUser = joinRelyPartAccountHelper.getAppUserAccount(appUuid, "", accountName);
        return (appUser != null);
    }
}
