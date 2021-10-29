package com.chinashb.www.mobileerp.utils;

/***
 * @date 创建时间 2019/6/19 9:50 AM
 * @author 作者: liweifeng
 * @description intent常量
 */
public class IntentConstant {



    public static final String Intent_Extra_do_id = "Extra_do_id";



    public static final String Intent_Extra_hr_id = "Extra_hr_id";
    public static final String Intent_Extra_from_name_pwd= "Extra_from_name_pwd";
    public static final String Intent_Extra_to_select_search_from_postition = "Extra_to_select_search_from_postition";
//    public static final String Intent_Extra_to_select_search_from_send_goods = "Extra_to_select_search_from_send_goods";


    public static final int Select_Search_From_Select_BU = 0x1;
    public static final int Select_Search_From_Select_Department = 0x2;
    public static final int Select_Search_From_Select_ReSearch_Program = 0x3;
    public static final int Select_Search_From_Select_Check_File = 0x4;
    public static final int Select_Search_From_Select_PanDina = 0x5;
    public static final int Select_Search_From_Select_Send_Goods_Item = 0x6;

    public static final String Intent_continue_put_directly = "continue_put_directly";
    public static String Intent_Extra_mpiWcBean = "intent_mpiWcBean";
    public static String Intent_PlanItemDetailBean = "PlanItemDetailBean";


    public static String Intent_Part_middle_map= "Part_middle_map";
    public static String Intent_Part_middle_map_list= "Part_middle_map_list";
    public static String Intent_supplier_input_title= "supplier_input_title";


    public static final String Intent_product_wc_id_name_entity = "product_wc_id_name_entity";
    public static final String Intent_product_delivery_order_bean = "product_delivery_order_bean";

    public static final String Intent_Extra_work_line_from = "Extra_work_line_from";
    public static final String Intent_Extra_logistics_entity = "Extra_logistics_entity";
    public static final String Intent_Extra_task_bean = "Extra_task_bean";


    public static final String Intent_Extra_logistics_customer_company_name = "Extra_logistics_customer_company_name";
    public static final String Intent_Extra_logistics_transport_type = "Extra_logistics_transport_type";
    public static final String Intent_Extra_logistics_logistics_company = "Extra_logistics_logistics_company";
    public static final String Intent_Extra_logistics_address = "Extra_logistics_address";
    public static final String Intent_Extra_logistics_remark = "Extra_logistics_remark";
    public static final String Intent_Extra_logistics_delivery_id = "Extra_logistics_delivery_id";
    public static final String Intent_Extra_logistics_cf_id = "Extra_logistics_cf_id";
    public static final String Intent_Extra_logistics_cf_name = "Extra_logistics_cf_name";
    public static final String Intent_Extra_do_delivery_bean = "Extra_do_delivery_bean";
    public static final String Intent_Extra_logistics_select_bean = "Extra_logistics_select_bean";
    public static final String Intent_Extra_logistics_customer_bean = "Extra_logistics_customer_bean";
    public static final String Intent_Extra_supplier_or_self_return_boolean = "Extra_supplier_or_self_return_boolean";
    public static final String Intent_Extra_check_from_zaizhipin = "Extra_check_from_zaizhipin";


    public static final String Intent_Extra_logistics_from = "Extra_logistics_from";



    public static final String Intent_Extra_current_bu_id = "Extra_current_bu_id";
    public static final String Intent_Extra_select_bu_bean = "Extra_select_bu_bean";


    public static final int Intent_Request_Code_Product_Out_And_Delivery_Order = 0X100;
    public static final int Intent_Request_Code_Product_Out_And_Check_Same = 0X109;
    public static final int Intent_Extra_work_line_from_product = 0X200;
    public static final int Intent_Extra_work_line_from_part = 0X201;
    public static final int Intent_Request_Code_Product_To_Logistics = 0X202;
    public static final int Intent_Request_Code_Logistics_Select_to_Logistics = 0X203;
    public static final int Intent_Request_Code_Sale_Out_to_Bu = 0X204;
    public static final int Intent_Request_Code_Logistics_Customer_to_Logistics = 0X205;


    public static final int Intent_Request_Code_Logistics_from_product_sale_out = 0X206;
    public static final int Intent_Request_Code_Logistics_from_sdzh = 0X207;
    public static final int Intent_Request_Code_Logistics_from_sdzh_scan_pallet = 0X208;



    public static final int Intent_Request_Code_Inv_Query_Middle_from_Activity_To_Activity = 0X209;
    public static final int Intent_Request_Code_Inv_Query_Middle_from_Dialog_To_Activity = 0X210;

}
