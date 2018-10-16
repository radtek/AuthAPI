package com.xiaoleitech.authapi.global.enumeration;

public enum ProtectMethodEnum {
    // protect_methods : string，保护方式，为打开AHAPP、认证身份、授权应用时提取认证信息时的保护方式。
    // protectMethod: int
    // 1:口令；2:指纹；3:虹膜；4:声纹；5:面部
    // 可以用字符串组合，先后顺序表示优先顺序。如：“241”表示可用指纹、虹膜、口令保护。默认为指纹。
    VERIFY_PASSWORD(1),
    VERIFY_FINGER_PRINT(2),
    VERIFY_IRIS(3),
    VERIFY_VOICE_PRINT(4),
    VERIFY_FACE(5),;

    private int protectMethod;

    ProtectMethodEnum(int protectMethod) {
        this.protectMethod = protectMethod;
    }

    public int getProtectMethod() {
        return protectMethod;
    }

    public void setProtectMethod(int protectMethod) {
        this.protectMethod = protectMethod;
    }

    public boolean isMatched(int protectMethod) {
        return (this.protectMethod == protectMethod);
    }
}
