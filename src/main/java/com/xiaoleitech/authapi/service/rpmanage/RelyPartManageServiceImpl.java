package com.xiaoleitech.authapi.service.rpmanage;

import com.xiaoleitech.authapi.helper.RelyPartHelper;
import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.AppStateEnum;
import com.xiaoleitech.authapi.model.enumeration.AuthorizationPolicyEnum;
import com.xiaoleitech.authapi.model.enumeration.ClientTypeEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.rpmanage.*;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class RelyPartManageServiceImpl implements RelyPartManageService{
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RelyPartHelper relyPartHelper;
    private final CreateRelyPartResponse createRelyPartResponse;
    private final GetRelyPartParamsResponse getRelyPartParamsResponse;
    private final ResetRelyPartAuthKeyResponse resetRelyPartAuthKeyResponse;

//    private RelyParts relyPartRecord = null;

    @Autowired
    public RelyPartManageServiceImpl(SystemErrorResponse systemErrorResponse, RelyPartsTableHelper relyPartsTableHelper, RelyPartHelper relyPartHelper, CreateRelyPartResponse createRelyPartResponse, GetRelyPartParamsResponse getRelyPartParamsResponse, ResetRelyPartAuthKeyResponse resetRelyPartAuthKeyResponse) {
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.relyPartHelper = relyPartHelper;
        this.createRelyPartResponse = createRelyPartResponse;
        this.getRelyPartParamsResponse = getRelyPartParamsResponse;
        this.resetRelyPartAuthKeyResponse = resetRelyPartAuthKeyResponse;
    }

    @Override
    public AuthAPIResponse createRelyPart(CreateRelyPartRequest createRelyPartRequest, BindingResult bindingResult) {
        // 检查请求参数
        ErrorCodeEnum errorCode = checkRelyPartParams(createRelyPartRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // TODO: 是否要求rp名称全局唯一
        if (relyPartsTableHelper.isExistRpName(createRelyPartRequest.getRp_name()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_EXIST_APP_NAME);

        // 设置应用记录的默认值
        RelyParts relyPartRecord = new RelyParts();
        setRelyPartRecordDefaultValue(relyPartRecord);

        // 拷贝请求参数到RelyPart记录
        errorCode = fillRelyPartRecordUsingRequestParams(relyPartRecord, createRelyPartRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // 生成 rp_uuid
        relyPartRecord.setRp_uuid(UtilsHelper.generateUuid());

        // 创建App记录时即激活
        relyPartRecord.setState(AppStateEnum.APP_ACTIVE.getApp_state());

        // 设置APP失效时间
        relyPartRecord.setExpire_at(relyPartHelper.getNewAppExpireTime());

        // 设置客户类型
        relyPartRecord.setClient_type(ClientTypeEnum.CLIENT_TYPE_WEB.getClientType());

        // 设置APP的认证策略
        relyPartRecord.setAuthorization_policy(AuthorizationPolicyEnum.AUTH_NEED_LOGIN.getAuthPolicy());

        // 生成新的 app_key
        relyPartRecord.setApp_key(relyPartHelper.generateNewAppKey());

        // 设置创建时间和更新时间
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        relyPartRecord.setCreated_at(currentTime);
        relyPartRecord.setUpdated_at(currentTime);

        // 生成OTP-KEY
        relyPartRecord.setOtp_key(UtilsHelper.generatePrintableRandom(8));

        // 插入这条新APP的记录
        int count = relyPartsTableHelper.insertOneRelyPart(relyPartRecord);
        if (count != 1)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        // 成功时，返回应用UUID和APP KEY
        systemErrorResponse.fillErrorResponse(createRelyPartResponse, ErrorCodeEnum.ERROR_OK);
        createRelyPartResponse.setApp_id(relyPartRecord.getRp_uuid());
        createRelyPartResponse.setApp_key(relyPartRecord.getApp_key());
        return createRelyPartResponse;
    }

    @Override
    public AuthAPIResponse setRelyPartParams(SetRelyPartParamsRequest setRelyPartParamsRequest, BindingResult bindingResult) {
        // 根据应用UUID读取应用记录
        RelyParts relyPartRecord = relyPartsTableHelper.getRelyPartByRpUuid(setRelyPartParamsRequest.getApp_id());
        if (relyPartRecord == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 检查token
        if (!relyPartHelper.verifyToken(relyPartRecord, setRelyPartParamsRequest.getToken()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 拷贝请求参数到RelyPart记录
        ErrorCodeEnum errorCode = fillRelyPartRecordUsingRequestParams(relyPartRecord, setRelyPartParamsRequest);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // 设置更新时间
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        relyPartRecord.setUpdated_at(currentTime);

        // 更新记录
        int count = relyPartsTableHelper.updateOneRelyPartRecordByUuid(relyPartRecord);
        if (count <= 0)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return systemErrorResponse.getSuccessResponse();
    }

    @Override
    public AuthAPIResponse getRelyPartParams(String appUuid, String token) {
        // 根据应用UUID读取应用记录
        RelyParts relyPartRecord = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPartRecord == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 检查token
        if (!relyPartHelper.verifyToken(relyPartRecord, token))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 提取relypart参数
        getRelyPartParamsResponse.setRp_name(relyPartRecord.getRp_name());
        getRelyPartParamsResponse.setRp_logo(relyPartRecord.getRp_logo_file_url());
        getRelyPartParamsResponse.setVerify_methods(relyPartRecord.getRp_protect_methods());
        getRelyPartParamsResponse.setRealname_scope(relyPartRecord.getReal_name_scope());
        getRelyPartParamsResponse.setNew_account_policy(relyPartRecord.getNew_account_policy());
        getRelyPartParamsResponse.setAccount_name_policy(relyPartRecord.getUniq_account_name());
        getRelyPartParamsResponse.setAuth_life_time(relyPartRecord.getAuth_life_time());
        getRelyPartParamsResponse.setOtp_alg(relyPartRecord.getOtp_alg());
        getRelyPartParamsResponse.setOtp_digits(relyPartRecord.getOtp_digits());
        getRelyPartParamsResponse.setOtp_interval(relyPartRecord.getInteval());
        getRelyPartParamsResponse.setOtp_cover(relyPartRecord.getStrong());
        getRelyPartParamsResponse.setOtp_c(relyPartRecord.getOtp_c());
        getRelyPartParamsResponse.setOtp_q(relyPartRecord.getOtp_q());
        getRelyPartParamsResponse.setNeed_active_code(relyPartRecord.getNeed_active_code());
        getRelyPartParamsResponse.setUse_cert(relyPartRecord.getUse_cert());
        getRelyPartParamsResponse.setCert_type(relyPartRecord.getCert_type());
        getRelyPartParamsResponse.setCa_id(relyPartRecord.getCaid());
        getRelyPartParamsResponse.setNeed_info(relyPartRecord.getNeed_info());
        getRelyPartParamsResponse.setRp_account_authorized_callback_url(relyPartRecord.getRp_account_authorized_callback_url());
        getRelyPartParamsResponse.setRp_account_unauthorized_callback_url(relyPartRecord.getRp_account_unauthorized_callback_url());
        getRelyPartParamsResponse.setRp_account_enrolleded_callback_url(relyPartRecord.getRp_account_enroll_callback_url());
        getRelyPartParamsResponse.setRp_account_unenrolled_callback_url(relyPartRecord.getRp_account_unenroll_callback_url());
        getRelyPartParamsResponse.setRp_account_exist_callback_url(relyPartRecord.getRp_account_exist_callback_url());

        systemErrorResponse.fillErrorResponse(getRelyPartParamsResponse, ErrorCodeEnum.ERROR_OK);

        return getRelyPartParamsResponse;
    }

    @Override
    public AuthAPIResponse resetRelyPartAppKey(String appUuid, String token) {
        // 根据应用UUID读取应用记录
        RelyParts relyPartRecord = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPartRecord == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 检查token
        if (!relyPartHelper.verifyToken(relyPartRecord, token))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 生成新的 app_key
        relyPartRecord.setApp_key(relyPartHelper.generateNewAppKey());

        // 设置更新时间
        java.sql.Timestamp currentTime = UtilsHelper.getCurrentSystemTimestamp();
        relyPartRecord.setUpdated_at(currentTime);

        // 更新记录
        int count = relyPartsTableHelper.updateOneRelyPartRecordByUuid(relyPartRecord);
        if (count <= 0)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        resetRelyPartAuthKeyResponse.setApp_key(relyPartRecord.getApp_key());
        systemErrorResponse.fillErrorResponse(resetRelyPartAuthKeyResponse, ErrorCodeEnum.ERROR_OK);

        return resetRelyPartAuthKeyResponse;
    }

    private ErrorCodeEnum checkRelyPartParams(RelyPartParams relyPartParams) {
        //======================================================================
        // 必填项检查
        //======================================================================
        if (relyPartParams.getRp_name().isEmpty())
            return ErrorCodeEnum.ERROR_NEED_PARAMETER;

        return ErrorCodeEnum.ERROR_OK;
    }

    private void setRelyPartRecordDefaultValue(RelyParts relyPartRecord) {
        // 1:口令；2:指纹；3:虹膜；4:声纹；5:面部。默认12345
        relyPartRecord.setRp_protect_methods("12345");

        // 1:电话号码；2:姓名；3:身份证号。默认1
        relyPartRecord.setReal_name_scope("1");

        // 0:enroll & allow；1:enroll & tobe active；2: ID code / Verify Code active。默认0
        relyPartRecord.setNew_account_policy(0);

        // 1:使用唯一昵称作为APP的账户名；2:使用昵称作为APP的账户名（可不唯一）；3:不使用昵称。默认1
        relyPartRecord.setUniq_account_name(1);

        // 1:AHAPP退出登录即终止授权；2:1小时；3:1天；4:1月；5: 一直有效直到取消授权。默认0
        relyPartRecord.setAuth_life_time(0);

        // 0:不使用OTP；1:Google OTP；2: SHA1；3: SM3；默认0
        relyPartRecord.setOtp_alg(0);

        // 6位或8位，默认6位
        relyPartRecord.setOtp_digits(6);

        // OTP间隔，30秒或60秒，默认30
        relyPartRecord.setInteval(30);

        // OTP覆盖范围，1到3取值，默认为1
        relyPartRecord.setStrong(1);

        // 是否使用激活码，0不使用，1使用，默认0
        relyPartRecord.setNeed_active_code(0);

        // 使用数字证书，0不使用，1使用。默认0
        relyPartRecord.setUse_cert(0);

        // 数字证书类型，0表示内部CA
        relyPartRecord.setCert_type(0);

        // CA机构的ID, 默认0
        relyPartRecord.setCaid(0);

        // 是否需要补充信息，1需要，0不需要，默认0
        relyPartRecord.setNeed_info(0);
    }

    private ErrorCodeEnum fillRelyPartRecordUsingRequestParams(RelyParts relyPartRecord, RelyPartParams relyPartParams) {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String value;

        // 应用名称不能为空
        value = request.getParameter("rp_name");
        if ((value == null) || (value.isEmpty()))
            return ErrorCodeEnum.ERROR_NEED_PARAMETER;
        else
            relyPartRecord.setRp_name(value);

        // rp_logo非必填项
        value = request.getParameter("rp_logo");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_logo_file_url(value);

        // 1:口令；2:指纹；3:虹膜；4:声纹；5:面部。默认12345
        value = request.getParameter("verify_methods");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_protect_methods(value);

        // 1:电话号码；2:姓名；3:身份证号。默认1
        value = request.getParameter("realname_scope");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setReal_name_scope(value);

        // 0:enroll & allow；1:enroll & tobe active；2: ID code / Verify Code active。默认0
        value = request.getParameter("new_account_policy");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setNew_account_policy(relyPartParams.getNew_account_policy());

        // 1:使用唯一昵称作为APP的账户名；2:使用昵称作为APP的账户名（可不唯一）；3:不使用昵称。默认1
        value = request.getParameter("account_name_policy");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setUniq_account_name(relyPartParams.getAccount_name_policy());

        // 1:AHAPP退出登录即终止授权；2:1小时；3:1天；4:1月；5: 一直有效直到取消授权。默认0
        value = request.getParameter("auth_life_time");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setAuth_life_time(relyPartParams.getAuth_life_time());

        // 0:不使用OTP；1:Google OTP；2: SHA1；3: SM3；默认0
        value = request.getParameter("otp_alg");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setOtp_alg(relyPartParams.getOtp_alg());

        // 6位或8位，默认6位
        value = request.getParameter("otp_digits");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setOtp_digits(relyPartParams.getOtp_digits());

        // OTP间隔，30秒或60秒，默认30
        value = request.getParameter("otp_interval");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setInteval(relyPartParams.getOtp_interval());

        // OTP覆盖范围，1到3取值，默认为1
        value = request.getParameter("otp_cover");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setStrong(relyPartParams.getOtp_cover());

        // otp_c 非必填项
        value = request.getParameter("otp_c");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setOtp_c(relyPartParams.getOtp_c());

        // otp_q 非必填项
        value = request.getParameter("otp_q");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setOtp_q(relyPartParams.getOtp_q());

        // 是否使用激活码，0不使用，1使用，默认0
        value = request.getParameter("need_active_code");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setNeed_active_code(relyPartParams.getNeed_active_code());

        // 使用数字证书，0不使用，1使用。默认0
        value = request.getParameter("use_cert");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setUse_cert(relyPartParams.getUse_cert());

        // 数字证书类型，0表示内部CA
        value = request.getParameter("cert_type");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setCert_type(relyPartParams.getCert_type());

        // CA机构的ID, 默认0
        value = request.getParameter("ca_id");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setCaid(relyPartParams.getCa_id());

        // 是否需要补充信息，1需要，0不需要，默认0
        value = request.getParameter("need_info");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setNeed_info(relyPartParams.getNeed_info());

        // rp_account_authorized_callback_url 非必填项
        value = request.getParameter("rp_account_authorized_callback_url");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_account_authorized_callback_url(relyPartParams.getRp_account_authorized_callback_url());

        // rp_account_unauthorized_callback_url 非必填项
        value = request.getParameter("rp_account_unauthorized_callback_url");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_account_unauthorized_callback_url(relyPartParams.getRp_account_unauthorized_callback_url());

        // rp_account_enrolleded_callback_url 非必填项
        value = request.getParameter("rp_account_enrolleded_callback_url");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_account_enroll_callback_url(relyPartParams.getRp_account_enrolleded_callback_url());

        // rp_account_unenrolled_callback_url 非必填项
        value = request.getParameter("rp_account_unenrolled_callback_url");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_account_unenroll_callback_url(relyPartParams.getRp_account_unenrolled_callback_url());

        // rp_account_exist_callback_url 非必填项
        value = request.getParameter("rp_account_exist_callback_url");
        if ((value != null) && !(value.isEmpty()))
            relyPartRecord.setRp_account_exist_callback_url(relyPartParams.getRp_account_exist_callback_url());

        return ErrorCodeEnum.ERROR_OK;
    }
}
