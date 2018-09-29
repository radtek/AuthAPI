package com.xiaoleitech.authapi.service.rpmanage;

import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.mapper.AccountAuthHistoryMapper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.AccountAuthHistories;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.rpmanage.GetAuthHistoriesResponse;
import com.xiaoleitech.authapi.model.rpmanage.AuthHistoryRecord;
import com.xiaoleitech.authapi.model.rpmanage.GetAuthHistoryCountResponse;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetAuthHistoryServiceImpl implements GetAuthHistoryService {
    private RpAccounts validRpAccount = null;

    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final SystemErrorResponse systemErrorResponse;
    private final GetAuthHistoryCountResponse getAuthHistoryCountResponse;
    private final GetAuthHistoriesResponse getAuthHistoriesResponse;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final AccountAuthHistoryMapper accountAuthHistoryMapper;

    @Autowired
    public GetAuthHistoryServiceImpl(RpAccountsTableHelper rpAccountsTableHelper, SystemErrorResponse systemErrorResponse, GetAuthHistoryCountResponse getAuthHistoryCountResponse, GetAuthHistoriesResponse getAuthHistoriesResponse, RelyPartsTableHelper relyPartsTableHelper, AccountAuthHistoryMapper accountAuthHistoryMapper) {
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.systemErrorResponse = systemErrorResponse;
        this.getAuthHistoryCountResponse = getAuthHistoryCountResponse;
        this.getAuthHistoriesResponse = getAuthHistoriesResponse;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.accountAuthHistoryMapper = accountAuthHistoryMapper;
    }

    private ErrorCodeEnum searchRpAccount(String appUuid, String token, String accountName, String accountUuid) {

        // 账户名称和账户UUID，二选一。不能全为空
        if (accountName.isEmpty() && accountUuid.isEmpty())
            return ErrorCodeEnum.ERROR_NEED_PARAMETER;

        // 根据appUuid获取app记录
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return ErrorCodeEnum.ERROR_APP_NOT_FOUND;

        // 优先用账户名查询账户记录，或者用账户uuid查询记录
        RpAccounts rpAccount = null;
        if (!accountName.isEmpty()) {
            rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndAccountName(relyPart.getId(), accountName);
        } else if (!accountUuid.isEmpty()) {
            rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(accountUuid);
        }
        if (rpAccount == null)
            return ErrorCodeEnum.ERROR_INVALID_ACCOUNT;

        validRpAccount = rpAccount;
        return ErrorCodeEnum.ERROR_OK;
    }

    @Override
    public AuthAPIResponse getAuthHistoryRecordsCount(String appUuid, String token, String accountName, String accountUuid) {
        // appUuid不能为空，否则查不到历史记录
        if ( (appUuid == null) || (appUuid.isEmpty())) {
            systemErrorResponse.fillErrorResponse(getAuthHistoryCountResponse, ErrorCodeEnum.ERROR_OK);
            getAuthHistoryCountResponse.setCount(0);
            return getAuthHistoryCountResponse;
        }

        // 查找账号记录，账号名或UUID二选一
        ErrorCodeEnum errorCode = searchRpAccount(appUuid, token, accountName, accountUuid);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // TODO: 暂时先读取所有记录，再取count值，需优化成直接取count
        List<AccountAuthHistories> accountAuthHistoriesList = accountAuthHistoryMapper.selectAuthHistoryByUserIdAndRpId(
                validRpAccount.getUser_id(), validRpAccount.getRp_id());
        systemErrorResponse.fillErrorResponse(getAuthHistoryCountResponse, ErrorCodeEnum.ERROR_OK);
        getAuthHistoryCountResponse.setCount(accountAuthHistoriesList.size());

        return getAuthHistoryCountResponse;
    }

    @Override
    public AuthAPIResponse getAuthHistories(String appUuid, String token, String accountName, String accountUuid, int from, int count) {
        if ((from <= 0) || (count <=0))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_PARAMETER);

        // appUuid不能为空，否则查不到历史记录
        if ( (appUuid == null) || (appUuid.isEmpty())) {
            systemErrorResponse.fillErrorResponse(getAuthHistoryCountResponse, ErrorCodeEnum.ERROR_OK);
            getAuthHistoryCountResponse.setCount(0);
            return getAuthHistoryCountResponse;
        }

        // 查找账号记录，账号名或UUID二选一
        ErrorCodeEnum errorCode = searchRpAccount(appUuid, token, accountName, accountUuid);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return systemErrorResponse.getGeneralResponse(errorCode);

        // TODO: 暂时先读取所有记录，再取从结果集中取指定范围的记录
        List<AccountAuthHistories> accountAuthHistoriesList = accountAuthHistoryMapper.selectAuthHistoryByUserIdAndRpId(
                validRpAccount.getUser_id(), validRpAccount.getRp_id());

        List<AuthHistoryRecord> authHistoryRecordList = new ArrayList();
        int index = 0;
        for (AccountAuthHistories accountAuthHistories : accountAuthHistoriesList){
            index++;
            if (index < from)
                continue;
            else if (index >= (from + count))
                break;

            AuthHistoryRecord authHistoryRecord = new AuthHistoryRecord();
            authHistoryRecord.setAuth_ip(accountAuthHistories.getAuth_ip());
            authHistoryRecord.setAuth_latitude(accountAuthHistories.getAuth_latitude());
            authHistoryRecord.setAuth_longitude(accountAuthHistories.getAuth_longitude());
            authHistoryRecord.setAuth_at(accountAuthHistories.getAuth_at());
            authHistoryRecord.setVerify_method(accountAuthHistories.getProtect_method());

            authHistoryRecordList.add(authHistoryRecord);
        }
        getAuthHistoriesResponse.setAuth_history(authHistoryRecordList);
        systemErrorResponse.fillErrorResponse(getAuthHistoriesResponse, ErrorCodeEnum.ERROR_OK);
        return getAuthHistoriesResponse;
    }
}
