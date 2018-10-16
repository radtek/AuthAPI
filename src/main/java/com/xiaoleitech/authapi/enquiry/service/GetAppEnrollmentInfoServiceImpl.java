package com.xiaoleitech.authapi.enquiry.service;

import com.xiaoleitech.authapi.dao.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.enquiry.bean.response.AppEnrollmentInfoResponse;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAppEnrollmentInfoServiceImpl implements GetAppEnrollmentInfoService {
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final AppEnrollmentInfoResponse appEnrollmentInfoResponse;

    @Autowired
    public GetAppEnrollmentInfoServiceImpl(RelyPartsTableHelper relyPartsTableHelper, SystemErrorResponse systemErrorResponse, AppEnrollmentInfoResponse appEnrollmentInfoResponse) {
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.appEnrollmentInfoResponse = appEnrollmentInfoResponse;
    }

    @Override
    public AuthAPIResponse getAppEnrollmentInfo(String appUuid) {
        // 获取 RelyPart 记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);

        appEnrollmentInfoResponse.setApp_name(relyPart.getRp_name());
        appEnrollmentInfoResponse.setApp_logo(relyPart.getRp_logo_file_url());
        appEnrollmentInfoResponse.setApp_protect_methods(relyPart.getRp_protect_methods());
        appEnrollmentInfoResponse.setObtain_realname_info_scope(relyPart.getReal_name_scope());
        appEnrollmentInfoResponse.setNeed_uniq_username(relyPart.getUniq_account_name());
        appEnrollmentInfoResponse.setNew_account_policy(relyPart.getNew_account_policy());
        appEnrollmentInfoResponse.setNeed_info(relyPart.getNeed_info());
        appEnrollmentInfoResponse.setUse_cert(relyPart.getUse_cert());
        appEnrollmentInfoResponse.setCert_type(relyPart.getCert_type());
        appEnrollmentInfoResponse.setCert_template("{ }");

        systemErrorResponse.fillErrorResponse(appEnrollmentInfoResponse, ErrorCodeEnum.ERROR_OK);
        return appEnrollmentInfoResponse;
    }
}
