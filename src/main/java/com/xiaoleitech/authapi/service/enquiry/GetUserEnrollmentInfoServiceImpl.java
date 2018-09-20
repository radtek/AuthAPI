package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.authenticate.ChallengeHelper;
import com.xiaoleitech.authapi.mapper.JoinRelyPartAccountMapper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enquiry.UserEnrollmentInfoResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.EnrollUserInfo;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 检查令牌是否有效
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_AUTHENTICATED);

        // 获取登记APP的记录（可能是多条）
        List<EnrollUserInfo> enrollUserInfoList = joinRelyPartAccountMapper.selectEnrollUserInfo(userUuid);
        if ((enrollUserInfoList == null) || (enrollUserInfoList.size() == 0))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        userEnrollmentInfoResponse.setApp(enrollUserInfoList);
        systemErrorResponse.fillErrorResponse(userEnrollmentInfoResponse, ErrorCodeEnum.ERROR_OK);
        return userEnrollmentInfoResponse;
    }
}
