package com.chinashb.www.mobileerp.warehouse.menu;

import com.chinashb.www.mobileerp.R;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2026/5/2 15:06
 * @author 作者: code-x John
 * @description 仓库主页菜单配置中心
 */
public class WarehouseMenuRegistry {

    public static final String MODULE_PART = "PART";
    public static final String MODULE_PRODUCT = "PRODUCT";

    public static final String MENU_PART_STOCK_IN = "PART_STOCK_IN";
    public static final String MENU_PART_STOCK_IN_WITH_DATE = "PART_STOCK_IN_WITH_DATE";
    public static final String MENU_PART_PRODUCT_SUPPLY = "PART_PRODUCT_SUPPLY";
    public static final String MENU_PART_REWORK_OUT = "PART_REWORK_OUT";
    public static final String MENU_PART_REWORK_OUT_MALAI = "PART_REWORK_OUT_MALAI";
    public static final String MENU_PART_MOVE = "PART_MOVE";
    public static final String MENU_PART_FREEZE = "PART_FREEZE";
    public static final String MENU_PART_DEPARTMENT_PICK = "PART_DEPARTMENT_PICK";
    public static final String MENU_PART_PRODUCT_QUERY = "PART_PRODUCT_QUERY";
    public static final String MENU_PART_QUERY = "PART_QUERY";
    public static final String MENU_PART_WIP_CHECK = "PART_WIP_CHECK";
    public static final String MENU_PART_CHECK = "PART_CHECK";
    public static final String MENU_PART_SELF_PRODUCT_CHECK = "PART_SELF_PRODUCT_CHECK";
    public static final String MENU_PART_LOOK_QR = "PART_LOOK_QR";
    public static final String MENU_PART_WORKLINE_PICK = "PART_WORKLINE_PICK";
    public static final String MENU_PART_INNER_SALE_OUT = "PART_INNER_SALE_OUT";
    public static final String MENU_PART_PLAN_GOODS = "PART_PLAN_GOODS";
    public static final String MENU_PART_SUPPLIER_RETURN = "PART_SUPPLIER_RETURN";
    public static final String MENU_PART_SELF_RETURN = "PART_SELF_RETURN";
    public static final String MENU_PART_SEND_GOODS = "PART_SEND_GOODS";
    public static final String MENU_PART_LOGISTICS_RECEIVE = "PART_LOGISTICS_RECEIVE";
    public static final String MENU_PART_PACK = "PART_PACK";
    public static final String MENU_PART_UNPACK = "PART_UNPACK";
    public static final String MENU_PART_PHOTO = "PART_PHOTO";
    public static final String MENU_PART_SCAN_IST = "PART_SCAN_IST";
    public static final String MENU_PART_MOVE_RECORD = "PART_MOVE_RECORD";
    public static final String MENU_PART_ALLOCATE_INNER_COMPANY = "PART_ALLOCATE_INNER_COMPANY";
    public static final String MENU_PART_GET_PICTURE = "PART_GET_PICTURE";
    public static final String MENU_PART_PICK_GOODS = "PART_PICK_GOODS";
    public static final String MENU_PART_FREEZE_RECORD = "PART_FREEZE_RECORD";
    public static final String MENU_PART_SEND_INFO = "PART_SEND_INFO";
    public static final String MENU_PART_COMPANY29 = "PART_COMPANY29";
    public static final String MENU_PART_SHIPMENT = "PART_SHIPMENT";

    public static final String MENU_PRODUCT_SALE_OUT = "PRODUCT_SALE_OUT";
    public static final String MENU_PRODUCT_SALE_OUT_PALLET = "PRODUCT_SALE_OUT_PALLET";
    public static final String MENU_PRODUCT_SCAN_BOX_IN = "PRODUCT_SCAN_BOX_IN";
    public static final String MENU_PRODUCT_SCAN_IST = "PRODUCT_SCAN_IST";
    public static final String MENU_PRODUCT_BU_WAREHOUSE_IN = "PRODUCT_BU_WAREHOUSE_IN";
    public static final String MENU_PRODUCT_BU_WAREHOUSE_OUT = "PRODUCT_BU_WAREHOUSE_OUT";
    public static final String MENU_PRODUCT_CODE_BOX = "PRODUCT_CODE_BOX";
    public static final String MENU_PRODUCT_CODE_BOX_MANU = "PRODUCT_CODE_BOX_MANU";
    public static final String MENU_PRODUCT_DELIVERY_ORDER = "PRODUCT_DELIVERY_ORDER";
    public static final String MENU_PRODUCT_REWORK_PALLET = "PRODUCT_REWORK_PALLET";
    public static final String MENU_PRODUCT_MOVE_RECORD = "PRODUCT_MOVE_RECORD";
    public static final String MENU_PRODUCT_CHECK_ALL = "PRODUCT_CHECK_ALL";

