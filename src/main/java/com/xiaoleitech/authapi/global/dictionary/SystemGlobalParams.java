package com.xiaoleitech.authapi.global.dictionary;

import com.xiaoleitech.authapi.helper.SystemDictHelper;
import com.xiaoleitech.authapi.helper.cache.RedisService;
import com.xiaoleitech.authapi.model.enumeration.globalparams.PasswordModeEnum;
import com.xiaoleitech.authapi.model.enumeration.SystemDictKeyEnum;
import com.xiaoleitech.authapi.model.enumeration.globalparams.TokenCheckModeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SystemGlobalParams {
    private static boolean isInitialized = false;
    private final SystemDictHelper systemDictHelper;
    private final RedisService redisService;
    private int passwordMode;
    private int tokenCheckMode;


    @Autowired
    public SystemGlobalParams(SystemDictHelper systemDictHelper,
                              RedisService redisService) {
        this.systemDictHelper = systemDictHelper;
        this.redisService = redisService;
        if (!isInitialized) {
            initGlobalDictionary();
            isInitialized = true;
        }
    }

    public int getPasswordMode() {
        String passMode = redisService.getValue(SystemDictKeyEnum.DICT_KEY_PASSWORD_MODE.getKey());
        if ((passMode == null) || (passMode.isEmpty()))
            this.passwordMode = PasswordModeEnum.PASSWORD_MODE_PLAIN.getId();
        else
            this.passwordMode = Integer.parseInt(passMode);

//        return PasswordModeEnum.PASSWORD_MODE_PLAIN.getId();
        return this.passwordMode;
    }

    public void setPasswordMode(int passwordMode) {
        this.passwordMode = passwordMode;
        redisService.setValue(SystemDictKeyEnum.DICT_KEY_PASSWORD_MODE.getKey(),
                Integer.toString(this.passwordMode));
    }

    public int getTokenCheckMode() {
        String mode = redisService.getValue(SystemDictKeyEnum.DICT_KEY_TOKEN_CHECK_MODE.getKey());
        if ((mode == null) || (mode.isEmpty()))
            this.tokenCheckMode = TokenCheckModeEnum.CHECK_TOKEN.getId();
        else
            this.tokenCheckMode = Integer.parseInt(mode);

        return this.tokenCheckMode;
    }

    public void setTokenCheckMode(int tokenCheckMode) {
        this.tokenCheckMode = tokenCheckMode;
        redisService.setValue(SystemDictKeyEnum.DICT_KEY_TOKEN_CHECK_MODE.getKey(),
                Integer.toString(this.tokenCheckMode));
    }

    public void initGlobalDictionary() {
        // 密码模式
        int passwordMode = systemDictHelper.getIntegerValue(SystemDictKeyEnum.DICT_KEY_PASSWORD_MODE.getKey());
        setPasswordMode(passwordMode);

        // token 校验模式
        int checkTokenMode = systemDictHelper.getIntegerValue(SystemDictKeyEnum.DICT_KEY_TOKEN_CHECK_MODE.getKey());
        setTokenCheckMode(checkTokenMode);
    }
}
