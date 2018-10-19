package com.xiaoleitech.authapi.authorization.service;

import com.xiaoleitech.authapi.global.restcall.RelyPartCallBackHelper;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.global.cipher.base64.Base64Coding;
import com.xiaoleitech.authapi.global.cipher.symmetric.SymmetricAlgorithm;
import com.xiaoleitech.authapi.global.websocket.MyWebSocket;
import com.xiaoleitech.authapi.dao.mybatis.mapper.AccountAuthHistoryMapper;
import com.xiaoleitech.authapi.authorization.bean.response.UserAuthorizeFailedResponse;
import com.xiaoleitech.authapi.authorization.bean.request.UserAuthorizeRequest;
import com.xiaoleitech.authapi.authorization.bean.response.UserAuthorizeSuccessResponse;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.global.enumeration.UserCertEnum;
import com.xiaoleitech.authapi.dao.pojo.AccountAuthHistories;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.dao.pojo.Users;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.crypto.Cipher;

@Component
public class UserAuthorizeServiceImpl implements UserAuthorizeService {
    private Logger logger = LoggerFactory.getLogger(UserAuthorizeServiceImpl.class);

    private final SystemErrorResponse systemErrorResponse;
    private final UserAuthorizeSuccessResponse userAuthorizeSuccessResponse;
    private final UsersTableHelper usersTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final SymmetricAlgorithm symmetricAlgorithm;
    private final UserAuthorizeFailedResponse userAuthorizeFailedResponse;
    private final RelyPartCallBackHelper relyPartCallBackHelper;
    private final AccountAuthHistories accountAuthHistory;
    private final AccountAuthHistoryMapper accountAuthHistoryMapper;

    @Autowired
    public UserAuthorizeServiceImpl(SystemErrorResponse systemErrorResponse,
                                    UserAuthorizeSuccessResponse userAuthorizeSuccessResponse,
                                    UsersTableHelper usersTableHelper,
                                    AuthenticationHelper authenticationHelper,
                                    RelyPartsTableHelper relyPartsTableHelper,
                                    RpAccountsTableHelper rpAccountsTableHelper,
                                    SymmetricAlgorithm symmetricAlgorithm,
                                    UserAuthorizeFailedResponse userAuthorizeFailedResponse, RelyPartCallBackHelper relyPartCallBackHelper, AccountAuthHistories accountAuthHistory, AccountAuthHistoryMapper accountAuthHistoryMapper) {
        this.systemErrorResponse = systemErrorResponse;
        this.userAuthorizeSuccessResponse = userAuthorizeSuccessResponse;
        this.usersTableHelper = usersTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.userAuthorizeFailedResponse = userAuthorizeFailedResponse;
        this.relyPartCallBackHelper = relyPartCallBackHelper;
        this.accountAuthHistory = accountAuthHistory;
        this.accountAuthHistoryMapper = accountAuthHistoryMapper;
    }

