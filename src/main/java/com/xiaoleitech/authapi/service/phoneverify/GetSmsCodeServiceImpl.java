package com.xiaoleitech.authapi.service.phoneverify;

import com.xiaoleitech.authapi.helper.authenticate.ChallengeHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import com.xiaoleitech.authapi.helper.sms.SmsHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
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
        String smsCode = challengeHelper.generateSmsCode(phoneNo);
        if (smsCode.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        // 向指定手机号发送验证码短信
        ErrorCodeEnum errorCode = smsHelper.sendSmsVerifyCode(phoneNo, smsCode);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        return systemErrorResponse.getSuccessResponse();
    }
}
