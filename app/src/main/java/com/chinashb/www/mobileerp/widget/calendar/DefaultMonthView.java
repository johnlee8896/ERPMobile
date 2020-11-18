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
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 默认高仿魅族日历布局
 * Created by liweifeng on 2017/11/15.
 */

public final class DefaultMonthView extends MonthView {

    private Paint mTextPaint = new Paint();
    private Paint mSchemeBasicPaint = new Paint();
    private float mRadio;
    private int mPadding;
    private float mSchemeBaseLine;

    public DefaultMonthView(Context context) {
        super(context);

        mTextPaint.setTextSize(CalendarUtil.dipToPx(context, 8));
        mTextPaint.setColor(0xffFFFFFF);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xffed5353);
        mSchemeBasicPaint.setFakeBoldText(true);
        mRadio = CalendarUtil.dipToPx(getContext(), 7);
        mPadding = CalendarUtil.dipToPx(getContext(), 4);
        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + CalendarUtil.dipToPx(getContext(), 1);

    }

    /**
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, CalendarEntity calendar, int x, int y, boolean hasScheme) {
        selectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + mPadding, y + mPadding, x + itemWidth - mPadding, y + itemHeight - mPadding, selectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, CalendarEntity calendar, int x, int y) {
        mSchemeBasicPaint.setColor(calendar.getSchemeColor());

        canvas.drawCircle(x + itemWidth - mPadding - mRadio / 2, y + mPadding + mRadio, mRadio, mSchemeBasicPaint);

        canvas.drawText(calendar.getScheme(),
                x + itemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.getScheme()) / 2,
                y + mPadding + mSchemeBaseLine, mTextPaint);
    }


    /**
     * 获取字体的宽
     * @param text text
     * @return return
     */
    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    @Override
    protected void onDrawText(Canvas canvas, CalendarEntity calendar, int x, int y, boolean hasScheme, boolean isSelected,boolean isBeforeToday) {
        int cx = x + itemWidth / 2;
        int top = y - itemHeight / 6;

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, textBaseLine + top,
                    selectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, textBaseLine + y + itemHeight / 10, selectedLunarTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, textBaseLine + top,
                    calendar.isCurrentDay() ? currentDayTextPaint :
                            calendar.isCurrentMonth() ? schemeTextPaint : otherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, textBaseLine + y + itemHeight / 10,
                    calendar.isCurrentDay() ? currentDayLunarTextPaint : schemeLunarTextPaint);
        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, textBaseLine + top,
                    calendar.isCurrentDay() ? currentDayTextPaint :
                            calendar.isCurrentMonth() ? currentMonthTextPaint : otherMonthTextPaint);
            canvas.drawText(calendar.getLunar(), cx, textBaseLine + y + itemHeight / 10,
                    calendar.isCurrentDay() ? currentDayLunarTextPaint :
                            calendar.isCurrentMonth() ? currentMonthLunarTextPaint : otherMonthLunarTextPaint);
        }
    }
}
