package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.helper.RelyPartHelper;
import com.xiaoleitech.authapi.helper.table.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.table.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.authenticate.AuthenticationHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enquiry.AccountInfoResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.stereotype.Component;

@Component
public class GetAccountInfoServiceImpl implements GetAccountInfoService {
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;
    private final AuthenticationHelper authenticationHelper;
    private final AccountInfoResponse accountInfoResponse;
    private final SystemErrorResponse systemErrorResponse;
    private final RelyPartHelper relyPartHelper;

    public GetAccountInfoServiceImpl(RpAccountsTableHelper rpAccountsTableHelper,
                                     RelyPartsTableHelper relyPartsTableHelper,
                                     AuthenticationHelper authenticationHelper,
                                     AccountInfoResponse accountInfoResponse,
                                     SystemErrorResponse systemErrorResponse,
                                     RelyPartHelper relyPartHelper) {
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
        this.authenticationHelper = authenticationHelper;
        this.accountInfoResponse = accountInfoResponse;
        this.systemErrorResponse = systemErrorResponse;
        this.relyPartHelper = relyPartHelper;
    }

    @Override
    public AuthAPIResponse getAccountInfo(String appUuid, String token, String accountUuid, String accountName) {
        //检查参数
        if (appUuid.isEmpty() || token.isEmpty() || (accountUuid.isEmpty() && (accountName.isEmpty())))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 检查应用是否有效
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpUuid(appUuid);
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_APP_NOT_FOUND);

        // 获取账户
        RpAccounts rpAccount = null;
        if (!accountName.isEmpty()) {
            // 通过账户名
            rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndAccountName(relyPart.getId(), accountName);
        } else  if (!accountUuid.isEmpty()) {
            // 通过账户UUID
            rpAccount = rpAccountsTableHelper.getRpAccountByRpAccountUuid(accountUuid);
        }
        if (rpAccount == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_ACCOUNT);

        // 校验 token
        if (!relyPartHelper.verifyToken(relyPart, token))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        accountInfoResponse.setAccount_id(rpAccount.getRp_account_uuid());
        accountInfoResponse.setAccount_name(rpAccount.getRp_account_name());
        accountInfoResponse.setAuthorized(rpAccount.getAuthred());
        accountInfoResponse.setAuthorized_at(rpAccount.getAuthr_at());
        systemErrorResponse.fillErrorResponse(accountInfoResponse, ErrorCodeEnum.ERROR_OK);
        return accountInfoResponse;
    }
}
