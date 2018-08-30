package com.xiaoleitech.authapi.service.enrollment;

import com.xiaoleitech.authapi.helper.RelyPartsTableHelper;
import com.xiaoleitech.authapi.helper.RpAccountsTableHelper;
import com.xiaoleitech.authapi.helper.UsersHelper;
import com.xiaoleitech.authapi.helper.UsersTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppRequest;
import com.xiaoleitech.authapi.model.enrollment.EnrollAppResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
import com.xiaoleitech.authapi.model.pojo.RpAccounts;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class EnrollmentServiceImpl implements EnrollmentService {
    private final SystemErrorResponse systemErrorResponse;
    private final EnrollAppResponse enrollAppResponse;
    private final UsersTableHelper usersTableHelper;
    private final RpAccountsTableHelper rpAccountsTableHelper;
    private final RelyPartsTableHelper relyPartsTableHelper;

    @Autowired
    public EnrollmentServiceImpl(SystemErrorResponse systemErrorResponse,
                                 EnrollAppResponse enrollAppResponse,
                                 UsersTableHelper usersTableHelper,
                                 RpAccountsTableHelper rpAccountsTableHelper,
                                 RelyPartsTableHelper relyPartsTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.enrollAppResponse = enrollAppResponse;
        this.usersTableHelper = usersTableHelper;
        this.rpAccountsTableHelper = rpAccountsTableHelper;
        this.relyPartsTableHelper = relyPartsTableHelper;
    }

    @Override
    public AuthAPIResponse enrollApp(EnrollAppRequest enrollAppRequest, BindingResult bindingResult) {
        // 用户必须存在
        Users user = usersTableHelper.getUserByUserId(enrollAppRequest.getUser_id());
        if (user == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 验证令牌
        if (!UsersHelper.isUserVerifyToken(user, enrollAppRequest.getVerify_token()))
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_TOKEN);

        // 通过app_id查找 rps(依赖方) 表中的记录，找不到记录则不能进行登记操作
        RelyParts relyPart = relyPartsTableHelper.getRelyPartByRpId(enrollAppRequest.getApp_id());
        if (relyPart == null)
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_INVALID_APP);

        // 查找 rpaccounts 中的记录
        RpAccounts rpAccount = rpAccountsTableHelper.getRpAccountByRpIdAndUserId(
                enrollAppRequest.getApp_id(), enrollAppRequest.getUser_id());
        if (rpAccount == null) {
            // TODO: rpaccounts 没有这条记录，新建一条记录
        } else {
            // TODO: 用请求的参数更新这条记录
        }

        // TODO: 没有实现。
        systemErrorResponse.fillErrorResponse(enrollAppResponse, ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
        return enrollAppResponse;
    }
}
