package com.xiaoleitech.authapi.cert.controller;

import com.xiaoleitech.authapi.cert.bean.*;
import com.xiaoleitech.authapi.cert.service.CloudSignService;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "手机盾接口控制器", tags = "12-CloudCert")
public class XL_CertAPI {
    private final CloudSignService cloudSignService;

    @Autowired
    public XL_CertAPI(CloudSignService cloudSignService) {
        this.cloudSignService = cloudSignService;
    }

    /**
     * 11.1 获取证书（APP）
     * post https://server/api/req_cert
     *
     * @param applyCertRequest {
     *                         user_id: <user_id>,
     *                         verify_token: <verify_token,
     *                         app_id: <app_id>,
     *                         req_json: <req_json> // base64 string
     *                         }
     * @param bindingResult    数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message,
     * sign_certificate: sign_certificate,
     * exchange_certificate: exchange_certificate,
     * exchange_user_key: exchange_user_key,
     * }
     */
    @RequestMapping(value = "/api/req_cert", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse appRequestCert(@ModelAttribute ApplyCertRequest applyCertRequest, BindingResult bindingResult) {
        return cloudSignService.applyCert(applyCertRequest, bindingResult);
    }

    /**
     * 11.2 签名（APP）
     * post https://server/api/sign_message
     *
     * @param signMessageRequest {
     *                           user_id: <user_id>,
     *                           verify_token: <verify_token>,
     *                           app_id: <app_id>
     *                           alg: // 1: RSA2048+SHA256, 2: SM2SM3
     *                           hash: <hash>, // optional if message exist
     *                           message: <message> // optional if hash exist
     *                           }
     * @param bindingResult      数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message,
     * signed_value: signed_value
     * }
     */
    @RequestMapping(value = "/api/sign_message", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse signMessage(@ModelAttribute SignMessageRequest signMessageRequest, BindingResult bindingResult) {
        return cloudSignService.signMessage(signMessageRequest, bindingResult);
    }

    /**
     * 11.3	验证签名
     * post https://server/api/verify_sign
     *
     * @param verifySignatureRequest {
     *                               signed_value=<p7_signed_value> // base64 encoded
     *                               alg=<alg> // 1: RSA2048, 2: SM2SM3
     *                               hash=<hash> // optional
     *                               message=<message> // optional
     *                               certificate=<certificate> // optional
     *                               cacert=<cacert> // optional
     *                               }
     * @param bindingResult          数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/verify_sign", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse verifySignature(@ModelAttribute VerifySignatureRequest verifySignatureRequest, BindingResult bindingResult) {
        return cloudSignService.verifySignature(verifySignatureRequest, bindingResult);
    }

    /**
     * 12.1 请求证书
     * post https://server/api/request_cloud_cert
     *
     * @param requestCloudCertRequest {
     *                                user_id: <user_id>
     *                                verify_token: <verify_token>
     *                                app_id: <app_id>
     *                                p1: <p1>
     *                                }
     * @param bindingResult           数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message,
     * p: p,
     * e: e,
     * q1: q1
     * }
     */
    @RequestMapping(value = "/api/request_cloud_cert", method = RequestMethod.POST)
    public @ResponseBody
    Object requestCloudCert(@ModelAttribute RequestCloudCertRequest requestCloudCertRequest, BindingResult bindingResult) {
        return cloudSignService.requestCloudCert(requestCloudCertRequest, bindingResult);
    }

    /**
     * 12.2 证书请求签名
     * post https://server/api/sign_cloud_cert_request
     *
     * @param signCloudCertRequest {
     *                             user_id: <user_id>
     *                             verify_token: <verify_token>
     *                             app_id: <app_id>
     *                             r: <r>
     *                             s2: <s2>
     *                             s3: <s3>
     *                             }
     * @param bindingResult        数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/sign_cloud_cert_request", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse signCloudCert(@ModelAttribute SignCloudCertRequest signCloudCertRequest, BindingResult bindingResult) {
        return cloudSignService.signCloudCert(signCloudCertRequest, bindingResult);
    }

    /**
     * 12.3. 获取证书(下载证书)
     * get https://server/api/download_cert?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param downloadCertRequest user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     * @param bindingResult       数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message,
     * [certificate: certificate]
     * }
     */
    @RequestMapping(value = "/api/download_cert", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse downloadCert(@ModelAttribute DownloadCertRequest downloadCertRequest, BindingResult bindingResult) {
        return cloudSignService.downloadCert(downloadCertRequest, bindingResult);
    }

    /**
     * 12.4 请求云签名
     * post https://server/api/cloud_sign
     *
     * @param cloudSignRequest {
     *                         user_id: <user_id>
     *                         verify_token: <verify_token>
     *                         app_id: <app_id>
     *                         e: <e>
     *                         q1: <q1>
     *                         }
     * @param bindingResult    数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message,
     * r: r,
     * s2: s2,
     * s3: s3
     * }
     */
    @RequestMapping(value = "/api/cloud_sign", method = RequestMethod.POST)
    public @ResponseBody
    Object cloudSign(@ModelAttribute CloudSignRequest cloudSignRequest, BindingResult bindingResult) {
        return cloudSignService.cloudSignature(cloudSignRequest, bindingResult);
    }

    /**
     * 12.5 获取证书信息
     * get https://server/api/get_cert_info?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param getCertInfoRequest user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     * @param bindingResult      数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message,
     * cert_info:
     * {
     * [item: <item_info>,]
     * ...
     * }
     * }
     */
    @RequestMapping(value = "/api/get_cert_info", method = RequestMethod.GET)
    public @ResponseBody
    Object getCertInfo(@ModelAttribute GetCertInfoRequest getCertInfoRequest, BindingResult bindingResult) {
        return cloudSignService.getCertInfo(getCertInfoRequest, bindingResult);
    }

    /**
     * 12.6 撤销证书
     * get https://server/api/revoke_cert?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param revokeCertRequest user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     * @param bindingResult     数据绑定结果
     * @return {
     * error_code: error_code,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/revoke_cert", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse revokeCert(@ModelAttribute RevokeCertRequest revokeCertRequest, BindingResult bindingResult) {
        return cloudSignService.revokeCert(revokeCertRequest, bindingResult);
    }

    /**
     * 12.7. 更新证书
     * get https://server/api/update_cert?user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     *
     * @param updateCertRequest user_id=<user_id>&verify_token=<verify_token>&app_id=<app_id>
     * @param bindingResult
     * @return {
     * error_code: error_code,
     * error_message: error_message
     * }
     */
    @RequestMapping(value = "/api/update_cert", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse updateCert(@ModelAttribute UpdateCertRequest updateCertRequest, BindingResult bindingResult) {
        return cloudSignService.updateCert(updateCertRequest, bindingResult);
    }

    /**
     * 12.8 解析证书
     * post https://server/api/parse_cert
     *
     * @param cert 证书 base64 编码
     * @return 举例如下：
     * {
     * "error_message": "OK",
     * "X509v3 Key Usage": "Digital Signature, Non Repudiation",
     * "Issuer": "C=CN,L=Beijing,CN=Xiaoleitech",
     * "PublicKey": "041233D019359A44612D589C54FFD5FE503DC76869159E57A09D99F5AB4D7B11170284B6D498717ECB7C9CDB9619EB7E402415846998474E6E7B1A0677FDE476CA",
     * "X509v3 Extended Key Usage": "TLS Web Server Authentication, TLS Web Client Authentication",
     * "SignatureAlgorithm": "sm2-sm3-sign",
     * "NotAfter": "2028-01-29 05:24:49 GMT",
     * "X509v3 Subject Key Identifier": "3B:C5:6B:FB:D6:BE:1F:E1:4E:4D:2D:72:26:69:07:77:E6:1C:37:B6",
     * "Subject": "O=myO,CN=test",
     * "Serial": "786C617574682153715A1C17E4C18C8E588371A8",
     * "X509v3 Basic Constraints": "CA:FALSE",
     * "Version": "3",
     * "PublicKeyAlgorithm": "id-ecPublicKey",
     * "error_code": 0,
     * "NotBefore": "2018-01-31 05:24:49 GMT"
     * }
     */
    @RequestMapping(value = "/api/parse_cert", method = RequestMethod.POST)
    public @ResponseBody
    Object parseCert(@RequestParam("Cert") String cert) {
        return cloudSignService.parseCert(cert);
    }
}
