package com.chinashb.www.mobileerp.utils;

/***
 * @date 创建时间 2019/6/19 10:28 AM
 * @author 作者: liweifeng
 * @description sharedPreference一些常量
 */
public class SPDefine {
    public static final String SP_login_user_name = "login_user_name";

    public static final String SP_logistics_company_bean_string = "logistics_company_bean_string";
    public static final String SP_logistics_bu_bean_string = "logistics_bu_bean_string";


    public static final String KEY_attendance_rule = "attendance_rule";


    public static String KEY_today_has_attendance = UnitFormatUtil.getCurrentYMD() + "attendance";
    public static String KEY_today_onduty_success = UnitFormatUtil.getCurrentYMD() + "ondutysuccess";
    public static String KEY_today_attendance_success = UnitFormatUtil.getCurrentYMD() + "todaysuccess";
}
