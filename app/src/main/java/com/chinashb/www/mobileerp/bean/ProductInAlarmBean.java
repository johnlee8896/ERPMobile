package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 5/30/25 3:15 PM
 * @author 作者: liweifeng
 * @description 成品入库判断是否是发货的紧急料
 */
public class ProductInAlarmBean {

    /**
     * DO_ID : 180160
     * DPI_ID : 372812
     * CPO_ID : 0
     * 销售公司 : 滁州座椅
     * BU_ID : 81
     * 销售车间 : 滁州座椅电机
     * 生产车间 : 滁州座椅电机
     * 发货地点 : null
     * 国内外 : 国内
     * 客户 : 麦格纳宏立汽车系统（合肥）有限公司
     * 编辑者 : 沈蕾
     * 指令创建时间 : /Date(1748503667230+0800)/
     * 指令最后更新时间 : /Date(1748503667243+0800)/
     * 指令明细最后更新时间 : /Date(1748503757493+0800)/
     * 跟踪号 : GN20250602-20
     * 发货日期 : /Date(1748793600000+0800)/
     * 客户窗口时间 : 上午9点
     * 指令类型 : 量产
     * Product_ID : 47398
     * PS_ID : 57348
     * 产品通称 : P-SE22100
     * 客户零件号 : SE22100
     * 产品名称 : 后抬高电机
     * 样件项目名称 : null
     * 当前版本 : 01
     * 最新版本 : 01
     * 发货指令数 : 324
     * 已出库数 : null
     * 余数 : 324.0
     * 排版 : false
     * 延迟申请 : false
     * 物流方式 : null
     * 包装方式 : null
     * 备注 : 5.29号生产，6.2号正常发货
     * 备注人 : 范振华
     * 到达日期 : /Date(1748880000000+0800)/
     * 接收信息 : 收货地址：安徽省合肥市高新区南岗工业园区长宁大道与响洪甸路交口向西200米
     1.到合肥麦格纳找“经明珠13956047524”签字
     2.安徽省合肥市蜀山区响洪甸路980号  一楼采购部 找  经明珠
     3.9:00前与14：00前在外库送检（其余时间在签证处送检）
     4.卸货到“迈睿思”，安徽省合肥市蜀山区高新区长宁大道3000号博玛轻工园区2厂2楼
     坐盆收货人管文瑞  18726624935
     （其余HA3的5个零件）
     焊接收货人张圣康  15956949031
     （零件号：SR10310与SR10311S)
     * 特殊要求 : PA、S311
     * 库存 : 324.0
     * 冻结库存 : null
     * 库存够发货 : true
     * 上海座椅 : null
     * 滁州座椅 : 324.0
     * 上海后装 : null
     * 滁州后装 : null
     * 计划员 : 范振华
     * 总装线别 : D5
     */

    @SerializedName("DO_ID") private int DOID;
    @SerializedName("DPI_ID") private int DPIID;
    @SerializedName("CPO_ID") private int CPOID;
    @SerializedName("销售公司") private String 销售公司;
    @SerializedName("BU_ID") private int BUID;
    @SerializedName("销售车间") private String 销售车间;
    @SerializedName("生产车间") private String 生产车间;
    @SerializedName("发货地点") private Object 发货地点;
    @SerializedName("国内外") private String 国内外;
    @SerializedName("客户") private String 客户;
    @SerializedName("编辑者") private String 编辑者;
    @SerializedName("指令创建时间") private String 指令创建时间;
    @SerializedName("指令最后更新时间") private String 指令最后更新时间;
    @SerializedName("指令明细最后更新时间") private String 指令明细最后更新时间;
    @SerializedName("跟踪号") private String 跟踪号;
    @SerializedName("发货日期") private String 发货日期;
    @SerializedName("客户窗口时间") private String 客户窗口时间;
    @SerializedName("指令类型") private String 指令类型;
    @SerializedName("Product_ID") private int ProductID;
    @SerializedName("PS_ID") private int PSID;
    @SerializedName("产品通称") private String 产品通称;
    @SerializedName("客户零件号") private String 客户零件号;
    @SerializedName("产品名称") private String 产品名称;
    @SerializedName("样件项目名称") private Object 样件项目名称;
    @SerializedName("当前版本") private String 当前版本;
    @SerializedName("最新版本") private String 最新版本;
    @SerializedName("发货指令数") private int 发货指令数;
    @SerializedName("已出库数") private Object 已出库数;
    @SerializedName("余数") private double 余数;
    @SerializedName("排版") private boolean 排版;
    @SerializedName("延迟申请") private boolean 延迟申请;
    @SerializedName("物流方式") private Object 物流方式;
    @SerializedName("包装方式") private Object 包装方式;
    @SerializedName("备注") private String 备注;
    @SerializedName("备注人") private String 备注人;
    @SerializedName("到达日期") private String 到达日期;
    @SerializedName("接收信息") private String 接收信息;
    @SerializedName("特殊要求") private String 特殊要求;
    @SerializedName("库存") private double 库存;
    @SerializedName("冻结库存") private Object 冻结库存;
    @SerializedName("库存够发货") private boolean 库存够发货;
    @SerializedName("上海座椅") private Object 上海座椅;
    @SerializedName("滁州座椅") private double 滁州座椅;
    @SerializedName("上海后装") private Object 上海后装;
    @SerializedName("滁州后装") private Object 滁州后装;
    @SerializedName("计划员") private String 计划员;
    @SerializedName("总装线别") private String 总装线别;

