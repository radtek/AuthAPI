package com.xiaoleitech.authapi.authorization.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.auxiliary.entity.AppAccountHelper;
import com.xiaoleitech.authapi.ca_service.CAProvider;
import com.xiaoleitech.authapi.global.cipher.sm_alg.SmAlgHelper;
import com.xiaoleitech.authapi.global.enumeration.*;
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
    private final AppAccountHelper appAccountHelper;
    private final SmAlgHelper smAlgHelper;

    @Autowired
    public UserAuthorizeServiceImpl(SystemErrorResponse systemErrorResponse,
                                    UserAuthorizeSuccessResponse userAuthorizeSuccessResponse,
                                    UsersTableHelper usersTableHelper,
                                    AuthenticationHelper authenticationHelper,
                                    RelyPartsTableHelper relyPartsTableHelper,
                                    RpAccountsTableHelper rpAccountsTableHelper,
                                    SymmetricAlgorithm symmetricAlgorithm,
                                    UserAuthorizeFailedResponse userAuthorizeFailedResponse, RelyPartCallBackHelper relyPartCallBackHelper, AccountAuthHistories accountAuthHistory, AccountAuthHistoryMapper accountAuthHistoryMapper, AppAccountHelper appAccountHelper, SmAlgHelper smAlgHelper) {
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
        this.appAccountHelper = appAccountHelper;
        this.smAlgHelper = smAlgHelper;
    }

    @Override
    public AuthAPIResponse authorize(UserAuthorizeRequest userAuthorizeRequest, BindingResult bindingResult) {
//        logger.info("--authorize pwd is: " + userAuthorizeRequest.get);
        // 读取用户、应用和账户的记录，并检查 verify_token
        ErrorCodeEnum errorCode = appAccountHelper.getAppAccount(userAuthorizeRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        Users user = appAccountHelper.getUser();
        // 授权前需经过认证（登录）
        if (user.getAuthenticated() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState()) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_USER_NOT_AUTHENTICATED);
        }

        RelyParts relyPart = appAccountHelper.getRelyPart();
        RpAccounts rpAccount = appAccountHelper.getRpAccount();

        // 根据应用和账户的证书状态，走验证签名或验证nonce加密结果
        String nonce = userAuthorizeRequest.getNonce();
        String response = userAuthorizeRequest.getResponse();
        if (isUsingDigitalCert(relyPart, rpAccount)) {
            if (!verifyLoginSignature(nonce, response, rpAccount.getCert())) {
                systemErrorResponse.fill(userAuthorizeFailedResponse, errorCode);
                userAuthorizeFailedResponse.setAccount_id(rpAccount.getRp_account_uuid());
                return userAuthorizeFailedResponse;
            }
        } else {
            // 不需要证书的情况下，对nonce做AES加密验证
            if (!verifyEncryptedNonce(nonce, response)) {
                // 验证失败，则返回错误，并返回应用账户的UUID
                systemErrorResponse.fill(userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED);
                userAuthorizeFailedResponse.setAccount_id(rpAccount.getRp_account_uuid());
                return userAuthorizeFailedResponse;
            }
        }

        // 验证成功
        // 产生一个认证令牌
        String authToken = authenticationHelper.generateTimingToken();
        rpAccount.setAuthred(UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState());
        rpAccount.setAuthr_at(UtilsHelper.getCurrentSystemTimestamp());
        rpAccount.setAuthorization_token(authToken);
        errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            systemErrorResponse.fill(userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED);
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

        systemErrorResponse.fill(userAuthorizeSuccessResponse, ErrorCodeEnum.ERROR_OK);
        userAuthorizeSuccessResponse.setAccount_id(rpAccount.getRp_account_uuid());
        userAuthorizeSuccessResponse.setAuthorization_token(authToken);
        return userAuthorizeSuccessResponse;
    }

    private boolean verifyLoginSignature(String plainText, String signature, String cert) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Plaintext", Base64Coding.encode(plainText.getBytes()));
        jsonInput.put("Signature", signature);
        jsonInput.put("Cert", cert);

        // 验证签名
        String result = smAlgHelper.verifySignature(jsonInput.toJSONString());

        JSONObject jsonResult = JSONObject.parseObject(result);

        if (jsonResult.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode()) {
            return false;
        }

        return (jsonResult.getIntValue("verifySignatureResult") == 1);
    }

    private boolean verifyEncryptedNonce(String nonce, String cryptedNonce) {
        // 取认证密钥
        byte [] authKey = appAccountHelper.getUser().getAuth_key().getBytes();

        // base64编码中可能存在\n
        cryptedNonce = cryptedNonce.replace("\n", "");

        // 对 nonce 做 AES256 加密
        byte[] cryptResult = symmetricAlgorithm.doAes(nonce.getBytes(), authKey, 256, Cipher.ENCRYPT_MODE);

        // 对计算结果做Base64编码，并和传入的 response 比较
        String cryptBase64 = Base64Coding.encode(cryptResult);
        return cryptBase64.equals(cryptedNonce);
    }

    private boolean isUsingDigitalCert(RelyParts relyPart, RpAccounts rpAccount) {
        int useCert = relyPart.getUse_cert();
        int certState = rpAccount.getCert_state();

        if (useCert == UserCertEnum.USE_CERT.getUseCert()) {
            return true;
        }else if (useCert == UserCertEnum.OPTIONAL.getUseCert()){
            return (certState == CertStateEnum.AVAILABLE.getState());
        } else {
            return false;
        }
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
