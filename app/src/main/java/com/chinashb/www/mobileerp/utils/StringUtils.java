package com.chinashb.www.mobileerp.utils;

/***
 * @date 创建时间 2018/7/12 1:32 PM
 * @author 作者: liweifeng
 * @description string集合的工具类
 */
public class StringUtils {
    private static int MAX_LENGTH_STRING_NUMBER = 6;
    public static boolean isStringValid(String str) {
        if (str != null && str.length() > 0) {
            return true;
        }
        return false;
    }

    //最多显示多少位，超过...
    public static String transferTooLongString(String originalStr) {

        if (originalStr == null){
            return "";
        }
        if (originalStr.length() > MAX_LENGTH_STRING_NUMBER) {
            return originalStr.subSequence(0, MAX_LENGTH_STRING_NUMBER) + "...";
        }
        return originalStr;
    }
}
