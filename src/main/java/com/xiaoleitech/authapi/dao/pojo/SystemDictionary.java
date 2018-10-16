package com.xiaoleitech.authapi.dao.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SystemDictionary {
    private int dict_id;
    private String dict_uuid;
    private String dict_key;
    private String dict_value;
    private String dict_comment;
}
