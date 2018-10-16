package com.xiaoleitech.authapi.global.enumeration;

public enum PasswordModeEnum {
    PASSWORD_MODE_PLAIN(1, "Plain Password"),
    PASSWORD_MODE_SHA256(2, "SHA256(pwd)"),
    PASSWORD_MODE_SALT_SHA256(3, "SHA256(SHA256(pwd)+salt)"),
    ;
    private int id;
    private String comment;

    PasswordModeEnum(int id, String comment) {
        this.id = id;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
