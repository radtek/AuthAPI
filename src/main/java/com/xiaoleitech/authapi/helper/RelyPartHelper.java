package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.global.dictionary.SystemGlobalParams;
import com.xiaoleitech.authapi.helper.cipher.HashAlgorithm;
import com.xiaoleitech.authapi.model.enumeration.globalparams.TokenCheckModeEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
