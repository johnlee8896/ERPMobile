package com.chinashb.www.mobileerp.utils;

import java.security.spec.PSSParameterSpec;

/***
 * @date 创建时间 2019/6/19 9:50 AM
 * @author 作者: liweifeng
 * @description intent常量
 */
public class IntentConstant {
    public static final String Intent_Extra_hr_id = "Extra_hr_id";
    public static final String Intent_Extra_from_name_pwd= "Extra_from_name_pwd";
    public static final String Intent_Extra_to_select_search_from_postition = "Extra_to_select_search_from_postition";


    public static final int Select_Search_From_Select_BU = 0x1;
    public static final int Select_Search_From_Select_Department = 0x2;
    public static final int Select_Search_From_Select_ReSearch_Program = 0x3;
    public static final int Select_Search_From_Select_Check_File = 0x4;

    public static final String Intent_continue_put_directly = "continue_put_directly";
    public static String Intent_Extra_mpiWcBean = "intent_mpiWcBean";
    public static String Intent_PlanItemDetailBean = "PlanItemDetailBean";


    public static String Intent_Part_middle_map= "Part_middle_map";
    public static String Intent_Part_middle_map_list= "Part_middle_map_list";
    public static String Intent_supplier_input_title= "supplier_input_title";


    public static final String Intent_product_wc_id_name_entity = "product_wc_id_name_entity";
    public static final String Intent_product_delivery_order_bean = "product_delivery_order_bean";


    public static final int Intent_Request_Code_Product_Out_And_Delivery_Order = 0X100;
}