    public int getDOID() {
        return DOID;
    }

    public void setDOID(int DOID) {
        this.DOID = DOID;
    }

    public int getDPIID() {
        return DPIID;
    }

    public void setDPIID(int DPIID) {
        this.DPIID = DPIID;
    }

    public int getCPOID() {
        return CPOID;
    }

    public void setCPOID(int CPOID) {
        this.CPOID = CPOID;
    }

    public String get销售公司() {
        return 销售公司;
    }

    public void set销售公司(String 销售公司) {
        this.销售公司 = 销售公司;
    }

    public int getBUID() {
        return BUID;
    }

    public void setBUID(int BUID) {
        this.BUID = BUID;
    }

    public String get销售车间() {
        return 销售车间;
    }

    public void set销售车间(String 销售车间) {
        this.销售车间 = 销售车间;
    }

    public String get生产车间() {
        return 生产车间;
    }

    public void set生产车间(String 生产车间) {
        this.生产车间 = 生产车间;
    }

    public Object get发货地点() {
        return 发货地点;
    }

    public void set发货地点(Object 发货地点) {
        this.发货地点 = 发货地点;
    }

    public String get国内外() {
        return 国内外;
    }

    public void set国内外(String 国内外) {
        this.国内外 = 国内外;
    }

    public String get客户() {
        return 客户;
    }

    public void set客户(String 客户) {
        this.客户 = 客户;
    }

    public String get编辑者() {
        return 编辑者;
    }

    public void set编辑者(String 编辑者) {
        this.编辑者 = 编辑者;
    }

    public String get指令创建时间() {
        return 指令创建时间;
    }

    public void set指令创建时间(String 指令创建时间) {
        this.指令创建时间 = 指令创建时间;
    }

    public String get指令最后更新时间() {
        return 指令最后更新时间;
    }

    public void set指令最后更新时间(String 指令最后更新时间) {
        this.指令最后更新时间 = 指令最后更新时间;
    }

    public String get指令明细最后更新时间() {
        return 指令明细最后更新时间;
    }

    public void set指令明细最后更新时间(String 指令明细最后更新时间) {
        this.指令明细最后更新时间 = 指令明细最后更新时间;
    }

    public String get跟踪号() {
        return 跟踪号;
    }

    public void set跟踪号(String 跟踪号) {
        this.跟踪号 = 跟踪号;
    }

    public String get发货日期() {
        return 发货日期;
    }

    public void set发货日期(String 发货日期) {
        this.发货日期 = 发货日期;
    }

    public String get客户窗口时间() {
        return 客户窗口时间;
    }

    public void set客户窗口时间(String 客户窗口时间) {
        this.客户窗口时间 = 客户窗口时间;
    }

    public String get指令类型() {
        return 指令类型;
    }

    public void set指令类型(String 指令类型) {
        this.指令类型 = 指令类型;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int ProductID) {
        this.ProductID = ProductID;
    }

    public int getPSID() {
        return PSID;
    }

