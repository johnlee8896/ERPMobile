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

import com.chinashb.www.mobileerp.R;


/**
 * 默认高仿魅族周视图
 * Created by liweifeng on 2017/11/29.
 */

public final class DefaultWeekView extends WeekView {
    private Paint textPaint = new Paint();
    private Paint schemeBasicPaint = new Paint();
    private Paint bottomNormalDotPaint = new Paint();
    private Paint bottomExceptionDotPaint = new Paint();
    private float radius;
    private int padding;
    private float schemeBaseLine;

    public DefaultWeekView(Context context) {
        super(context);

        textPaint.setTextSize(CalendarUtil.dipToPx(context, 8));
        textPaint.setColor(0xffffffff);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        schemeBasicPaint.setAntiAlias(true);
        schemeBasicPaint.setStyle(Paint.Style.FILL);
        schemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        schemeBasicPaint.setColor(0xffed5353);
        schemeBasicPaint.setFakeBoldText(true);

        bottomNormalDotPaint.setAntiAlias(true);
        bottomNormalDotPaint.setStyle(Paint.Style.FILL);
        bottomNormalDotPaint.setStrokeWidth(1);
        bottomNormalDotPaint.setColor(getResources().getColor(R.color.color_blue_2E7FEF));

        bottomExceptionDotPaint.setAntiAlias(true);
        bottomExceptionDotPaint.setStyle(Paint.Style.FILL);
        bottomExceptionDotPaint.setStrokeWidth(1);
        bottomExceptionDotPaint.setColor(getResources().getColor(R.color.color_red_E94156));

        radius = CalendarUtil.dipToPx(getContext(), 7);
        padding = CalendarUtil.dipToPx(getContext(), 4);
        Paint.FontMetrics metrics = schemeBasicPaint.getFontMetrics();
        schemeBaseLine = radius - metrics.descent + (metrics.bottom - metrics.top) / 2 + CalendarUtil.dipToPx(getContext(), 1);

    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, CalendarEntity calendar, int x, boolean hasScheme) {
        selectedPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + padding, padding, x + itemWidth - padding, itemHeight - padding, selectedPaint);
        return true;
    }


    @Override
    protected void onDrawScheme(Canvas canvas, CalendarEntity calendar, int x) {
        schemeBasicPaint.setColor(calendar.getSchemeColor());

        canvas.drawCircle(x + itemWidth - padding - radius / 2, padding + radius, radius, schemeBasicPaint);

        canvas.drawText(calendar.getScheme(),
                x + itemWidth - padding - radius / 2 - getTextWidth(calendar.getScheme()) / 2,
                padding + schemeBaseLine, textPaint);
    }

    @Override
    protected void onDrawBottomDot(Canvas canvas, CalendarEntity calendarEntity, int x) {
        int cx = x + itemWidth / 2;
        BottomMarkDotBean markDotBean = calendarEntity.getBottomMarkDotBean();
        if (markDotBean != null && markDotBean.isShow()) {
            if (!markDotBean.isNormal()) {
                canvas.drawCircle(cx, textBaseLine + itemHeight / 5 + 1, radius / 2 - 2 , bottomExceptionDotPaint);
            } else {
                canvas.drawCircle(cx, textBaseLine + itemHeight / 5 + 1, radius / 2  - 2, bottomNormalDotPaint);
            }
        }

    }

    @Override
    protected void onDrawText(Canvas canvas, CalendarEntity calendar, int x, boolean hasScheme, boolean isSelected,boolean isBeforeToday) {
        int cx = x + itemWidth / 2;
        int top = -itemHeight / 6;

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, textBaseLine + top,
                    selectTextPaint);
            canvas.drawText(calendar.getLunar(), cx, textBaseLine + itemHeight / 10, selectedLunarTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, textBaseLine + top,
                    calendar.isCurrentDay() ? currentDayTextPaint :
                            calendar.isCurrentMonth() ? schemeTextPaint : otherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, textBaseLine + itemHeight / 10,
                    calendar.isCurrentDay() ? currentDayLunarTextPaint :
                            schemeLunarTextPaint);
        } else {

            if (isBeforeToday){
                currentMonthTextPaint.setColor(getResources().getColor(R.color.black));
            }
            canvas.drawText(String.valueOf(calendar.getDay()), cx, textBaseLine + top,
                    calendar.isCurrentDay() ? currentDayTextPaint :
                            calendar.isCurrentMonth() ? currentMonthTextPaint : otherMonthTextPaint);
            canvas.drawText(calendar.getLunar(), cx, textBaseLine + itemHeight / 10,
                    calendar.isCurrentDay() ? currentDayLunarTextPaint :
                            calendar.isCurrentMonth() ? currentMonthLunarTextPaint : otherMonthLunarTextPaint);
        }

    }

    /**
     * 获取字体的宽
     *
     * @param text text
     * @return return
     */
    private float getTextWidth(String text) {
        return textPaint.measureText(text);
    }
}
