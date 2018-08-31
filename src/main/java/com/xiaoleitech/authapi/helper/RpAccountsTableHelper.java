package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.mapper.RpAccountsMapper;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RpAccountsTableHelper {
    private final RpAccountsMapper rpAccountsMapper;

    @Autowired
    public RpAccountsTableHelper(RpAccountsMapper rpAccountsMapper) {
        this.rpAccountsMapper = rpAccountsMapper;
    }

    public RpAccounts getRpAccountByRpIdAndUserId(int rpId, int userId) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpIdAndUserId(rpId, userId);
        if (rpAccountsList.size() == 0)
            return null;
        else
            return rpAccountsList.get(0);
    }

    public int getRpAccountCountByRpIdAndUserName(int rpId, String userName) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpIdAndUserName(rpId, userName);
        return rpAccountsList.size();
    }

    public ErrorCodeEnum updateOneRpAccountRecord(RpAccounts rpAccount) {
        // 设置更新时间
        rpAccount.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());
        int num = rpAccountsMapper.updateRpAccountByRpaUuid(rpAccount);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_INTERNAL_ERROR;
    }

}
