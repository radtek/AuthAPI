package com.xiaoleitech.authapi.cert.service;

import com.xiaoleitech.authapi.cert.bean.*;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class CloudSignServiceImpl implements CloudSignService{
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public CloudSignServiceImpl(SystemErrorResponse systemErrorResponse) {
        this.systemErrorResponse = systemErrorResponse;
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
    public AuthAPIResponse requestCloudCert(RequestCloudCertRequest requestCloudCert, BindingResult bindingResult) {
        String userUuid = requestCloudCert.getUser_id();
        String appUuid = requestCloudCert.getApp_id();
        String verifyToken = requestCloudCert.getVerify_token();
        String sm2P1 = requestCloudCert.getP1();

        if (userUuid.isEmpty() || appUuid.isEmpty() || verifyToken.isEmpty() || sm2P1.isEmpty())
            return systemErrorResponse.needParameters();

        return systemErrorResponse.notImplemented();
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
