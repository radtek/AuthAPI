package com.xiaoleitech.authapi.phoneverify.service;

import com.xiaoleitech.authapi.auxiliary.authentication.ChallengeHelper;
import com.xiaoleitech.authapi.global.cache.redis.RedisService;
import com.xiaoleitech.authapi.global.phone.SmsHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetSmsCodeServiceImpl implements GetSmsCodeService {
    private final ChallengeHelper challengeHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final RedisService redisService;
    private final SmsHelper smsHelper;

    @Autowired
    public GetSmsCodeServiceImpl(ChallengeHelper challengeHelper, SystemErrorResponse systemErrorResponse, RedisService redisService, SmsHelper smsHelper) {
        this.challengeHelper = challengeHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.redisService = redisService;
        this.smsHelper = smsHelper;
    }

    @Override
    public AuthAPIResponse getSmsCode(String phoneNo) {
        // 产生SMS短信，并在缓存中保存一段时间
        String smsCode = challengeHelper.generateSmsCode(phoneNo, "");
        if (smsCode.isEmpty())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        // 向指定手机号发送验证码短信
        ErrorCodeEnum errorCode = smsHelper.sendSmsVerifyCode(phoneNo, smsCode);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        return systemErrorResponse.success();
    }
}
