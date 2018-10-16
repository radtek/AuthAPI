package com.xiaoleitech.authapi.rpmanage.service;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
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
