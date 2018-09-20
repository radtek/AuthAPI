package com.xiaoleitech.authapi.helper.jointable;

import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.mapper.JoinAppUsersMapper;
import com.xiaoleitech.authapi.mapper.JoinRelyPartAccountMapper;
import com.xiaoleitech.authapi.model.pojo.AppUsers;
import com.xiaoleitech.authapi.model.pojo.EnrollDeviceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JoinRelyPartAccountHelper {
    private final JoinAppUsersMapper joinAppUsersMapper;
    private final JoinRelyPartAccountMapper joinRelyPartAccountMapper;

    @Autowired
    public JoinRelyPartAccountHelper(JoinAppUsersMapper joinAppUsersMapper, JoinRelyPartAccountMapper joinRelyPartAccountMapper) {
        this.joinAppUsersMapper = joinAppUsersMapper;
        this.joinRelyPartAccountMapper = joinRelyPartAccountMapper;
    }

    public List<AppUsers> getAppUserAccountList(String appUuid,
                                                String userUuid,
                                                String accountName) {

        return joinAppUsersMapper.selectAccount(appUuid, userUuid, accountName);
    }

    public AppUsers getAppUserAccount(String appUuid,
                                      String userUuid,
                                      String accountName) {
        List<AppUsers> appUsersList = getAppUserAccountList(appUuid, userUuid, accountName);
        return UtilsHelper.getFirstValid(appUsersList);
    }

    public List<AppUsers> getAppUserListWithSameName (String appUuid,
                                                      String userSelfUuid,
                                                      String accountName) {
        return joinAppUsersMapper.findOtherAppAccountWithSameName(appUuid, userSelfUuid, accountName);
    }

    public AppUsers getAppUserWithSameName (String appUuid,
                                            String userSelfUuid,
                                            String accountName) {
        List<AppUsers> appUsersList = getAppUserListWithSameName(appUuid, userSelfUuid, accountName);
        return UtilsHelper.getFirstValid(appUsersList);
    }

    public EnrollDeviceInfo getEnrollDeviceInfo(String userUuid, String deviceUuid, String appUuid) {
        List<EnrollDeviceInfo> enrollDeviceInfoList = joinRelyPartAccountMapper.selectEnrollDeviceInfo(userUuid, deviceUuid, appUuid);
        return UtilsHelper.getFirstValid(enrollDeviceInfoList);
    }
}
