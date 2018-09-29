package com.xiaoleitech.authapi.model.rpmanage;

import com.xiaoleitech.authapi.model.bean.AuthAPIResponse;
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
