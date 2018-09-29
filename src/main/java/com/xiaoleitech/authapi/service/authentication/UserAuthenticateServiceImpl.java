package com.xiaoleitech.authapi.service.authentication;

import com.xiaoleitech.authapi.helper.*;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.authenticate.ChallengeHelper;
import com.xiaoleitech.authapi.helper.cipher.MyHmacAlgorithm;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.mapper.AccountAuthHistoryMapper;
import com.xiaoleitech.authapi.model.authentication.UserAuthFailResponse;
import com.xiaoleitech.authapi.model.authentication.UserAuthRequest;
import com.xiaoleitech.authapi.model.authentication.UserAuthSuccessResponse;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.ProtectMethodEnum;
import com.xiaoleitech.authapi.model.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.model.pojo.AccountAuthHistories;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserAuthenticateServiceImpl implements UserAuthenticateService{
    private Logger logger = LoggerFactory.getLogger(UserAuthenticateServiceImpl.class);

    private final UsersTableHelper usersTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final ChallengeHelper challengeHelper;
    private final UsersHelper usersHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final UserAuthSuccessResponse userAuthSuccessResponse;
    private final UserAuthFailResponse userAuthFailResponse;
    private final AccountAuthHistories accountAuthHistory;
    private final AccountAuthHistoryMapper accountAuthHistoryMapper;

    public UserAuthenticateServiceImpl(UsersTableHelper usersTableHelper,
                                       SystemErrorResponse systemErrorResponse,
                                       ChallengeHelper challengeHelper,
                                       UsersHelper usersHelper,
                                       AuthenticationHelper authenticationHelper,
                                       RpAccountsTableHelper rpAccountsTableHelper,
                                       UserAuthSuccessResponse userAuthSuccessResponse,
                                       UserAuthFailResponse userAuthFailResponse, AccountAuthHistories accountAuthHistory, AccountAuthHistoryMapper accountAuthHistoryMapper) {
        this.usersTableHelper = usersTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.challengeHelper = challengeHelper;
        this.usersHelper = usersHelper;
        this.authenticationHelper = authenticationHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.userAuthSuccessResponse = userAuthSuccessResponse;
        this.userAuthFailResponse = userAuthFailResponse;
        this.accountAuthHistory = accountAuthHistory;
        this.accountAuthHistoryMapper = accountAuthHistoryMapper;
    }

    public boolean isPassword(int protectMethod) {
        return ProtectMethodEnum.VERIFY_PASSWORD.isMatched(protectMethod);
    }

    @Override
    public AuthAPIResponse userAuthenticate(UserAuthRequest userAuthRequest, BindingResult bindingResult) {
//        logger.info("--> authenticate password is: " + userAuthRequest.get) ;
        // 获取指定UUID的 users 记录
         Users user = usersTableHelper.getUserByUserUuid(userAuthRequest.getUser_id());
         if (user == null)
             return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 获取缓存的挑战码（user_uuid作为键值）
        String challenge = challengeHelper.getChallenge(userAuthRequest.getUser_id());
        if ((challenge == null) || (challenge.isEmpty()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_CHALLENGE_EXPIRED);

        int protectMethod = userAuthRequest.getProtect_method();

        // 如果是密码校验方式，检查用户密码的状态（是否锁定，剩余次数）
        ErrorCodeEnum errorCode = authenticationHelper.getAuthticateRight(user, protectMethod);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // 如果有 app_id 参数，使用 Rely-Part 的 sdk_auth_key，否则使用 user 的 auth_key
        String authKey;
        RpAccounts rpAccount;
        if (userAuthRequest.getApp_id() != null) {
            rpAccount = rpAccountsTableHelper.getRpAccountByRpUuidAndUserUuid(
                    userAuthRequest.getApp_id(), userAuthRequest.getUser_id());
            if (rpAccount == null)
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);
            authKey = rpAccount.getSdk_auth_key();
        } else {
            authKey = user.getAuth_key();
        }

        // 计算iHMAC
        String iHmac = MyHmacAlgorithm.calculate(challenge, user.getPassword(), user.getPassword_salt(), authKey);

        // 用系统计算的iHmac验证用户请求参数中的response，需相等
        if (userAuthRequest.getResponse().equals(iHmac)) {
            // 记录验证时间
            user.setAuth_at(UtilsHelper.getCurrentSystemTimestamp());
            // 清除尝试次数
            if (isPassword(protectMethod))
                user.setPassword_attempt_fail_count(0);
            else
                user.setSecond_factor_attempt_fail_count(0);
            // 记录其他参数
            user.setAuthenticated(UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState());
            user.setProtect_method(protectMethod);
            user.setAuth_latitude(userAuthRequest.getLatitude());
            user.setAuth_longitude(userAuthRequest.getLongitude());
            user.setVerify_token(authenticationHelper.generateTimingToken());

            // 更新用户数据到数据库中
            errorCode = usersTableHelper.updateOneUserRecord(user);
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return systemErrorResponse.getGeneralResponse(errorCode);

            // 更新 auth_histories 表
            addAuthHistoryRecord(userAuthRequest, ErrorCodeEnum.ERROR_OK.getCode());

            // 成功时返回验证令牌和令牌失效时间
            userAuthSuccessResponse.setVerify_token(user.getVerify_token());
            userAuthSuccessResponse.setExpire_at(authenticationHelper.getTokenExpireTime(user.getVerify_token()));
            systemErrorResponse.fillErrorResponse(userAuthSuccessResponse, ErrorCodeEnum.ERROR_OK);
            return userAuthSuccessResponse;
        } else {
            // HMAC 比较不相等，则走验证失败流程
            int count;
            java.sql.Timestamp lockTime = authenticationHelper.getLockTime();
            // 尝试次数加1，达到最大次数时设置锁定时间
            if (isPassword(protectMethod)) {
                // 密码模式
                count = user.getPassword_attempt_fail_count() + 1;
                user.setPassword_attempt_fail_count(count);
                if (count >= authenticationHelper.getAuthRetryMax())
                    user.setPassword_lock_to(lockTime);
            }
            else {
                // 非密码模式
                count = user.getSecond_factor_attempt_fail_count() + 1;
                user.setSecond_factor_attempt_fail_count(count);
                if (count >= authenticationHelper.getAuthRetryMax())
                    user.setSecond_factor_lock_to(lockTime);
            }
            int remainRetryCount = authenticationHelper.getAuthRetryMax() - count;

            // 保存尝试次数
            errorCode = usersTableHelper.updateOneUserRecord(user);
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return systemErrorResponse.getGeneralResponse(errorCode);

            // 更新 auth_histories 表
            addAuthHistoryRecord(userAuthRequest, ErrorCodeEnum.ERROR_AUTH_FAILED.getCode());

            // 失败时返回剩余尝试次数
            userAuthFailResponse.setRemain_retry_count(remainRetryCount);
            systemErrorResponse.fillErrorResponse(userAuthFailResponse, ErrorCodeEnum.ERROR_AUTH_FAILED);
            return userAuthFailResponse;
        }

    }

    private ErrorCodeEnum addAuthHistoryRecord(UserAuthRequest userAuthRequest, int authResult) {
        int rpId = 0;
        int userId = 0;

        if (userAuthRequest.getApp_id() != null) {
            // 如果请求中有rp_id，则查记录表，获取rpId和userId
            RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpUuidAndUserUuid(
                    userAuthRequest.getApp_id(), userAuthRequest.getUser_id());
            if (rpAccount == null)
                return ErrorCodeEnum.ERROR_INVALID_ACCOUNT;
            rpId = rpAccount.getRp_id();
            userId = rpAccount.getUser_id();
        } else {
            // 否则只读取用户记录，获取userId
            Users user = usersTableHelper.getUserByUserUuid(userAuthRequest.getUser_id());
            if (user == null)
                return ErrorCodeEnum.ERROR_USER_NOT_FOUND;
            userId = user.getId();
        }

        // 设置记录的各字段
        accountAuthHistory.setUser_id(userId);
        accountAuthHistory.setRp_id(rpId);
        accountAuthHistory.setProtect_method(userAuthRequest.getProtect_method());
        // 获取参数中的 remote_ip
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String remoteIp = req.getRemoteAddr();
        accountAuthHistory.setAuth_ip(remoteIp);
        accountAuthHistory.setAuth_latitude(userAuthRequest.getLatitude());
        accountAuthHistory.setAuth_longitude(userAuthRequest.getLongitude());
        // 取当前时间
        java.sql.Timestamp authTime = UtilsHelper.getCurrentSystemTimestamp();
        accountAuthHistory.setAuth_at(authTime);
        accountAuthHistory.setCreated_at(authTime);
        accountAuthHistory.setUpdated_at(authTime);

        // 插入一条新记录
        int count = accountAuthHistoryMapper.insertOneHistory(accountAuthHistory);
        if (count != 1)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        return ErrorCodeEnum.ERROR_OK;
    }

//    private ErrorCodeEnum checkUserPasswordState(Users user) {
//        if (user.getPassword_lock_to())
//    }
}
