package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.mapper.RpAccountsMapper;
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
        List<RpAccounts> rpAccountsList = rpAccountsMapper.SelectRpAccountsByRpIdAndUserId(rpId, userId);
        if (rpAccountsList.size() == 0)
            return null;
        else
            return rpAccountsList.get(0);
    }

}
