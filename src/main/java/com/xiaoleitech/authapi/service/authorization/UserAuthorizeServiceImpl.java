package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.cipher.Base64Coding;
import com.xiaoleitech.authapi.helper.cipher.SymmetricAlgorithm;
import com.xiaoleitech.authapi.model.authorization.UserAuthorizeFailedResponse;
import com.xiaoleitech.authapi.model.authorization.UserAuthorizeRequest;
import com.xiaoleitech.authapi.model.authorization.UserAuthorizeSuccessResponse;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.model.enumeration.UserCertEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.authentication.UserAuthenticateServiceImpl;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

    @Autowired
    public UserAuthorizeServiceImpl(SystemErrorResponse systemErrorResponse,
                                    UserAuthorizeSuccessResponse userAuthorizeSuccessResponse,
                                    UsersTableHelper usersTableHelper,
                                    AuthenticationHelper authenticationHelper,
                                    RelyPartsTableHelper relyPartsTableHelper,
                                    RpAccountsTableHelper rpAccountsTableHelper,
                                    SymmetricAlgorithm symmetricAlgorithm,
                                    UserAuthorizeFailedResponse userAuthorizeFailedResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.userAuthorizeSuccessResponse = userAuthorizeSuccessResponse;
        this.usersTableHelper = usersTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.userAuthorizeFailedResponse = userAuthorizeFailedResponse;
    }

    @Override
    public AuthAPIResponse authorize(UserAuthorizeRequest userAuthorizeRequest, BindingResult bindingResult) {
//        logger.info("--authorize pwd is: " + userAuthorizeRequest.get);
        // 获取用户记录
        Users user = usersTableHelper.getUserByUserUuid(userAuthorizeRequest.getUser_id());
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 检查验证令牌
        if (user.getAuthenticated() != UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState()) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_AUTHENTICATED);
        }
        if ( !authenticationHelper.isTokenVerified(userAuthorizeRequest.getVerify_token()) ) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);
        }

        // 获取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(userAuthorizeRequest.getApp_id());
        if (relyPart == null) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);
        }

        // 获取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(relyPart.getId(), user.getId());
        if (rpAccount == null) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ENROLLED);
        }

        // 应用账户必须激活
        if (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState()) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_ACTIVATED);
        }

        // 目前只处理不使用证书的流程
        if (relyPart.getUse_cert() != UserCertEnum.NOT_VERIFY_USER_CERT.getUseCert()) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_CERTIFICATE_NOT_FOUND);
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
                systemErrorResponse.fillErrorResponse( userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED );
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
//                systemErrorResponse.fillErrorResponse( userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED );
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
            systemErrorResponse.fillErrorResponse( userAuthorizeFailedResponse, ErrorCodeEnum.ERROR_AUTH_FAILED );
            userAuthorizeFailedResponse.setAccount_id(rpAccount.getRp_account_uuid());
            return userAuthorizeFailedResponse;
        }

        // TODO: 添加认证的历史记录

        // TODO: 添加回调 rp_account_authorized_callback_url

        // TODO: 添加回调 rp_login_redirection_url

        systemErrorResponse.fillErrorResponse( userAuthorizeSuccessResponse, ErrorCodeEnum.ERROR_OK );
        userAuthorizeSuccessResponse.setAccount_id(rpAccount.getRp_account_uuid());
        userAuthorizeSuccessResponse.setAuthorization_token(authToken);
        return userAuthorizeSuccessResponse;
    }
}
