package com.xiaoleitech.authapi.controller.utilities;

import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.cipher.Base64Coding;
import com.xiaoleitech.authapi.helper.cipher.SymmetricAlgorithm;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UTILS_cipherAPI {
    private final UtilsResponse utilsResponse;
    private final SystemErrorResponse systemErrorResponse;
    private final SymmetricAlgorithm symmetricAlgorithm;

    @Autowired
    public UTILS_cipherAPI(UtilsResponse utilsResponse, SystemErrorResponse systemErrorResponse, SymmetricAlgorithm symmetricAlgorithm) {
        this.utilsResponse = utilsResponse;
        this.systemErrorResponse = systemErrorResponse;
        this.symmetricAlgorithm = symmetricAlgorithm;
    }

    @RequestMapping(value = "/utils/cipher/base64", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse base64Calculate(@RequestParam("source") String source,
                                    @RequestParam("mode") int mode) {
        if (mode == 1) {
            byte[] sourceBytes = UtilsHelper.hexStringToBytes(source);
            String result = Base64Coding.encode(sourceBytes);
            utilsResponse.setResponse(result);
        } else if (mode == 2) {
            byte[] result = Base64Coding.decode(source);
            utilsResponse.setResponse(UtilsHelper.bytesToHexString(result));
        } else
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);

        systemErrorResponse.fillErrorResponse(utilsResponse, ErrorCodeEnum.ERROR_OK);
        return utilsResponse;
    }
//
//    @RequestMapping(value = "/utils/cipher/base64decoding", method = RequestMethod.GET)
//    public @ResponseBody
//    AuthAPIResponse  base64Decoding(@RequestParam("source") String source) {
//        byte[] result = Base64Coding.decode(source);
//        utilsResponse.setResponse(UtilsHelper.bytesToHexString(result));
//        systemErrorResponse.fillErrorResponse(utilsResponse, ErrorCodeEnum.ERROR_OK);
//
//        return utilsResponse;
//    }

    @RequestMapping(value = "/utils/cipher/AES", method = RequestMethod.GET)
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
                return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
        }

        byte[] result = { };
        if ((inputFormat == null) || inputFormat.isEmpty()) {
            result = symmetricAlgorithm.doAes(UtilsHelper.hexStringToBytes(source),
                    UtilsHelper.hexStringToBytes(key), bits, mode);
        } else {
            result = symmetricAlgorithm.doAes(source.getBytes(), key.getBytes(), bits, mode);
        }

        utilsResponse.setResponse(UtilsHelper.bytesToHexString(result));
        systemErrorResponse.fillErrorResponse(utilsResponse, ErrorCodeEnum.ERROR_OK);
        return utilsResponse;
    }

    @RequestMapping(value = "/utils/common/generate_uuid", method = RequestMethod.GET)
    public @ResponseBody
    List<String> generateUuids(@RequestParam("count") int count) {
        List<String> uuidList = new ArrayList();

        for (int i=0; i<count; i++) {
            String uuid = UtilsHelper.generateUuid();
            uuidList.add(uuid);
        }
        return uuidList;
    }

}
