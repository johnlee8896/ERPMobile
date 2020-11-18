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
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.chinashb.www.mobileerp.R;


/**
 * 日历布局
 */
public class CalendarLayout extends LinearLayout {

    /**
     * 周月视图
     */
    private static final int CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW = 0;


    /**
     * 仅周视图
     */
    private static final int CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW = 1;

    /**
     * 仅月视图
     */
    private static final int CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW = 2;

    /**
     * 默认展开
     */
    private static final int STATUS_EXPAND = 0;

    /**
     * 默认收缩
     */
    private static final int STATUS_SHRINK = 1;

    /**
     * 默认状态
     */
    private int defaultStatus;

    /**
     * 星期栏
     */
    WeekBar weekBar;

    /**
     * 自定义ViewPager，月视图
     */
    MonthViewPager monthView;

    /**
     * 自定义的周视图
     */
    WeekViewPager weekPager;

    /**
     * ContentView 日历下面的控件内容
     */
    ViewGroup contentView;


    private int calendarShowMode;

    private int mTouchSlop;
    private int contentViewTranslateY; //ContentView  可滑动的最大距离距离 , 固定
    private int viewPagerTranslateY = 0;// ViewPager可以平移的距离，不代表mMonthView的平移距离

    private float downY;
    private float mLastY;
    private boolean isAnimating = false;

    /**
     * 内容布局id
     */
    private int contentViewId;

    /**
     * 手速判断
     */

    int mItemHeight;

