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

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 一些日期辅助计算工具
 */
final class CalendarUtil {

    private static final long ONE_DAY = 1000 * 3600 * 24;

    @SuppressLint("SimpleDateFormat")
    static int getDate(String formatStr, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return Integer.parseInt(format.format(date));
    }

    /**
     * 判断一个日期是否是周末，即周六日
     *
     * @param calendar calendar
     * @return 判断一个日期是否是周末，即周六日
     */
    static boolean isWeekend(CalendarEntity calendar) {
        int week = getWeekFormCalendar(calendar);
        return week == 0 || week == 6;
    }

    /**
     * 获取某月的天数
     *
     * @param year  年
     * @param month 月
     * @return 某月的天数
     */
    static int getMonthDaysCount(int year, int month) {
        int count = 0;
        //判断大月份
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            count = 31;
        }

        //判断小月
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            count = 30;
        }

        //判断平年与闰年
        if (month == 2) {
            if (isLeapYear(year)) {
                count = 29;
            } else {
                count = 28;
            }
        }
        return count;
    }


    /**
     * 是否是闰年
     *
     * @param year year
     * @return return
     */
    static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }


    /**
     * 获取月视图的确切高度
     * Test pass
     *
     * @param year       年
     * @param month      月
     * @param itemHeight 每项的高度
     * @return 不需要多余行的高度
     */
    static int getMonthViewHeight(int year, int month, int itemHeight, int weekStartWith) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, 1);
        int preDiff = getMonthViewStartDiff(year, month, weekStartWith);
        int monthDaysCount = getMonthDaysCount(year, month);
        int nextDiff = getMonthEndDiff(year, month, monthDaysCount, weekStartWith);
        return (preDiff + monthDaysCount + nextDiff) / 7 * itemHeight;
    }


    /**
     * 获取某天在该月的第几周,换言之就是获取这一天在该月视图的第几行
     * Test pass
     *
     * @param calendar  calendar
     * @param weekStart 其实星期是哪一天？
     * @return 获取某天在该月的第几周 the week line in MonthView
     */
    static int getWeekFromDayInMonth(CalendarEntity calendar, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, 1);
        //该月第一天为星期几,星期天 == 0，也就是偏移量
        int diff = getMonthViewStartDiff(calendar, weekStart);
        return (calendar.getDay() + diff - 1) / 7 + 1;
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * 获取日期所在月视图对应的起始偏移量
     * Test pass
     *
     * @param calendar  calendar
     * @param weekStart weekStart 星期的起始
     * @return 获取日期所在月视图对应的起始偏移量 the start diff with MonthView
     */
    static int getMonthViewStartDiff(CalendarEntity calendar, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, 1);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return week - 1;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == CalendarViewDelegate.WEEK_START_WITH_SAT ? 0 : week;
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * 获取日期所在月份的结束偏移量，用于计算两个年份之间总共有多少周，不用于MonthView
     * Test pass
     *
     * @param calendar  calendar
     * @param weekStart weekStart 星期的起始
     * @return 获取日期所在月份的结束偏移量 the end diff in Month not MonthView
     */
    @SuppressWarnings("unused")
    static int getMonthEndDiff(CalendarEntity calendar, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, getMonthDaysCount(calendar.getYear(), calendar.getMonth()));
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return 7 - week;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 0 : 7 - week + 1;
        }
        return week == CalendarViewDelegate.WEEK_START_WITH_SAT ? 6 : 7 - week - 1;
    }

    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * 获取日期所在月视图对应的起始偏移量
     * Test pass
     *
     * @param year      年
     * @param month     月
     * @param weekStart 周起始
     * @return 获取日期所在月视图对应的起始偏移量 the start diff with MonthView
     */
    static int getMonthViewStartDiff(int year, int month, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, 1);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return week - 1;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == CalendarViewDelegate.WEEK_START_WITH_SAT ? 0 : week;
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * 获取日期月份对应的结束偏移量,用于计算两个年份之间总共有多少周，不用于MonthView
     * Test pass
     *
     * @param year      年
     * @param month     月
     * @param weekStart 周起始
     * @return 获取日期月份对应的结束偏移量 the end diff in Month not MonthView
     */
    static int getMonthEndDiff(int year, int month, int weekStart) {
        return getMonthEndDiff(year, month, getMonthDaysCount(year, month), weekStart);
    }


    /**
     * DAY_OF_WEEK return  1  2  3 	4  5  6	 7，偏移了一位
     * 获取日期月份对应的结束偏移量,用于计算两个年份之间总共有多少周，不用于MonthView
     * Test pass
     *
     * @param year      年
     * @param month     月
     * @param weekStart 周起始
     * @return 获取日期月份对应的结束偏移量 the end diff in Month not MonthView
     */
    private static int getMonthEndDiff(int year, int month, int day, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, day);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return 7 - week;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == 1 ? 0 : 7 - week + 1;
        }
        return week == 7 ? 6 : 7 - week - 1;
    }

    /**
     * 获取某个日期是星期几
     * 测试通过
     *
     * @param calendar 某个日期
     * @return 返回某个日期是星期几
     */
    static int getWeekFormCalendar(CalendarEntity calendar) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        return date.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }


    /**
     * 获取周视图的切换默认选项位置 WeekView index
     *
     * @param calendar  calendar
     * @param weekStart weekStart
     * @return 获取周视图的切换默认选项位置
     */
    static int getWeekViewIndexFromCalendar(CalendarEntity calendar, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        int weekStartDiff = getWeekViewStartDiff(calendar.getYear(), calendar.getMonth(), calendar.getDay(), weekStart);
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return weekStartDiff;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return weekStartDiff == 1 ? 6 : weekStartDiff;
        }
        return weekStartDiff == 7 ? 0 : weekStartDiff;
    }

    /**
     * 获取某年第几周是在第几个月
     * 无用
     *
     * @param year       年
     * @param weekInYear 某年第几周
     * @return 第几个月
     */
    @Deprecated
    static int getMonthFromWeekFirstDayInYear(int year, int weekInYear) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, 0, 1);
        int diff = date.get(java.util.Calendar.DAY_OF_WEEK) - 1;//1月第一天为星期几,星期天 == 0，也就是偏移量
        int count = 0;
        int diy = (weekInYear - 1) * 7 - diff + 1;
        for (int i = 1; i <= 12; i++) {
            count += getMonthDaysCount(year, i);
            if (diy <= count) {
                return i;
            }
        }
        return 0;
    }


    /**
     * 获取两个年份之间一共有多少周，注意周起始周一、周日、周六
     * 测试通过 test pass
     *
     * @param minYear      minYear 最小年份
     * @param minYearMonth maxYear 最小年份月份
     * @param maxYear      maxYear 最大年份
     * @param maxYearMonth maxYear 最大年份月份
     * @param weekStart    周起始
     * @return 周数用于WeekViewPager itemCount
     */
    static int getWeekCountBetweenYearAndYear(int minYear, int minYearMonth, int maxYear, int maxYearMonth, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(minYear, minYearMonth - 1, 1);
        long minTimeMills = date.getTimeInMillis();//给定时间戳
        int preDiff = getMonthViewStartDiff(minYear, minYearMonth, weekStart);

        date.set(maxYear, maxYearMonth - 1, getMonthDaysCount(maxYear, maxYearMonth));

        long maxTimeMills = date.getTimeInMillis();//给定时间戳

        int nextDiff = getMonthEndDiff(maxYear, maxYearMonth, weekStart);

        int count = preDiff + nextDiff;

        int c = (int) ((maxTimeMills - minTimeMills) / ONE_DAY) + 1;
        count += c;
        return count / 7;
    }

    /**
     * 根据日期获取两个年份中第几周,用来设置 WeekView currentItem
     * 测试通过 Test Pass
     *
     * @param calendar calendar
     * @param minYear  minYear
     * @return 返回两个年份中第几周 the WeekView currentItem
     */
    static int getWeekFromCalendarBetweenYearAndYear(CalendarEntity calendar, int minYear, int minYearMonth, int weekStar) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(minYear, minYearMonth - 1, 1);//起始日期
        long firstTimeMill = date.getTimeInMillis();//获得范围起始时间戳

        int preDiff = getWeekViewStartDiff(minYear, minYearMonth, 1, weekStar);//范围起始的周偏移量

        int weekStartDiff = getWeekViewStartDiff(calendar.getYear(),
                calendar.getMonth(),
                calendar.getDay(),
                weekStar);//获取点击的日子在周视图的起始，为了兼容全球时区，最大日差为一天，如果周起始偏差weekStartDiff=0，则日期加1

        date.set(calendar.getYear(),
                calendar.getMonth() - 1,
                weekStartDiff == 0 ? calendar.getDay() + 1 : calendar.getDay());

        long curTimeMills = date.getTimeInMillis();//给定时间戳

        int c = (int) ((curTimeMills - firstTimeMill) / ONE_DAY);

        int count = preDiff + c;

        return count / 7 + 1;
    }


    /**
     * 是否在日期范围內
     *
     * @param calendar     calendar
     * @param minYear      minYear
     * @param minYearMonth minYearMonth
     * @param maxYear      maxYear
     * @param maxYearMonth maxYearMonth
     * @return 是否在日期范围內
     */
    static boolean isCalendarInRange(CalendarEntity calendar, int minYear, int minYearMonth, int maxYear, int maxYearMonth) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(minYear, minYearMonth - 1, 1);
        long minTime = c.getTimeInMillis();
        c.set(maxYear, maxYearMonth - 1, getMonthDaysCount(maxYear, maxYearMonth));
        long maxTime = c.getTimeInMillis();
        c.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curTime = c.getTimeInMillis();
        return curTime >= minTime && curTime <= maxTime;
    }

    static boolean isCalendarInRange(CalendarEntity calendar, CalendarViewDelegate delegate) {
        return isCalendarInRange(calendar, delegate.getMinYear(), delegate.getMinYearMonth(),
                delegate.getMaxYear(), delegate.getMaxYearMonth());
    }

    /**
     * 是否在日期范围內
     *
     * @param year         year
     * @param month        month
     * @param minYear      minYear
     * @param minYearMonth minYearMonth
     * @param maxYear      maxYear
     * @param maxYearMonth maxYearMonth
     * @return 是否在日期范围內
     */
    static boolean isMonthInRange(int year, int month, int minYear, int minYearMonth, int maxYear, int maxYearMonth) {
        return !(year < minYear || year > maxYear) &&
                !(year == minYear && month < minYearMonth) &&
                !(year == maxYear && month > maxYearMonth);
    }

    /**
     * 根据星期数和最小年份推算出该星期的第一天
     * 测试通过 Test pass
     *
     * @param minYear      最小年份如2017
     * @param minYearMonth maxYear 最小年份月份，like : 2017-07
     * @param week         从最小年份minYear月minYearMonth 日1 开始的第几周 week > 0
     * @return 该星期的第一天日期
     */
    static CalendarEntity getFirstCalendarFromWeekCount(int minYear, int minYearMonth, int week, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(minYear, minYearMonth - 1, 1);//

        long firstTimeMills = date.getTimeInMillis();//获得起始时间戳


        long weekTimeMills = (week - 1) * 7 * ONE_DAY;

        long timeCountMills = weekTimeMills + firstTimeMills;

        date.setTimeInMillis(timeCountMills);

        int startDiff = getWeekViewStartDiff(date.get(java.util.Calendar.YEAR),
                date.get(java.util.Calendar.MONTH) + 1,
                date.get(java.util.Calendar.DAY_OF_MONTH), weekStart);

        timeCountMills -= startDiff * ONE_DAY;
        date.setTimeInMillis(timeCountMills);

        CalendarEntity calendar = new CalendarEntity();
        calendar.setYear(date.get(java.util.Calendar.YEAR));
        calendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
        calendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));

        return calendar;
    }


    /**
     * 为月视图初始化日历
     *
     * @param year        year
     * @param month       month
     * @param currentDate currentDate
     * @param weekStar    weekStar
     * @return 为月视图初始化日历项
     */
    static List<CalendarEntity> initCalendarForMonthView(int year, int month, CalendarEntity currentDate, int weekStar) {
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(year, month - 1, 1);

        int mPreDiff = getMonthViewStartDiff(year, month, weekStar);//获取月视图其实偏移量

        int monthDayCount = getMonthDaysCount(year, month);//获取月份真实天数

        int preYear, preMonth;
        int nextYear, nextMonth;

        int size = 42;

        List<CalendarEntity> mItems = new ArrayList<>();

        int preMonthDaysCount;
        if (month == 1) {//如果是1月
            preYear = year - 1;
            preMonth = 12;
            nextYear = year;
            nextMonth = month + 1;
            preMonthDaysCount = mPreDiff == 0 ? 0 : CalendarUtil.getMonthDaysCount(preYear, preMonth);
        } else if (month == 12) {//如果是12月
            preYear = year;
            preMonth = month - 1;
            nextYear = year + 1;
            nextMonth = 1;
            preMonthDaysCount = mPreDiff == 0 ? 0 : CalendarUtil.getMonthDaysCount(preYear, preMonth);
        } else {//平常
            preYear = year;
            preMonth = month - 1;
            nextYear = year;
            nextMonth = month + 1;
            preMonthDaysCount = mPreDiff == 0 ? 0 : CalendarUtil.getMonthDaysCount(preYear, preMonth);
        }
        int nextDay = 1;
        for (int i = 0; i < size; i++) {
            CalendarEntity calendarDate = new CalendarEntity();
            if (i < mPreDiff) {
                calendarDate.setYear(preYear);
                calendarDate.setMonth(preMonth);
                calendarDate.setDay(preMonthDaysCount - mPreDiff + i + 1);
            } else if (i >= monthDayCount + mPreDiff) {
                calendarDate.setYear(nextYear);
                calendarDate.setMonth(nextMonth);
                calendarDate.setDay(nextDay);
                ++nextDay;
            } else {
                calendarDate.setYear(year);
                calendarDate.setMonth(month);
                calendarDate.setCurrentMonth(true);
                calendarDate.setDay(i - mPreDiff + 1);
            }
            if (calendarDate.equals(currentDate)) {
                calendarDate.setCurrentDay(true);
            }
            LunarCalendar.setupLunarCalendar(calendarDate);
            mItems.add(calendarDate);
        }
        return mItems;
    }


    /**
     * 生成周视图的7个item
     *
     * @param calendar  calendar
     * @param mDelegate delegate
     * @param weekStart weekStart
     * @return 生成周视图的7个item
     */
    static List<CalendarEntity> initCalendarForWeekView(CalendarEntity calendar, CalendarViewDelegate mDelegate, int weekStart) {

        java.util.Calendar date = java.util.Calendar.getInstance();//当天时间
        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        long curDateMills = date.getTimeInMillis();//生成选择的日期时间戳

        int weekEndDiff = getWeekViewEndDiff(calendar.getYear(), calendar.getMonth(), calendar.getDay(), weekStart);
        List<CalendarEntity> mItems = new ArrayList<>();

        date.setTimeInMillis(curDateMills);
        CalendarEntity selectCalendar = new CalendarEntity();
        selectCalendar.setYear(date.get(java.util.Calendar.YEAR));
        selectCalendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
        selectCalendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));
        if (selectCalendar.equals(mDelegate.getCurrentDay())) {
            selectCalendar.setCurrentDay(true);
        }
        LunarCalendar.setupLunarCalendar(selectCalendar);
        selectCalendar.setCurrentMonth(true);
        mItems.add(selectCalendar);


        for (int i = 1; i <= weekEndDiff; i++) {
            date.setTimeInMillis(curDateMills + i * ONE_DAY);
            CalendarEntity calendarDate = new CalendarEntity();
            calendarDate.setYear(date.get(java.util.Calendar.YEAR));
            calendarDate.setMonth(date.get(java.util.Calendar.MONTH) + 1);
            calendarDate.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));
            if (calendarDate.equals(mDelegate.getCurrentDay())) {
                calendarDate.setCurrentDay(true);
            }
            LunarCalendar.setupLunarCalendar(calendarDate);
            calendarDate.setCurrentMonth(true);
            mItems.add(calendarDate);
        }
        return mItems;
    }

    /**
     * 单元测试通过
     * 从选定的日期，获取周视图起始偏移量，用来生成周视图布局
     *
     * @param year      year
     * @param month     month
     * @param day       day
     * @param weekStart 周起始，1，2，7 日 一 六
     * @return 获取周视图起始偏移量，用来生成周视图布局
     */
    private static int getWeekViewStartDiff(int year, int month, int day, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, day);//
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == 1) {
            return week - 1;
        }
        if (weekStart == 2) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == 7 ? 0 : week;
    }


    /**
     * 单元测试通过
     * 从选定的日期，获取周视图结束偏移量，用来生成周视图布局
     *
     * @param year      year
     * @param month     month
     * @param day       day
     * @param weekStart 周起始，1，2，7 日 一 六
     * @return 获取周视图结束偏移量，用来生成周视图布局
     */
    private static int getWeekViewEndDiff(int year, int month, int day, int weekStart) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, month - 1, day);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        if (weekStart == 1) {
            return 7 - week;
        }
        if (weekStart == 2) {
            return week == 1 ? 0 : 7 - week + 1;
        }
        return week == 7 ? 6 : 7 - week - 1;
    }


    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
