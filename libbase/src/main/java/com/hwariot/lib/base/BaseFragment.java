package com.hwariot.lib.base;

import android.content.Intent;
import android.support.v4.app.Fragment;


/***
 * @date 创建时间 2018/3/22 13:20
 * @author 作者: yulong
 * @description 基类的Fragment
 */
public class BaseFragment extends Fragment {


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        enterPendingAnim();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        enterPendingAnim();
    }


    private void enterPendingAnim() {
        getActivity().overridePendingTransition(R.anim.base_slide_in_right, R.anim.base_anim_normal);
    }

}
