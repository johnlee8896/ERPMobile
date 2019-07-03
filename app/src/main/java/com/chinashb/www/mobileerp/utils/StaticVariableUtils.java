package com.chinashb.www.mobileerp.utils;

import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.basicobject.s_WCList;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2019/7/2 13:21
 * @author 作者: xxblwf
 * @description 存储一些static变量，暂时放这里，之后要统一整理出去
 */

public class StaticVariableUtils {
    //用来保存先前选过的产线组
    public static s_WCList selected_list;
    public static WorkCenter selected_wc;
    public static List<MpiWcBean> selected_mws = new ArrayList<>();
}
