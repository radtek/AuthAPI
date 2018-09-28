package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.helper.otp.OtpHelper;
import com.xiaoleitech.authapi.helper.RelyPartHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.websocket.MyWebSocket;
import com.xiaoleitech.authapi.model.authorization.OtpAuthResponse;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.bean.OtpParams;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.enumeration.UserAuthStateEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OtpAuthorizeServiceImpl implements OtpAuthorizeService {
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RelyPartHelper relyPartHelper;
    private final OtpHelper otpHelper;
    private final AuthenticationHelper authenticationHelper;
    private final OtpAuthResponse otpAuthResponse;

    @Autowired
    public OtpAuthorizeServiceImpl(RpAccountsTableHelper rpAccountsTableHelper, SystemErrorResponse systemErrorResponse, RelyPartsTableHelper relyPartsTableHelper, RelyPartHelper relyPartHelper, OtpHelper otpHelper, AuthenticationHelper authenticationHelper, OtpAuthResponse otpAuthResponse) {
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.relyPartHelper = relyPartHelper;
        this.otpHelper = otpHelper;
        this.authenticationHelper = authenticationHelper;
        this.otpAuthResponse = otpAuthResponse;
    }

    @Override
    public AuthAPIResponse otpAuthorize(String appUuid, String token, String accountUuid, String accountName, String otp, String nonce) {
        // 检查参数
        if (appUuid.isEmpty() || token.isEmpty() || otp.isEmpty() || (accountUuid.isEmpty() && accountName.isEmpty()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);

        // 读取应用账户记录
        RpAccounts rpAccount = null;
        if (!accountName.isEmpty()) {
            rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndAccountName(relyPart.getRp_id(), accountName);
        } else if (!accountUuid.isEmpty()) {
            rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(accountUuid);
        }
        if (rpAccount == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        // 检查令牌
        if (!relyPartHelper.verifyToken(relyPart, token))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 流程修改，免去 get_otp_code 接口调用，在本接口中一步完成
        // 生成OTP，并存放到缓存中
        OtpParams otpParamsOrigin = new OtpParams();
        otpParamsOrigin.setOwner(rpAccount.getRp_account_uuid());
        BeanUtils.copyProperties(relyPart, otpParamsOrigin);
        otpParamsOrigin.setNonce(nonce);
        otpParamsOrigin.setOtp_seed(rpAccount.getOtp_seed());
        String accountOtp = otpHelper.generateOtp(otpParamsOrigin);

        // 校验OTP
        OtpParams otpParams = new OtpParams();
        otpParams.setOwner(rpAccount.getRp_account_uuid());
        BeanUtils.copyProperties(relyPart, otpParams);
        otpParams.setOtp_seed(rpAccount.getOtp_seed());
        if (!otpHelper.checkOtp(otpParams, otp)) {
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_OTP);
        }

        // OTP校验通过，保存账户认证状态
        // 产生一个认证令牌
        String authToken = authenticationHelper.generateTimingToken();
        rpAccount.setAuthred(UserAuthStateEnum.AUTH_STATE_AUTHED.getAuthState());
        rpAccount.setAuth_at(UtilsHelper.getCurrentSystemTimestamp());
        rpAccount.setAuthorization_token(authToken);
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        systemErrorResponse.fillErrorResponse(otpAuthResponse, ErrorCodeEnum.ERROR_OK);

        // 取出登录成功的回调URL
        String url = relyPart.getRp_login_redirection_url();
        if ( (url != null) && !url.isEmpty()) {
            // 拼接回调接口参数
            url += "?account_id=" + accountUuid + "&authorization_token=" + authToken;
            otpAuthResponse.setRedirect_url(url);
            // url和nonce均有效时，通过websocket调用回调URL
            if (!nonce.isEmpty())
                MyWebSocket.websocketNotifyRedirect(appUuid, accountUuid, authToken, nonce, url);
        }

        return otpAuthResponse;
    }
}
