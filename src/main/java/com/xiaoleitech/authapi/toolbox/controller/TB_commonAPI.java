package com.xiaoleitech.authapi.toolbox.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TB_commonAPI {

    /**
     * 获取一组GUID
     *
     * @param count GUID的需求数量
     * @return 返回数据例子
     * [
     * "dd83577d-606d-4b26-96ad-2fe7925d6c87",
     * "8f40ae20-e7c8-4287-adc0-e8fe5fe35d4b",
     * "4f71c691-df9e-401f-bf23-f65af24e6db2",
     * "8744a994-6805-4734-8516-a615e6c1e52a",
     * "16e9fb82-be23-43f2-b4d9-bd3f1ddfc76f"
     * ]
     */
    @RequestMapping(value = "/toolbox/common/generate_uuid", method = RequestMethod.GET)
    public @ResponseBody
    List<String> generateUuids(@RequestParam("count") int count) {
        List<String> uuidList = new ArrayList();

        for (int i = 0; i < count; i++) {
            String uuid = UtilsHelper.generateUuid();
            uuidList.add(uuid);
        }
        return uuidList;
    }

    /**
     * 对输入数字返回多进制格式，缺省返回2进制，8进制，10进制和16进制，如extraOutputRadix不为空，则输出中增加指定的进制
     *
     * @param input            输入数字
     * @param inputRadix       input的进制，比如2表示二进制，16表示16进制，为0，则表示默认10进制
     * @param extraOutputRadix 额外的output进制
     * @return
     */
    @RequestMapping(value = "/toolbox/common/radix_convert", method = RequestMethod.GET)
    public @ResponseBody
    Object radixConvert(@RequestParam("input") String input,
                        @RequestParam(value = "input_radix", defaultValue = "10") int inputRadix,
                        @RequestParam(value = "extra_output_radix", defaultValue = "0") int extraOutputRadix) {
        JSONObject jsonObject = new JSONObject();

        if (inputRadix == 0)
            inputRadix = 10;

        if ((inputRadix < 0) || (inputRadix > 10000)) {
            jsonObject.put("error_code", ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getCode());
            jsonObject.put("error_message", ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getMsg());
            return jsonObject;
        }

//        if ((inputRadix != 2) && (inputRadix != 8) && (inputRadix != 10) && (inputRadix != 16)) {
//            jsonObject.put("error_code", ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getCode());
//            jsonObject.put("error_message", ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getMsg());
//            return jsonObject;
//        }

        int inputNumber = Integer.parseInt(input, inputRadix);

        jsonObject.put("BIN", Integer.toString(inputNumber, 2));
        jsonObject.put("OCT", Integer.toString(inputNumber, 8));
        jsonObject.put("DEC", Integer.toString(inputNumber, 10));
        jsonObject.put("HEX", Integer.toString(inputNumber, 16));

        if (extraOutputRadix != 0)
            jsonObject.put("radis-" + extraOutputRadix, Integer.toString(inputNumber, extraOutputRadix));

        return jsonObject;

//        switch (inputType) {
//            case 2:
//                break;
//            case 8:
//                break;
//            case 10:
//                break;
//            case 16:
//                break;
//            default:
//                jsonObject.put("error_code", ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getCode());
//                jsonObject.put("error_message", ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getMsg());
//                return jsonObject;
//        }

    }


}
