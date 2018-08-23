package com.xiaoleitech.authapi.model.enumeration;

public enum ClientTypeEnum {
    //   client_type: integer
    //		0:未知／通用；1:iOS；2:android；4:web
    CLIENT_TYPE_UNKNOWN(0),
    CLIENT_TYPE_IOS(1),
    CLIENT_TYPE_ANDROID(2),
    CLIENT_TYPE_WEB(4),;

    private int clientType;

    ClientTypeEnum(int clientType) {
        this.clientType = clientType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }
}
