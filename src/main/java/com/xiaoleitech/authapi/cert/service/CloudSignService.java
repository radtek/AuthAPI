package com.xiaoleitech.authapi.cert.service;

import com.xiaoleitech.authapi.cert.bean.*;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public interface CloudSignService {
    AuthAPIResponse applyCert(ApplyCertRequest applyCertRequest, BindingResult bindingResult);

    AuthAPIResponse signMessage(SignMessageRequest signMessageRequest, BindingResult bindingResult);

    AuthAPIResponse verifySignature(VerifySignatureRequest verifySignatureRequest, BindingResult bindingResult);

    AuthAPIResponse requestCloudCert(RequestCloudCertRequest requestCloudCert, BindingResult bindingResult);

    AuthAPIResponse signCloudCert(SignCloudCertRequest signCloudCertRequest, BindingResult bindingResult);

    AuthAPIResponse downloadCert(DownloadCertRequest downloadCertRequest, BindingResult bindingResult);

    AuthAPIResponse cloudSignature(CloudSignRequest cloudSignRequest, BindingResult bindingResult);

    AuthAPIResponse getCertInfo(GetCertInfoRequest getCertInfoRequest, BindingResult bindingResult);

    AuthAPIResponse revokeCert(RevokeCertRequest revokeCertRequest, BindingResult bindingResult);

    AuthAPIResponse updateCert(UpdateCertRequest updateCertRequest, BindingResult bindingResult);

    AuthAPIResponse parseCert(String cert);
}
