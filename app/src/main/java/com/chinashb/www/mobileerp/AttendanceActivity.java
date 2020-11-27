package com.chinashb.www.mobileerp;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.AttendanceDetailBean;
import com.chinashb.www.mobileerp.bean.AttendanceInfoBean;
import com.chinashb.www.mobileerp.bean.AttendanceReportBean;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.bean.SystemDateBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.libapi.bean.BaseBean;
import com.chinashb.www.mobileerp.libapi.bean.BaseListBean;
import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.singleton.GPSLocationSingleton;
import com.chinashb.www.mobileerp.singleton.SPSingleton;
import com.chinashb.www.mobileerp.utils.APIDefine;
import com.chinashb.www.mobileerp.utils.ColorStateUtils;
import com.chinashb.www.mobileerp.utils.DeviceUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.SPDefine;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.utils.WeakHandler;
import com.chinashb.www.mobileerp.widget.AttendanceOutsideRemarkDialog;
import com.chinashb.www.mobileerp.widget.ClockPanelLayout;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.SuccessNoticeDialog;
import com.chinashb.www.mobileerp.widget.calendar.BottomMarkDotBean;
import com.chinashb.www.mobileerp.widget.calendar.CalendarEntity;
import com.chinashb.www.mobileerp.widget.calendar.CalendarView;
import com.chinashb.www.mobileerp.widget.group.GroupImageTextLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/***
 * @date 创建时间 2020/11/13 4:27 PM
 * @author 作者: liweifeng
 * @description 考勤
 */

//    {
//        "status": "1",
//            "info": "OK",
//            "infocode": "10000",
//            "count": "1",
//            "geocodes": [
//        {
//            "formatted_address": "上海市嘉定区胜华波集团上海分公司",
//                "country": "中国",
//                "province": "上海市",
//                "citycode": "021",
//                "city": "上海市",
//                "district": "嘉定区",
//                "township": [],
//            "neighborhood": {
//            "name": [],
//            "type": []
//        },
//            "building": {
//            "name": [],
//            "type": []
//        },
//            "adcode": "310114",
//                "street": [],
//            "number": [],
//            "location": "121.197985,31.322222",
//                "level": "兴趣点"
//        }
//]
//    }
@SuppressWarnings("ALL")
public class AttendanceActivity extends BaseActivity {
    private static final int TAG_UPDATE_CURRENT_TIME = 100;
    private static final long ONE_DAY_TIME_MILLIS = 86400000;

    @BindView(R.id.fragment_attendance_calendarView) CalendarView calendarView;
    @BindView(R.id.fragment_attendance_location_layout) GroupImageTextLayout locationLayout;
    @BindView(R.id.fragment_attendance_current_time_TextView) TextView currentTimeTextView;
    @BindView(R.id.fragment_attendance_attendance_info_TextView) TextView infoTextView;
    @BindView(R.id.fragment_attendance_clock_panel_Layout) ClockPanelLayout clockPanelLayout;
    @BindView(R.id.fragment_attendance_in_outside_textView) TextView inOutsideTextView;
    @BindView(R.id.fragment_attendance_on_duty_time_textView) TextView onDutyTimeTextView;
    @BindView(R.id.fragment_attendance_on_duty_status_textView) TextView onDutyStatusTextView;
    @BindView(R.id.fragment_attendance_on_duty_layout) RelativeLayout onDutyLayout;
    @BindView(R.id.fragment_attendance_off_duty_time_textView) TextView offDutyTimeTextView;
    @BindView(R.id.fragment_attendance_off_duty_status_textView) TextView offDutyStatusTextView;
    @BindView(R.id.fragment_attendance_off_duty_layout) RelativeLayout offDutyLayout;
    @BindView(R.id.fragment_attendance_attendance_Layout) LinearLayout attendanceLayout;
    @BindView(R.id.fragment_bottom_content_ScrollView) NestedScrollView bottomContentLayout;
    @BindView(R.id.fragment_attendance_on_duty_location_layout) GroupImageTextLayout onDutyLocationLayout;
    @BindView(R.id.fragment_attendance_off_duty_location_layout) GroupImageTextLayout offDutyLocationLayout;
    @BindView(R.id.fragment_attendance_on_duty_out_textView) TextView onDutyOutTextView;
    @BindView(R.id.fragment_attendance_off_duty_out_textView) TextView offDutyOutTextView;
    @BindView(R.id.fragment_attendance_today_textView) TextView todayTextView;

