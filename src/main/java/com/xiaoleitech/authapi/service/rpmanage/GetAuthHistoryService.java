package com.xiaoleitech.authapi.service.rpmanage;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetAuthHistoryService {

    AuthAPIResponse getAuthHistoryRecordsCount(String appUuid,
                                               String token,
                                               String accountName,
                                               String accountUuid);

    AuthAPIResponse getAuthHistories(String appUuid,
                                     String token,
                                     String accountName,
                                     String accountUuid,
                                     int from,
                                     int count);
}
