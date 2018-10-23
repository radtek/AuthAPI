package com.xiaoleitech.authapi.global.restcall;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SMServiceCallHelper {
    private final RestTemplate restTemplate;
    private static String SM3_SERVER_PATH = "http://192.168.1.9:8777/api/v1/";
    Logger logger = LoggerFactory.getLogger(SMServiceCallHelper.class);

    @Autowired
    public SMServiceCallHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String postForResult(String url, String request) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        String response = responseEntity.getBody();
        return response;
    }

    public String hello() {
        return postForResult(SM3_SERVER_PATH + "areuok", "");
    }

    public ErrorCodeEnum sm3(String input, StringBuffer hashResult) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("inputData", input);

        String output = postForResult(SM3_SERVER_PATH + "sm3", jsonInput.toJSONString());
        logger.info("sm3 返回：" + output);
//        String output = "{\n" +
//                "\t\"errorCode\" : 0,\n" +
//                "\t\"hash\" : \"JJS1AYdHgaEXUM+/tAq+s1ORVinbrJhAEtuAD+uD0xU=\"\n" +
//                "}";

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String data = jsonOutput.getString("hash");
        if (data.isEmpty())
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        hashResult.append(data);

        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum generatePrivKey(StringBuffer privateKey) {
        String output = postForResult(SM3_SERVER_PATH + "generatePriKey", "");
//        String output = "{\n" +
//                "\t\"errorCode\":0, \"D2\":\"aaQvc/8ZIE0NojyfxfiLcADCcCvwS7LqI0RIjGj/vE8=\"\n" +
//                "}\n";

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String data = jsonOutput.getString("D2");
        if (data.isEmpty())
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        privateKey.append(data);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum getP1(String d2, StringBuffer p1) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("D", d2);

        String output = postForResult(SM3_SERVER_PATH + "generateP1", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String data = jsonOutput.getString("P1");
        if (data.isEmpty())
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        p1.append(data);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum getPubKey(String p1, StringBuffer pubKey, StringBuffer d2) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("P1", p1);

        String output = postForResult(SM3_SERVER_PATH + "generatePubKey", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String dataPubKey = jsonOutput.getString("PubKey");
        String dataD2 = jsonOutput.getString("D2");
        if ((dataPubKey.isEmpty()) || (dataD2.isEmpty()))
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        pubKey.append(dataPubKey);
        d2.append(dataD2);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum coSign(String inE, String inQ1, String inD2,
                                StringBuffer outS2, StringBuffer outS3, StringBuffer outR) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("E", inE);
        jsonInput.put("Q1", inQ1);
        jsonInput.put("D2", inD2);

        String output = postForResult(SM3_SERVER_PATH + "coSign", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String dataS2 = jsonOutput.getString("S2");
        String dataS3 = jsonOutput.getString("S3");
        String dataR = jsonOutput.getString("R");
        if ((dataS2.isEmpty()) || (dataS3.isEmpty()) || (dataR.isEmpty()))
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        outS2.append(dataS2);
        outS3.append(dataS3);
        outR.append(dataR);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum requestP10(String inDN, String inPubKey, String inHashAlg, String inExtension,
                                    StringBuffer outE, StringBuffer outQ1, StringBuffer outK1,
                                    StringBuffer outP10) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("DN", inDN);
        jsonInput.put("PubKey", inPubKey);
        jsonInput.put("hashAlg", inHashAlg);
        jsonInput.put("extension", inExtension);

        String output = postForResult(SM3_SERVER_PATH + "requestP10", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String dataE = jsonOutput.getString("E");
        String dataQ1 = jsonOutput.getString("Q1");
        String dataK1 = jsonOutput.getString("k1");
        String dataP10 = jsonOutput.getString("P10");
        if ((dataE.isEmpty()) || (dataQ1.isEmpty()) || (dataK1.isEmpty()) || (dataP10.isEmpty()))
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        outE.append(dataE);
        outQ1.append(dataQ1);
        outK1.append(dataK1);
        outP10.append(dataP10);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum generateP10(String inK1, String inP10, String inD2,
                                     String inS2, String inS3, String inR,
                                     StringBuffer outP10) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("k1", inK1);
        jsonInput.put("P10", inP10);
        jsonInput.put("D2", inD2);
        jsonInput.put("S2", inS2);
        jsonInput.put("S3", inS3);
        jsonInput.put("R", inR);

        String output = postForResult(SM3_SERVER_PATH + "generateP10", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String dataP10 = jsonOutput.getString("P10");
        if (dataP10.isEmpty())
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        outP10.append(dataP10);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum getCert(String inP10,
                                 StringBuffer outCert) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("P10", inP10);

        String output = postForResult(SM3_SERVER_PATH + "getCert", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        String dataCert = jsonOutput.getString("Cert");
        if (dataCert.isEmpty())
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        outCert.append(dataCert);
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum verifySignature(String inPlainText, String inSignature, String inCert,
                                         int[] outVerifySignatureResult) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Plaintext", inPlainText);
        jsonInput.put("Signature", inSignature);
        jsonInput.put("Cert", inCert);

        String output = postForResult(SM3_SERVER_PATH + "verifySignature", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        outVerifySignatureResult[0] = jsonOutput.getInteger("verifySignatureResult");
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum parseCert(String inCert, JSONArray outCertProperties) {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("Cert", inCert);

        String output = postForResult(SM3_SERVER_PATH + "parseCert", jsonInput.toJSONString());

        JSONObject jsonOutput = JSONObject.parseObject(output);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        jsonOutput.remove("errorCode");
        outCertProperties.add(jsonOutput);
        return ErrorCodeEnum.ERROR_OK;
    }
}
