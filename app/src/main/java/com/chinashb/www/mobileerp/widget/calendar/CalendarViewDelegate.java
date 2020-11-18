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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.chinashb.www.mobileerp.R;

import java.util.Date;
import java.util.List;

/**
 * Google规范化的属性委托,
 * 这里基本是没有逻辑的，代码量多，但是不影响阅读性
 */
final class CalendarViewDelegate {

    static final int WEEK_START_WITH_SUN = 1;

    static final int WEEK_START_WITH_MON = 2;

    static final int WEEK_START_WITH_SAT = 7;

    /**
     * 全部显示
     */
    static final int MODE_ALL_MONTH = 0;
    /**
     * 仅显示当前月份
     */
    static final int MODE_ONLY_CURRENT_MONTH = 1;

    /**
     * 自适应显示，不会多出一行，但是会自动填充
     */
    static final int MODE_FIT_MONTH = 2;

    /**
     * 月份显示模式
     */
    private int mMonthViewShowMode;


    /**
     * 周起始
     */
    private int mWeekStart;

//    /**
//     * 默认选择模式
//     */
//    static final int SELECT_MODE_DEFAULT = 0;
//
//    /**
//     * 单选模式
//     */
//    static final int SELECT_MODE_SINGLE = 1;
//
//    private int mSelectMode;

    /**
     * 支持转换的最小农历年份
     */
    static final int MIN_YEAR = 1900;
    /**
     * 支持转换的最大农历年份
     */
    private static final int MAX_YEAR = 2099;

    /**
     * 各种字体颜色，看名字知道对应的地方
     */
    private int currentDayTextColor,
            currentDayLunarTextColor,
            weekTextColor,
            schemeTextColor,
            schemeLunarTextColor,
            otherMonthTextColor,
            currentMonthTextColor,
            selectedTextColor,
            selectedLunarTextColor,
            currentLunarAfterTodayTextColor,
            currentLunarBeforeTodayTextColor,
            otherMonthLunarTextColor;

    private boolean preventLongPressedSelected;

    /**
     * 日历内部左右padding
     */
    private int mCalendarPadding;

    /**
     * 年视图字体大小
     */
    private int mYearViewMonthTextSize,
            mYearViewDayTextSize;

    /**
     * 年视图字体和标记颜色
     */
    private int mYearViewMonthTextColor,
            mYearViewDayTextColor,
            mYearViewSchemeTextColor;

    /**
     * 星期栏的背景、线的背景、年份背景
     */
    private int weekLineBackground,
            yearViewBackground,
            weekBackground;

    /**
     * 星期栏字体大小
     */
    private int weekTextSize;

    /**
     * 标记的主题色和选中的主题色
     */
    private int schemeThemeColor, selectedThemeColor;


    /**
     * 自定义的日历路径
     */
    private String monthViewClass;

    /**
     * 自定义周视图路径
     */
    private String weekViewClass;

    /**
     * 自定义周栏路径
     */
    private String weekBarClass;


    /**
     * 年月视图是否打开
     */
    boolean isShowYearSelectedLayout;

    /**
     * 标记文本
     */
    private String mSchemeText;

    /**
     * 最小年份和最大年份
     */
    private int minYear, maxYear;

    /**
     * 最小年份和最大年份对应最小月份和最大月份
     * when you want set 2015-07 to 2017-08
     */
    private int minYearMonth, maxYearMonth;

    /**
     * 日期和农历文本大小
     */
    private int dayTextSize, lunarTextSize;

    /**
     * 日历卡的项高度
     */
    private int calendarItemHeight;

    /**
     * 星期栏的高度
     */
    private int weekBarHeight;

    /**
     * 今天的日子
     */
    private CalendarEntity currentDate;


    private boolean monthViewScrollable,
            weekViewScrollable,
            yearViewScrollable;

    /**
     * 当前月份和周视图的item位置
     */
    @SuppressWarnings("all")
    int currentMonthViewItem, currentWeekViewItem;

    /**
     * 标记的日期
     */
    List<CalendarEntity> schemeDateList;


    /**
     * 日期被选中监听
     */
    CalendarView.OnDateSelectedListener dateSelectedListener;

    /**
     * 内部日期切换监听，用于内部更新计算
     */
    CalendarView.OnInnerDateSelectedListener innerListener;

    /**
     * 快速年份切换
     */


