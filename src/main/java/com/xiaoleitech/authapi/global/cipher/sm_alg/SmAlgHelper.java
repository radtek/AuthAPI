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

    /** 已禁用
     * 由D2获取P1，输入输出数据格式均为base64编码
     * @param inParams JSON字串，需包含 "D" 键值
     * @return  JSON字串，包含错误码和"P1"键值
     */
    public String getP1(String inParams) {
        return smServiceCallHelper.getP1(inParams);
    }

    /**
     * 由D1获取P1，输入输出数据格式均为base64编码
     * @param inParams JSON字串，需包含 "D1" 键值
     *                 {“D1”:”7IIFRUJ8XAdJgJiKxZ0HDuyCBUVCfFwHSYCYisWdBw4=”}
     * @return JSON字串，包含错误码和"P1"键值(base64编码)
     *      {	"P1" : "BDf2tskHC0Yq6CPZ680k9cDQjRkbgt1Zypo+VlgdNDm7r1n1/7ucPe0YkXLrhbLUQOS5wqDQZTtG13ctTdvbxH4=",
     * 	        "errorCode" : 0
     *      }
     */
    public String getPublicParam(String inParams) {
        return smServiceCallHelper.callMethod("getPublicParam", inParams);
    }

    /**
     * 初始化签名--原文与Z值，输入输出数据格式均为base64编码
     * @param inParams JSON字符串，包含"hashAlg"、"Plaintext"、"Z"
     *                 {“hashAlg”:0, “Plaintext”:”MTIzNDU2Nzg=”, “Z”:”7IIFRUJ8XAdJgJiKxZ0HDiml1KiwrlbJqf3F85zOeow=”}
     * @return JSON字串，包含错误码和"E"、"Q1"、"k1"键值
     *      {
     * 	        "E" : "rKfv6Prv0Q9XlQX296XQmr9OBn3l9DFA2ufamF7cZtg=",
     * 	        "Q1" : "BBGY4jO9Kkq7OjMfkOnxE32rkXVHNpCwsmvf3Fu/jk06L//7vg6P/7uuID+BE5sVLHqsIjEa0AU+Mu7HMRXO7pI=",
     * 	        "errorCode" : 0,
     * 	        "k1" : "yXxAoolZpuL1/UMD5u1zfKp6Qt5sekUuFOHVoe6UatA="
     *      }
     */
    public String coSignInitWithMessage(String inParams) {
        return smServiceCallHelper.callMethod("coSignInitwithMessage", inParams);
    }

    /**
     * 初始化签名--杂凑值，输入输出数据格式均为base64编码
     * @param inParams  JSON字符串，包含"hash"
     *                  {"hash":"7IIFRUJ8XAdJgJiKxZ0HDiml1KiwrlbJqf3F85zOeow="}
     * @return  JSON字串，包含错误码和"Q1"、"k1"键值
     */
    public String coSignInitWithDigest(String inParams) {
        return smServiceCallHelper.callMethod("coSignInitwithDigest", inParams);
    }

    /**
     * 结束签名，输入输出数据格式均为base64编码
     * @param inParams JSON字符串，包含"k1"、"D1"、"S2"、"S3"、"R"
     *              {
     *                 "k1":"wTs6sRpF5lzFOiv29PYhGQjCdPnrdvJADLz/ZVfqEeM=",
     *                 "D1":"7IIFRUJ8XAdJgJiKxZ0HDuyCBUVCfFwHSYCYisWdBw4=",
     *                 "S2":"71vUoN+HmNk/OIzkOEg8+adGJl8UOoq/M4ntxFz8/KM=",
     *                 "S3":"PAIzei2fN7UNSZ9wtrUFB8DU1V/JXxWvNEHBvjyhmmA=",
     *                 "R":"ztTOIvFZCWp0yBekW5+GM7l/TFqDqjnSJDYUCyv7aW0="
     *              }
     * @return  JSON字串，包含错误码和"Signature"键值
     *      {
     * 	        "Signature" : "MEUCIQDO1M4i8VkJanTIF6Rbn4YzuX9MWoOqOdIkNhQLK/tpbQIgMSsx3A6m9pWLN+hbpGB5y7iEkxCeG8tZL4Xf/g3Z17Y=",
     * 	        "errorCode" : 0
     *      }
     */
    public String coSignFinal(String inParams) {
        return smServiceCallHelper.callMethod("coSignFinal", inParams);
    }

    /**
     * 从证书获取公钥，输入输出数据格式均为base64编码
     * @param inParams JSON字符串，包含"Cert"
     * @return  JSON字串，包含错误码和"PubKey"键值
     *      {
     * 	        "PubKey" : "BBIz0Bk1mkRhLVicVP/V/lA9x2hpFZ5XoJ2Z9atNexEXAoS21Jhxfst8nNuWGet+QCQVhGmYR05uexoGd/3kdso=",
     * 	        "errorCode" : 0
     *      }
     */
    public String getPubKeyFromCert(String inParams) {
        return smServiceCallHelper.callMethod("getPubKeyFromCert", inParams);
    }

    /**
     * 计算Z值，输入输出数据格式均为base64编码
     * @param inParams JSON字符串，包含"PubKey"、"ID"
     *      {
     *          "PubKey":"BBIz0Bk1mkRhLVicVP/V/lA9x2hpFZ5XoJ2Z9atNexEXAoS21Jhxfst8nNuWGet+QCQVhGmYR05uexoGd/3kdso=",
     *          "ID":"MTIzNDU2NzgxMjM0NTY3OA=="
     *      }
     * @return  JSON字串，包含错误码和"Z"键值
     *      {
     * 	        "Z" : "HQ9TAdnL9BY66ymR/CruufQpw4gQpzFvBx3WamSxPls=",
     * 	        "errorCode" : 0
     *      }
     */
    public String calcZ(String inParams) {
        return smServiceCallHelper.callMethod("calcZ", inParams);
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

