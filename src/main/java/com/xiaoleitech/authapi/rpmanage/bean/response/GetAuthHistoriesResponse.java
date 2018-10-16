package com.xiaoleitech.authapi.rpmanage.bean.response;

import com.xiaoleitech.authapi.global.bean.AuthAPIResponse;
import com.xiaoleitech.authapi.rpmanage.bean.AuthHistoryRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class GetAuthHistoriesResponse extends AuthAPIResponse {
    private List<AuthHistoryRecord> auth_history;
}
