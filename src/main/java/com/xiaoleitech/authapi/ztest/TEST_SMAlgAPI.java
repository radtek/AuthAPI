package com.xiaoleitech.authapi.ztest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.ca_service.CAProvider;
import com.xiaoleitech.authapi.global.cipher.sm_alg.SmAlgHelper;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import com.xiaoleitech.authapi.global.restcall.SMServiceCallHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class TEST_SMAlgAPI {
    private final Logger logger = LoggerFactory.getLogger(TEST_SMAlgAPI.class);

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
    Object callSM3(HttpServletRequest request) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("inputData", request.getParameter("inputData"));

        String outResult = smAlgHelper.sm3Hash(jsonInput.toJSONString());
        logger.info("SM3 result: " + outResult);

        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/generate_privkey", method = RequestMethod.GET)
    public @ResponseBody
    Object callGeneratePrivateKey() {
        String outResult = smAlgHelper.generatePrivKey();
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/getPublicParam", method = RequestMethod.POST)
    public @ResponseBody
    Object callGetPublicParam(@RequestParam("D1") String d1) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("D1", d1);

        String outResult = smAlgHelper.getPublicParam(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/coSignInitWithMessage", method = RequestMethod.POST)
    public @ResponseBody
    Object callCoSignInitWithMessage(@RequestParam("hashAlg") int hashAlg,
                                     @RequestParam("Plaintext") String plainText,
                                     @RequestParam("Z") String coSignZ) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("hashAlg", hashAlg);
        jsonInput.put("Plaintext", plainText);
        jsonInput.put("Z", coSignZ);

        String outResult = smAlgHelper.coSignInitWithMessage(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/coSignInitwithDigest", method = RequestMethod.POST)
    public @ResponseBody
    Object callCoSignInitWithDigest(@RequestParam("hash") String hash) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("hash", hash);

        String outResult = smAlgHelper.coSignInitWithDigest(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/coSignFinal", method = RequestMethod.POST)
    public @ResponseBody
    Object callCoSignFinal(@RequestParam("k1") String sm2K1,
                           @RequestParam("D1") String sm2D1,
                           @RequestParam("S2") String sm2S2,
                           @RequestParam("S3") String sm2S3,
                           @RequestParam("R") String sm2R) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("k1", sm2K1);
        jsonInput.put("D1", sm2D1);
        jsonInput.put("S2", sm2S2);
        jsonInput.put("S3", sm2S3);
        jsonInput.put("R", sm2R);

        String outResult = smAlgHelper.coSignFinal(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/getPubKeyFromCert", method = RequestMethod.POST)
    public @ResponseBody
    Object callGetPubKeyFromCert(@RequestParam("Cert") String cert) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Cert", cert);

        String outResult = smAlgHelper.getPubKeyFromCert(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/calcZ", method = RequestMethod.POST)
    public @ResponseBody
    Object callCalcZ(@RequestParam("PubKey") String pubKey,
                     @RequestParam("ID") String id) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("PubKey", pubKey);
        jsonInput.put("ID", id);

        String outResult = smAlgHelper.calcZ(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

        @RequestMapping(value = "/test/smalg/get_p1", method = RequestMethod.POST)
    public @ResponseBody
    Object callGetP1(String d) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("D", d);

        String outResult = smAlgHelper.getP1(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/get_pubkey", method = RequestMethod.POST)
    public @ResponseBody
    Object callGetPubKey(String p1) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("P1", p1);

        String outResult = smAlgHelper.getPubKey(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/coSign", method = RequestMethod.POST)
    public @ResponseBody
    Object callCoSign(@RequestParam("E") String requestE,
                      @RequestParam("Q1") String requestQ1,
                      @RequestParam("D2") String requestD2) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("E", requestE);
        jsonInput.put("Q1", requestQ1);
        jsonInput.put("D2", requestD2);

        String outResult = smAlgHelper.coSign(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/requestP10", method = RequestMethod.POST)
    public @ResponseBody
    Object requestP10(@RequestParam("DN") String requestDN,
                      @RequestParam("PubKey") String requestPubKey,
                      @RequestParam("hashAlg") String hashAlg,
                      @RequestParam("extension") String extension) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("DN", requestDN);
        jsonInput.put("PubKey", requestPubKey);
        jsonInput.put("hashAlg", hashAlg);
        jsonInput.put("extension", extension);

        String outResult = smAlgHelper.requestP10(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/sayhello", method = RequestMethod.GET)
    public String sayHello(@RequestParam("ca_id") int caId) {
        CAProvider caProvider = getCaProvider(caId);

        return caProvider.callSayHello();
    }

    @RequestMapping(value = "/test/smalg/requestP10_xlca", method = RequestMethod.POST)
    public @ResponseBody
    Object requestP10Xlca(@RequestParam("DN") String requestDN,
                      @RequestParam("PubKey") String requestPubKey,
                      @RequestParam("hashAlg") String hashAlg,
                      @RequestParam("extension") String extension) {

        JSONObject jsonInput = new JSONObject();
        JSONObject jsonResult = new JSONObject();

        // 获取DN、hsah算法、以及扩展字段
        jsonInput.put("DN", requestDN);
        jsonInput.put("PubKey", requestPubKey);
        jsonInput.put("HashAlg", hashAlg);
        jsonInput.put("Extension", extension);

        // 获取 CA-Provider
        CAProvider caProvider = getDefaultCaProvider();

        // 向XLCA请求P10
        jsonResult = caProvider.requestP10(jsonInput);
        if (jsonResult.getIntValue("error_code") != ErrorCodeEnum.ERROR_OK.getCode()) {
            return jsonResult;
        }

        // 添加公钥
        jsonResult.put("P", requestPubKey);
        // 转换证书键
        jsonResult.put("x509req", jsonResult.getString("P10"));
        jsonResult.remove("P10");

        // 添加成功代码
        jsonResult.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonResult.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());
        return jsonResult;
    }

    private CAProvider getDefaultCaProvider() {
        return getCaProvider(0);
    }

    private CAProvider getCaProvider(int caId) {
        return new CAProvider(caId);
    }

    @RequestMapping(value = "/test/smalg/generateP10", method = RequestMethod.POST)
    public @ResponseBody
    Object generateP10(@RequestParam("k1") String requestK1,
                       @RequestParam("P10") String requestP10,
                       @RequestParam("D2") String requestD2,
                       @RequestParam("S2") String requestS2,
                       @RequestParam("S3") String requestS3,
                       @RequestParam("R") String requestR) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("k1", requestK1);
        jsonInput.put("P10", requestP10);
        jsonInput.put("D2", requestD2);
        jsonInput.put("S2", requestS2);
        jsonInput.put("S3", requestS3);
        jsonInput.put("R", requestR);
        StringBuffer outP10 = new StringBuffer("");

        String outResult = smAlgHelper.generateP10(jsonInput.toJSONString());

        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/getCert", method = RequestMethod.POST)
    public @ResponseBody
    Object getCert(@RequestParam("P10") String requestP10) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("P10", requestP10);

        String outResult = smAlgHelper.getCert(jsonInput.toJSONString());

        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/verifySignature", method = RequestMethod.POST)
    public @ResponseBody
    Object verifySignature(@RequestParam("Plaintext") String requestPlainText,
                           @RequestParam("Signature") String requestSignature,
                           @RequestParam("Cert") String requestCert) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Plaintext", requestPlainText);
        jsonInput.put("Signature", requestSignature);
        jsonInput.put("Cert", requestCert);

        String outResult = smAlgHelper.verifySignature(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

    @RequestMapping(value = "/test/smalg/parseCert", method = RequestMethod.POST)
    public @ResponseBody
    Object parseCert(@RequestParam("Cert") String requestCert) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Cert", requestCert);

        String outResult = smAlgHelper.parseCert(jsonInput.toJSONString());
        return JSONObject.parseObject(outResult);
    }

}
