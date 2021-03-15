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

    private UserSingleton() {

    }

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

    private UserInfoEntity userInfo;

    public UserInfoEntity getUserInfo() {
        return userInfo;
    }

    private int HRID;
    private String HRName;
    private String HRNO;
    private String HR_IDCardNO;

    private boolean stockPermit = false;

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

    public boolean isStockPermit() {
        return stockPermit;
    }

    public UserSingleton setHRName(String HRName) {
        this.HRName = HRName;
        return this;
    }

    public int getHRID() {
        return HRID;
    }

    public UserSingleton setHRID(int HRID) {
        this.HRID = HRID;
        return this;
    }

    public UserSingleton setUserInfo(UserInfoEntity userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public static UserSingleton get() {
        return UserSingletonHelper.instance;
    }

    public void setDepartmentMap(HashMap<Integer, String> departmentIDNameMap) {
        this.departmentMap = departmentIDNameMap;
    }

    public HashMap<Integer, String> getDepartmentMap() {
        return departmentMap;
    }

    public void setUserAllInfoEntity(UserAllInfoEntity userAllInfoEntity) {
        this.userAllInfoEntity = userAllInfoEntity;
    }

    public UserAllInfoEntity getUserAllInfoEntity() {
        return userAllInfoEntity;
    }

    public void setStockPermit(boolean b) {
        stockPermit = b;
    }


    private static class UserSingletonHelper {
        private static UserSingleton instance = new UserSingleton();

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

    public boolean hasLogin() {
        return userInfo != null && userInfo.getHR_ID() > 0;
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
