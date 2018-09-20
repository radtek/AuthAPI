package com.xiaoleitech.authapi.service.enquiry;

import com.xiaoleitech.authapi.helper.table.UsersTableHelper;
import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.model.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.model.pojo.Users;
import com.xiaoleitech.authapi.service.exception.SystemErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckUserIdExistServiceImpl implements CheckUserIdExistService {
    private final SystemErrorResponse systemErrorResponse;
    private final UsersTableHelper usersTableHelper;

    @Autowired
    public CheckUserIdExistServiceImpl(SystemErrorResponse systemErrorResponse, UsersTableHelper usersTableHelper) {
        this.systemErrorResponse = systemErrorResponse;
        this.usersTableHelper = usersTableHelper;
    }

    @Override
    public AuthAPIResponse isUserIdExist(String userUuid) {
        // 检查参数
        if (userUuid.isEmpty())
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 根据 user_uuid 查找系统中的有效用户
        Users user = usersTableHelper.getUserByUserUuid(userUuid);
        if ( user == null )
            return systemErrorResponse.getGeneralResponse(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return systemErrorResponse.getSuccessResponse();
    }
}
