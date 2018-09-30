package com.xiaoleitech.authapi.controller;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.rpmanage.CreateRelyPartRequest;
import com.xiaoleitech.authapi.model.rpmanage.SetRelyPartParamsRequest;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import com.xiaoleitech.authapi.service.rpmanage.GetAuthHistoryService;
import com.xiaoleitech.authapi.service.rpmanage.RelyPartManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class XL_RpManageAPI {
    private final GetAuthHistoryService getAuthHistoryService;
    private final RelyPartManageService relyPartManageService;
    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public XL_RpManageAPI(GetAuthHistoryService getAuthHistoryService, RelyPartManageService relyPartManageService, SystemErrorResponse systemErrorResponse) {
        this.getAuthHistoryService = getAuthHistoryService;
        this.relyPartManageService = relyPartManageService;
        this.systemErrorResponse = systemErrorResponse;
    }

    /**
     * 获取指定账户授权登录记录条数
     * get https://server/api/get_account_auth_history_count?app_id=<app_id>&token=<token>&account_id=<account_id>&account_name=<account_name>
     *
     * @param appUuid 应用UUID
     * @param token 令牌
     * @param accountName  账户名
     * @param accountUuid  账户UUID
     * @return
     * {
     *      error_code: errorCode,
     *      error_message: errorMessage
     *      count: 授权记录条数
     * }
     */
    @RequestMapping(value = "/api/get_account_auth_history_count", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAccountAuthHistoryCount(@RequestParam("app_id") String appUuid,
                                               @RequestParam("token") String token,
                                               @RequestParam(value = "account_name", required = false, defaultValue = "") String accountName,
                                               @RequestParam(value = "account_id", required = false, defaultValue = "") String accountUuid) {
        // account_name 和 account_id 任选其一
        return getAuthHistoryService.getAuthHistoryRecordsCount(appUuid, token, accountName, accountUuid);
    }

    /**
     * 获取账户授权登录记录
     * get https://server/api/get_account_auth_history_count?app_id=<app_id>&token=<token>&account_id=<account_id>&
     *          account_name=<account_name>&from=<from>&count=<count>
     * @param appUuid 应用UUID
     * @param token 令牌
     * @param accountName  账户名
     * @param accountUuid  账户UUID
     * @param from 从第几条记录开始
     * @param count 需要返回几条记录
     * @return
     * {
     *      error_code: 0,
     *      error_message: "OK",
     *      auth_history:
     *      [
     *          {
     *              auth_ip: "202.196.0.101", // IP地址
     *              auth_latitude: "23.1234", // 纬度
     *              auth_longitude: "109.3244", // 经度
     *              auth_at: "2018-1-1 12:00:00", // 授权登录时间
     *              verify_method: "2" // 身份验证方式
     *          },
     *          ...
     *      ]
     * }
     */
    @RequestMapping(value = "/api/get_account_auth_history", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getAccountAuthHistoryRecords(@RequestParam("app_id") String appUuid,
                                                 @RequestParam("token") String token,
                                                 @RequestParam(value = "account_name", required = false, defaultValue = "") String accountName,
                                                 @RequestParam(value = "account_id", required = false, defaultValue = "") String accountUuid,
                                                 @RequestParam(value = "from") int from,
                                                 @RequestParam(value = "count") int count) {
        // account_name 和 account_id 任选其一
        return getAuthHistoryService.getAuthHistories(appUuid, token, accountName, accountUuid, from, count);
    }

    /**
     * 创建应用
     * post https://server/api/create_rp
     *
     * @param createRelyPartRequest 请求参数
     * @param bindingResult 数据绑定结果
     * @return
     *   {
     *      error_code: 0,
     *      error_message: "OK",
     *      app_id: XXXXXXXXXXX
     *      app_key: XXXXXXXXXXXXX
     *   }
     *
     */
    @RequestMapping(value = "/api/create_rp", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse createRelyPart(@ModelAttribute CreateRelyPartRequest createRelyPartRequest, BindingResult bindingResult) {
        return relyPartManageService.createRelyPart(createRelyPartRequest, bindingResult);
    }

    /**
     * 设置应用参数
     * post https://server/api/set_rp_param
     *
     * @param setRelyPartParamsRequest  relypart参数
     * @param bindingResult 数据绑定结果
     * @return
     * {
     *      error_code: 0,
     *      error_message: "OK"
     * }
     */
    @RequestMapping(value = "/api/set_rp_param", method = RequestMethod.POST)
    public @ResponseBody
    AuthAPIResponse setRelyPartParams(@ModelAttribute SetRelyPartParamsRequest setRelyPartParamsRequest, BindingResult bindingResult) {
        return relyPartManageService.setRelyPartParams(setRelyPartParamsRequest, bindingResult);
    }

    /**
     * 获取应用方（依赖方）参数
     * get https://server/api/get_rp_param?app_id=<app_id>&token=<token>
     *
     * @param appUuid  应用UUID
     * @param token  验证令牌
     * @return 应用或依赖方记录
     */
    @RequestMapping(value = "/api/get_rp_param", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse getRelyPartParams(@RequestParam("app_id") String appUuid,
                                      @RequestParam("token") String token) {
        return relyPartManageService.getRelyPartParams(appUuid, token);
    }

    /**
     * 重置应用认证密钥
     * get https://server/api/reset_rp_appkey?app_id=<app_id>&token=<token>
     *
     * @param appUuid  应用UUID
     * @param token  验证令牌
     * @return
     * {
     *      error_code: 0,
     *      error_message: "OK",
     *      app_key: "hijklmn"
     * }
     */
    @RequestMapping(value = "/api/reset_rp_appkey", method = RequestMethod.GET)
    public @ResponseBody
    AuthAPIResponse resetRelyPartAuthKey(@RequestParam("app_id") String appUuid,
                                         @RequestParam("token") String token) {
        return relyPartManageService.resetRelyPartAppKey(appUuid, token);
    }

//
//    @RequestMapping(value = "/api/create_rp2", method = RequestMethod.POST)
//    public @ResponseBody
//    AuthAPIResponse testModelAttribute(@RequestBody ModelMap modelMap) {
//        System.out.println(modelMap);
//        return systemErrorResponse.getSuccessResponse();
//    }
}


