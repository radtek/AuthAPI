package com.xiaoleitech.authapi.global.callback;

import com.xiaoleitech.authapi.auxiliary.entity.RelyPartHelper;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class RelyPartCallBackHelper {
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartHelper relyPartHelper;
    private final RestTemplate restTemplate;

    @Autowired
    public RelyPartCallBackHelper(SystemErrorResponse systemErrorResponse, RelyPartHelper relyPartHelper, RestTemplate restTemplate) {
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartHelper = relyPartHelper;
        this.restTemplate = restTemplate;
    }

    public ErrorCodeEnum rpRestfulCallback(RelyParts relyPart, String url) {
        if (relyPart == null)
            return ErrorCodeEnum.ERROR_INVALID_APP;
        if ( (url == null) || (url.isEmpty() ) )
            return ErrorCodeEnum.ERROR_NEED_APP_CALLBACK_URL;

        // 生成一个令牌
        String token = relyPartHelper.generateToken(relyPart);

        // 添加验证令牌
        Map<String, String> requestEntity = new HashMap<>();
        requestEntity.put("token", token);
        if (url.indexOf("?") > 0)
            url = url + "&token={token}";
        else
            url = url + "?token={token}";

        // 调用应用方提供的接口
        ResponseEntity<String> results = restTemplate.getForEntity(url, String.class, requestEntity);

        if (results.getStatusCode() != HttpStatus.OK)
            return ErrorCodeEnum.ERROR_FAIL_CALLBACK;

        return ErrorCodeEnum.ERROR_OK;

//        ResponseEntity<String> results = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }

    public ErrorCodeEnum rpEnrollCallback(RelyParts relyPart, String accountName, String rpAccountUuid, String idCode, String activeCode) {
        String urlFormat = relyPart.getRp_account_enroll_callback_url();
        urlFormat = urlFormat + "?account_name={0}&account_id={1}&id_code={2}&active_code={3}";
        String url = MessageFormat.format(urlFormat, accountName, rpAccountUuid, idCode, activeCode);

        return rpRestfulCallback(relyPart, url);
    }

    public ErrorCodeEnum rpUnenrollCallback(RelyParts relyPart, String rpAccountUuid) {
        String urlFormat = relyPart.getRp_account_unenroll_callback_url();
        urlFormat = urlFormat + "?account_id={0}";
        String url = MessageFormat.format(urlFormat, rpAccountUuid);

        return rpRestfulCallback(relyPart, url);
    }

    public ErrorCodeEnum rpAuthorizeCallback(RelyParts relyPart, String rpAccountUuid, String nonce, String authorizeToken) {
        String urlFormat = relyPart.getRp_account_authorized_callback_url();
        urlFormat = urlFormat + "?account_id={0}&nonce={1}&authorization_token={2}";
        String url = MessageFormat.format(urlFormat, rpAccountUuid, nonce, authorizeToken);

        return rpRestfulCallback(relyPart, url);
    }

    public ErrorCodeEnum rpUnauthorizeCallback(RelyParts relyPart, String rpAccountUuid) {
        String urlFormat = relyPart.getRp_account_unauthorized_callback_url();
        urlFormat = urlFormat + "?account_id={0}";
        String url = MessageFormat.format(urlFormat, rpAccountUuid);

        return rpRestfulCallback(relyPart, url);
    }
}
