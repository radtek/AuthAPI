package com.xiaoleitech.authapi.global.cipher.sm_alg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.restcall.SMServiceCallHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmAlgHelper {
    private final SMServiceCallHelper smServiceCallHelper;

    @Autowired
    public SmAlgHelper(SMServiceCallHelper smServiceCallHelper) {
        this.smServiceCallHelper = smServiceCallHelper;
    }

    /**
     * 对 inputData 计算SM3散列值，输入输出数据格式均为base64编码
     *
     * @param inParams  JSON字串，需包含 "inputData" 键值
     * @return JSON字串，包含错误码和"hash"键值
     */
    public String sm3Hash(String inParams) {
        return smServiceCallHelper.sm3(inParams);
    }

    /**
     * 生成 SM2 私钥，输出数据格式为base64编码
     *
     * @return JSON字串，包含错误码和"D2"键值
     */
    public String generatePrivKey() {
        return smServiceCallHelper.generatePrivKey();
    }

    /**
     * 由D2获取P1，输入输出数据格式均为base64编码
     * @param inParams JSON字串，需包含 "D" 键值
     * @return  JSON字串，包含错误码和"P1"键值
     */
    public String getP1(String inParams) {
        return smServiceCallHelper.getP1(inParams);
    }

    /**
     * 生成公钥
     * @param inParams  JSON字串，需包含 "P1" 键值
     * @return JSON字串，包含错误码和"PubKey"和"D2"键值
     */
    public String getPubKey(String inParams) {
        return smServiceCallHelper.getPubKey(inParams);
    }

    /**
     * 组合签名
     * @param inParams JSON字串，需包含 "E"、"Q1"、"D2" 键值
     * @return  JSON字串，包含错误码和"S2"、"S3"、"R"键值
     */
    public String coSign(String inParams) {
        return smServiceCallHelper.coSign(inParams);
    }

    /**
     * 请求P10
     * @param inParams JSON字串，需包含 "DN"、"PubKey"、"hashAlg" 、"extension" 键值
     * @return  JSON字串，包含错误码和"Q1"、"E"、"k1"、"P10"键值
     */
    public String requestP10(String inParams) {
        return smServiceCallHelper.requestP10(inParams);
    }

    /**
     * 生成P10
     * @param inParams JSON字串，需包含 "k1"、"P10"、"D2" 、"S2" 、"S3" 、"R"键值
     * @return JSON字串，包含错误码和"P10"键值
     */
    public String generateP10(String inParams) {
        return smServiceCallHelper.generateP10(inParams);
    }

    /**
     * 获取证书
     * @param inParams  JSON字串，需包含"P10"键值
     * @return JSON字串，包含错误码和"Cert"键值
     */
    public String getCert(String inParams) {
        return smServiceCallHelper.getCert(inParams);
    }

    /**
     * 验证签名
     * @param inParams JSON字串，需包含"Plaintext"、"Plaintext"、"Cert"键值
     * @return   JSON字串，包含错误码和"verifySignatureResult"键值
     */
    public String verifySignature(String inParams) {
        return smServiceCallHelper.verifySignature(inParams);
    }

    /**
     * 解析证书
     * @param inParams   JSON字串，需包含"Cert"键值
     * @return  JSON字串，包含错误码和证书的各个属性键值
     */
    public String parseCert(String inParams) {
        return smServiceCallHelper.parseCert(inParams);
    }

}

