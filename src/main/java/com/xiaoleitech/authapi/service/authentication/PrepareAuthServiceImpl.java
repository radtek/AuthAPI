package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.helper.authenticate.ChallengeHelper;
import com.xiaoleitech.authapi.model.authentication.PrepareAuthResponse;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrepareAuthServiceImpl implements PrepareAuthService {
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;
    private final ChallengeHelper challengeHelper;
    private final PrepareAuthResponse prepareAuthResponse;

    @Autowired
    public PrepareAuthServiceImpl(SystemErrorResponse systemErrorResponse, UsersTableHelper usersTableHelper, ChallengeHelper challengeHelper, PrepareAuthResponse prepareAuthResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
        this.challengeHelper = challengeHelper;
        this.prepareAuthResponse = prepareAuthResponse;
    }

    @Override
    public AuthAPIResponse prepareAuthenticate(String userUuid) {
        // 检查系统中是否存在指定ID的用户
         if ( !usersTableHelper.isValidUserUuid(userUuid) )
             return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_USER);

         // 产生一个随机挑战码，并将挑战码保存到缓存中，有效时间60秒(challengeHelper内部定时间)
        String challenge = challengeHelper.generateUserChallenge(userUuid);

        // 可能存在挑战码缓存的问题
        if (challenge.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        systemErrorResponse.fillErrorResponse(prepareAuthResponse, ErrorCodeEnum.ERROR_OK);
        prepareAuthResponse.setChallenge(challenge);
        return prepareAuthResponse;
    }
}
