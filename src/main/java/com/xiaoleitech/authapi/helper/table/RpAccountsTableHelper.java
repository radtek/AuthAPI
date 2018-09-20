package com.xiaoleitech.authapi.helper.table;

import com.xiaoleitech.authapi.helper.UtilsHelper;
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

    public RpAccounts getRpAccountByRpAccountUuid(String rpAccountUuid){
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpAccountUuid(rpAccountUuid);
        return UtilsHelper.getFirstValid(rpAccountsList);
    }

    /**
     * 读取活跃状态的账户记录
     * @param rpId 应用ID
     * @param userId 用户ID
     * @return 单条账户记录
     */
    public RpAccounts getRpAccountByRpIdAndUserId(int rpId, int userId) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpIdAndUserId(rpId, userId);
        return UtilsHelper.getFirstValid(rpAccountsList);
    }

    /**
     * 读取系统中存在的账户记录，包括活跃和非活跃
     * @param rpId 应用ID
     * @param userId 用户ID
     * @return 单条账户记录
     */
    public RpAccounts getExistRpAccountByRpIdAndUserId(int rpId, int userId) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectAllRpAccountsByRpIdAndUserId(rpId, userId);
        return UtilsHelper.getFirstValid(rpAccountsList);
    }

    public int getRpAccountCountByRpIdAndAccountName(int rpId, String accountName) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpIdAndAccountName(rpId, accountName);
        return rpAccountsList.size();
    }

    public RpAccounts getRpAccountByRpIdAndAccountName(int rpId, String accountName) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpIdAndAccountName(rpId, accountName);
        return UtilsHelper.getFirstValid(rpAccountsList);
    }

    public RpAccounts getRpAccountByRpUuidAndUserUuid(String rpUuid, String userUuid) {
        List<RpAccounts> rpAccountsList = rpAccountsMapper.selectRpAccountsByRpUuidAndUserUuid(rpUuid, userUuid);
        return UtilsHelper.getFirstValid(rpAccountsList);
    }

    public ErrorCodeEnum updateOneRpAccountRecord(RpAccounts rpAccount) {
        // 设置更新时间
        rpAccount.setUpdated_at(UtilsHelper.getCurrentSystemTimestamp());
        int num = rpAccountsMapper.updateRpAccountByRpaUuid(rpAccount);

        return (num == 1) ? ErrorCodeEnum.ERROR_OK : ErrorCodeEnum.ERROR_INTERNAL_ERROR;
    }

}
