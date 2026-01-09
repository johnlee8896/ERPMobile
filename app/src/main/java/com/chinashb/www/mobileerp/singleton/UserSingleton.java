package com.chinashb.www.mobileerp.singleton;

import com.chinashb.www.mobileerp.basicobject.UserAllInfoEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;

import java.util.HashMap;

/***
 * @date 创建时间 2019/6/17 10:46 AM
 * @author 作者: liweifeng
 * @description 用单例模式管理用户，包含登录状态等
 */
public class UserSingleton {
    public boolean hasSwitchedBu;
    private HashMap<Integer, String> departmentMap;
    private UserAllInfoEntity userAllInfoEntity;
    private boolean isCurrentInnerNetLink;
    //2024-01-04暂时去掉serverback的判断
//    private boolean serverBack;
    private boolean testEnvironment;
    private UserInfoEntity userInfo;
    private int HRID;
    private String HRName;
    private String HRNO;
    private String HR_IDCardNO;
    private boolean stockPermit = false;
    //// TODO: 5/6/25  全局变量，给查冻结备注使用
    private int itemID;

    private UserSingleton() {

    }

    public static UserSingleton get() {
        return UserSingletonHelper.instance;
    }

    public boolean isTestEnvironment() {
        return testEnvironment;
    }

    public UserSingleton setTestEnvironment(boolean testEnvironment) {
        this.testEnvironment = testEnvironment;
        return this;
    }

//    public boolean isServerBack() {
//        return serverBack;
//    }
//
//    public void setServerBack(boolean serverBack) {
//        this.serverBack = serverBack;
//    }

    public boolean isCurrentInnerNetLink() {
        return isCurrentInnerNetLink;
    }

    public void setCurrentInnerNetLink(boolean currentInnerNetLink) {
        isCurrentInnerNetLink = currentInnerNetLink;
    }

    public boolean isHasSwitchedBu() {
        return hasSwitchedBu;
    }

    public void setHasSwitchedBu(boolean hasSwitchedBu) {
        this.hasSwitchedBu = hasSwitchedBu;
    }

    public UserInfoEntity getUserInfo() {
        return userInfo;
    }

    public UserSingleton setUserInfo(UserInfoEntity userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public String getHR_IDCardNO() {
        return HR_IDCardNO;
    }

    public void setHR_IDCardNO(String HR_IDCardNO) {
        this.HR_IDCardNO = HR_IDCardNO;
    }

    public String getHRNO() {
        return HRNO;
    }

    public void setHRNO(String HRNO) {
        this.HRNO = HRNO;
    }

    public String getHRName() {
        return HRName;
    }

    public int getItemID() {
        return itemID;
    }

    public UserSingleton setItemID(int itemID) {
        this.itemID = itemID;
        return this;
    }

    public UserSingleton setHRName(String HRName) {
        this.HRName = HRName;
        return this;
    }

    public boolean isStockPermit() {
        return stockPermit;
    }

    public void setStockPermit(boolean b) {
        stockPermit = b;
    }

    public int getHRID() {
        return HRID;
    }

    public UserSingleton setHRID(int HRID) {
        this.HRID = HRID;
        return this;
    }

    public HashMap<Integer, String> getDepartmentMap() {
        return departmentMap;
    }

    public void setDepartmentMap(HashMap<Integer, String> departmentIDNameMap) {
        this.departmentMap = departmentIDNameMap;
    }

    public UserAllInfoEntity getUserAllInfoEntity() {
        return userAllInfoEntity;
    }

    public void setUserAllInfoEntity(UserAllInfoEntity userAllInfoEntity) {
        this.userAllInfoEntity = userAllInfoEntity;
    }

    public boolean hasLogin() {
        return userInfo != null && userInfo.getHR_ID() > 0;
    }

//    public boolean isUserLogin() {
//        if (userInfo != null) {
//            if (userInfo.getHR_ID() > 0) {
//                return true;
//            } else {
//                CommonUtil.ShowToast(this, "请先登录", R.mipmap.warning, Toast.LENGTH_SHORT);
//                return false;
//            }
//
//        } else {
//            CommonUtil.ShowToast(this, "请先登录", R.mipmap.warning, Toast.LENGTH_SHORT);
//            return false;
//        }
//
//    }

    private static class UserSingletonHelper {
        private static UserSingleton instance = new UserSingleton();

    }

//    protected Boolean isUserLogin() {
//        if (userInfo != null) {
//            if (userInfo.getHR_ID() > 0) {
//                return true;
//            } else {
//                CommonUtil.ShowToast(this, "请先登录", R.mipmap.warning, Toast.LENGTH_SHORT);
//                return false;
//            }
//
//        } else {
//            CommonUtil.ShowToast(this, "请先登录", R.mipmap.warning, Toast.LENGTH_SHORT);
//            return false;
//        }
//
//    }

}