    private static final int TAG_UPDATE_ORDER_SUCCESS = 0X101;
    private static final int TAG_Request_fail = 0X102;
    private double longitude;
    private double latitude;
    private String locationName;
    private boolean canAttendance = false;
    private boolean isInnerRange = false;
    private int commitType = 0;
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    locationName = location.getAddress();

                    System.out.println("================================================longitude = " + longitude);
                    System.out.println("================================================latitude = " + latitude);
                    System.out.println("================================================locationName = " + locationName);

                    String rule = SPSingleton.get().getString(SPDefine.KEY_attendance_rule, "");
                    if (!StringUtils.isStringValid(rule)) {
//                        canAttendance = false;
                        //规则拿到与否并不影响打卡
                    } else {
                        AttendanceInfoBean myInfoBean = JsonUtil.parseJsonToObject(rule, AttendanceInfoBean.class);
                        if (myInfoBean != null && StringUtils.isStringValid(myInfoBean.getLatitude()) && StringUtils.isStringValid(myInfoBean.getLongitude())) {
                            canAttendance = true;
                            double distance = GPSLocationSingleton.get().getDistanceByLatLng(latitude, longitude, Double.valueOf(myInfoBean.getLatitude()), Double.valueOf(myInfoBean.getLongitude()));
                            inOutsideTextView.setVisibility(View.VISIBLE);
                            if (distance <= myInfoBean.getScope()) {
                                inOutsideTextView.setText("在正常范围内");
                                locationLayout.setText("明道大厦");
                                isInnerRange = true;
                            } else {
                                inOutsideTextView.setText("外勤,距离目标地" + distance + "m");
                                locationLayout.setText(locationName);
                                isInnerRange = false;
                            }
                        }
                    }
                } else {
                    //定位失败
                    locationLayout.setText("定位失败" + "错误信息:" + location.getErrorInfo());
                    canAttendance = false;
                }
            } else {
                locationLayout.setText("定位失败，获取不到定位位置");
                canAttendance = false;
            }
        }
    };
    private boolean isToday = true;
    private boolean isBeforeToday = false;
    private int attendanceRecordCounter = 0;
    //    private SparseBooleanArray eachDayPointStatusArray ;
    private Map<Long, Boolean> dateStatusMap = new HashMap<>();
    private WeakHandler<AttendanceActivity> handler = new WeakHandler<AttendanceActivity>(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TAG_UPDATE_CURRENT_TIME) {
                currentTimeTextView.setText(UnitFormatUtil.formatLongToHMS(System.currentTimeMillis()));
                currentTimeTextView.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(TAG_UPDATE_CURRENT_TIME, 1000);
            }else if (msg.what == TAG_UPDATE_ORDER_SUCCESS) {
                currentTimeTextView.setText(UnitFormatUtil.formatLongToHMS(System.currentTimeMillis()));
                currentTimeTextView.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(TAG_UPDATE_CURRENT_TIME, 1000);
//                updateAttendanceLayooutInfo();
            }

        }
    };

    private boolean hasRequestedPermission = false;
    private Unbinder unbinder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_layout);
        unbinder = ButterKnife.bind(this);
        GPSLocationSingleton.get().startRequestPermission(new PermissionsUtil.OnPermissionCallbackImpl(){
            @Override
            public void onSuccess(String[] permission) {
                super.onSuccess(permission);
                hasRequestedPermission = true;
            }
        });

        //// TODO: 2020/11/20
        GPSLocationSingleton.get().beginLocationWithPermission(true, locationListener);

        getAttendanceDetail(System.currentTimeMillis());
        initViews();
        getSystemDate();
        getRecentDaysReport(30);
        startUpdateTime();

    }

    private void getRecentDaysReport(int dayNumber) {
        Type type = new TypeToken<BaseBean<BaseListBean<AttendanceReportBean>>>() {
        }.getType();
        Map<String, String> map = new HashMap<>();
        map.put("day", dayNumber + "");
//        PresenterSingleton.get().doGetData(AttendanceActivity.this, APIDefine.API_attendance_report, map, type, this);
    }

    private void initClockPanel() {
        // 创建 ObjectAnimator 对象
        ObjectAnimator animator = ObjectAnimator.ofFloat(clockPanelLayout, "progressDegree", 0, clockPanelLayout.getTimeDegree());
        animator.setDuration(1500);
        animator.start();
    }

    private void startUpdateTime() {
        handler.sendEmptyMessage(TAG_UPDATE_CURRENT_TIME);
    }

    private void getAttendanceDetail(long dateLong) {
        Map<String, String> map = new HashMap<>();
        map.put("date", String.valueOf(dateLong));
        Type type = new TypeToken<BaseBean<BaseListBean<AttendanceDetailBean>>>() {
        }.getType();
//        PresenterSingleton.get().doGetData(AttendanceActivity.this, APIDefine.API_get_attendance_by_date, map, type, this);

        GetAttendanceDetailTask task = new GetAttendanceDetailTask();
        task.execute(UnitFormatUtil .getCurrentYMD());
    }

    private void refreshUIByDay(long selectDate) {
        getAttendanceDetail(selectDate);
    }

    private void showRemarkDialog() {
        AttendanceOutsideRemarkDialog outerDialog = new AttendanceOutsideRemarkDialog(AttendanceActivity.this);
        outerDialog.show();
        outerDialog.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public <T> void onClickAction(View v, String tag, T t) {
                beginAttendance((String) t);
            }
        });
    }

    private void initViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("今天 yyyy-MM-dd EEEE");
        todayTextView.setText(sdf.format(Calendar.getInstance(TimeZone.getDefault()).getTime()));

        //考虑时区因素
        calendarView.setOnDateSelectedListener((calendarEntity, isClick) -> {

            Calendar todayCalendar = Calendar.getInstance(TimeZone.getDefault());
            int toTodayDayCount = (int) (todayCalendar.getTimeInMillis()/ ONE_DAY_TIME_MILLIS);

            Calendar tempCalendar = Calendar.getInstance(TimeZone.getDefault());
            tempCalendar.set(calendarEntity.getYear(),calendarEntity.getMonth() - 1,calendarEntity.getDay());
            int toSelectDayDayCount = (int) (tempCalendar.getTimeInMillis()/ ONE_DAY_TIME_MILLIS);

            if (toSelectDayDayCount > toTodayDayCount){
                handleAfterToday();
            }else if (toSelectDayDayCount == toTodayDayCount){
                handleToday(todayCalendar.getTimeInMillis());
            }else{
                handleBeforeToday(tempCalendar.getTimeInMillis());
            }

        });

        attendanceLayout.setOnClickListener(v -> executeAttendance());
    }

    private void handleBeforeToday(long selectDate) {
        attendanceLayout.setEnabled(false);
        isToday = false;
        isBeforeToday = true;
        stopUpdateTime();
        stopLocation();
        refreshUIByDay(selectDate);
    }

    private void handleAfterToday() {
        attendanceLayout.setEnabled(false);
        isToday = false;
        isBeforeToday = false;
        showAfterTodayUI();
    }

    private void handleToday(long selectDate) {
        isToday = true;
        attendanceLayout.setEnabled(true);
        startUpdateTime();
        refreshUIByDay(selectDate);
    }

    private void showAfterTodayUI() {
        clockPanelLayout.setProgressDegree(0);
        infoTextView.setText("");
        stopUpdateTime();
        stopLocation();
        attendanceLayout.setEnabled(false);
        currentTimeTextView.setText("");
        currentTimeTextView.setVisibility(View.GONE);
        locationLayout.setText("");
        onDutyLayout.setVisibility(View.GONE);
        offDutyLayout.setVisibility(View.GONE);
        onDutyOutTextView.setVisibility(View.GONE);
        offDutyOutTextView.setVisibility(View.GONE);
        infoTextView.setText("待打卡");
        inOutsideTextView.setVisibility(View.GONE);
    }

    private void getSystemDate() {
        Type type = new TypeToken<BaseBean<SystemDateBean>>() {
        }.getType();
        Map<String, String> map = new HashMap<>();
        map.put("date", String.valueOf(System.currentTimeMillis()));
//        PresenterSingleton.get().doGetData(AttendanceActivity.this, APIDefine.API_get_system_time, map, type, this);
    }

    private CalendarEntity getSchemeCalendar(long dateTime, boolean isException) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateTime);
        CalendarEntity calendar = new CalendarEntity();
        calendar.setYear(c.get(Calendar.YEAR));
        calendar.setMonth(c.get(Calendar.MONTH) + 1);
        calendar.setDay(c.get(Calendar.DAY_OF_MONTH));

        BottomMarkDotBean bottomMarkDotBean = new BottomMarkDotBean();
        bottomMarkDotBean.setShow(true);
        bottomMarkDotBean.setNormal(!isException);
        if (!isException) {
            calendar.setSchemeColor(getResources().getColor(R.color.color_blue_2E7FEF));
        } else {
            calendar.setSchemeColor(getResources().getColor(R.color.color_red_E94156));
        }
        calendar.setBottomMarkDotBean(bottomMarkDotBean);
        return calendar;
    }

    private void addDateStatusToView(Map<Long, Boolean> dateStatusMap) {
        final List<CalendarEntity> schemeList = new ArrayList<>();
        Set<Long> dates = dateStatusMap.keySet();
        for (long date : dates) {
            schemeList.add(getSchemeCalendar(date, dateStatusMap.get(date)));
        }
        calendarView.setSchemeDateList(schemeList);

    }

    private void showBeforeTodayAttendance(List<AttendanceDetailBean> listBean) {
        attendanceLayout.setEnabled(false);
        infoTextView.setText("");
        onDutyOutTextView.setVisibility(View.GONE);
        offDutyOutTextView.setVisibility(View.GONE);
        inOutsideTextView.setVisibility(View.GONE);
        stopUpdateTime();
        currentTimeTextView.setText("");
        currentTimeTextView.setVisibility(View.GONE);
        //其他时间如果是今天之前的话，只有一条数据和两条数据，一条判断是旷工还是休息
        if (isBeforeToday) {
//            if (listBean.getCount() == 1) {
            if (listBean.size() == 1) {
                AttendanceDetailBean bean = listBean.get(0);
                if (bean != null) {
                    infoTextView.setText(bean.getStatus().getMessage());
                }
                //休息或旷工
                if (bean.getStatus().getCode() == 3 || bean.getStatus().getCode() == 4) {
                    onDutyLayout.setVisibility(View.GONE);
                    offDutyLayout.setVisibility(View.GONE);
                }

                clockPanelLayout.setProgressDegree(0);
            } else if (listBean.size() == 2) {
                AttendanceDetailBean onBean = listBean.get(0);
                AttendanceDetailBean offBean = listBean.get(1);
                initOnDutyLayout(onBean);
                initOffDutyLayout(offBean);
                infoTextView.setText("已完成");

                drawProgressWithAnimation(onBean, offBean);
            }
        }
    }