    /**
     * 月份切换事件
     */
    CalendarView.OnMonthChangeListener monthChangeListener;

    /**
     * 视图改变事件
     */
    CalendarView.OnViewChangeListener onViewChangeListener;

    /**
     * 保存选中的日期
     */
    CalendarEntity selectedCalendar;

//    /**
//     * 保存标记位置
//     */
//    CalendarEntity mIndexCalendar;

    CalendarViewDelegate(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        LunarCalendar.init(context);

        mCalendarPadding = (int) a.getDimension(R.styleable.CalendarView_calendar_padding, 0);
        schemeTextColor = a.getColor(R.styleable.CalendarView_scheme_text_color, 0xFFFFFFFF);
        schemeLunarTextColor = a.getColor(R.styleable.CalendarView_scheme_lunar_text_color, 0xFFe1e1e1);
        schemeThemeColor = a.getColor(R.styleable.CalendarView_scheme_theme_color, 0x50CFCFCF);
        monthViewClass = a.getString(R.styleable.CalendarView_month_view);

        weekViewClass = a.getString(R.styleable.CalendarView_week_view);
        weekBarClass = a.getString(R.styleable.CalendarView_week_bar_view);
        weekTextSize = a.getDimensionPixelSize(R.styleable.CalendarView_week_text_size, CalendarUtil.dipToPx(context, 12));
        weekBarHeight = (int) a.getDimension(R.styleable.CalendarView_week_bar_height, CalendarUtil.dipToPx(context, 40));

        mSchemeText = a.getString(R.styleable.CalendarView_scheme_text);
        if (TextUtils.isEmpty(mSchemeText)) {
//            mSchemeText = "记";
            mSchemeText = "";
        }

        monthViewScrollable = a.getBoolean(R.styleable.CalendarView_month_view_scrollable, true);
        weekViewScrollable = a.getBoolean(R.styleable.CalendarView_week_view_scrollable, true);
        yearViewScrollable = a.getBoolean(R.styleable.CalendarView_year_view_scrollable, true);

        mMonthViewShowMode = a.getInt(R.styleable.CalendarView_month_view_show_mode, MODE_ALL_MONTH);
        mWeekStart = a.getInt(R.styleable.CalendarView_week_start_with, WEEK_START_WITH_SUN);
        //mSelectMode = array.getInt(R.styleable.CalendarView_select_mode, SELECT_MODE_DEFAULT);

        weekBackground = a.getColor(R.styleable.CalendarView_week_background, Color.WHITE);
        weekLineBackground = a.getColor(R.styleable.CalendarView_week_line_background, Color.TRANSPARENT);
        yearViewBackground = a.getColor(R.styleable.CalendarView_year_view_background, Color.WHITE);
        weekTextColor = a.getColor(R.styleable.CalendarView_week_text_color, 0xFF333333);

        currentDayTextColor = a.getColor(R.styleable.CalendarView_current_day_text_color, Color.RED);
        currentDayLunarTextColor = a.getColor(R.styleable.CalendarView_current_day_lunar_text_color, Color.RED);

        selectedThemeColor = a.getColor(R.styleable.CalendarView_selected_theme_color, 0x50CFCFCF);
        selectedTextColor = a.getColor(R.styleable.CalendarView_selected_text_color, 0xFF111111);

        selectedLunarTextColor = a.getColor(R.styleable.CalendarView_selected_lunar_text_color, 0xFF111111);
        currentMonthTextColor = a.getColor(R.styleable.CalendarView_current_month_text_color, 0xFF111111);
        otherMonthTextColor = a.getColor(R.styleable.CalendarView_other_month_text_color, 0xFFe1e1e1);

        currentLunarAfterTodayTextColor = a.getColor(R.styleable.CalendarView_current_month_after_today_lunar_text_color, 0xffe1e1e1);
        currentLunarBeforeTodayTextColor = a.getColor(R.styleable.CalendarView_current_month_before_today_lunar_text_color,0xFF111111);
        otherMonthLunarTextColor = a.getColor(R.styleable.CalendarView_other_month_lunar_text_color, 0xffe1e1e1);
        minYear = a.getInt(R.styleable.CalendarView_min_year, 1971);
        maxYear = a.getInt(R.styleable.CalendarView_max_year, 2055);
        minYearMonth = a.getInt(R.styleable.CalendarView_min_year_month, 1);
        maxYearMonth = a.getInt(R.styleable.CalendarView_max_year_month, 12);

        dayTextSize = a.getDimensionPixelSize(R.styleable.CalendarView_day_text_size, CalendarUtil.dipToPx(context, 16));
        lunarTextSize = a.getDimensionPixelSize(R.styleable.CalendarView_lunar_text_size, CalendarUtil.dipToPx(context, 10));
        calendarItemHeight = (int) a.getDimension(R.styleable.CalendarView_calendar_height, CalendarUtil.dipToPx(context, 56));

        //年视图相关
        mYearViewMonthTextSize = a.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_text_size, CalendarUtil.dipToPx(context, 18));
        mYearViewDayTextSize = a.getDimensionPixelSize(R.styleable.CalendarView_year_view_day_text_size, CalendarUtil.dipToPx(context, 8));
        mYearViewMonthTextColor = a.getColor(R.styleable.CalendarView_year_view_month_text_color, 0xFF111111);
        mYearViewDayTextColor = a.getColor(R.styleable.CalendarView_year_view_day_text_color, 0xFF111111);
        mYearViewSchemeTextColor = a.getColor(R.styleable.CalendarView_year_view_scheme_color, schemeThemeColor);

