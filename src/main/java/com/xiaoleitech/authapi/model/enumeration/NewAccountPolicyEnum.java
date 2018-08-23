package com.xiaoleitech.authapi.model.enumeration;

public enum NewAccountPolicyEnum {
    //   new_account_policy: integer
    //		0:enroll & allow；1:enroll & tobe active；2: ID code / Verify Code active
    NEED_ENROLL_ONLY(0),
    NEED_ENROLL_TOKEN_ACTIVE(1),
    NEED_VERIFY_CODE(2),;

    private int newAccountPolicy;

    NewAccountPolicyEnum(int newAccountPolicy) {
        this.newAccountPolicy = newAccountPolicy;
    }

    public int getNewAccountPolicy() {
        return newAccountPolicy;
    }

    public void setNewAccountPolicy(int newAccountPolicy) {
        this.newAccountPolicy = newAccountPolicy;
    }
}