    public void setPSID(int PSID) {
        this.PSID = PSID;
    }

    public String get产品通称() {
        return 产品通称;
    }

    public void set产品通称(String 产品通称) {
        this.产品通称 = 产品通称;
    }

    public String get客户零件号() {
        return 客户零件号;
    }

    public void set客户零件号(String 客户零件号) {
        this.客户零件号 = 客户零件号;
    }

    public String get产品名称() {
        return 产品名称;
    }

    public void set产品名称(String 产品名称) {
        this.产品名称 = 产品名称;
    }

    public Object get样件项目名称() {
        return 样件项目名称;
    }

    public void set样件项目名称(Object 样件项目名称) {
        this.样件项目名称 = 样件项目名称;
    }

    public String get当前版本() {
        return 当前版本;
    }

    public void set当前版本(String 当前版本) {
        this.当前版本 = 当前版本;
    }

    public String get最新版本() {
        return 最新版本;
    }

    public void set最新版本(String 最新版本) {
        this.最新版本 = 最新版本;
    }

    public int get发货指令数() {
        return 发货指令数;
    }

    public void set发货指令数(int 发货指令数) {
        this.发货指令数 = 发货指令数;
    }

    public Object get已出库数() {
        return 已出库数;
    }

    public void set已出库数(Object 已出库数) {
        this.已出库数 = 已出库数;
    }

    public double get余数() {
        return 余数;
    }

    public void set余数(double 余数) {
        this.余数 = 余数;
    }

    public boolean is排版() {
        return 排版;
    }

    public void set排版(boolean 排版) {
        this.排版 = 排版;
    }

    public boolean is延迟申请() {
        return 延迟申请;
    }

    public void set延迟申请(boolean 延迟申请) {
        this.延迟申请 = 延迟申请;
    }

    public Object get物流方式() {
        return 物流方式;
    }

    public void set物流方式(Object 物流方式) {
        this.物流方式 = 物流方式;
    }

    public Object get包装方式() {
        return 包装方式;
    }

    public void set包装方式(Object 包装方式) {
        this.包装方式 = 包装方式;
    }

    public String get备注() {
        return 备注;
    }

    public void set备注(String 备注) {
        this.备注 = 备注;
    }

    public String get备注人() {
        return 备注人;
    }

    public void set备注人(String 备注人) {
        this.备注人 = 备注人;
    }

    public String get到达日期() {
        return 到达日期;
    }

    public void set到达日期(String 到达日期) {
        this.到达日期 = 到达日期;
    }

    public String get接收信息() {
        return 接收信息;
    }

    public void set接收信息(String 接收信息) {
        this.接收信息 = 接收信息;
    }

    public String get特殊要求() {
        return 特殊要求;
    }

    public void set特殊要求(String 特殊要求) {
        this.特殊要求 = 特殊要求;
    }

    public double get库存() {
        return 库存;
    }

    public void set库存(double 库存) {
        this.库存 = 库存;
    }

    public Object get冻结库存() {
        return 冻结库存;
    }

    public void set冻结库存(Object 冻结库存) {
        this.冻结库存 = 冻结库存;
    }

    public boolean is库存够发货() {
        return 库存够发货;
    }

    public void set库存够发货(boolean 库存够发货) {
        this.库存够发货 = 库存够发货;
    }

    public Object get上海座椅() {
        return 上海座椅;
    }

    public void set上海座椅(Object 上海座椅) {
        this.上海座椅 = 上海座椅;
    }

    public double get滁州座椅() {
        return 滁州座椅;
    }

    public void set滁州座椅(double 滁州座椅) {
        this.滁州座椅 = 滁州座椅;
    }

    public Object get上海后装() {
        return 上海后装;
    }

    public void set上海后装(Object 上海后装) {
        this.上海后装 = 上海后装;
    }

    public Object get滁州后装() {
        return 滁州后装;
    }

    public void set滁州后装(Object 滁州后装) {
        this.滁州后装 = 滁州后装;
    }

    public String get计划员() {
        return 计划员;
    }

    public void set计划员(String 计划员) {
        this.计划员 = 计划员;
    }

    public String get总装线别() {
        return 总装线别;
    }

    public void set总装线别(String 总装线别) {
        this.总装线别 = 总装线别;
    }
}
