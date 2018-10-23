package com.xiaoleitech.authapi.global.enumeration;

public enum CaAgencyEnum {
    CA_XL(0),       // 小雷科技自有CA，内部测试CA
    CA_XBCA(1),     // 西部CA
    CA_GZCA(2),     // 贵州CA
    CA_TWCX(3),     // 天威诚信CA
    CA_SHCA(4),     // 上海CA
    ;
    private int agency;

    CaAgencyEnum(int agency) {
        this.agency = agency;
    }

    public int getAgency() {
        return agency;
    }

    public void setAgency(int agency) {
        this.agency = agency;
    }
}