    private CalendarViewDelegate mDelegate;

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarLayout);
        contentViewId = array.getResourceId(R.styleable.CalendarLayout_calendar_content_view_id, 0);
        defaultStatus = array.getInt(R.styleable.CalendarLayout_default_status, STATUS_EXPAND);
        calendarShowMode = array.getInt(R.styleable.CalendarLayout_calendar_show_mode, CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW);
        array.recycle();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    /**
     * 初始化
     *
     * @param delegate delegate
     */
    final void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        initCalendarPosition(delegate.selectedCalendar);
        updateContentViewTranslateY();
    }

    /**
     * 初始化当前时间的位置
     *
     * @param cur 当前日期时间
     */
    private void initCalendarPosition(CalendarEntity cur) {
        int diff = CalendarUtil.getMonthViewStartDiff(cur, mDelegate.getWeekStart());
        int size = diff + cur.getDay() - 1;
        setSelectPosition(size);
    }

    /**
     * 当前第几项被选中
     */
    final void setSelectPosition(int selectPosition) {
        int line = (selectPosition + 7) / 7;
        viewPagerTranslateY = (line - 1) * mItemHeight;
    }

    /**
     * 设置选中的周，更新位置
     *
     * @param line line
     */
    final void setSelectWeek(int line) {
        viewPagerTranslateY = (line - 1) * mItemHeight;
    }


    /**
     * 更新内容ContentView可平移的最大距离
     */
    void updateContentViewTranslateY() {
        if (mDelegate == null || contentView == null) {
            return;
        }
        CalendarEntity calendar = mDelegate.selectedCalendar;
        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            contentViewTranslateY = 5 * mItemHeight;
        } else {
            contentViewTranslateY = CalendarUtil.getMonthViewHeight(calendar.getYear(), calendar.getMonth(), mItemHeight, mDelegate.getWeekStart())
                    - mItemHeight;
        }
        //已经显示周视图，如果月视图高度是动态改变的，则需要动态平移contentView的高度
        if (weekPager.getVisibility() == VISIBLE && mDelegate.getMonthViewShowMode() != CalendarViewDelegate.MODE_ALL_MONTH) {
            if (contentView == null) {
                return;
            }
            contentView.setTranslationY(-contentViewTranslateY);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (delegate.isShowYearSelectedLayout) {
//            return false;
//        }
//        if (calendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
//                calendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {
//            return false;
//        }
//        if (contentView == null) {
//            return false;
//        }
//        int action = event.getAction();
//        float y = event.getY();
//        mVelocityTracker.addMovement(event);
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastY = downY = y;
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                float dy = y - mLastY;
//                //向上滑动，并且contentView平移到最大距离，显示周视图
//                if (dy < 0 && contentView.getTranslationY() == -contentViewTranslateY) {
//                    contentView.onTouchEvent(event);
//                    showWeek();
//                    return false;
//                }
//                hideWeek();
//
//                //向下滑动，并且contentView已经完全到底部
//                if (dy > 0 && contentView.getTranslationY() + dy >= 0) {
//                    contentView.setTranslationY(0);
//                    translationViewPager();
//                    return super.onTouchEvent(event);
//                }
//                //向上滑动，并且contentView已经平移到最大距离，则contentView平移到最大的距离
//                if (dy < 0 && contentView.getTranslationY() + dy <= -contentViewTranslateY) {
//                    contentView.setTranslationY(-contentViewTranslateY);
//                    translationViewPager();
//                    return super.onTouchEvent(event);
//                }
//                //否则按比例平移
//                contentView.setTranslationY(contentView.getTranslationY() + dy);
//                translationViewPager();
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                final VelocityTracker velocityTracker = mVelocityTracker;
//                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//                float mYVelocity = velocityTracker.getYVelocity();
//                if (contentView.getTranslationY() == 0
//                        || contentView.getTranslationY() == contentViewTranslateY) {
//                    break;
//                }
//                if (Math.abs(mYVelocity) >= 800) {
//                    if (mYVelocity < 0) {
//                        shrink();
//                    } else {
////                        expand();
//                    }
//                    return super.onTouchEvent(event);
//                }
//                if (event.getY() - downY > 0) {
////                    expand();
//                } else {
//                    shrink();
//                }
//                break;
//        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (contentView != null && monthView != null) {
            int h = getHeight() - mItemHeight
                    - (mDelegate != null ? mDelegate.getWeekBarHeight() :
                    CalendarUtil.dipToPx(getContext(), 40))
                    - CalendarUtil.dipToPx(getContext(), 1);
            int heightSpec = MeasureSpec.makeMeasureSpec(h,
                    MeasureSpec.EXACTLY);
            contentView.measure(widthMeasureSpec, heightSpec);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        monthView = (MonthViewPager) findViewById(R.id.vp_calendar).findViewById(R.id.vp_calendar);
        weekPager = (WeekViewPager) findViewById(R.id.vp_week).findViewById(R.id.vp_week);
        contentView = (ViewGroup) findViewById(contentViewId);
        if (contentView != null) {
            contentView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (isAnimating) {
//            return true;
//        }
//        final int action = ev.getAction();
//        float y = ev.getY();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastY = downY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float dy = y - mLastY;
//                 /*
//                   如果向上滚动，且ViewPager已经收缩，不拦截事件
//                 */
//                if (dy < 0 && contentView.getTranslationY() == -contentViewTranslateY) {
//                    return false;
//                }
//                /*
//                 * 如果向下滚动，有 2 种情况处理 且y在ViewPager下方
//                 * 1、RecyclerView 或者其它滚动的View，当mContentView滚动到顶部时，拦截事件
//                 * 2、非滚动控件，直接拦截事件
//                 */
//                if (dy > 0 && contentView.getTranslationY() == -contentViewTranslateY
//                        && y >= CalendarUtil.dipToPx(getContext(), 98)) {
//                    if (!isScrollTop()) {
//                        return false;
//                    }
//                }
//
//                if (dy > 0 && contentView.getTranslationY() == 0 && y >= CalendarUtil.dipToPx(getContext(), 98)) {
//                    return false;
//                }
//
//                if (Math.abs(dy) > mTouchSlop) {//大于mTouchSlop开始拦截事件，ContentView和ViewPager得到CANCEL事件
//                    if ((dy > 0 && contentView.getTranslationY() <= 0)
//                            || (dy < 0 && contentView.getTranslationY() >= -contentViewTranslateY)) {
//                        mLastY = y;
//                        return true;
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                break;
//        }
        return super.onInterceptTouchEvent(ev);
    }


    /**
     * 平移ViewPager月视图
     */
    private void translationViewPager() {
        float percent = contentView.getTranslationY() * 1.0f / contentViewTranslateY;
        monthView.setTranslationY(viewPagerTranslateY * percent);
    }


    /**
     * 是否展开了
     *
     * @return isExpand
     */
    public final boolean isExpand() {
        return contentView == null || monthView.getVisibility() == VISIBLE;
    }

    public void setDefaultExpand(boolean isExpand){
        if (!isExpand){
            showWeek();
        }
    }


    /**
     * 展开
     *
     * @return 展开是否成功
     */
    public boolean expand() {
        if (isAnimating ||
                calendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW ||
                contentView == null) {
            return false;
        }
        if (monthView.getVisibility() != VISIBLE) {
            weekPager.setVisibility(GONE);
            onShowMonthView();
            monthView.setVisibility(VISIBLE);
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentView,
                "translationY", contentView.getTranslationY(), 0f);
        objectAnimator.setDuration(240);
        objectAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / contentViewTranslateY;
                monthView.setTranslationY(viewPagerTranslateY * percent);
                isAnimating = true;
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                hideWeek();

            }
        });
        objectAnimator.start();
        return true;
    }


    /**
     * 收缩
     *
     * @return 成功或者失败
     */
    public boolean shrink() {
        if (isAnimating || contentView == null) {
            return false;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentView,
                "translationY", contentView.getTranslationY(), -contentViewTranslateY);
        objectAnimator.setDuration(240);
        objectAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (Float) animation.getAnimatedValue();
                float percent = currentValue * 1.0f / contentViewTranslateY;
                monthView.setTranslationY(viewPagerTranslateY * percent);
                isAnimating = true;
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                showWeek();

            }
        });
        objectAnimator.start();
        return true;
    }

    /**
     * 初始化状态
     */
    final void initStatus() {
        if (contentView == null) {
            return;
        }
        if ((defaultStatus == STATUS_SHRINK ||
                calendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) &&
                calendarShowMode != CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW) {
            post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentView,
                            "translationY", contentView.getTranslationY(), -contentViewTranslateY);
                    objectAnimator.setDuration(0);
                    objectAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float currentValue = (Float) animation.getAnimatedValue();
                            float percent = currentValue * 1.0f / contentViewTranslateY;
//                            monthView.setTranslationY(viewPagerTranslateY * percent);
                            isAnimating = true;
                        }
                    });
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isAnimating = false;
                            showWeek();

                        }
                    });
                    objectAnimator.start();
                }
            });
        }else {
            if(mDelegate.onViewChangeListener == null){
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    mDelegate.onViewChangeListener.onViewChange(true);
                }
            });
        }
    }

    /**
     * 隐藏周视图
     */
    private void hideWeek() {
        onShowMonthView();
        weekPager.setVisibility(GONE);
        monthView.setVisibility(VISIBLE);
    }

    /**
     * 显示周视图
     */
    private void showWeek() {
        onShowWeekView();
        weekPager.getAdapter().notifyDataSetChanged();
        weekPager.setVisibility(VISIBLE);
        monthView.setVisibility(INVISIBLE);
    }

    /**
     * 周视图显示事件
     */
    private void onShowWeekView(){
        if(weekPager.getVisibility() == VISIBLE){
            return;
        }
        if(mDelegate != null && mDelegate.onViewChangeListener != null){
            mDelegate.onViewChangeListener.onViewChange(false);
        }
    }


    /**
     * 周视图显示事件
     */
    private void onShowMonthView(){
        if(monthView.getVisibility() == VISIBLE){
            return;
        }
        if(mDelegate.onViewChangeListener != null){
            mDelegate.onViewChangeListener.onViewChange(true);
        }
    }

    /**
     * ContentView是否滚动到顶部 如果完全不适合，就复写这个方法
     * @return 是否滚动到顶部
     */
    protected boolean isScrollTop() {
        if (contentView instanceof CalendarScrollView) {
            return ((CalendarScrollView) contentView).isScrollToTop();
        }
        if (contentView instanceof RecyclerView) {
            return ((RecyclerView) contentView).computeVerticalScrollOffset() == 0;
        }
        if (contentView instanceof AbsListView) {
            boolean result = false;
            AbsListView listView = (AbsListView) contentView;
            if (listView.getFirstVisiblePosition() == 0) {
                final View topChildView = listView.getChildAt(0);
                result = topChildView.getTop() == 0;
            }
            return result;
        }
        return contentView.getScrollY() == 0;
    }


    /**
     * 隐藏内容布局
     */
    final void hideContentView() {
        if (contentView == null) {
            return;
        }
        contentView.animate()
                .translationY(getHeight() - monthView.getHeight())
                .setDuration(220)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        contentView.setVisibility(INVISIBLE);
                        contentView.clearAnimation();
                    }
                });
    }

    /**
     * 显示内容布局
     */
    final void showContentView() {
        if (contentView == null) {
            return;
        }
        contentView.setTranslationY(getHeight() - monthView.getHeight());
        contentView.setVisibility(VISIBLE);
        contentView.animate()
                .translationY(0)
                .setDuration(180)
                .setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }


    /**
     * 如果有十分特别的ContentView，可以自定义实现这个接口
     */
    public interface CalendarScrollView {
        /**
         * 是否滚动到顶部
         * @return 是否滚动到顶部
         */
        boolean isScrollToTop();
    }
}
