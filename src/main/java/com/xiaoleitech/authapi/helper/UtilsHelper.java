package com.xiaoleitech.authapi.helper;

public class UtilsHelper {
    /**
     * 获取当前系统时间，按SQL的时间戳格式
     *
     * @return java.sql.Timestamp currentTime
     */
    static public java.sql.Timestamp getCurrentSystemTimestamp() {
        java.util.Date utilDate = new java.util.Date();

        return new java.sql.Timestamp(utilDate.getTime());
    }
}
