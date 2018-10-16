package com.xiaoleitech.authapi.phoneverify.service;

import com.xiaoleitech.authapi.auxiliary.authentication.ChallengeHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifySmsCodeServiceImpl implements VerifySmsCodeService {
    private final ChallengeHelper challengeHelper;
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public VerifySmsCodeServiceImpl(ChallengeHelper challengeHelper, SystemErrorResponse systemErrorResponse) {
        this.challengeHelper = challengeHelper;
        this.systemErrorResponse = systemErrorResponse;
    }

    @Override
    public AuthAPIResponse verifySmsCode(String phoneNo, String smsCode) {

        String cachedSmsCode = challengeHelper.getSmsCode(phoneNo);
        if (!cachedSmsCode.equals(smsCode))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_VERIFY_CODE);
        else
            return systemErrorResponse.getSuccessResponse();
    }
}
