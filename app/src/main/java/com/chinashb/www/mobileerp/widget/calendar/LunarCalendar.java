/*
 * Copyright (C) 2016  liweifeng
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinashb.www.mobileerp.widget.calendar;

import android.content.Context;

import com.chinashb.www.mobileerp.R;


final class LunarCalendar {

    /**
     * 农历月份第一天转写
     */
    private static String[] MONTH_STR = null;
    /**
     * 农历大写
     */
    private static String[] DAY_STR = null;

    static void init(Context context) {
        if (MONTH_STR != null) {
            return;
        }
        MONTH_STR = context.getResources().getStringArray(R.array.lunar_first_of_month);
        DAY_STR = context.getResources().getStringArray(R.array.lunar_str);
    }

    /**
     * 数字转换为汉字月份
     *
     * @param month 月
     * @param leap  1==闰月
     * @return 数字转换为汉字月份
     */
    private static String numToChineseMonth(int month, int leap) {
        if (leap == 1) {
            return String.format("闰%s", MONTH_STR[month - 1]);
        }
        return MONTH_STR[month - 1];
    }

    /**
     * 数字转换为农历节日或者日期
     *
     * @param month 月
     * @param day   日
     * @param leap  1==闰月
     * @return 数字转换为汉字日
     */
    private static String numToChinese(int month, int day, int leap) {
        if (day == 1) {
            return numToChineseMonth(month, leap);
        }
        return DAY_STR[day - 1];
    }

    /**
     * 初始化各种农历、节日
     *
     * @param calendar calendar
     */
    static void setupLunarCalendar(CalendarEntity calendar) {
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        calendar.setWeekend(CalendarUtil.isWeekend(calendar));
        calendar.setWeek(CalendarUtil.getWeekFormCalendar(calendar));

        CalendarEntity lunarCalendar = new CalendarEntity();
        calendar.setLunarCalendar(lunarCalendar);
        int[] lunar = LunarUtil.solarToLunar(year, month, day);
        lunarCalendar.setYear(lunar[0]);
        lunarCalendar.setMonth(lunar[1]);
        lunarCalendar.setDay(lunar[2]);
        calendar.setLeapYear(CalendarUtil.isLeapYear(year));
        if (lunar[3] == 1) {//如果是闰月
            calendar.setLeapMonth(lunar[1]);
            lunarCalendar.setLeapMonth(lunar[1]);
        }
        calendar.setLunar(LunarCalendar.numToChinese(lunar[1], lunar[2], lunar[3]));
        lunarCalendar.setLunar(calendar.getLunar());
    }

}
