package com.xiaoleitech.authapi.toolbox.controller;

import com.xiaoleitech.authapi.toolbox.bean.UtilsResponse;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.auxiliary.authentication.AuthenticationHelper;
import com.xiaoleitech.authapi.global.cipher.base64.Base64Coding;
import com.xiaoleitech.authapi.global.cipher.symmetric.SymmetricAlgorithm;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;

@RestController
public class TB_cipherAPI {
    private final UtilsResponse utilsResponse;
    private final SystemErrorResponse systemErrorResponse;
    private final SymmetricAlgorithm symmetricAlgorithm;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TB_cipherAPI(UtilsResponse utilsResponse, SystemErrorResponse systemErrorResponse, SymmetricAlgorithm symmetricAlgorithm, AuthenticationHelper authenticationHelper) {
        this.utilsResponse = utilsResponse;
        this.systemErrorResponse = systemErrorResponse;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.authenticationHelper = authenticationHelper;
    }

    /**
     * base64 编码和解码
     *
     * @param source 输入数据，待计算数据
     * @param mode   模式：1表示编码；2表示解码。
     * @return 返回数据例子
     * {
     * "error_code": 0,
     * "error_message": "OK",
     * "response": "eefe22f887abd4e84b1386edaa1abca0b6edd8227c9defe83b667f97d64c0c47"
     * }
     */
    @RequestMapping(value = "/toolbox/cipher/base64", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse base64Decoding(@RequestParam("source") String source,
                                   @RequestParam("mode") int mode) {
        if (mode == 1) {
            byte[] sourceBytes = UtilsHelper.hexStringToBytes(source);
            String result = Base64Coding.encode(sourceBytes);
            utilsResponse.setResponse(result);
        } else if (mode == 2) {
            byte[] result = Base64Coding.decode(source);
            utilsResponse.setResponse(UtilsHelper.bytesToHexString(result));
        } else
            return systemErrorResponse.notImplemented();

        systemErrorResponse.fill(utilsResponse, ErrorCodeEnum.ERROR_OK);
        return utilsResponse;
    }

    /**
     * AES 计算，目前仅支持 AES 加密和解密
     *
     * @param inputFormat 输入数据格式，包括source和key。为空或0表示HexString；1表示原生字符串；其他待实现。
     * @param source      输入数据，待计算数据
     * @param key         AES密钥
     * @param bits        算法位数
     * @param mode        1表示加密，2表示解密
     * @return 返回数据例子
     * {
     * "error_code": 0,
     * "error_message": "OK",
     * "response": "7fe8b4022a557afcde5a7bf3c749d9cee9ec0338481c296d5831f2020065525b"
     * }
     */
    @RequestMapping(value = "/toolbox/cipher/AES", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse aesCalculate(@RequestParam("input_format") String inputFormat,
                                 @RequestParam("source") String source,
                                 @RequestParam("key") String key,
                                 @RequestParam("bits") int bits,
                                 @RequestParam("mode") int mode) {
        switch (mode) {
            case Cipher.ENCRYPT_MODE:
            case Cipher.DECRYPT_MODE:
                break;
            default:
                return systemErrorResponse.notImplemented();
        }

        int inputDataFormat = 0;
        // inputFormat参数为空时，默认为：0 -- HexString
        if ((inputFormat != null) && !inputFormat.isEmpty()) {
            try {
                inputDataFormat = Integer.valueOf(inputFormat);
            } catch (Exception e) {
                return systemErrorResponse.notImplemented();
            }
        }

        byte[] result = {};

        switch (inputDataFormat) {
            case 0:     // HexString
                result = symmetricAlgorithm.doAes(UtilsHelper.hexStringToBytes(source),
                        UtilsHelper.hexStringToBytes(key), bits, mode);
                break;
            case 1:     // 原生字符串
                result = symmetricAlgorithm.doAes(source.getBytes(), key.getBytes(), bits, mode);
                break;
            case 2:     // TODO: base64编码
            default:
                return systemErrorResponse.notImplemented();
        }

        utilsResponse.setResponse(UtilsHelper.bytesToHexString(result));
        systemErrorResponse.fill(utilsResponse, ErrorCodeEnum.ERROR_OK);
        return utilsResponse;
    }

}
