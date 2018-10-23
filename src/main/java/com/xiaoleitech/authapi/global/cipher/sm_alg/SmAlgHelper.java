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
     * @param inputData  需计算数据的base64编码
     * @param hashResult SM3散列结果的base64编码
     * @return 错误代码，0表示成功
     */
    public ErrorCodeEnum sm3Hash(String inputData, StringBuffer hashResult) {
        return smServiceCallHelper.sm3(inputData, hashResult);
    }

    /**
     * 生成 SM2 私钥
     *
     * @param privateKey SM2 私钥
     * @return 错误代码，0表示成功
     */
    public ErrorCodeEnum generatePrivKey(StringBuffer privateKey) {
        return smServiceCallHelper.generatePrivKey(privateKey);
    }

    public ErrorCodeEnum getP1(String d2, StringBuffer p1) {
        return smServiceCallHelper.getP1(d2, p1);
    }

    public ErrorCodeEnum getPubKey(String p1, StringBuffer pubKey, StringBuffer d2) {
        return smServiceCallHelper.getPubKey(p1, pubKey, d2);
    }

    public ErrorCodeEnum coSign(String inE, String inQ1, String inD2,
                                StringBuffer outS2, StringBuffer outS3, StringBuffer outR) {
        return smServiceCallHelper.coSign(inE, inQ1, inD2, outS2, outS3, outR);
    }

    public ErrorCodeEnum requestP10(String inDN, String inPubKey, String inHashAlg, String inExtension,
                                    StringBuffer outE, StringBuffer outQ1, StringBuffer outK1,
                                    StringBuffer outP10) {
        return smServiceCallHelper.requestP10(inDN, inPubKey, inHashAlg, inExtension, outE, outQ1, outK1, outP10);
    }

    public ErrorCodeEnum generateP10(String inK1, String inP10, String inD2,
                                     String inS2, String inS3, String inR,
                                     StringBuffer outP10) {
        return smServiceCallHelper.generateP10(inK1, inP10, inD2, inS2, inS3, inR, outP10);
    }

    public ErrorCodeEnum getCert(String inP10,
                                 StringBuffer outCert) {
        return smServiceCallHelper.getCert(inP10, outCert);
    }

    public ErrorCodeEnum verifySignature(String inPlainText, String inSignature, String inCert,
                                         int[] outVerifySignatureResult) {
        return smServiceCallHelper.verifySignature(inPlainText, inSignature, inCert, outVerifySignatureResult);
    }

    public ErrorCodeEnum parseCert(String inCert, JSONArray outCertProperties) {
        return smServiceCallHelper.parseCert(inCert, outCertProperties);
    }

}

