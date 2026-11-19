package com.chinashb.www.mobileerp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 成品发货服务端日期与数量的统一显示格式。
 */
public final class ShipWorkFormatUtil {

    private static final Pattern ASP_NET_DATE = Pattern.compile("/Date\\((\\d+)");
    private static final Pattern ISO_DATE = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}).*");

    private ShipWorkFormatUtil() {
    }

    public static String date(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        Matcher aspNet = ASP_NET_DATE.matcher(raw);
        if (aspNet.find()) {
            try {
                Date date = new Date(Long.parseLong(aspNet.group(1)));
                return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
            } catch (Exception ignored) {
            }
        }
        Matcher iso = ISO_DATE.matcher(raw);
        if (iso.matches()) {
            return iso.group(1);
        }
        return raw;
    }

    public static String dateTime(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        Matcher aspNet = ASP_NET_DATE.matcher(raw);
        if (aspNet.find()) {
            try {
                Date date = new Date(Long.parseLong(aspNet.group(1)));
                return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date);
            } catch (Exception ignored) {
            }
        }
        return raw;
    }

    public static String quantity(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
