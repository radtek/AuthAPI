package com.xiaoleitech.authapi.model.enumeration;

public enum ProtectMethodEnum {
    // TODO: protect_method : string，保护方式，为打开AHAPP、认证身份、授权应用时提取认证信息时的保护方式。
    // 1:口令；2:指纹；3:虹膜；4:声纹；5:面部
    // 先后顺序表示优先顺序。如：“241”表示可用指纹、虹膜、口令保护。默认为指纹。
}
