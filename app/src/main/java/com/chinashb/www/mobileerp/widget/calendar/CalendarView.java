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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.chinashb.www.mobileerp.R;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 日历布局
 * 各个类使用包权限，避免不必要的public
 */
@SuppressWarnings("unused")
public class CalendarView extends FrameLayout {

    /**
     * 使用google官方推荐的方式抽取自定义属性
     */
    private final CalendarViewDelegate mDelegate;

    /**
     * 自定义自适应高度的ViewPager
     */
    private MonthViewPager mMonthPager;

    /**
     * 日历周视图
     */
    private WeekViewPager mWeekPager;

    /**
     * 星期栏的线
     */
    private View mWeekLine;

    /**
     * 星期栏
     */
    private WeekBar mWeekBar;

    /**
     * 日历外部收缩布局
     */
    CalendarLayout mParentLayout;


    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDelegate = new CalendarViewDelegate(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_calendar_view_layout, this, true);
        FrameLayout frameContent = (FrameLayout) findViewById(R.id.frameContent);
        this.mWeekPager = (WeekViewPager) findViewById(R.id.vp_week);
        this.mWeekPager.setup(mDelegate);

        if (TextUtils.isEmpty(mDelegate.getWeekBarClass())) {
            this.mWeekBar = new WeekBar(getContext());
        } else {
            try {
                Class<?> cls = Class.forName(mDelegate.getWeekBarClass());
                Constructor constructor = cls.getConstructor(Context.class);
                mWeekBar = (WeekBar) constructor.newInstance(getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        frameContent.addView(mWeekBar, 2);
        mWeekBar.setup(mDelegate);
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());

        this.mWeekLine = findViewById(R.id.line);
        this.mWeekLine.setBackgroundColor(mDelegate.getWeekLineBackground());
        LayoutParams lineParams = (LayoutParams) this.mWeekLine.getLayoutParams();
        lineParams.setMargins(lineParams.leftMargin, mDelegate.getWeekBarHeight(), lineParams.rightMargin, 0);
        this.mWeekLine.setLayoutParams(lineParams);

        this.mMonthPager = (MonthViewPager) findViewById(R.id.vp_calendar);
        this.mMonthPager.mWeekPager = mWeekPager;
        this.mMonthPager.mWeekBar = mWeekBar;
        LayoutParams params = (LayoutParams) this.mMonthPager.getLayoutParams();
        params.setMargins(0, mDelegate.getWeekBarHeight() + CalendarUtil.dipToPx(context, 1), 0, 0);
        mWeekPager.setLayoutParams(params);



        mDelegate.innerListener = new OnInnerDateSelectedListener() {
            @Override
            public void onMonthDateSelected(CalendarEntity calendar, boolean isClick) {
                if (calendar.getYear() == mDelegate.getCurrentDay().getYear() &&
                        calendar.getMonth() == mDelegate.getCurrentDay().getMonth()
                        && mMonthPager.getCurrentItem() != mDelegate.currentMonthViewItem) {
                    return;
                }
                mDelegate.selectedCalendar = calendar;
                mWeekPager.updateSelected(mDelegate.selectedCalendar, false);
                mMonthPager.updateSelected();
                if (mWeekBar != null) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }

            @Override
            public void onWeekDateSelected(CalendarEntity calendar, boolean isClick) {
                mDelegate.selectedCalendar = calendar;
                int y = calendar.getYear() - mDelegate.getMinYear();
                int position = 12 * y + mDelegate.selectedCalendar.getMonth() - mDelegate.getMinYearMonth();
                mMonthPager.setCurrentItem(position, false);
                mMonthPager.updateSelected();
                if (mWeekBar != null) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }
        };

        mDelegate.selectedCalendar = mDelegate.createCurrentDate();
        mWeekBar.onDateSelected(mDelegate.selectedCalendar, mDelegate.getWeekStart(), false);

        mMonthPager.setup(mDelegate);
        mMonthPager.setCurrentItem(mDelegate.currentMonthViewItem);
        mWeekPager.updateSelected(mDelegate.selectedCalendar, false);
    }

    /**
     * 设置日期范围
     *
     * @param minYear      最小年份
     * @param minYearMonth 最小年份对应月份
     * @param maxYear      最大月份
     * @param maxYearMonth 最大月份对应月份
     */
    public void setRange(int minYear, int minYearMonth,
                         int maxYear, int maxYearMonth) {
        mDelegate.setRange(minYear, minYearMonth,
                maxYear, maxYearMonth);
        mWeekPager.notifyDataSetChanged();
        mMonthPager.notifyDataSetChanged();
        if (CalendarUtil.isCalendarInRange(mDelegate.selectedCalendar, mDelegate)) {
            scrollToCalendar(mDelegate.selectedCalendar.getYear(),
                    mDelegate.selectedCalendar.getMonth(),
                    mDelegate.selectedCalendar.getDay());

        } else {
            scrollToCurrent();
        }

    }

    /**
     * 获取当天
     *
     * @return 返回今天
     */
    public int getCurDay() {
        return mDelegate.getCurrentDay().getDay();
    }

    /**
     * 获取本月
     *
     * @return 返回本月
     */
    public int getCurMonth() {
        return mDelegate.getCurrentDay().getMonth();
    }

    /**
     * 获取本年
     *
     * @return 返回本年
     */
    public int getCurYear() {
        return mDelegate.getCurrentDay().getYear();
    }


    /**
     * 打开日历年月份快速选择
     *
     * @param year 年
     */
    @SuppressWarnings("deprecation")
    public void showYearSelectLayout(final int year) {
        showSelectLayout(year);
    }

    /**
     * 打开日历年月份快速选择
     * 请使用 showYearSelectLayout(final int year) 代替，这个没什么，越来越规范
     *
     * @param year 年
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public void showSelectLayout(final int year) {
        if (mParentLayout != null && mParentLayout.contentView != null) {
            if (!mParentLayout.isExpand()) {
//                parentLayout.expand();
                return;
            }
        }
        mWeekPager.setVisibility(GONE);
        mDelegate.isShowYearSelectedLayout = true;
        if (mParentLayout != null) {
            mParentLayout.hideContentView();
        }
        mWeekBar.animate()
                .translationY(-mWeekBar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(260)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWeekBar.setVisibility(GONE);
                        if (mParentLayout != null && mParentLayout.contentView != null) {
//                            parentLayout.expand();
                        }
                    }
                });

        mMonthPager.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(260)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }


    /**
     * 关闭年月视图选择布局
     */
    public void closeYearSelectLayout() {
        int position = 12 * (mDelegate.selectedCalendar.getYear() - mDelegate.getMinYear()) +
                mDelegate.selectedCalendar.getMonth() - mDelegate.getMinYearMonth();
        closeSelectLayout(position);
    }

    /**
     * 关闭日历布局，同时会滚动到指定的位置
     *
     * @param position 某一年
     */
    private void closeSelectLayout(final int position) {
        mWeekBar.setVisibility(VISIBLE);
        if (position == mMonthPager.getCurrentItem()) {
            if (mDelegate.dateSelectedListener != null) {
                mDelegate.dateSelectedListener.onDateSelected(mDelegate.selectedCalendar, false);
            }
        } else {
            mMonthPager.setCurrentItem(position, false);
        }
        mWeekBar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(280)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWeekBar.setVisibility(VISIBLE);
                    }
                });
        mMonthPager.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(180)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mMonthPager.setVisibility(VISIBLE);
                        mMonthPager.clearAnimation();
                        if (mParentLayout != null) {
                            mParentLayout.showContentView();
                        }
                    }
                });
    }

    /**
     * 滚动到当前
     */
    public void scrollToCurrent() {
        scrollToCurrent(false);
    }

    /**
     * 滚动到当前
     *
     * @param smoothScroll smoothScroll
     */
    public void scrollToCurrent(boolean smoothScroll) {
        if (!CalendarUtil.isCalendarInRange(mDelegate.getCurrentDay(), mDelegate)) {
            return;
        }
        mDelegate.selectedCalendar = mDelegate.createCurrentDate();
        mWeekBar.onDateSelected(mDelegate.selectedCalendar, mDelegate.getWeekStart(), false);
        if (mMonthPager.getVisibility() == VISIBLE) {
            mMonthPager.scrollToCurrent(smoothScroll);
        } else {
            mWeekPager.scrollToCurrent(smoothScroll);
        }
    }


    /**
     * 滚动到下一个月
     */
    public void scrollToNext() {
        scrollToNext(false);
    }

    /**
     * 滚动到下一个月
     *
     * @param smoothScroll smoothScroll
     */

    public void scrollToNext(boolean smoothScroll) {
        if (mWeekPager.getVisibility() == VISIBLE) {
            mWeekPager.setCurrentItem(mWeekPager.getCurrentItem() + 1, smoothScroll);
        } else {
            mMonthPager.setCurrentItem(mMonthPager.getCurrentItem() + 1, smoothScroll);
        }

    }

    /**
     * 滚动到上一个月
     */
    public void scrollToPre() {
        scrollToPre(false);
    }

    /**
     * 滚动到上一个月
     *
     * @param smoothScroll smoothScroll
     */
    public void scrollToPre(boolean smoothScroll) {
        if (mWeekPager.getVisibility() == VISIBLE) {
            mWeekPager.setCurrentItem(mWeekPager.getCurrentItem() - 1, smoothScroll);
        } else {
            mMonthPager.setCurrentItem(mMonthPager.getCurrentItem() - 1, smoothScroll);
        }
    }

    /**
     * 滚动到指定日期
     *
     * @param year  year
     * @param month month
     * @param day   day
     */
    public void scrollToCalendar(int year, int month, int day) {
        scrollToCalendar(year, month, day, false);
    }

    /**
     * 滚动到指定日期
     *
     * @param year         year
     * @param month        month
     * @param day          day
     * @param smoothScroll smoothScroll
     */
    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll) {
        if (mWeekPager.getVisibility() == VISIBLE) {
            mWeekPager.scrollToCalendar(year, month, day, smoothScroll);
        } else {
            mMonthPager.scrollToCalendar(year, month, day, smoothScroll);
        }
    }

    /**
     * 滚动到某一年
     *
     * @param year 快速滚动的年份
     */
    public void scrollToYear(int year) {
        scrollToYear(year, false);
    }

    /**
     * 滚动到某一年
     *
     * @param year         快速滚动的年份
     * @param smoothScroll smoothScroll
     */
    public void scrollToYear(int year, boolean smoothScroll) {
    }


    /**
     * 月份改变事件
     *
     * @param listener listener
     */
    public void setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.mDelegate.monthChangeListener = listener;
        if (mDelegate.monthChangeListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mDelegate.monthChangeListener.onMonthChange(mDelegate.selectedCalendar.getYear(),
                            mDelegate.selectedCalendar.getMonth());
                }
            });
        }
    }

    /**
     * 设置日期选中事件
     *
     * @param listener 日期选中事件
     */
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.mDelegate.dateSelectedListener = listener;
        if (mDelegate.dateSelectedListener != null) {
            if (!CalendarUtil.isCalendarInRange(mDelegate.selectedCalendar, mDelegate)) {
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    mDelegate.dateSelectedListener.onDateSelected(mDelegate.selectedCalendar, false);
                }
            });
        }
    }


    /**
     * 视图改变事件
     *
     * @param listener listener
     */
    public void setOnViewChangeListener(OnViewChangeListener listener) {
        this.mDelegate.onViewChangeListener = listener;
    }

    /**
     * 初始化时初始化日历卡默认选择位置
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() != null && getParent() instanceof CalendarLayout) {
            mParentLayout = (CalendarLayout) getParent();
            mParentLayout.mItemHeight = mDelegate.getCalendarItemHeight();
            mMonthPager.mParentLayout = mParentLayout;
            mWeekPager.calendarLayout = mParentLayout;
            mParentLayout.weekBar = mWeekBar;
            mParentLayout.setup(mDelegate);
            mParentLayout.initStatus();
        }
    }


    /**
     * 标记哪些日期有事件
     *
     * @param schemeDateList schemeDateList 通过自己的需求转换即可
     */
    public void setSchemeDateList(List<CalendarEntity> schemeDateList) {
        this.mDelegate.schemeDateList = schemeDateList;
        this.mDelegate.clearSelectedScheme();
        mMonthPager.updateScheme();
        mWeekPager.updateScheme();
    }

    /**
     * 清空日期标记
     */
    public void clearSchemeDate() {
        this.mDelegate.schemeDateList = null;
        this.mDelegate.clearSelectedScheme();
        mMonthPager.updateScheme();
        mWeekPager.updateScheme();
    }


    /**
     * 移除某天的标记
     * 这个API是安全的，无效try cache
     *
     * @param calendar calendar
     */
    public void removeSchemeDate(CalendarEntity calendar) {
        if (mDelegate.selectedCalendar.equals(calendar)) {
            mDelegate.clearSelectedScheme();
        }
        if (mDelegate.schemeDateList == null ||
                mDelegate.schemeDateList.size() == 0 ||
                calendar == null) {
            return;
        }
        if (mDelegate.schemeDateList.contains(calendar)) {
            mDelegate.schemeDateList.remove(calendar);
        }
        mMonthPager.updateScheme();
        mWeekPager.updateScheme();
    }

    /**
     * 设置背景色
     *
     * @param yearViewBackground 年份卡片的背景色
     * @param weekBackground     星期栏背景色
     * @param lineBg             线的颜色
     */
    public void setBackground(int yearViewBackground, int weekBackground, int lineBg) {
        mWeekBar.setBackgroundColor(weekBackground);
        mWeekLine.setBackgroundColor(lineBg);
    }


    /**
     * 设置文本颜色
     *
     * @param currentDayTextColor      今天字体颜色
     * @param curMonthTextColor        当前月份字体颜色
     * @param otherMonthColor          其它月份字体颜色
     * @param curMonthLunarTextColor   当前月份农历字体颜色
     * @param otherMonthLunarTextColor 其它农历字体颜色
     */
    public void setTextColor(
            int currentDayTextColor,
            int curMonthTextColor,
            int otherMonthColor,
            int curMonthLunarTextColor,
            int curMonthLunarBeforeTodayTextColor,
            int otherMonthLunarTextColor) {
        mDelegate.setTextColor(currentDayTextColor, curMonthTextColor, otherMonthColor, curMonthLunarTextColor, curMonthLunarBeforeTodayTextColor,otherMonthLunarTextColor);
    }

    /**
     * 设置选择的效果
     *
     * @param selectedThemeColor     选中的标记颜色
     * @param selectedTextColor      选中的字体颜色
     * @param selectedLunarTextColor 选中的农历字体颜色
     */
    public void setSelectedColor(int selectedThemeColor, int selectedTextColor, int selectedLunarTextColor) {
        mDelegate.setSelectColor(selectedThemeColor, selectedTextColor, selectedLunarTextColor);
    }

    /**
     * 定制颜色
     *
     * @param selectedThemeColor 选中的标记颜色
     * @param schemeColor        标记背景色
     */
    public void setThemeColor(int selectedThemeColor, int schemeColor) {
        mDelegate.setThemeColor(selectedThemeColor, schemeColor);
    }

    /**
     * 设置标记的色
     *
     * @param schemeLunarTextColor 标记农历颜色
     * @param schemeColor          标记背景色
     * @param schemeTextColor      标记字体颜色
     */
    public void setSchemeColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        mDelegate.setSchemeColor(schemeColor, schemeTextColor, schemeLunarTextColor);
    }

    /**
     * 设置年视图的颜色
     *
     * @param yearViewMonthTextColor 年视图月份颜色
     * @param yearViewDayTextColor   年视图天的颜色
     * @param yarViewSchemeTextColor 年视图标记颜色
     */
    public void setYearViewTextColor(int yearViewMonthTextColor, int yearViewDayTextColor, int yarViewSchemeTextColor) {
        mDelegate.setYearViewTextColor(yearViewMonthTextColor, yearViewDayTextColor, yarViewSchemeTextColor);
    }

    /**
     * 设置星期栏的背景和字体颜色
     *
     * @param weekBackground 背景色
     * @param weekTextColor  字体颜色
     */
    public void setWeeColor(int weekBackground, int weekTextColor) {
        mWeekBar.setBackgroundColor(weekBackground);
        mWeekBar.setTextColor(weekTextColor);
    }


    /**
     * 设置星期日周起始
     */
    public void setWeekStarWithSun() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_SUN);
    }

    /**
     * 设置星期一周起始
     */
    public void setWeekStarWithMon() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_MON);
    }

    /**
     * 设置星期六周起始
     */
    public void setWeekStarWithSat() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_SAT);
    }

    /**
     * 设置周起始
     * CalendarViewDelegate.WEEK_START_WITH_SUN
     * CalendarViewDelegate.WEEK_START_WITH_MON
     * CalendarViewDelegate.WEEK_START_WITH_SAT
     *
     * @param weekStart 周起始
     */
    private void setWeekStart(int weekStart) {
        if (weekStart != CalendarViewDelegate.WEEK_START_WITH_SUN &&
                weekStart != CalendarViewDelegate.WEEK_START_WITH_MON &&
                weekStart != CalendarViewDelegate.WEEK_START_WITH_SAT) {
            return;
        }
        if (weekStart == mDelegate.getWeekStart()) {
            return;
        }
        mDelegate.setWeekStart(weekStart);
        mWeekBar.onWeekStartChange(weekStart);
        mWeekBar.onDateSelected(mDelegate.selectedCalendar, weekStart, false);
        mWeekPager.updateWeekStart();
        mMonthPager.updateWeekStart();
        mWeekPager.notifyDataSetChanged();
    }


    /**
     * 设置显示模式为全部
     */
    public void setAllMode() {
        setShowMode(CalendarViewDelegate.MODE_ALL_MONTH);
    }

    /**
     * 设置显示模式为仅当前月份
     */
    public void setOnlyCurrentMode() {
        setShowMode(CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH);
    }

    /**
     * 设置显示模式为填充
     */
    public void setFixMode() {
        setShowMode(CalendarViewDelegate.MODE_FIT_MONTH);
    }

    /**
     * 设置显示模式
     * CalendarViewDelegate.MODE_ALL_MONTH
     * CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH
     * CalendarViewDelegate.MODE_FIT_MONTH
     *
     * @param mode 月视图显示模式
     */
    private void setShowMode(int mode) {
        if (mode != CalendarViewDelegate.MODE_ALL_MONTH &&
                mode != CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH &&
                mode != CalendarViewDelegate.MODE_FIT_MONTH) {
            return;
        }
        if (mDelegate.getMonthViewShowMode() == mode) {
            return;
        }
        mDelegate.setMonthViewShowMode(mode);
        mWeekPager.updateShowMode();
        mMonthPager.updateShowMode();
        mWeekPager.notifyDataSetChanged();
    }

    /**
     * 更新界面，
     * 重新设置颜色等都需要调用该方法
     */
    public void update() {
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
        mMonthPager.updateScheme();
        mWeekPager.updateScheme();
    }

    /**
     * 更新周视图
     */
    public void updateWeekBar() {
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
    }


    /**
     * 更新当前日期
     */
    public void updateCurrentDate() {
        mDelegate.updateCurrentDay();
        mMonthPager.updateCurrentDate();
        mWeekPager.updateCurrentDate();
    }

    /**
     * 获取选择的日期
     *
     * @return 获取选择的日期
     */
    public CalendarEntity getSelectedCalendar() {
        return mDelegate.selectedCalendar;
    }



    /**
     * 月份切换事件
     */
    public interface OnMonthChangeListener {
        void onMonthChange(int year, int month);
    }

    /**
     * 内部日期选择，不暴露外部使用
     * 主要是用于更新日历CalendarLayout位置
     */
    interface OnInnerDateSelectedListener {
        /**
         * 月视图点击
         *
         * @param calendar calendar
         * @param isClick  是否是点击
         */
        void onMonthDateSelected(CalendarEntity calendar, boolean isClick);

        /**
         * 周视图点击
         *
         * @param calendar calendar
         */
        void onWeekDateSelected(CalendarEntity calendar, boolean isClick);
    }

    /**
     * 外部日期选择事件
     */
    public interface OnDateSelectedListener {
        void onDateSelected(CalendarEntity calendar, boolean isClick);
    }


    /**
     * 视图改变事件
     */
    public interface OnViewChangeListener {
        /**
         * 视图改变事件
         *
         * @param isMonthView isMonthView是否是月视图
         */
        void onViewChange(boolean isMonthView);
    }

}