    public static List<WarehouseMenuItem> getPartMenuItems() {
        List<WarehouseMenuItem> list = new ArrayList<>();
        list.add(item(MODULE_PART, MENU_PART_STOCK_IN, "扫描入库", R.mipmap.greenhouse, 10));
        list.add(item(MODULE_PART, MENU_PART_PRODUCT_SUPPLY, "生产投料", R.mipmap.xenogenesis_2, 20));
        list.add(item(MODULE_PART, MENU_PART_REWORK_OUT, "返工出库", R.mipmap.monster_mike, 30));
        list.add(item(MODULE_PART, MENU_PART_MOVE, "移动库位", R.mipmap.xmas_4, 40));
        list.add(item(MODULE_PART, MENU_PART_FREEZE, "冻结库存", R.mipmap.monster_yellow, 50));
        list.add(item(MODULE_PART, MENU_PART_DEPARTMENT_PICK, "部门领料", R.mipmap.monster_blue, 60));
        list.add(item(MODULE_PART, MENU_PART_PRODUCT_QUERY, "成品库存", R.mipmap.folder_1, 70));
        list.add(item(MODULE_PART, MENU_PART_QUERY, "零部件库存", R.mipmap.book, 80));
        list.add(item(MODULE_PART, MENU_PART_WIP_CHECK, "在制品盘存", R.mipmap.roundbutton4, 90));
        list.add(item(MODULE_PART, MENU_PART_CHECK, "零部件盘点", R.mipmap.box_2, 100));
        list.add(item(MODULE_PART, MENU_PART_SELF_PRODUCT_CHECK, "自制成品盘存", R.mipmap.roundutton2, 110));
        list.add(item(MODULE_PART, MENU_PART_LOOK_QR, "看看条码", R.mipmap.ic_launcher, 120));
        list.add(item(MODULE_PART, MENU_PART_WORKLINE_PICK, "生产线领料", R.mipmap.xenogenesis_2, 130));
        list.add(item(MODULE_PART, MENU_PART_INNER_SALE_OUT, "集团内销售出库", R.mipmap.book, 140));
        list.add(item(MODULE_PART, MENU_PART_PLAN_GOODS, "车间要货计划", R.mipmap.xenogenesis_2, 150));
        list.add(item(MODULE_PART, MENU_PART_SUPPLIER_RETURN, "供应商退货", R.mipmap.folder_1, 160));
        list.add(item(MODULE_PART, MENU_PART_SELF_RETURN, "自制车间退货", R.mipmap.xenogenesis_2, 170));
        list.add(item(MODULE_PART, MENU_PART_SEND_GOODS, "发货管理", R.mipmap.greenhouse, 180));
        list.add(item(MODULE_PART, MENU_PART_LOGISTICS_RECEIVE, "物流来料接收", R.mipmap.xenogenesis_2, 190));
        list.add(item(MODULE_PART, MENU_PART_STOCK_IN_WITH_DATE, "扫描入库(可指定日期)", R.mipmap.greenhouse, 200));
        list.add(item(MODULE_PART, MENU_PART_PACK, "编辑拆解包装内容", R.mipmap.xenogenesis_2, 210));
        list.add(item(MODULE_PART, MENU_PART_UNPACK, "编辑增加包装内容", R.mipmap.greenhouse, 220));
        list.add(item(MODULE_PART, MENU_PART_PHOTO, "图片处理", R.mipmap.xenogenesis_2, 230));
        list.add(item(MODULE_PART, MENU_PART_SCAN_IST, "扫描库位查物料", R.mipmap.xenogenesis_2, 240));
        list.add(item(MODULE_PART, MENU_PART_MOVE_RECORD, "移库查询", R.mipmap.xenogenesis_2, 250));
        list.add(item(MODULE_PART, MENU_PART_ALLOCATE_INNER_COMPANY, "同公司零件调拨", R.mipmap.xenogenesis_2, 260));
        list.add(item(MODULE_PART, MENU_PART_GET_PICTURE, "获取图片", R.mipmap.xenogenesis_2, 270));
        list.add(item(MODULE_PART, MENU_PART_PICK_GOODS, "拣货任务", R.mipmap.xenogenesis_2, 280));
        list.add(item(MODULE_PART, MENU_PART_FREEZE_RECORD, "查询冻结记录", R.mipmap.xenogenesis_2, 290));
        list.add(item(MODULE_PART, MENU_PART_SEND_INFO, "标签发货信息", R.mipmap.xenogenesis_2, 300));
        list.add(item(MODULE_PART, MENU_PART_COMPANY29, "零部件公司入库", R.mipmap.xenogenesis_2, 310));
        list.add(item(MODULE_PART, MENU_PART_REWORK_OUT_MALAI, "马来返工出库", R.mipmap.xenogenesis_2, 320));
        list.add(item(MODULE_PART, MENU_PART_SHIPMENT, "外贸发运扫码", R.mipmap.box_2, 330));
        return list;
    }

    public static List<WarehouseMenuItem> getProductMenuItems() {
        List<WarehouseMenuItem> list = new ArrayList<>();
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_SALE_OUT, "成品出库(MES)", R.mipmap.folder_1, 10));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_SALE_OUT_PALLET, "成品出库(扫托盘标签)", R.mipmap.book, 20));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_SCAN_BOX_IN, "成品入库(扫描MES箱号)", R.mipmap.greenhouse, 30));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_SCAN_IST, "扫描库位查找物料", R.mipmap.xmas_4, 40));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_BU_WAREHOUSE_IN, "车间仓尾数扫描入库", R.mipmap.xenogenesis_2, 50));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_BU_WAREHOUSE_OUT, "车间仓尾数解除", R.mipmap.monster_blue, 60));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_CODE_BOX, "成品标签操作", R.mipmap.box_2, 70));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_CODE_BOX_MANU, "成品(补打)标签操作", R.mipmap.monster_yellow, 80));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_DELIVERY_ORDER, "发货指令查询", R.mipmap.book, 90));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_REWORK_PALLET, "成品整托返工", R.mipmap.monster_mike, 100));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_MOVE_RECORD, "成品移库记录查询", R.mipmap.folder_1, 110));
        list.add(item(MODULE_PRODUCT, MENU_PRODUCT_CHECK_ALL, "成品统一盘点", R.mipmap.roundbutton4, 120));
        return list;
    }

    private static WarehouseMenuItem item(String moduleCode, String menuCode, String title, int iconRes, int sortNo) {
        return new WarehouseMenuItem(moduleCode, menuCode, title, iconRes, sortNo, true);
    }
}
