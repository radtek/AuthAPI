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

    private String checkCallResult(String result) {
        JSONObject jsonOutput = JSONObject.parseObject(result);
        int error = jsonOutput.getIntValue("errorCode");
        if (error != 0) {
            jsonOutput.put("error_code", ErrorCodeEnum.ERROR_INTERNAL_ERROR.getCode());
            jsonOutput.put("error_message", ErrorCodeEnum.ERROR_INTERNAL_ERROR.getMsg());
            return jsonOutput.toJSONString();
        }

        jsonOutput.put("error_code", ErrorCodeEnum.ERROR_OK.getCode());
        jsonOutput.put("error_message", ErrorCodeEnum.ERROR_OK.getMsg());
        jsonOutput.remove("errorCode");
        return jsonOutput.toJSONString();

    }

    private String postForResult(String url, String request) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
        String response = responseEntity.getBody();
        return response;
    }

    public String callMethod(String method, String inParams) {
        String output = postForResult(SM3_SERVER_PATH + method, inParams);
        return checkCallResult(output);
    }

    public String hello() {
        return postForResult(SM3_SERVER_PATH + "areuok", "");
    }

    public String sm3(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "sm3", inParams);
        return checkCallResult(output);
    }

    public String generatePrivKey() {
        String output = postForResult(SM3_SERVER_PATH + "generatePriKey", "");
        return checkCallResult(output);
    }

    public String getP1(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "generateP1", inParams);
        return checkCallResult(output);
    }

    public String getPubKey(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "generatePubKey", inParams);
        return checkCallResult(output);
    }

    public String coSign(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "coSign", inParams);
        return checkCallResult(output);
    }

    public String requestP10(String inParams) {

        String output = postForResult(SM3_SERVER_PATH + "requestP10", inParams);

        return checkCallResult(output);
    }

    public String generateP10(String inParams) {

        String output = postForResult(SM3_SERVER_PATH + "generateP10", inParams);

        return checkCallResult(output);
    }

    public String getCert(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "getCert", inParams);

        return checkCallResult(output);
    }

    public String verifySignature(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "verifySignature", inParams);

        return checkCallResult(output);
    }

    public String parseCert(String inParams) {
        String output = postForResult(SM3_SERVER_PATH + "parseCert", inParams);

        return checkCallResult(output);
    }

}
