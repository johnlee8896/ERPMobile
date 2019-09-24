package com.chinashb.www.mobileerp;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.utils.WeakHandler;
import com.chinashb.www.mobileerp.widget.ClockPanelLayout;

import java.util.Calendar;
import java.util.Date;

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

    private static final int TAG_UPDATE_CURRENT_TIME = 100;
    private static final long ONE_DAY_TIME_MILLIS = 86400000;

    private WeakHandler<FoodOrderActivity> handler = new WeakHandler<FoodOrderActivity>(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TAG_UPDATE_CURRENT_TIME) {
                currentTimeTextView.setText(UnitFormatUtil.formatLongToHMS(System.currentTimeMillis()));
                currentTimeTextView.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(TAG_UPDATE_CURRENT_TIME, 1000);
            }
        }
    };
    private int commitType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_order_layout);
        ButterKnife.bind(this);
//        drawProgressWithAnimation();
        initClockPanel();
        startUpdateTime();
        getFoodOrderInfo();
        foodLayout.setOnClickListener(v -> {


            commitType = 1;
            CommitFoodAsyncTask task = new CommitFoodAsyncTask();
            task.execute();
        });
    }

    private void getFoodOrderInfo() {
                Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//        cal.clear(Calendar.MINUTE);
//        cal.clear(Calendar.SECOND);
//        cal.clear(Calendar.MILLISECOND);
        return cal.getTime();

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 13 ){
            long tempTime = calendar.getTimeInMillis() + ONE_DAY_TIME_MILLIS;
            Date date = new Date(tempTime);
            return date;
        }else if (hour < 10){
            return calendar.getTime();
        }else{
            currentTimeTextView.setTextColor(Color.DKGRAY);
            currentTimeTextView.setEnabled(false);
            infoTextView.setText("已过时间，不可点击");
        }

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
        if (hour >= 13 ){
            long tempTime = calendar.getTimeInMillis() + ONE_DAY_TIME_MILLIS;
            Date date = new Date(tempTime);
            return date;
        }else if (hour < 10){
            return calendar.getTime();
        }
        return null;
    }
}
