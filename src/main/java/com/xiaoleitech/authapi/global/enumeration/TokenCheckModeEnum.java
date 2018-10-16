package com.xiaoleitech.authapi.global.enumeration;

public enum TokenCheckModeEnum {
    NOT_CHECK(0, "not check the token"),
    CHECK_TOKEN(1, "check the token"),
    ;

    private int id;
    private String comment;

    TokenCheckModeEnum(int id, String comment) {
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
