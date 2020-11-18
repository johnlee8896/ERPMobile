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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

/**
 * 周视图滑动ViewPager，需要动态固定高度
 * 周视图是连续不断的视图，因此不能简单的得出每年都有52+1周，这样会计算重叠的部分
 * WeekViewPager需要和CalendarView关联:
 */

public final class WeekViewPager extends ViewPager {

    private int weekCount;
    private CalendarViewDelegate delegate;

    /**
     * 日历布局，需要在日历下方放自己的布局
     */
    CalendarLayout calendarLayout;

    /**
     * 是否使用滚动到某一天
     */
    private boolean isUsingScrollToCalendar = false;

    public WeekViewPager(Context context) {
        this(context, null);
    }

    public WeekViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setup(CalendarViewDelegate delegate) {
        this.delegate = delegate;
        init();
    }

    private void init() {
        weekCount = CalendarUtil.getWeekCountBetweenYearAndYear(delegate.getMinYear(),
                delegate.getMinYearMonth(),
                delegate.getMaxYear(),
                delegate.getMaxYearMonth(),
                delegate.getWeekStart());
        setAdapter(new WeekViewPagerAdapter());
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //默认的显示星期四，周视图切换就显示星期4
                if (getVisibility() != VISIBLE) {
                    isUsingScrollToCalendar = false;
                    return;
                }
//                WeekView view = (WeekView) findViewWithTag(position);
//                if (view != null) {
//                    view.performClickCalendar(delegate.selectedCalendar, !isUsingScrollToCalendar);
//                }
                isUsingScrollToCalendar = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void notifyDataSetChanged() {
        weekCount = CalendarUtil.getWeekCountBetweenYearAndYear(delegate.getMinYear(), delegate.getMinYearMonth(),
                delegate.getMaxYear(), delegate.getMaxYearMonth(), delegate.getWeekStart());
        getAdapter().notifyDataSetChanged();
    }

    /**
     * 滚动到指定日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    void scrollToCalendar(int year, int month, int day, boolean smoothScroll) {
        isUsingScrollToCalendar = true;
        CalendarEntity calendar = new CalendarEntity();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setCurrentDay(calendar.equals(delegate.getCurrentDay()));
        LunarCalendar.setupLunarCalendar(calendar);
        delegate.selectedCalendar = calendar;
    }

    /**
     * 滚动到当前
     */
    void scrollToCurrent(boolean smoothScroll) {
        isUsingScrollToCalendar = true;
        int position = CalendarUtil.getWeekFromCalendarBetweenYearAndYear(delegate.getCurrentDay(),
                delegate.getMinYear(),
                delegate.getMinYearMonth(),
                delegate.getWeekStart()) - 1;
        int curItem = getCurrentItem();
        if (curItem == position) {
            isUsingScrollToCalendar = false;
        }
        setCurrentItem(position, smoothScroll);
        WeekView view = (WeekView) findViewWithTag(position);
        if (view != null) {
            view.performClickCalendar(delegate.getCurrentDay(), false);
            view.setSelectedCalendar(delegate.getCurrentDay());
            view.invalidate();
        }
        if (delegate.dateSelectedListener != null && getVisibility() == VISIBLE) {
            delegate.dateSelectedListener.onDateSelected(delegate.createCurrentDate(), false);
        }
        if(getVisibility() == VISIBLE){
            delegate.innerListener.onWeekDateSelected(delegate.getCurrentDay(),false);
        }
//        int i = CalendarUtil.getWeekFromDayInMonth(delegate.getCurrentDay(), delegate.getWeekStart());
//        calendarLayout.setSelectWeek(i);
    }

    /**
     * 更新任意一个选择的日期
     */
    void updateSelected(CalendarEntity calendar, boolean smoothScroll) {
        int position = CalendarUtil.getWeekFromCalendarBetweenYearAndYear(calendar,
                delegate.getMinYear(),
                delegate.getMinYearMonth(),
                delegate.getWeekStart()) - 1;
        int curItem = getCurrentItem();
        if (curItem == position) {
            isUsingScrollToCalendar = false;
        }
        setCurrentItem(position, smoothScroll);
        WeekView view = (WeekView) findViewWithTag(position);
        if (view != null) {
            view.setSelectedCalendar(calendar);
            view.invalidate();
        }
    }


    /**
     * 更新标记日期
     */
    void updateScheme() {
        for (int i = 0; i < getChildCount(); i++) {
            WeekView view = (WeekView) getChildAt(i);
            view.update();
        }
    }

    /**
     * 更新当前日期，夜间过度的时候调用这个函数，一般不需要调用
     */
    void updateCurrentDate() {
        for (int i = 0; i < getChildCount(); i++) {
            WeekView view = (WeekView) getChildAt(i);
            view.updateCurrentDate();
        }
    }

    /**
     * 更新显示模式
     */
    void updateShowMode() {
        for (int i = 0; i < getChildCount(); i++) {
            WeekView view = (WeekView) getChildAt(i);
            view.updateShowMode();
        }
    }

    /**
     * 更新周起始
     */
    void updateWeekStart() {
        weekCount = CalendarUtil.getWeekCountBetweenYearAndYear(delegate.getMinYear(),
                delegate.getMinYearMonth(),
                delegate.getMaxYear(),
                delegate.getMaxYearMonth(),
                delegate.getWeekStart());
        for (int i = 0; i < getChildCount(); i++) {
            WeekView view = (WeekView) getChildAt(i);
            view.updateWeekStart();
        }
        updateSelected(delegate.selectedCalendar, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return delegate.isWeekViewScrollable() && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return delegate.isWeekViewScrollable() && super.onInterceptTouchEvent(ev);
    }

    /**
     * 周视图的高度应该与日历项的高度一致
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(delegate.getCalendarItemHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 周视图切换
     */
    private class WeekViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return weekCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            CalendarEntity calendar = CalendarUtil.getFirstCalendarFromWeekCount(delegate.getMinYear(),
                    delegate.getMinYearMonth(),
                    position + 1,
                    delegate.getWeekStart());
            WeekView view;
            if (TextUtils.isEmpty(delegate.getWeekViewClass())) {
                view = new DefaultWeekView(getContext());
            } else {
                try {
                    Class<?> cls = Class.forName(delegate.getWeekViewClass());
                    Constructor constructor = cls.getConstructor(Context.class);
                    view = (WeekView) constructor.newInstance(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            view.parentLayout = calendarLayout;
            view.setup(delegate);
            view.setup(calendar);
            view.setTag(position);
            view.setSelectedCalendar(delegate.selectedCalendar);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
