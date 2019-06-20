package com.chinashb.www.mobileerp.task;

import android.os.AsyncTask;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;

/***
 * @date 创建时间 2019/6/19 1:29 PM
 * @author 作者: liweifeng
 * @description
 */
public class GetHrNameAsyncTask extends AsyncTask<String, Void, Void> {
    //Image hr_photo;
    private UserInfoEntity userInfo;


    @Override
    protected Void doInBackground(String... params) {

//        int hrId = Integer.parseInt(params[0]);
////            userInfo = WebServiceUtil.getHRName_Bu(userInfo.getHR_ID());
//        userInfo = WebServiceUtil.getHRName_Bu(hrId);
//
//
//        if (userInfo != null) {
//            pictureBitmap = CommonUtil.getUserPic(MobileMainActivity.this, userPictureMap, userInfo.getHR_ID());
//        }


        return null;
    }

    @Override
    protected void onPreExecute() {
//        scanProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void result) {
//        if (userInfo != null) {
//            setTvUserName();
//            if (pictureBitmap != null) {
//                avatarImageView.setImageBitmap(pictureBitmap);
//            }
//        } else {
//            Toast.makeText(MobileMainActivity.this, "无法访问服务器，请检查网络连接是否正常", Toast.LENGTH_LONG).show();
//        }
//
//        scanProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

}
