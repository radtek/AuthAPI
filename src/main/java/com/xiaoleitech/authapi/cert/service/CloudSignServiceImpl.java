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
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.dao.pojo.RpAccounts;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.cipher.sm_alg.SmAlgHelper;
import com.xiaoleitech.authapi.global.enumeration.CaAgencyEnum;
import com.xiaoleitech.authapi.global.enumeration.CertStateEnum;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.enumeration.UserCertEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class CloudSignServiceImpl implements CloudSignService {
    private String certD2 = "";
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
        // 在系统中读取请求参数中指定的用户、应用和账户记录
        ErrorCodeEnum errorCode = getAppAccount(requestCloudCert);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 检查P1参数
        String sm2P1 = requestCloudCert.getP1();
        if (sm2P1.isEmpty())
            return systemErrorResponse.needParameters();

        // 向国密算法服务器，请求公钥
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("P1", sm2P1);
        String outResult = smAlgHelper.getPubKey(jsonInput.toJSONString());
        JSONObject jsonResult = JSONObject.parseObject(outResult);
        if (jsonResult.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode())
            return jsonResult;

        // 暂存D2私钥
        certD2 = jsonResult.getString("D2");

        // 根据CA_ID，向关联CA请求证书
        jsonResult = requestP10FromCA(jsonResult.getString("PubKey"));
        if (jsonResult.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode())
            return systemErrorResponse.response(jsonResult);

        // 保存证书或证书参数(如有)和证书状态
        JSONObject jsonCert = (JSONObject) jsonResult.clone();
        errorCode = saveAccountCert(jsonCert);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 返回数据中移除 k1 和 x509req
//        jsonResult.remove("k1");
//        jsonResult.remove("x509req");
        // 临时导出用于调试
        jsonResult.put("D2", certD2);
        return jsonResult;
    }

    @Override
    public AuthAPIResponse signCloudCert(SignCloudCertRequest signCloudCertRequest, BindingResult bindingResult) {
        // 在系统中读取请求参数中指定的用户、应用和账户记录
        ErrorCodeEnum errorCode = getAppAccount(signCloudCertRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 检查R/S2/S3参数
        String sm2R = signCloudCertRequest.getR();
        String sm2S2 = signCloudCertRequest.getS2();
        String sm2S3 = signCloudCertRequest.getS3();
        if (sm2R.isEmpty() || sm2S2.isEmpty() || sm2S3.isEmpty())
            return systemErrorResponse.needParameters();

        // 检查该账户是否有证书参数
        // 证书参数中包含Q1、P、x509req、E、K1
        String cert = appAccountHelper.getRpAccount().getCert();
        if ((cert == null) || (cert.isEmpty()))
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_CERTIFICATE_NOT_FOUND);

        JSONObject jsonCertParams = JSONObject.parseObject(cert);
        // 准备调用的输入参数
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("k1", jsonCertParams.getString("k1"));
        jsonInput.put("P10", jsonCertParams.getString("x509req"));
        jsonInput.put("D2", appAccountHelper.getRpAccount().getCert_key());
        jsonInput.put("S2", signCloudCertRequest.getS2());
        jsonInput.put("S3", signCloudCertRequest.getS3());
        jsonInput.put("R", signCloudCertRequest.getR());

        // =============================================================
        // TODO: 临时测试用代码
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 应用名称不能为空
        String newK1 = request.getParameter("k1");
        if ((newK1 != null) && !newK1.isEmpty())
            jsonInput.put("k1", newK1);
        String newP10 = request.getParameter("P10");
        if ((newP10 != null) && !newP10.isEmpty())
            jsonInput.put("P10", newP10);
        String newD2 = request.getParameter("D2");
        if ((newD2 != null) && !newD2.isEmpty())
            jsonInput.put("D2", newD2);
//        jsonInput.put("k1", "wTs6sRpF5lzFOiv29PYhGQjCdPnrdvJADLz/ZVfqEeM=");
//        jsonInput.put("P10", "MIHTMH4CAQAwHDENMAsGA1UEAwwEdGVzdDELMAkGA1UEBhMCQ04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAATW09pTL4LbNZ6wgZlyC9nF5BAPIBbCytxstyVTgdoRvg+vV8m6eiqYG8mCQ3A35AuhDVBfr8kAYOdciayyOYAtoAAwCgYIKoEcz1UBg3UDRQAwRAIgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");
//        jsonInput.put("D2", "zH7eIYcgdF8XFXzrOuYAd4yXNbXm74B68D1utXnbe1E=");
        // =============================================================

        // 调用关联CA的方法生成P10证书
        CAProvider caProvider = getCAProvider();
        JSONObject jsonOutput = caProvider.generateP10(jsonInput);
        if (jsonOutput.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode()) {
            return systemErrorResponse.response(jsonOutput);
        }

        // 保存证书(如有)和证书状态
        errorCode = saveAccountCert(jsonOutput);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 调用关联CA的 getCert
        jsonInput.clear();
        jsonInput.put("P10", jsonOutput.getString("P10"));
        JSONObject jsonCert = caProvider.getCert(jsonInput);
        if (jsonCert.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode()) {
            return systemErrorResponse.response(ErrorCodeEnum.ERROR_FAILED_GET_CERT);
        }

        // 保存证书(如有)和证书状态
        errorCode = saveAccountCert(jsonCert);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        return systemErrorResponse.success();
    }

    @Override
    public AuthAPIResponse downloadCert(DownloadCertRequest downloadCertRequest, BindingResult bindingResult) {
        // 在系统中读取请求参数中指定的用户、应用和账户记录
        ErrorCodeEnum errorCode = getAppAccount(downloadCertRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 检查证书状态
        errorCode = checkAccountValidCert();
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        RpAccounts rpAccount = appAccountHelper.getRpAccount();
        DownloadCertResponse downloadCertResponse = new DownloadCertResponse();
        downloadCertResponse.setCertificate(rpAccount.getCert());
        systemErrorResponse.fill(downloadCertResponse, ErrorCodeEnum.ERROR_OK);

        return downloadCertResponse;
    }

    @Override
    public Object cloudSignature(CloudSignRequest cloudSignRequest, BindingResult bindingResult) {
        // 在系统中读取请求参数中指定的用户、应用和账户记录
        ErrorCodeEnum errorCode = getAppAccount(cloudSignRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        String sm2E = cloudSignRequest.getE();
        String sm2Q1 = cloudSignRequest.getQ1();
        if (sm2E.isEmpty() || sm2Q1.isEmpty())
            return systemErrorResponse.needParameters();

        // 调用国密算法协同签名接口
        JSONObject jsonInParams = new JSONObject();
        jsonInParams.put("E", sm2E);
        jsonInParams.put("Q1", sm2Q1);
        jsonInParams.put("D2", appAccountHelper.getRpAccount().getCert_key());
        String coSignResult = smAlgHelper.coSign(jsonInParams.toJSONString());

        JSONObject jsonResult = JSONObject.parseObject(coSignResult);
        // 判断调用是否成功返回
        if (jsonResult.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode())
            return jsonResult;

        return jsonResult;
    }

    @Override
    public Object getCertInfo(GetCertInfoRequest getCertInfoRequest, BindingResult bindingResult) {
        // 在系统中读取请求参数中指定的用户、应用和账户记录
        ErrorCodeEnum errorCode = getAppAccount(getCertInfoRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 检查证书状态
        errorCode = checkAccountValidCert();
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 调用国密算法服务器的解析证书服务
        RpAccounts rpAccount = appAccountHelper.getRpAccount();
        JSONObject jsonCertInfo = parseCert(rpAccount.getCert());
        // 判断调用是否成功返回
        if (jsonCertInfo.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode())
            return jsonCertInfo;

        JSONObject jsonResult = new JSONObject();
        jsonResult.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonResult.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());
        jsonCertInfo.remove("error_code");
        jsonCertInfo.remove("error_message");
        jsonResult.put("cert_info", jsonCertInfo);

        return jsonResult;
    }

    @Override
    public AuthAPIResponse revokeCert(RevokeCertRequest revokeCertRequest, BindingResult bindingResult) {
        // 在系统中读取请求参数中指定的用户、应用和账户记录
        ErrorCodeEnum errorCode = getAppAccount(revokeCertRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        // 检查证书状态
        errorCode = checkAccountValidCert();
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.response(errorCode);

        RpAccounts rpAccount = appAccountHelper.getRpAccount();

        // 向服务器发出证书撤销申请
        CAProvider caProvider = getCAProvider();
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Cert", rpAccount.getCert());
        JSONObject jsonOutput = caProvider.revokeCert(jsonInput);
        if (jsonOutput.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode()) {
            return systemErrorResponse.response(jsonOutput);
        }

        // 保存账户的证书状态为 NO_CERT
        rpAccount.setCert_state(CertStateEnum.NO_CERT.getState());
        errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);

        return systemErrorResponse.response(errorCode);
    }

    @Override
    public AuthAPIResponse updateCert(UpdateCertRequest updateCertRequest, BindingResult bindingResult) {
        return systemErrorResponse.notImplemented();
    }

    @Override
    public JSONObject parseCert(String cert) {
        // 调用国密算法服务器的解析证书服务
        JSONObject jsonCert = new JSONObject();
        jsonCert.put("Cert", cert);
        String parseCertResult = smAlgHelper.parseCert(jsonCert.toJSONString());

        return JSONObject.parseObject(parseCertResult);
    }

    private ErrorCodeEnum checkAccountValidCert() {
        // 检查应用方（依赖方）是否要求使用证书
        RelyParts relyPart = appAccountHelper.getRelyPart();
        if (relyPart.getUse_cert() == UserCertEnum.NOT_USE_CERT.getUseCert())
            return ErrorCodeEnum.ERROR_CERTIFICATE_NO_CERTREQ;

        // 检查账户证书状态，是否为可用状态
        RpAccounts rpAccount = appAccountHelper.getRpAccount();
        int certState = rpAccount.getCert_state();
        if (certState == CertStateEnum.NO_CERT.getState()) {
            return ErrorCodeEnum.ERROR_CERTIFICATE_NO_CERTREQ;
        } else if (certState == CertStateEnum.CERT_PENDING.getState()) {
            return ErrorCodeEnum.ERROR_CERTIFICATE_CERTREQ_NOT_SIGN;
        } else if (certState == CertStateEnum.CERT_PENDING2.getState()) {
            return ErrorCodeEnum.ERROR_CERTIFICATE_PENDING;
        }

        // 检查证书是否为空
        if (rpAccount.getCert().isEmpty())
            return ErrorCodeEnum.ERROR_CERTIFICATE_NOT_FOUND;

        // 检查证书是否能解析

        return ErrorCodeEnum.ERROR_OK;
    }

    private JSONObject requestP10FromCA(String pubKey) {
        JSONObject jsonInput = new JSONObject();
        JSONObject jsonResult = new JSONObject();

        // 获取DN、hsah算法、以及扩展字段
        jsonInput.put("DN", getDN());
        jsonInput.put("PubKey", pubKey);
        jsonInput.put("HashAlg", getHashAlg());
        jsonInput.put("Extension", getEntension());

        // 获取 CA-Provider
        CAProvider caProvider = getCAProvider();

        // 向XLCA请求P10
        jsonResult = caProvider.requestP10(jsonInput);
        if (jsonResult.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode()) {
            return jsonResult;
        }

        // 添加公钥
        jsonResult.put("P", pubKey);
        // 转换证书键
        jsonResult.put("x509req", jsonResult.getString("P10"));
        jsonResult.remove("P10");

        // 添加成功代码
        jsonResult.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonResult.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());

        return jsonResult;
    }

    private ErrorCodeEnum saveAccountCert(JSONObject jsonCert) {
        // 取出证书状态，并移除该键值
        int certState = jsonCert.getIntValue("cert_state");
        jsonCert.remove("cert_state");

        // 移除错误码的键值
        jsonCert.remove("error_code");
        jsonCert.remove("error_message");

        RpAccounts rpAccount = appAccountHelper.getRpAccount();

        // 保存证书数据，
        // 证书状态为 CERT_PENDING 时，保存证书参数
        // 证书状态为 AVAILABLE 时，保存证书
        if (certState == CertStateEnum.CERT_PENDING.getState())
            rpAccount.setCert(jsonCert.toJSONString());
        else if (certState == CertStateEnum.AVAILABLE.getState())
            rpAccount.setCert(jsonCert.getString("Cert"));
        else if (certState == CertStateEnum.CERT_PENDING2.getState())
            rpAccount.setCert(jsonCert.getString("P10"));

        // 不同CA的证书状态的设置（由CA接口决定证书状态）
        rpAccount.setCert_state(certState);

        // 证书状态为 CERT_PENDING 时，保存私钥
        if (certState == CertStateEnum.CERT_PENDING.getState())
            rpAccount.setCert_key(getPrivKey());

        // 更新账户记录
        ErrorCodeEnum errorCode = rpAccountsTableHelper.updateOneRpAccountRecord(rpAccount);

        return errorCode;
    }

    private ErrorCodeEnum getAppAccount(AppAccountRequest appAccountRequest) {
        String userUuid = appAccountRequest.getUser_id();
        String appUuid = appAccountRequest.getApp_id();
        String verifyToken = appAccountRequest.getVerify_token();

        // 检查参数
        if (userUuid.isEmpty() || appUuid.isEmpty() || verifyToken.isEmpty())
            return ErrorCodeEnum.ERROR_NEED_PARAMETER;

        // 取应用账户记录（同时也获取了user和rp记录）
        ErrorCodeEnum errorCode = appAccountHelper.fetchAppAccount(appUuid, userUuid);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return errorCode;

        // 验证令牌
        if (!authenticationHelper.isTokenVerified(verifyToken))
            return ErrorCodeEnum.ERROR_INVALID_TOKEN;

        return ErrorCodeEnum.ERROR_OK;
    }

    private String getEntension() {
        return "ext-json";
//        return "{\"D2\" : \"cU3iUq+I42phS0HYfMon/R763mTaLaew+3sKV8Yp2bo=\",\"PubKey\" : \"BCRp0jPAeLn6gyr6s84CKmnuNOpDdauRWHSXiiRIxEMdB9CtEjzTtMy4ZwsiJ7OwKu4Nkt56upXZ4MEnIY85IIo=\"}";
    }

    private String getHashAlg() {
        return "SM3";
    }

    private String getDN() {
        return "C=CN,CN=test";
    }

    private String getPrivKey() {
//        return certD2;
        return "cU3iUq+I42phS0HYfMon/R763mTaLaew+3sKV8Yp2bo=";
    }

    private CAProvider getCAProvider() {
        return new CAProvider(appAccountHelper.getRelyPart().getCaid());
    }
}
