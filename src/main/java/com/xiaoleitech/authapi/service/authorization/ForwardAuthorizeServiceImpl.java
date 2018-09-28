package com.xiaoleitech.authapi.service.authorization;

import com.xiaoleitech.authapi.helper.RelyPartHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import com.xiaoleitech.authapi.helper.msgqueue.PushMessageHelper;
import com.xiaoleitech.authapi.helper.table.DevicesTableHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.model.bean.PushPhoneMessage;
import com.xiaoleitech.authapi.model.enumeration.AccountStateEnum;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Devices;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class ForwardAuthorizeServiceImpl implements ForwardAuthorizeService{
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final DevicesTableHelper devicesTableHelper;
    private final UsersTableHelper usersTableHelper;
    private final PushMessageHelper pushMessageHelper;
    private final RedisService redisService;
    private final RelyPartHelper relyPartHelper;

    private final SystemErrorResponse systemErrorResponse;

    @Autowired
    public ForwardAuthorizeServiceImpl(SystemErrorResponse systemErrorResponse, RelyPartsTableHelper relyPartsTableHelper, RpAccountsTableHelper rpAccountsTableHelper, DevicesTableHelper devicesTableHelper, UsersTableHelper usersTableHelper, PushMessageHelper pushMessageHelper, RedisService redisService, RelyPartHelper relyPartHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.devicesTableHelper = devicesTableHelper;
        this.usersTableHelper = usersTableHelper;
        this.pushMessageHelper = pushMessageHelper;
        this.redisService = redisService;
        this.relyPartHelper = relyPartHelper;
    }

    @Override
    public String forwardAuthorize(String appUuid, String token, String accountName, String nonce) {

        // 检查参数
        if (appUuid.isEmpty() || accountName.isEmpty() || nonce.isEmpty())
            return Integer.toString(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED.getCode());

        // 读取应用记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return Integer.toString(ErrorCodeEnum.ERROR_APP_NOT_FOUND.getCode());

        // 读取应用账户记录，并检查账户是否为激活状态
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndAccountName(
                relyPart.getId(), accountName);
        if (rpAccount == null)
            return Integer.toString(ErrorCodeEnum.ERROR_INVALID_ACCOUNT.getCode());
        else if (rpAccount.getState() != AccountStateEnum.ACCOUNT_STATE_ACTIVE.getState())
            return Integer.toString(ErrorCodeEnum.ERROR_USER_NOT_ACTIVATED.getCode());

        // 校验 token
        if (!relyPartHelper.verifyToken(relyPart, token))
            return Integer.toString(ErrorCodeEnum.ERROR_INVALID_TOKEN.getCode());

        // 读取用户记录
        Users user = usersTableHelper.getUserByUserId(rpAccount.getUser_id());
        if (user == null)
            return Integer.toString(ErrorCodeEnum.ERROR_USER_NOT_FOUND.getCode());

        // 读取设备记录
        Devices device = devicesTableHelper.getDeviceById(user.getDevice_id());
        if (device == null)
            return Integer.toString(ErrorCodeEnum.ERROR_DEVICE_NOT_FOUND.getCode());

        // 获取参数中的 remote_ip
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String remoteIp = req.getRemoteAddr();

        // 从百度地图服务获取ip对应的地址
        // open("https://api.map.baidu.com/location/ip?ak=x6Pz7TNARnSHxhdRFv6g9HZbI4cIPjRG&ip=#{ip}")

        // 设备类型是ios系统，send_apn_notify
        // 设备类型是android系统，send_mqtt_notify
        PushPhoneMessage pushPhoneMessage = new PushPhoneMessage();
        pushPhoneMessage.setMessage_type(0x40);
        pushPhoneMessage.setAlert(relyPart.getRp_name() + "正在申请授权登录。");
        pushPhoneMessage.setApp_id(relyPart.getRp_uuid());
        pushPhoneMessage.setNonce(nonce);
        pushPhoneMessage.setIp(remoteIp);
        pushPhoneMessage.setAddress("");
        JSONObject jsonObject = JSONObject.fromObject(pushPhoneMessage);
        String message = jsonObject.toString();
        ErrorCodeEnum errorCode = pushMessageHelper.sendMessage(device, message);

        // 在缓存中保存 nonce 一段时间
        redisService.setValueForSeconds("web_auth_req_" + nonce, "0", 300);

        return Integer.toString(errorCode.getCode());
    }
}
