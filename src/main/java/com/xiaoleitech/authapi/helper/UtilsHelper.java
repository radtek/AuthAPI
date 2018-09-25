package com.xiaoleitech.authapi.helper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsHelper {
    static public String generatePrintableRandom(int length) {
        String valueBuff = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        StringBuilder stringBuilder = new StringBuilder();
        String authKey = "";
        Random random = new Random();
        for (int i=0; i<length; i++) {
            int index = random.nextInt(valueBuff.length());
            stringBuilder.append(valueBuff.charAt(index));
        }

        return stringBuilder.toString();
    }

    /**
     * 获取一个List集合的第一项
     * @param objList List 集合
     * @return List 集合的第一项，如果集合为空，则返回 null
     */
    static public <T> T getFirstValid(List<T> objList) {
        if (objList == null)
            return null;

        if (objList.size() == 0)
            return null;
        else
            return objList.get(0);
    }

    /**
     * 判定传入的属性字符串里，是否含有指定的属性
     * @param props 属性字符串，含 0 到多个属性
     * @param property 属性（个位正整数，0-9）
     * @return
     *          true -- 字符串中含有指定属性
     *          false -- 字符串中找不到指定属性
     */
    static public boolean isPropertyExist(String props, int property) {
        if ((props == null) || props.isEmpty())
            return false;
        String propertyString = Integer.toString(property);
        return props.contains(propertyString);
    }

    /**
     * 获取当前系统时间，按SQL的时间戳格式
     *
     * @return java.sql.Timestamp currentTime
     */
    static public java.sql.Timestamp getCurrentSystemTimestamp() {
        java.util.Date utilDate = new java.util.Date();

        return new java.sql.Timestamp(utilDate.getTime());
    }

    /** 提取字符串中的数字，并转换成整数。支持负数。
     *
     * @param origin 可能带有非数字的字符串
     * @return 提取并转换得到的整数
     */
    static public int extractInt(String origin) {
        String regex="[^\\d^-]+";
        origin = origin.replaceAll(regex, "");
        return Integer.parseInt(origin);
    }
//    static public int extractInt(String origin) {
//        origin = origin.replaceAll("[^0-9]", "");
//        return Integer.parseInt(origin);
//    }

    /** 从字符串中提取时间，并转换成 java.sql.Timestamp 类型返回
     *
     * @param timeString 含有时间的字符串
     * @param timeFormat 字符串内时间格式，比如: "yyyy/MM/dd"
     * @return java.sql.Timestamp 类型的时间
     */
    static public Timestamp parseTimeFromString(String timeString, String timeFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        Timestamp timestamp = new Timestamp(0);
        try {
            Date date = simpleDateFormat.parse(timeString);
            timestamp = new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    static public Timestamp parseExpireTimeFromString(String timeString, char splitChar) {
        String timeFormat = "yyyy/MM/dd";
        // 如果原始时间字符串中，分隔符不是'/'，则更换分隔符为'/'
        if (splitChar != '/') {
            timeFormat = timeFormat.replace(splitChar, '/');
        }

        // 从输入字符串中提取SQL的Timestamp时间
        Timestamp timestamp = parseTimeFromString(timeString, timeFormat);
        // 此时获得的时间不是当天最后一秒。对过期时间来说，需要把时间调整到当天最后一秒
        Long milliSeconds = timestamp.getTime();
        milliSeconds += (24 * 60 * 60 - 1) * 1000;
        timestamp.setTime(milliSeconds);

        return timestamp;
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
    * @param src byte[] data
    * @return hex string
    */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
