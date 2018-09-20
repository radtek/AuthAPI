package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.helper.otp.OtpHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import com.xiaoleitech.authapi.helper.msgqueue.PushMessageHelper;
import com.xiaoleitech.authapi.helper.table.DevicesTableHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.model.bean.OtpParams;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetOtpCodeServiceImpl implements GetOtpCodeService {
    private final PushMessageHelper pushMessageHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final UsersTableHelper usersTableHelper;
    private final DevicesTableHelper devicesTableHelper;
    private final OtpHelper otpHelper;
    private final RedisService redisService;

    @Autowired
    public GetOtpCodeServiceImpl(RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, UsersTableHelper usersTableHelper, DevicesTableHelper devicesTableHelper, OtpHelper otpHelper, RedisService redisService, PushMessageHelper pushMessageHelper) {
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.usersTableHelper = usersTableHelper;
        this.devicesTableHelper = devicesTableHelper;
        this.otpHelper = otpHelper;
        this.redisService = redisService;
        this.pushMessageHelper = pushMessageHelper;
    }

    @Override
    public String getOtpCode(String appUuid, String accountName, String nonce) {

        // 检查参数
        if (appUuid.isEmpty() || accountName.isEmpty() || nonce.isEmpty())
            return ErrorCodeEnum.ERROR_NEED_PARAMETER.getCodeString();

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return ErrorCodeEnum.ERROR_INVALID_APP.getCodeString();

        // 读取应用账户记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndAccountName(relyPart.getRp_id(), accountName);
        if (rpAccount == null)
            return ErrorCodeEnum.ERROR_INVALID_ACCOUNT.getCodeString();

        // 读取用户记录
        Users user = usersTableHelper.getUserByUserId(rpAccount.getUser_id());
        if (user == null)
            return ErrorCodeEnum.ERROR_INVALID_USER.getCodeString();

        // 读取设备记录
        Devices device = devicesTableHelper.getDeviceById(user.getDevice_id());
        if (device == null)
            return ErrorCodeEnum.ERROR_INVALID_DEVICE.getCodeString();

        // 生成OTP，并存放到缓存中
        OtpParams otpParams = new OtpParams();
        otpParams.setOwner(rpAccount.getRp_account_uuid());
        BeanUtils.copyProperties(relyPart, otpParams);
        otpParams.setNonce(nonce);
        otpParams.setOtp_seed(rpAccount.getOtp_seed());
        String accountOtp = otpHelper.generateOtp(otpParams);

        // TODO: 本接口暂时不用，sendMessage暂时不用
        // 根据设备类型 device_type，发送APN或MQTT消息到手机应用
//        pushMessageHelper.sendMessage(device, );

        return ErrorCodeEnum.ERROR_OK.getCodeString();
    }
}
