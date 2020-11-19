package com.chinashb.www.mobileerp;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.OnLoadDataListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.utils.WeakHandler;
import com.chinashb.www.mobileerp.widget.ClockPanelLayout;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/***
 * @date 创建时间 2019/9/23 16:57
 * @author 作者: xxblwf
 * @description 订餐页面，目前 是小食堂
 */

public class FoodOrderActivity extends BaseActivity {

    @BindView(R.id.food_current_time_TextView) TextView currentTimeTextView;
    @BindView(R.id.food_attendance_info_TextView) TextView infoTextView;
    @BindView(R.id.food_attendance_Layout) LinearLayout foodLayout;
    @BindView(R.id.food_clock_panel_Layout) ClockPanelLayout clockPanelLayout;

    private static final int TAG_UPDATE_CURRENT_TIME = 0X100;
    private static final int TAG_UPDATE_ORDER_SUCCESS = 0X101;
    //    private static final int TAG_UPDATE_CANCEL_ORDER_SUCCESS = 0X102;
    private static final long ONE_DAY_TIME_MILLIS = 86400000;
    private boolean order = true;

    private WeakHandler<FoodOrderActivity> handler = new WeakHandler<FoodOrderActivity>(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TAG_UPDATE_CURRENT_TIME:
                    currentTimeTextView.setText(UnitFormatUtil.formatLongToHMS(System.currentTimeMillis()));
                    currentTimeTextView.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(TAG_UPDATE_CURRENT_TIME, 1000);
                    break;
                case TAG_UPDATE_ORDER_SUCCESS:
                    updateFoodOrderInfo();
                    break;

            }
        }
    };
    private int commitType = 1;
    private boolean canOrder = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_order_layout);
        ButterKnife.bind(this);
//        drawProgressWithAnimation();
        initClockPanel();
        startUpdateTime();
        updateFoodOrderInfo();
        foodLayout.setOnClickListener(v -> {
            if (canOrder) {
                commitType = order ? 1 : 2;
                CommitFoodAsyncTask task = new CommitFoodAsyncTask();
                task.execute();
            }
        });
    }

    private void updateFoodOrderInfo() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 10 && hour < 13) {
            currentTimeTextView.setTextColor(Color.DKGRAY);
            currentTimeTextView.setEnabled(false);
            infoTextView.setText("已过时间，不可点击");
            canOrder = false;
            return;
        }
        String foodDate = null;
        String sql;
        if (hour >= 13) {
            long tempTime = calendar.getTimeInMillis() + ONE_DAY_TIME_MILLIS;
            foodDate = UnitFormatUtil.formatTimeToDay(tempTime);
        } else if (hour < 10) {
            foodDate = UnitFormatUtil.formatTimeToDay(calendar.getTimeInMillis());
        }
        canOrder = true;
        sql = String.format("select * from Food_Order where HR_ID = %s and FO_Date = '%s' and Which_Food = 5", UserSingleton.get().getHRID(), foodDate);
        QueryAsyncTask queryAsyncTask = new QueryAsyncTask();
        queryAsyncTask.execute(sql);
        queryAsyncTask.setLoadDataCompleteListener(new OnLoadDataListener() {
            @Override public void loadComplete(List<JsonObject> result) {
                if (result == null || result.size() == 0) {
                    //没有订过
                    infoTextView.setText("点击订餐");
                    order = true;
                } else {
                    //已经订过
                    infoTextView.setText("点击取消订餐");
                    currentTimeTextView.setTextColor(Color.GREEN);
                    order = false;
                }
            }
        });
    }

    private void initClockPanel() {
        // 创建 ObjectAnimator 对象
        ObjectAnimator animator = ObjectAnimator.ofFloat(clockPanelLayout, "progressDegree", 0, clockPanelLayout.getTimeDegree());
        animator.setDuration(1500);
        animator.start();
    }

    private void drawProgressWithAnimation() {
        //休息不作处理
//        clockPanelLayout.setStartTime(System.currentTimeMillis() - 3*60*60*1000);
//        if (judgeBeanIsException(onBean)) {
//            clockPanelLayout.setStartException();
//        } else {
//            clockPanelLayout.setStartNormal();
//        }
//        clockPanelLayout.setEndTime(System.currentTimeMillis() + 3*60*60*1000);
//        if (judgeBeanIsException(offBean)) {
//            clockPanelLayout.setEndException();
//        } else {
//            clockPanelLayout.setEndNormal();
//        }
//        initClockPanel();
    }

    private void startUpdateTime() {
        handler.sendEmptyMessage(TAG_UPDATE_CURRENT_TIME);
    }

    private void stopUpdateTime() {
        handler.removeMessages(TAG_UPDATE_CURRENT_TIME);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(TAG_UPDATE_CURRENT_TIME);
    }

    private class CommitFoodAsyncTask extends AsyncTask<String, Void, WsResult> {
        @Override
        protected WsResult doInBackground(String... params) {
            return WebServiceUtil.commitFoodOrderForSmallCanteen(UserSingleton.get().getHRID(), getFoodDate(), commitType);
//            return null;
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result.getResult()) {
                ToastUtil.showToastLong(commitType == 1 ? "订餐成功！" : "取消订餐成功！");
            } else {
                ToastUtil.showToastLong("操作失败！" + result.getErrorInfo());
            }
            handler.sendEmptyMessage(TAG_UPDATE_ORDER_SUCCESS);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private Date getFoodDate() {
        //假定订餐时间为 下午1点至第二天10天
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 13) {
            long tempTime = calendar.getTimeInMillis() + ONE_DAY_TIME_MILLIS;
            Date date = new Date(tempTime);
            return date;
        } else if (hour < 10) {
            return calendar.getTime();
        }
        return null;
    }
}
