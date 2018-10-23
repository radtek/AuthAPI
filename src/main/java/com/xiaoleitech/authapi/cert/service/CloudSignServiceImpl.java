package com.xiaoleitech.authapi.cert.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.auxiliary.entity.AppAccountHelper;
import com.xiaoleitech.authapi.auxiliary.entity.UsersHelper;
import com.xiaoleitech.authapi.ca_service.CAProvider;
import com.xiaoleitech.authapi.cert.bean.*;
import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.dao.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.dao.helper.UsersTableHelper;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.cipher.sm_alg.SmAlgHelper;
import com.xiaoleitech.authapi.global.enumeration.CaAgencyEnum;
import com.xiaoleitech.authapi.global.enumeration.CertStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class CloudSignServiceImpl implements CloudSignService {
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;
    private final UsersHelper usersHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final AppAccountHelper appAccountHelper;
    private final SmAlgHelper smAlgHelper;

    @Autowired
    public CloudSignServiceImpl(SystemErrorResponse systemErrorResponse, UsersTableHelper usersTableHelper, UsersHelper usersHelper, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, AuthenticationHelper authenticationHelper, AppAccountHelper appAccountHelper, SmAlgHelper smAlgHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
        this.usersHelper = usersHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.appAccountHelper = appAccountHelper;
        this.smAlgHelper = smAlgHelper;
    }

    @Override
    public AuthAPIResponse applyCert(ApplyCertRequest applyCertRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse signMessage(SignMessageRequest signMessageRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse verifySignature(VerifySignatureRequest verifySignatureRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public Object requestCloudCert(RequestCloudCertRequest requestCloudCert, BindingResult bindingResult) {
        String userUuid = requestCloudCert.getUser_id();
        String appUuid = requestCloudCert.getApp_id();
        String verifyToken = requestCloudCert.getVerify_token();
        String sm2P1 = requestCloudCert.getP1();

        // 检查参数
        if (userUuid.isEmpty() || appUuid.isEmpty() || verifyToken.isEmpty() || sm2P1.isEmpty())
            return systemErrorResponse.needParameters();

        // 取应用账户记录（同时也获取了user和rp记录）
        ErrorCodeEnum errorCode = appAccountHelper.fetchAppAccount(appUuid, userUuid);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return systemErrorResponse.invalidToken();

        // 向国密算法服务器，请求公钥
        StringBuffer pubKey = new StringBuffer("");
        StringBuffer d2 = new StringBuffer("");
        errorCode = smAlgHelper.getPubKey(sm2P1, pubKey, d2);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 根据CA_ID，向关联CA请求证书
        if (appAccountHelper.getRelyPart().getCaid() == CaAgencyEnum.CA_XL.getAgency()) {
            return requestP10FromXLCA(pubKey.toString());
        } else if (appAccountHelper.getRelyPart().getCaid() == CaAgencyEnum.CA_GZCA.getAgency()) {

        }

        return systemErrorResponse.notImplemented();
    }

    private Object requestP10FromXLCA(String pubKey) {
        // 获取DN、hsah算法、以及扩展字段
        String inDN = getDN();
        String inHashAlg = getHashAlg();
        String inExtension = getEntension();
        // 准备接受数据的buffer
        StringBuffer outE = new StringBuffer("");
        StringBuffer outQ1 = new StringBuffer("");
        StringBuffer outK1 = new StringBuffer("");
        StringBuffer outP10 = new StringBuffer("");

        // 获取XLCA-Provider
        CAProvider caProvider = new CAProvider(appAccountHelper.getRelyPart().getCaid());

        // 向XLCA请求P10
        ErrorCodeEnum errorCode = caProvider.requestP10(inDN, pubKey, inHashAlg, inExtension,
                outE, outQ1, outK1, outP10);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 保存证书数据
        JSONObject jsonCert = new JSONObject();
        jsonCert.put("p", pubKey);
        jsonCert.put("e", outE.toString());
        jsonCert.put("q1", outQ1.toString());
        jsonCert.put("k1", outK1.toString());
        jsonCert.put("x509req", outP10.toString());
        RpAccounts rpAccount = appAccountHelper.getRpAccount();
        rpAccount.setCert(jsonCert.toJSONString());
        rpAccount.setCert_state(CertStateEnum.CERT_PENDING.getState());
        rpAccount.setCert_key(pubKey);
        errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 向调用者返回公钥参数
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonResult.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());
        jsonResult.put("p", pubKey);
        jsonResult.put("e", outE.toString());
        jsonResult.put("q1", outQ1.toString());

        return jsonResult;
    }

    private String getEntension() {
        return "{\"D2\" : \"cU3iUq+I42phS0HYfMon/R763mTaLaew+3sKV8Yp2bo=\",\"PubKey\" : \"BCRp0jPAeLn6gyr6s84CKmnuNOpDdauRWHSXiiRIxEMdB9CtEjzTtMy4ZwsiJ7OwKu4Nkt56upXZ4MEnIY85IIo=\"}";
    }

    private String getHashAlg() {
        return "SM3";
    }

    private String getDN() {
        return "C=CN,CN=test";
    }

    @Override
    public AuthAPIResponse signCloudCert(SignCloudCertRequest signCloudCertRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse downloadCert(DownloadCertRequest downloadCertRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse cloudSignature(CloudSignRequest cloudSignRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse getCertInfo(GetCertInfoRequest getCertInfoRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse revokeCert(RevokeCertRequest revokeCertRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse updateCert(UpdateCertRequest updateCertRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public AuthAPIResponse parseCert(String cert) {
        return systemErrorResponse.notImplemented();
    }
}
