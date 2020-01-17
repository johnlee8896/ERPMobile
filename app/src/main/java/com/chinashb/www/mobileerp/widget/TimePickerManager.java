package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/***
 * @date 创建时间 2018/8/28 下午3:26
 * @author 作者: liweifeng
 * @description 时间选择的帮助类
 */
public class TimePickerManager {

    private Context context;
    private TimePickerView timePickerView;
    private TimePickerBuilder timePickerBuilder;
    private OnViewClickListener onViewClickListener;
    public final static String PICK_TYPE_START = "PICK_TYPE_START";
    public final static String PICK_TYPE_END = "PICK_TYPE_END";

    public final static String PICK_TYPE_OUT_DATE = "PICK_TYPE_OUT_DATE";
    public final static String PICK_TYPE_LOAD_TIME = "PICK_TYPE_LOAD_TIME";
    public final static String PICK_TYPE_ARRIVE_DATE = "PICK_TYPE_ARRIVE_DATE";

    public final static String PICK_TYPE_YEAR = "year";
    public final static String PICK_TYPE_MONTH = "month";
    public final static String PICK_TYPE_DAY = "day";
    public final static String PICK_TYPE_HOUR = "hour";
    public final static String PICK_TYPE_MINUTE = "minute";
    //区分选择的事件,可自定义值
    private String pickAction;
    private String typeArr[] = {PICK_TYPE_YEAR, PICK_TYPE_MONTH, PICK_TYPE_DAY, PICK_TYPE_HOUR, PICK_TYPE_MINUTE};
    private XScaleOption xScaleOption;


    public TimePickerManager(Context context) {
        this.context = context;
        initTimePicker();
    }

    private void initTimePicker() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2000, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2100, 11, 31);
        timePickerBuilder = new TimePickerBuilder(context, (date, v) -> {
            if (onViewClickListener != null) {
                if (TextUtils.isEmpty(pickAction)) {
                    return;
                }
//                onViewClickListener.onClickAction(v, pickAction.equals(PICK_TYPE_END) ? PICK_TYPE_END : PICK_TYPE_START, date);
                onViewClickListener.onClickAction(v, pickAction, date);
            }
        })
                .setLayoutRes(R.layout.dialog_time_picker_layout, v -> {
                    TextView cancelTextView = v.findViewById(R.id.dialog_time_picker_cancel_TextView);
                    TextView confirmTextView = v.findViewById(R.id.dialog_time_picker_confirm_TextView);
                    cancelTextView.setOnClickListener(cancel -> {
                        timePickerView.dismiss();
                    });
                    confirmTextView.setOnClickListener(confirm -> {
                        timePickerView.dismiss();
                        timePickerView.returnData();
                    });
                })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(2.5f)
                .setOutSideCancelable(true)
//                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .setTextXOffset(-20, 5, 20, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFFE9E9E9);
    }


    /**
     * 传入的值为 Calendar
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public TimePickerManager setDateRange(Calendar startDate, Calendar endDate) {
        timePickerBuilder.setRangDate(startDate, endDate);
        return this;
    }

    public TimePickerManager setOutSideCancelable(Boolean canOutCancelable) {
        timePickerBuilder.setOutSideCancelable(canOutCancelable);
        return this;
    }

    public TimePickerManager setLayoutRes(int layoutId, CustomListener customListener) {
        timePickerBuilder.setLayoutRes(layoutId, customListener);
        return this;
    }

    /**
     * @param type   必须为PICK_TYPE_YEAR定义的常量
     * @param number 表示该单位连续的几个显示 比如PICK_TYPE_YEAR 后续的1个 那么显示年和月 最大值为5
     * @return
     */
    public TimePickerManager setShowType(String type, int number) {
        if (number > 5 || number < 0) {
            throw new RuntimeException("number error");
        }
        //数组大小
        int typeSize = 6;
        boolean[] showTypeArr = new boolean[typeSize];
        List<String> showTypeList = Arrays.asList(typeArr);
        int typeIndex = showTypeList.indexOf(type);
        if (typeIndex != -1) {
            for (int i = typeIndex; i < ((typeIndex + number) > typeSize ? typeSize : (typeIndex + number)); i++) {
                showTypeArr[i] = true;
            }
        }
        timePickerBuilder.setType(showTypeArr);
        return this;
    }

    public TimePickerManager setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    public TimePickerManager setXScaleOption(XScaleOption option) {
        this.xScaleOption = option;
//        TimePickerBuilder timePickerBuilder = this.timePickerBuilder.setTextXOffset(option.getXScaleValue()[0],);
        return this;
    }

    public void showDialog(String pickAction) {
        if (pickAction == null) {
            throw new RuntimeException("pickAction can not be null");
        }
        this.pickAction = pickAction;
        timePickerView = timePickerBuilder.build();
        timePickerView.show();
    }

    public void onDestroy() {
        context = null;
        timePickerView = null;
        timePickerBuilder = null;
        onViewClickListener = null;
        pickAction = null;
    }

    public static class XScaleOption {
        private boolean isXScale;
        private int[] XScaleValue;

        public boolean isXScale() {
            return isXScale;
        }

        public void setXScale(boolean XScale) {
            isXScale = XScale;
        }

        public int[] getXScaleValue() {
            return XScaleValue;
        }

        public void setXScaleValue(int[] XScaleValue) {
            this.XScaleValue = XScaleValue;
        }
    }

}
