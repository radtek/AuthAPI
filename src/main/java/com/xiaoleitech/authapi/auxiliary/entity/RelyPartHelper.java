package com.xiaoleitech.authapi.auxiliary.entity;

import com.xiaoleitech.authapi.global.systemparams.SystemGlobalParams;
import com.xiaoleitech.authapi.global.cipher.hash.HashAlgorithm;
import com.xiaoleitech.authapi.global.enumeration.TokenCheckModeEnum;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class RelyPartHelper {
    private final SystemGlobalParams systemGlobalParams;

    @Autowired
    public RelyPartHelper(SystemGlobalParams systemGlobalParams) {
        this.systemGlobalParams = systemGlobalParams;
    }

    /**
     * 采用应用方的算法和密钥验证令牌
     * @param relyPart 应用方数据记录
     * @param token  待验证令牌
     * @return true -- 验证通过；false -- 验证失败
     */
    public boolean verifyToken(RelyParts relyPart, String token) {
        if ( (relyPart == null) || (token.isEmpty()) )
            return false;

        // 如果全局设置了不校验token，则直接返回验证通过
        if ( systemGlobalParams.getTokenCheckMode() == TokenCheckModeEnum.NOT_CHECK.getId())
            return true;

        // 获取当前系统时间
        java.sql.Timestamp timestamp = UtilsHelper.getCurrentSystemTimestamp();

        // 换算成分钟数
        int currentCount = (int)timestamp.getTime() / 1000 / 60;
        int previousCount = currentCount - 1;
        int nextCount = currentCount + 1;

        String msg = relyPart.getRp_uuid() + relyPart.getApp_key();

        // 计算三个时间点的HASH值
        String hashPrev = HashAlgorithm.getMD5(msg + Integer.toString(previousCount));
        String hashCurrent = HashAlgorithm.getMD5(msg + Integer.toString(currentCount));
        String hashNext = HashAlgorithm.getMD5(msg + Integer.toString(nextCount));

        // 能匹配上上述HASH值任意一个，都可以认为令牌通过验证。
        if (token.equals(hashPrev) || token.equals(hashCurrent) || token.equals(hashNext))
            return true;
        else
            return false;

    }

    /**
     * 对指定的APP生成新的 token
     * @param relyPart 依赖方（应用方）
     * @return token 字符串
     */
    public String generateToken(RelyParts relyPart) {
        if (relyPart == null)
            return "";

        // 获取当前系统时间
        java.sql.Timestamp timestamp = UtilsHelper.getCurrentSystemTimestamp();
        // 换算成分钟数
        int currentCount = (int)timestamp.getTime() / 1000 / 60;

        String msg = relyPart.getRp_uuid() + relyPart.getApp_key();

        String hashCurrent = HashAlgorithm.getMD5(msg + Integer.toString(currentCount));

        return hashCurrent;
    }

    /**
     * 获取新应用的失效时间
     * @return 失效时间戳
     */
    public java.sql.Timestamp getNewAppExpireTime() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, 1);
        return new java.sql.Timestamp(now.getTimeInMillis());
    }

    public String generateNewAppKey() {
        return UtilsHelper.generateUuid();
    }
}
