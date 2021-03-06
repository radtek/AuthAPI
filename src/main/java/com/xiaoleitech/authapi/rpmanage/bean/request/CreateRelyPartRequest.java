package com.xiaoleitech.authapi.rpmanage.bean.request;

import com.xiaoleitech.authapi.rpmanage.bean.RelyPartParams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class CreateRelyPartRequest extends RelyPartParams {

}