//    private void showTodayAttendance(BaseListBean<AttendanceDetailBean> listBean) {
    private void showTodayAttendance(List<AttendanceDetailBean> listBean) {
        attendanceLayout.setEnabled(true);
//        GPSLocationSingleton.get().startLocationLoop(locationListener);
        if (hasRequestedPermission) {
            GPSLocationSingleton.get().beginLocationWithPermission(true, locationListener);
        }
        inOutsideTextView.setVisibility(View.VISIBLE);
        if (listBean.size() == 0) {
            infoTextView.setText("上班打卡");
            onDutyLayout.setVisibility(View.GONE);
            offDutyLayout.setVisibility(View.GONE);
            onDutyOutTextView.setVisibility(View.GONE);
            offDutyOutTextView.setVisibility(View.GONE);

            clockPanelLayout.setProgressDegree(0);
        } else if (listBean.size() == 1) {
            AttendanceDetailBean bean = listBean.get(0);
            initOnDutyLayout(bean);
            infoTextView.setText("下班打卡");
            offDutyLayout.setVisibility(View.GONE);
            offDutyOutTextView.setVisibility(View.GONE);
            clockPanelLayout.setStartTime(bean.getDate());
            clockPanelLayout.setProgressDegree(1);
            clockPanelLayout.setStartBitmapException(judgeBeanIsException(listBean.get(0)));
        } else if (listBean.size() == 2) {
            AttendanceDetailBean onBean = listBean.get(0);
            AttendanceDetailBean offBean = listBean.get(1);
            initOnDutyLayout(onBean);
            initOffDutyLayout(offBean);
            infoTextView.setText("更新打卡");

            drawProgressWithAnimation(onBean, offBean);
        }
    }

    private void drawProgressWithAnimation(AttendanceDetailBean onBean, AttendanceDetailBean offBean) {
        //休息不作处理
        clockPanelLayout.setStartTime(onBean.getDate());
        if (judgeBeanIsException(onBean)) {
            clockPanelLayout.setStartException();
        } else {
            clockPanelLayout.setStartNormal();
        }
        clockPanelLayout.setEndTime(offBean.getDate());
        if (judgeBeanIsException(offBean)) {
            clockPanelLayout.setEndException();
        } else {
            clockPanelLayout.setEndNormal();
        }
        initClockPanel();
    }

    private boolean judgeBeanIsException(AttendanceDetailBean bean) {
        return (bean != null && bean.getOut() != null && bean.getOut().getCode() != 0 || bean.getStatus().getCode() != 0) && (bean.getStatus().getCode() != 4);
    }

    private boolean isReportBeanException(AttendanceReportBean bean) {
//        return (bean != null && bean.getOut() != null && bean.getOut().getCode() != 0 || bean.getStatus().getCode() != 0) && (bean.getStatus().getCode() != 4);
        return  !(((bean != null) && (bean.getStatus() != null && bean.getStatus().getCode() == 0)
                && (bean.getOut() != null && bean.getOut().getCode() == 0)) || (bean != null && bean.getStatus().getCode() == 4 && bean.getOut() == null)) ;
    }

    private void stopUpdateTime() {
        handler.removeMessages(TAG_UPDATE_CURRENT_TIME);
    }

    private void initOnDutyLayout(AttendanceDetailBean bean) {
        onDutyLayout.setVisibility(View.VISIBLE);
//        onDutyTimeTextView.setText(String.format("%s %s ", bean.getType().getMessage(), UnitFormatUtil.formatLongToHMS(bean.getDate())));
        onDutyTimeTextView.setText(String.format("%s %s ", bean.getType().getMessage(), bean.getDate()));
        onDutyLocationLayout.setText(StringUtils.transferTooLongString(bean != null ? bean.getAddress() : ""));
        onDutyStatusTextView.setText(bean.getStatus().getMessage());
        onDutyStatusTextView.setTextColor(ColorStateUtils.getColorByState(AttendanceActivity.this, bean.getStatus().getCode()));
        if (bean.getOut() != null && bean.getOut().getCode() == 1) {
            onDutyStatusTextView.setText(bean.getOut().getMessage());
            onDutyStatusTextView.setTextColor(ColorStateUtils.getColorByState(AttendanceActivity.this, bean.getOut().getCode()));
            onDutyOutTextView.setText(String.format("外勤原因：%s", bean.getRemark()));
            onDutyOutTextView.setVisibility(View.VISIBLE);
        }else{
            onDutyOutTextView.setVisibility(View.GONE);
        }
    }

    private void initOffDutyLayout(AttendanceDetailBean bean) {
        offDutyLayout.setVisibility(View.VISIBLE);
        offDutyTimeTextView.setText(String.format("%s %s ", bean.getType().getMessage(), bean.getDate()));
        offDutyLocationLayout.setText(StringUtils.transferTooLongString(bean != null ? bean.getAddress() : ""));

        offDutyStatusTextView.setText(bean.getStatus().getMessage());
        offDutyStatusTextView.setTextColor(ColorStateUtils.getColorByState(AttendanceActivity.this, bean.getStatus().getCode()));

        if (bean.getOut() != null && bean.getOut().getCode() == 1) {
            offDutyStatusTextView.setText(bean.getOut().getMessage());
            offDutyStatusTextView.setTextColor(ColorStateUtils.getColorByState(AttendanceActivity.this, bean.getOut().getCode()));
            offDutyOutTextView.setText(String.format("外勤原因：%s", bean.getRemark()));
            offDutyOutTextView.setVisibility(View.VISIBLE);
        }else{
            offDutyOutTextView.setVisibility(View.GONE);
        }
    }

    private void executeAttendance() {
        if (canAttendance) {
            if (isInnerRange) {
                if (attendanceRecordCounter == 2) {
                    showConfirmDialog();
                } else {
                    beginAttendance("");
                }
            } else {
                //提示外勤
                showRemarkDialog();
            }
        }
        
        //// TODO: 2020/11/20  
        beginAttendance("");


    }

    private void showConfirmDialog() {
        new CommAlertDialog.DialogBuilder(AttendanceActivity.this)
                .setTitle("温馨提示").setMessage("你确定要更新打卡吗")
                .setLeftText("确定").setRightText("取消")
                .setAllButtonColorRes(R.color.color_228BE0)
                .setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        if (tag == CommAlertDialog.TAG_CLICK_LEFT) {
                            beginAttendance("");
                        }
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void beginAttendance(String remark) {
//        executeAttendance(HR_ID As Integer, HR_Name As String, day_span As String, device_id As String, latitude As String, longitude As String, address As String, remark As String, out As Integer)

        Map<String, String> map = new HashMap<>();
        map.put("address", locationName);
        map.put("day_span", 0 + "");
        map.put("device_id", DeviceUtil.getDeviceIdString());
        map.put("latitude", latitude + "");
        map.put("longitude", longitude + "");
        map.put("remark", remark);
        map.put("out", isInnerRange ? "0" : "1");

//        Type type = new TypeToken<BaseBean>() {
////        }.getType();
////
////        PresenterSingleton.get().doPostAction(AttendanceActivity.this, APIDefine.API_attendance, map, type, this);
        ExecuteAttendanceTask task = new ExecuteAttendanceTask();
        task.execute(map);
    }

    private void stopLocation() {
        // 停止定位
        GPSLocationSingleton.get().stopLocationLoop();
    }

    private void getAttendanceRule() {
        Type type = new TypeToken<BaseBean<AttendanceInfoBean>>() {
        }.getType();
//        PresenterSingleton.get().doGetData(APIDefine.API_get_attendance_myinfo, type, this);
    }

    private void showApiError(String api, BaseBean msg, String requestApi) {
        if (api.equals(requestApi)) {
//            ToastUtil.showToastShort(getString(R.string.request_server_error) + api + msg.getMessage());
            ToastUtil.showToastShort("接口请求错误" + api + msg.getMessage());
        }
    }



    public void onFail(String api, BaseBean msg) {
        showApiError(api, msg, APIDefine.API_get_system_time);
        showApiError(api, msg, APIDefine.API_get_attendance_myinfo);
        showApiError(api, msg, APIDefine.API_attendance);
        showApiError(api, msg, APIDefine.API_get_attendance_by_date);
        showApiError(api, msg, APIDefine.API_attendance_report);

        if (api.equals(APIDefine.API_get_attendance_by_date)) {
            getSystemDate();
        }
        if (api.equals(APIDefine.API_attendance)) {
            ToastUtil.showToastLong("打卡失败！");
        }
    }


    public <T> void initData(String api, T t) {
        if (api.equals(APIDefine.API_get_system_time)) {
            SystemDateBean bean = (SystemDateBean) t;
            if (bean != null) {
                String attendanceRule = SPSingleton.get().getString(SPDefine.KEY_attendance_rule, null);
                if (bean.isExpired() || !StringUtils.isStringValid(attendanceRule)) {
                    stopLocation();
//                    canAttendance = false;
                    getAttendanceRule();
                }
            }
        } else if (api.equals(APIDefine.API_get_attendance_myinfo)) {
            AttendanceInfoBean bean = (AttendanceInfoBean) t;
            if (bean != null) {
                canAttendance = true;
                String json = JsonUtil.objectToJson(bean);
                SPSingleton.get().putString(SPDefine.KEY_attendance_rule, json);
                if (hasRequestedPermission) {
                    GPSLocationSingleton.get().beginLocationWithPermission(true, locationListener);
                }
            }
        } else if (api.equals(APIDefine.API_attendance)) {
            if (t != null) {
                SuccessNoticeDialog dialog = new SuccessNoticeDialog(AttendanceActivity.this);
                dialog.show();
                getAttendanceDetail(System.currentTimeMillis());

            }
        } else if (api.equals(APIDefine.API_get_attendance_by_date)) {
//            BaseListBean<AttendanceDetailBean> listBean = (BaseListBean<AttendanceDetailBean>) t;
//            attendanceRecordCounter = listBean.getCount();
//            if (isToday) {
//                showTodayAttendance(listBean);
//            } else {
//                showBeforeTodayAttendance(listBean);
//            }
        } else if (api.equals(APIDefine.API_attendance_report)) {
            BaseListBean<AttendanceReportBean> listBean = (BaseListBean<AttendanceReportBean>) t;
            if (listBean != null && listBean.getCount() > 0) {
                dateStatusMap.clear();
                int i = 0;
                for (AttendanceReportBean bean : listBean.getResults()) {
                    dateStatusMap.put(bean.getDate(), isReportBeanException(bean));
                }
                addDateStatusToView(dateStatusMap);
            }
        }
    }

    private class ExecuteAttendanceTask extends AsyncTask< Map<String, String> ,Void,WsResult>{

        @Override protected WsResult doInBackground(Map<String, String>... maps) {
            if (maps.length > 0){
              return  WebServiceUtil.executeAttendance(maps[0]);
            }
            return null;
        }

        @Override protected void onPostExecute(WsResult result) {
            if (result.getResult()) {
                ToastUtil.showToastLong(commitType == 1 ? "打卡成功！" : "更新打卡成功！");
            } else {
                ToastUtil.showToastLong("操作失败！" + result.getErrorInfo());
            }
            handler.sendEmptyMessage(TAG_UPDATE_ORDER_SUCCESS);
        }
    }


    private class GetAttendanceDetailTask extends AsyncTask< String ,Void,WsResult>{

        @Override protected WsResult doInBackground(String... strings) {
            try{
                if (strings.length > 0){
                    return  WebServiceUtil.getAttendanceDetailByDay(strings[0]);
                }
                return null;
            }catch(Exception e){
                return null;
            }

        }

        @Override protected void onPostExecute(WsResult result) {
            if (result == null){
                ToastUtil.showToastLong("获取考勤信息失败！" );
            }else{
                if (result.getResult()) {
                    ToastUtil.showToastLong(commitType == 1 ? "打卡成功！" : "更新打卡成功！");
                    try{
                        Type type = new TypeToken<List<AttendanceDetailBean>>() {
                        }.getType();
                        List<AttendanceDetailBean> listBean = JsonUtil .parseJsonToObject(result.getErrorInfo(),type);

                        if (isToday) {
                            showTodayAttendance(listBean);
                        } else {
                            showBeforeTodayAttendance(listBean);
                        }
                    }catch(Exception e){
                        ToastUtil.showToastShort(e.getMessage());
                    }

                } else {
                    ToastUtil.showToastLong("操作失败！" + result.getErrorInfo());
                }
//                handler.sendEmptyMessage(TAG_UPDATE_ORDER_SUCCESS);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //todo 当界面离开时，停止定位
        stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        handler.removeMessages(TAG_UPDATE_CURRENT_TIME);
    }
}
