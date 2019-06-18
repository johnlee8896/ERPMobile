package com.chinashb.www.mobileerp.singleton;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;

/***
 * @date 创建时间 2019/6/17 10:46 AM
 * @author 作者: liweifeng
 * @description 用单例模式管理用户，包含登录状态等
 */
public class UserSingleton {
    private UserSingleton(){

    }
    private UserInfoEntity userInfo;

    public UserSingleton setUserInfo(UserInfoEntity userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public UserSingleton get(){
        return UserSingletonHelper.instance;
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

    public boolean hasLogin(){
        return userInfo != null && userInfo.getHR_ID() > 0;
    }

}
