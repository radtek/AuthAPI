package com.xiaoleitech.authapi.ztest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.ca_service.CAProvider;
import com.xiaoleitech.authapi.global.cipher.sm_alg.SmAlgHelper;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import com.xiaoleitech.authapi.global.restcall.SMServiceCallHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class TEST_SMAlgAPI {
    private final SMServiceCallHelper smServiceCallHelper;
    private final CallSMAlgService callSMAlgService;
    private final SmAlgHelper smAlgHelper;
    private final TestResponse testResponse;
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public TEST_SMAlgAPI(SMServiceCallHelper smServiceCallHelper, CallSMAlgService callSMAlgService, SmAlgHelper smAlgHelper, TestResponse testResponse, SystemErrorResponse systemErrorResponse) {
        this.smServiceCallHelper = smServiceCallHelper;
        this.callSMAlgService = callSMAlgService;
        this.smAlgHelper = smAlgHelper;
        this.testResponse = testResponse;
        this.systemErrorResponse = systemErrorResponse;
    }

    @RequestMapping(value = "/test/smalg/hello", method = RequestMethod.POST)
    public String callHello() {
        return smServiceCallHelper.hello();
    }

    @RequestMapping(value = "/test/smalg/sm3", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse callSM3(HttpServletRequest request) {
        String input = request.getParameter("inputData");

        StringBuffer hash = new StringBuffer("");
        ErrorCodeEnum errorCode = smAlgHelper.sm3Hash(input, hash);
        testResponse.setResult(hash.toString());
        systemErrorResponse.fill(testResponse, errorCode);

        return testResponse;
    }

    @RequestMapping(value = "/test/smalg/generate_privkey", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse callGeneratePrivateKey() {
        StringBuffer privateKey = new StringBuffer("");
        ErrorCodeEnum errorCode = smAlgHelper.generatePrivKey(privateKey);
        testResponse.setResult(privateKey.toString());
        systemErrorResponse.fill(testResponse, errorCode);

        return testResponse;
    }

    @RequestMapping(value = "/test/smalg/get_p1", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse callGetP1(String d) {
        StringBuffer p1 = new StringBuffer("");
        ErrorCodeEnum errorCode = smAlgHelper.getP1(d, p1);
        testResponse.setResult(p1.toString());
        systemErrorResponse.fill(testResponse, errorCode);

        return testResponse;
    }

    @RequestMapping(value = "/test/smalg/get_pubkey", method = RequestMethod.POST)
    public @ResponseBody
    Object callGetPubKey(String p1) {
        StringBuffer pubKey = new StringBuffer("");
        StringBuffer d2 = new StringBuffer("");

        ErrorCodeEnum errorCode = smAlgHelper.getPubKey(p1, pubKey, d2);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("pub-key", pubKey.toString());
        jsonObject.put("d2", d2.toString());

        return jsonObject;
    }

    @RequestMapping(value = "/test/smalg/coSign", method = RequestMethod.POST)
    public @ResponseBody
    Object callCoSign(@RequestParam("E") String requestE,
                      @RequestParam("Q1") String requestQ1,
                      @RequestParam("D2") String requestD2) {
        StringBuffer outS2 = new StringBuffer("");
        StringBuffer outS3 = new StringBuffer("");
        StringBuffer outR = new StringBuffer("");

        ErrorCodeEnum errorCode = smAlgHelper.coSign(requestE, requestQ1, requestD2, outS2, outS3, outR);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("S2", outS2.toString());
        jsonObject.put("S3", outS3.toString());
        jsonObject.put("R", outR.toString());

        return jsonObject;
    }

    @RequestMapping(value = "/test/smalg/requestP10", method = RequestMethod.POST)
    public @ResponseBody
    Object requestP10(@RequestParam("DN") String requestDN,
                      @RequestParam("PubKey") String requestPubKey,
                      @RequestParam("hashAlg") String hashAlg,
                      @RequestParam("extension") String extension) {
        StringBuffer outE = new StringBuffer("");
        StringBuffer outQ1 = new StringBuffer("");
        StringBuffer outK1 = new StringBuffer("");
        StringBuffer outP10 = new StringBuffer("");

        ErrorCodeEnum errorCode = smAlgHelper.requestP10(requestDN, requestPubKey, hashAlg, extension,
                outE, outQ1, outK1, outP10);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("E", outE.toString());
        jsonObject.put("Q1", outQ1.toString());
        jsonObject.put("K1", outK1.toString());
        jsonObject.put("P10", outP10.toString());

        return jsonObject;
    }

    @RequestMapping(value = "/test/smalg/sayhello", method = RequestMethod.GET)
    public String sayHello() {
        CAProvider caProvider = getCaProvider();

        return caProvider.callSayHello();
    }

    @RequestMapping(value = "/test/smalg/requestP10_xlca", method = RequestMethod.POST)
    public @ResponseBody
    Object requestP10Xlca(@RequestParam("DN") String requestDN,
                      @RequestParam("PubKey") String requestPubKey,
                      @RequestParam("hashAlg") String hashAlg,
                      @RequestParam("extension") String extension) {

        CAProvider caProvider = getCaProvider();

        StringBuffer outE = new StringBuffer("");
        StringBuffer outQ1 = new StringBuffer("");
        StringBuffer outK1 = new StringBuffer("");
        StringBuffer outP10 = new StringBuffer("");
        ErrorCodeEnum errorCode = caProvider.requestP10(requestDN, requestPubKey, hashAlg, extension, outE, outQ1, outK1, outP10);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("E", outE.toString());
        jsonObject.put("Q1", outQ1.toString());
        jsonObject.put("K1", outK1.toString());
        jsonObject.put("P10", outP10.toString());

        return jsonObject;
//        return caProvider.getModuleFilePath();
//        StringBuffer outE = new StringBuffer("");
//        StringBuffer outQ1 = new StringBuffer("");
//        StringBuffer outK1 = new StringBuffer("");
//        StringBuffer outP10 = new StringBuffer("");
//
//        ErrorCodeEnum errorCode = smAlgHelper.requestP10(requestDN, requestPubKey, hashAlg, extension,
//                outE, outQ1, outK1, outP10);
    }

    private CAProvider getCaProvider() {
        return new CAProvider(0);
    }

    @RequestMapping(value = "/test/smalg/generateP10", method = RequestMethod.POST)
    public @ResponseBody
    Object generateP10(@RequestParam("k1") String requestK1,
                       @RequestParam("P10") String requestP10,
                       @RequestParam("D2") String requestD2,
                       @RequestParam("S2") String requestS2,
                       @RequestParam("S3") String requestS3,
                       @RequestParam("R") String requestR) {
        StringBuffer outP10 = new StringBuffer("");

        ErrorCodeEnum errorCode = smAlgHelper.generateP10(requestK1, requestP10, requestD2,
                requestS2, requestS3, requestR,
                outP10);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("P10", outP10.toString());

        return jsonObject;
    }

    @RequestMapping(value = "/test/smalg/getCert", method = RequestMethod.POST)
    public @ResponseBody
    Object getCert(@RequestParam("P10") String requestP10) {
        StringBuffer outCert = new StringBuffer("");

        ErrorCodeEnum errorCode = smAlgHelper.getCert(requestP10, outCert);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("Cert", outCert.toString());

        return jsonObject;
    }

    @RequestMapping(value = "/test/smalg/verifySignature", method = RequestMethod.POST)
    public @ResponseBody
    Object verifySignature(@RequestParam("Plaintext") String requestPlainText,
                           @RequestParam("Signature") String requestSignature,
                           @RequestParam("Cert") String requestCert) {
        int[] verifyResult = {-1};
        ErrorCodeEnum errorCode = smAlgHelper.verifySignature(requestPlainText, requestSignature, requestCert, verifyResult);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", errorCode.getCode());
        jsonObject.put("error_message", errorCode.getMsg());
        jsonObject.put("verifySignatureResult", verifyResult[0]);

        return jsonObject;
    }

    @RequestMapping(value = "/test/smalg/parseCert", method = RequestMethod.POST)
    public @ResponseBody
    Object parseCert(@RequestParam("Cert") String requestCert) {
        JSONArray jsonArray = new JSONArray();

        ErrorCodeEnum errorCode = smAlgHelper.parseCert(requestCert, jsonArray);

        JSONObject jsonOutput = (JSONObject) jsonArray.get(0);
        jsonOutput.put("error_code", errorCode.getCode());
        jsonOutput.put("error_message", errorCode.getMsg());

        return jsonOutput;
    }

}
