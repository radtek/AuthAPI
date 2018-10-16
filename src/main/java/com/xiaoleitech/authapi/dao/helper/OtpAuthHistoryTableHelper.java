package com.xiaoleitech.authapi.dao.helper;

import com.xiaoleitech.authapi.dao.mybatis.mapper.OtpAuthHistoryMapper;
import com.xiaoleitech.authapi.dao.pojo.OtpAuthHistories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OtpAuthHistoryTableHelper {
    private final OtpAuthHistoryMapper otpAuthHistoryMapper;

    @Autowired
    public OtpAuthHistoryTableHelper(OtpAuthHistoryMapper otpAuthHistoryMapper) {
        this.otpAuthHistoryMapper = otpAuthHistoryMapper;
    }

    public int insertOneRecord(OtpAuthHistories otpAuthHistory) {
        return otpAuthHistoryMapper.insertOneRecord(otpAuthHistory);
    }

    public boolean checkUsedInRecent(int rpAccountId, String otp, int recentCount) {
        if (recentCount <= 0)
            return false;

        List<OtpAuthHistories> otpAuthHistoriesList = otpAuthHistoryMapper.selectRecentHistory(rpAccountId, recentCount);
        for (OtpAuthHistories otpAuthHistory : otpAuthHistoriesList) {
            if (otpAuthHistory.getOtp().equals(otp)) {
                return true;
            }
        }

        return false;
    }
}
