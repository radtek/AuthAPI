package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.ChallengeHelper;
import com.xiaoleitech.authapi.dao.mybatis.mapper.JoinRelyPartAccountMapper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.UserEnrollmentInfoResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.EnrollUserInfo;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetUserEnrollmentInfoServiceImpl implements GetUserEnrollmentInfoService {
    private final JoinRelyPartAccountMapper joinRelyPartAccountMapper;
    private final SystemErrorResponse systemErrorResponse;
    private final AuthenticationHelper authenticationHelper;
    private final ChallengeHelper challengeHelper;
    private final UserEnrollmentInfoResponse userEnrollmentInfoResponse;


    @Autowired
    public GetUserEnrollmentInfoServiceImpl(JoinRelyPartAccountMapper joinRelyPartAccountMapper, SystemErrorResponse systemErrorResponse, AuthenticationHelper authenticationHelper, ChallengeHelper challengeHelper, UserEnrollmentInfoResponse userEnrollmentInfoResponse) {
        this.joinRelyPartAccountMapper = joinRelyPartAccountMapper;
        this.systemErrorResponse = systemErrorResponse;
        this.authenticationHelper = authenticationHelper;
        this.challengeHelper = challengeHelper;
        this.userEnrollmentInfoResponse = userEnrollmentInfoResponse;
    }

    @Override
    public AuthAPIResponse getUserEnrollInfo(String userUuid, String verifyToken) {
        // 检查请求参数
        if (userUuid.isEmpty() || verifyToken.isEmpty())
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 检查令牌是否有效
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_AUTHENTICATED);

        // 获取登记APP的记录（可能是多条）
        List<EnrollUserInfo> enrollUserInfoList = joinRelyPartAccountMapper.selectEnrollUserInfo(userUuid);
        if ((enrollUserInfoList != null) && (enrollUserInfoList.size() > 0)) {
            userEnrollmentInfoResponse.setApp(enrollUserInfoList);
        } else {
            enrollUserInfoList = new ArrayList<>();
            userEnrollmentInfoResponse.setApp(enrollUserInfoList);
        }

        systemErrorResponse.fill(userEnrollmentInfoResponse, ErrorCodeEnum.ERROR_OK);
        return userEnrollmentInfoResponse;
    }
}