    @Override
    public AuthAPIResponse authorize(UserAuthorizeRequest userAuthorizeRequest, BindingResult bindingResult) {
//        logger.info("--authorize pwd is: " + userAuthorizeRequest.get);
        // 获取用户记录
        Users user = usersTableHelper.getUserByUserUuid(userAuthorizeRequest.getUser_id());
        if (user == null)
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 检查验证令牌
        if (user.getAuthenticated() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState()) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_AUTHENTICATED);
        }
        if ( !authenticationHelper.isTokenVerified(userAuthorizeRequest.getVerify_token()) ) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_INVALID_TOKEN);
        }

        // 获取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(userAuthorizeRequest.getApp_id());
        if (relyPart == null) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_APP_NOT_FOUND);
        }

        // 获取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getId(), user.getId());
        if (rpAccount == null) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED);
        }

        // 应用账户必须激活
        if (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState()) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_ACTIVATED);
        }

        // 目前只处理不使用证书的流程
        if (relyPart.getUse_cert() != UserCertEnum.NOT_USE_CERT.getUseCert()) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_CERTIFICATE_NOT_FOUND);
        } else {
            // 对 nonce 做 AES256 加密
            byte[] cryptResult = symmetricAlgorithm.doAes(userAuthorizeRequest.getNonce().getBytes(),
                    user.getAuth_key().getBytes(), 256, Cipher.ENCRYPT_MODE);
            // 对计算结果做Base64编码，并和传入的 response 比较
            String cryptBase64 = Base64Coding.encode(cryptResult);
            String verifyResponse = userAuthorizeRequest.getResponse();
            // base64编码中可能存在\n
            verifyResponse = verifyResponse.replace("\n", "");
            if ( !cryptBase64.equals(verifyResponse) ) {
                // 验证失败，则返回错误，并返回应用账户的UUID
                systemErrorResponse.fill( userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED );
                userAuthorizeFailedResponse.setAccount_id(rpAccount.getRp_account_uuid());
                return userAuthorizeFailedResponse;
            }

//            String result = symmetricAlgorithm.doAes(UtilsHelper.bytesToHexString(userAuthorizeRequest.getNonce().getBytes()), user.getAuth_key(), 256, Cipher.ENCRYPT_MODE);
//            // 先做URL解码
//            String strDecoded = userAuthorizeRequest.getResponse();
//            strDecoded = strDecoded.replace("\n", "");
////            try {
////                strDecoded = URLDecoder.decode(userAuthorizeRequest.getResponse(), "UTF-8");
////            } catch (UnsupportedEncodingException e) {
////                strDecoded = "";
////                e.printStackTrace();
////            }
//            byte[] responseBytes = Base64Coding.decode(strDecoded);
//            // 验证失败，则返回错误，并返回应用账户的UUID
//            if ( !result.equals(UtilsHelper.bytesToHexString(responseBytes)) ){
//                systemErrorResponse.fill( userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED );
//                userAuthorizeFailedResponse.setAccount_id(rpAccount.getRp_account_uuid());
//                return userAuthorizeFailedResponse;
//            }
        }

        // 验证成功
        // 产生一个认证令牌
        String authToken = authenticationHelper.generateTimingToken();
        rpAccount.setAuthred(UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState());
        rpAccount.setAuthr_at(UtilsHelper.getCurrentSystemTimestamp());
        rpAccount.setAuthorization_token(authToken);
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            systemErrorResponse.fill( userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED );
            userAuthorizeFailedResponse.setAccount_id(rpAccount.getRp_account_uuid());
            return userAuthorizeFailedResponse;
        }

        // 添加认证的历史记录
        errorCode = addAuthorizeHistoryRecord(userAuthorizeRequest, 1);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 回调 rp_account_authorized_callback_url
//        relyPartCallBackHelper.rpAuthorizeCallback(relyPart, rpAccount.getRp_account_uuid(),
//                userAuthorizeRequest.getNonce(), authToken);

        // 通过 WebSocket 向应用方发送 rp_login_redirection_url
        String url = relyPart.getRp_login_redirection_url();
        MyWebSocket.websocketNotifyRedirect(relyPart.getRp_uuid(), rpAccount.getRp_account_uuid(),
                authToken, userAuthorizeRequest.getNonce(), url);

        systemErrorResponse.fill( userAuthorizeSuccessResponse, ErrorCodeEnum.ERROR_OK );
        userAuthorizeSuccessResponse.setAccount_id(rpAccount.getRp_account_uuid());
        userAuthorizeSuccessResponse.setAuthorization_token(authToken);
        return userAuthorizeSuccessResponse;
    }

    private ErrorCodeEnum addAuthorizeHistoryRecord(UserAuthorizeRequest userAuthorizeRequest, int authResult) {
        // authResult目前不用，留待扩展

        int rpId = 0;

        if (userAuthorizeRequest.getApp_id() != null) {
            // 如果请求中有rp_id，则查记录表，获取rpId
            RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpUuidAndUserUuid(
                    userAuthorizeRequest.getApp_id(), userAuthorizeRequest.getUser_id());
            if (rpAccount == null)
                return ErrorCodeEnum.ERROR_INVALID_ACCOUNT;
            rpId = rpAccount.getRp_id();
        }

        // 否则只读取用户记录，获取userId
        Users user = usersTableHelper.getUserByUserUuid(userAuthorizeRequest.getUser_id());
        if (user == null)
            return ErrorCodeEnum.ERROR_USER_NOT_FOUND;

        // 设置记录的各字段
        accountAuthHistory.setUser_id(user.getId());
        accountAuthHistory.setRp_id(rpId);
        accountAuthHistory.setProtect_method(user.getProtect_method());
        accountAuthHistory.setAuth_ip(UtilsHelper.getRemoteIp());
        accountAuthHistory.setAuth_latitude(user.getAuth_latitude());
        accountAuthHistory.setAuth_longitude(user.getAuth_longitude());
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

}