        if (minYear <= MIN_YEAR) {
            minYear = 1971;
        }
        if (maxYear >= MAX_YEAR) {
            maxYear = 2055;
        }
        a.recycle();
        init();
    }

    private void init() {
        currentDate = new CalendarEntity();
        Date d = new Date();
        currentDate.setYear(CalendarUtil.getDate("yyyy", d));
        currentDate.setMonth(CalendarUtil.getDate("MM", d));
        currentDate.setDay(CalendarUtil.getDate("dd", d));
        currentDate.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(currentDate);
        setRange(minYear, minYearMonth, maxYear, maxYearMonth);
    }


    void setRange(int minYear, int minYearMonth,
                  int maxYear, int maxYearMonth) {
        this.minYear = minYear;
        this.minYearMonth = minYearMonth;
        this.maxYear = maxYear;
        this.maxYearMonth = maxYearMonth;
        if (this.maxYear < currentDate.getYear()) {
            this.maxYear = currentDate.getYear();
        }
        int y = currentDate.getYear() - this.minYear;
        currentMonthViewItem = 12 * y + currentDate.getMonth() - this.minYearMonth;
        currentWeekViewItem = CalendarUtil.getWeekFromCalendarBetweenYearAndYear(currentDate, this.minYear, this.minYearMonth, mWeekStart);
    }

    String getSchemeText() {
        return mSchemeText;
    }

    int getCurDayTextColor() {
        return currentDayTextColor;
    }

    int getCurDayLunarTextColor() {
        return currentDayLunarTextColor;
    }

    int getWeekTextColor() {
        return weekTextColor;
    }

    int getSchemeTextColor() {
        return schemeTextColor;
    }

    int getSchemeLunarTextColor() {
        return schemeLunarTextColor;
    }

    int getOtherMonthTextColor() {
        return otherMonthTextColor;
    }

    int getCurrentMonthTextColor() {
        return currentMonthTextColor;
    }

    int getSelectedTextColor() {
        return selectedTextColor;
    }

    int getSelectedLunarTextColor() {
        return selectedLunarTextColor;
    }

    int getCurrentMonthLunarTextColor() {
        return currentLunarAfterTodayTextColor;
    }

    public int getCurrentLunarBeforeTodayTextColor() {
        return currentLunarBeforeTodayTextColor;
    }

    int getOtherMonthLunarTextColor() {
        return otherMonthLunarTextColor;
    }

    int getSchemeThemeColor() {
        return schemeThemeColor;
    }

    int getSelectedThemeColor() {
        return selectedThemeColor;
    }

    int getWeekBackground() {
        return weekBackground;
    }

    int getYearViewBackground() {
        return yearViewBackground;
    }

    int getWeekLineBackground() {
        return weekLineBackground;
    }


    String getMonthViewClass() {
        return monthViewClass;
    }

    String getWeekViewClass() {
        return weekViewClass;
    }

    String getWeekBarClass() {
        return weekBarClass;
    }

    int getWeekBarHeight() {
        return weekBarHeight;
    }

    int getMinYear() {
        return minYear;
    }

    int getMaxYear() {
        return maxYear;
    }

    int getDayTextSize() {
        return dayTextSize;
    }

    int getLunarTextSize() {
        return lunarTextSize;
    }

    int getCalendarItemHeight() {
        return calendarItemHeight;
    }

    int getMinYearMonth() {
        return minYearMonth;
    }

    int getMaxYearMonth() {
        return maxYearMonth;
    }


    int getYearViewMonthTextSize() {
        return mYearViewMonthTextSize;
    }

    int getYearViewMonthTextColor() {
        return mYearViewMonthTextColor;
    }

    int getYearViewDayTextColor() {
        return mYearViewDayTextColor;
    }

    int getYearViewDayTextSize() {
        return mYearViewDayTextSize;
    }

    int getYearViewSchemeTextColor() {
        return mYearViewSchemeTextColor;
    }

    int getMonthViewShowMode() {
        return mMonthViewShowMode;
    }

    void setMonthViewShowMode(int monthViewShowMode) {
        this.mMonthViewShowMode = monthViewShowMode;
    }

    void setTextColor(int curDayTextColor, int curMonthTextColor, int otherMonthTextColor, int curMonthLunarTextColor,int curMonthLunarBeforeTodayTextColor, int otherMonthLunarTextColor) {
        currentDayTextColor = curDayTextColor;
        this.otherMonthTextColor = otherMonthTextColor;
        currentMonthTextColor = curMonthTextColor;
        currentLunarAfterTodayTextColor = curMonthLunarTextColor;
        currentLunarBeforeTodayTextColor = currentLunarBeforeTodayTextColor;
        this.otherMonthLunarTextColor = otherMonthLunarTextColor;
    }

    void setSchemeColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        this.schemeThemeColor = schemeColor;
        this.schemeTextColor = schemeTextColor;
        this.schemeLunarTextColor = schemeLunarTextColor;
    }

    void setYearViewTextColor(int yearViewMonthTextColor, int yearViewDayTextColor, int yarViewSchemeTextColor) {
        this.mYearViewMonthTextColor = yearViewMonthTextColor;
        this.mYearViewDayTextColor = yearViewDayTextColor;
        this.mYearViewSchemeTextColor = yarViewSchemeTextColor;
    }

    void setSelectColor(int selectedColor, int selectedTextColor, int selectedLunarTextColor) {
        this.selectedThemeColor = selectedColor;
        this.selectedTextColor = selectedTextColor;
        this.selectedLunarTextColor = selectedLunarTextColor;
    }

    void setThemeColor(int selectedThemeColor, int schemeColor) {
        this.selectedThemeColor = selectedThemeColor;
        this.schemeThemeColor = schemeColor;
    }

    boolean isMonthViewScrollable() {
        return monthViewScrollable;
    }

    boolean isWeekViewScrollable() {
        return weekViewScrollable;
    }

    boolean isYearViewScrollable() {
        return yearViewScrollable;
    }

    int getWeekStart() {
        return mWeekStart;
    }

    void setWeekStart(int mWeekStart) {
        this.mWeekStart = mWeekStart;
    }

    int getWeekTextSize() {
        return weekTextSize;
    }

    CalendarEntity getCurrentDay() {
        return currentDate;
    }

    void updateCurrentDay() {
        Date d = new Date();
        currentDate.setYear(CalendarUtil.getDate("yyyy", d));
        currentDate.setMonth(CalendarUtil.getDate("MM", d));
        currentDate.setDay(CalendarUtil.getDate("dd", d));
        LunarCalendar.setupLunarCalendar(currentDate);
    }

    int getCalendarPadding() {
        return mCalendarPadding;
    }

    void setPreventLongPressedSelected(boolean preventLongPressedSelected) {
        this.preventLongPressedSelected = preventLongPressedSelected;
    }

    boolean isPreventLongPressedSelected() {
        return preventLongPressedSelected;
    }

    void clearSelectedScheme() {
        selectedCalendar.setScheme(null);
        selectedCalendar.setSchemeColor(0);
        selectedCalendar.setSchemes(null);
    }

    CalendarEntity createCurrentDate() {
        CalendarEntity calendar = new CalendarEntity();
        calendar.setYear(currentDate.getYear());
        calendar.setWeek(currentDate.getWeek());
        calendar.setMonth(currentDate.getMonth());
        calendar.setDay(currentDate.getDay());
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }
}
