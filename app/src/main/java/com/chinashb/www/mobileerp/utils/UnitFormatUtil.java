package com.chinashb.www.mobileerp.utils;

import android.text.TextUtils;

import com.chinashb.www.mobileerp.APP;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * @date 创建时间 2018/3/22 17:36
 * @author 作者: yulong
 * @description 一些单位换算的工具类
 */
public final class UnitFormatUtil {
//    /*** 大写数字*/
//    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆",
//            "柒", "捌", "玖"};
//    /**整数部分的单位*/
//    private static final String[] IUNIT = {"元", "拾", "佰", "仟", "万", "拾", "佰",
//            "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟"};
//    /**小数部分的单位*/
//    private static final String[] DUNIT = {"角", "分"};

    public final static String UNIT_TON = "吨";
    public final static String UNIT_KG = "千克";
    public final static String UNIT_RMB_YUAN = "元";
    public final static String UNIT_PIECE = "件";
    public final static String UNIT_YUAN_EVERY_TON = "元/吨";
    public final static String UNIT_TEN_THOUSANDS = "万";

    public  static  SimpleDateFormat sdf_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static  SimpleDateFormat sdf_YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static  SimpleDateFormat sdf_YMD = new SimpleDateFormat("yyyy-MM-dd");
    public static  SimpleDateFormat sdf_YMD_NO_Line = new SimpleDateFormat("yyyyMMdd");
    public static  SimpleDateFormat sdf_MDH = new SimpleDateFormat("MM月dd日 HH点");
    public static  SimpleDateFormat sdf_YMDH = new SimpleDateFormat("yyyy年MM月dd日 HH点");
    public static SimpleDateFormat sdf_HMS = new SimpleDateFormat("HH:mm:ss");

    public static final int ONE_DAY_TIME_IN_MILL_SECOND = 86400000;

    private static class UtilHelper {
//        private static SimpleDateFormat sdf_YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        private static SimpleDateFormat sdf_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        private static SimpleDateFormat sdf_YMD = new SimpleDateFormat("yyyy-MM-dd");

//        private static SimpleDateFormat sdf_HMS = new SimpleDateFormat("HH:mm:ss");
    }

    /***故意私有构造和final声明，目的为了禁止继承和实例化*/
    private UnitFormatUtil() {
    }

    // 12345 -> "123.45"
    public static float kiloToTonReturnFloat(long kilo) {
        BigDecimal fenDecimal = new BigDecimal(kilo);
        BigDecimal yuanDecimal = fenDecimal.divide(new BigDecimal(1000));
        return yuanDecimal.floatValue();
    }

    /***
     * 将千克格式化为吨
     * @param kilo 千克
     * @param unit 单位
     * @return
     */
    public static String kiloToTonReturnString(long kilo, String unit) {
        return kiloToTonReturnString(kilo) + unit;
    }

    /****
     * 将千克格式化为吨
     * @param kilo 千克
     * @return
     */
    public static String kiloToTonReturnString(long kilo) {
        return trimDotEndZero(String.valueOf(kiloToTonReturnFloat(kilo)));
    }


    public static float centimeterToMeterReturnFloat(long centimeter) {
        BigDecimal centimeterDecimal = new BigDecimal(centimeter);
        BigDecimal meterDecimal = centimeterDecimal.divide(new BigDecimal(100));
        return meterDecimal.floatValue();
    }

    public static String centimeterToMeterReturnString(long centimeter) {
        return trimDotEndZero(String.valueOf(centimeterToMeterReturnFloat(centimeter)));
    }

    /*将元转换为分*/
    public static int yuan2CentInt(double cent) {
        return (int) (cent * 100);
    }

    public static long ton2KgLong(double ton) {
        return (long) (ton * 1000);
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param oriStr
     * @return
     */
    public static String trimDotEndZero(String oriStr) {
        if (oriStr.indexOf(".") > 0) {
            //去掉多余的0
            oriStr = oriStr.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            oriStr = oriStr.replaceAll("[.]$", "");
        }
        return oriStr;
    }


    public static String formatTimeToDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf_YMD.format(calendar.getTime());
    }

    public static String formatTimeToDayWithoutLine(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf_YMD_NO_Line.format(calendar.getTime());
    }

    public static String formatTimeToSecond(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf_YMDHMS.format(calendar.getTime());
    }

    /*时间换算*/
    public static String formatTimeToMinute(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf_YMDHM.format(calendar.getTime());
    }

    /*年月日小时*/
    public static String formatTimeToYearHour(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf_YMDH.format(calendar.getTime());
    }

    /*月日小时*/
    public static String formatTimeToHour(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf_MDH.format(calendar.getTime());
    }

    public static String formatLongToHMS(long date){
//        return UtilHelper.sdf_HMS.format(date);
        return sdf_HMS.format(date);
    }

    public static String formatKG2TonWeight(long weight, String unit) {
        return formatKG2TonWeight(weight) + unit;
    }

    /**
     * 将千克格式化为吨，小数点留3位
     */
    public static String formatKG2TonWeight(long weight) {
        return trimDotEndZero(String.format("%.03f", (double) weight / 1000));

    }

    /***
     * 将数额换算成钱的格式
     * @param price 传入的参数已分为单位
     * @return 例如: 123456,换算成 1,234.56
     */
    public static String formatMoney(double price) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return trimDotEndZero(numberFormat.format(price / (100)));
    }

    /***
     * 将数额换算成钱的格式
     * @param price 传入的参数已分为单位
     * @param unit 传入的单位，如: 元、元/吨
     * @return 例如: 123456,换算成 1,234.56
     */
    public static String formatMoney(double price, String unit) {
        if (unit.equals(UNIT_TEN_THOUSANDS)) {
            price /= 10000;
        }
        return formatMoney(price) + unit;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        float scale = APP.get().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /*** @param date 字符串转为 long 型*/
    public static long formatDateLong(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0;
        }
        Date date1 = null;
        try {
            date1 = sdf_YMD.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date1.getTime();
    }


    /*将手机号码格式化为3-4-4样式*/
    public static String formatPhoneNumber(String s) {
        String regex1 = "(.{3})";
        String regex2 = "(.{8})";
        return s.replaceAll(" ", "").replaceFirst(regex1, "$1 ").replaceFirst(regex2, "$1 ");
    }

    // Java调试以上方法用
    public static void main(String[] args) {
        System.out.println((UnitFormatUtil.formatMoney(13213120)));
        System.out.println((UnitFormatUtil.formatTimeToMinute(System.currentTimeMillis() - 3600 * 1000)));
    }

    //将厘米转换为米
    public static String formatCM2M(long cm) {
        double meter = ((double) cm) / 100;
        return trimDotEndZero(String.format("%.02f", meter));
    }


    /**
     * 不使用千分符保留两位小数
     */
    public static String fenToYuanReturnString(long fen) {
        if (fen <= 0) {
            return "0.00";
        } else {
            double yuan = (double) fen / 100;
            return String.format("%.02f", yuan);
        }
    }

    /**
     * 使用千分符保留两位小数
     */
    public static String fenToYuanWalletReturnString(long fen) {
        if (fen <= 0) {
            return "0.00";
        } else {
            double yuan = (double) fen / 100;
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            return decimalFormat.format(yuan);
        }
    }


    public static String formatVideoTime(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }


}
