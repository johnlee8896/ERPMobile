package com.chinashb.www.mobileerp.basicobject;


import java.io.Serializable;

public class BoxItemEntity implements Serializable {
    private boolean result;
    private String ErrorInfo;
    private long DIII_ID;
    private long SMT_ID;
    private long SMM_ID;
    private long SMLI_ID;
    private long EntityID;
    private String EntityName;
    private long LotID;
    private String LotNo;
    private long Item_ID;
    private long IV_ID;
    private String ItemName;
    private String BoxName;
    private String BoxNo;
    private float Qty;
    private float BoxQty;
    //还需投料数量
    private float NeedMoreQty;
    private int Bu_ID;
    private String BuName;
    private boolean select = true;
    private long Ist_ID;
    private long Sub_Ist_ID;
    private String OldIstName = "";
    private String IstName = "";
    private String FreezeStatus = "";
    private String LotDescription = "";
    private String SmlRemark = "";
    private String ManuLotNo = "";
    private WsResult ws_result = new WsResult();
    private boolean canNotEdit = false;

    public void setCanNotEdit(boolean canNotEdit) {
        this.canNotEdit = canNotEdit;
    }

    public boolean getCanNotEdit(){
        return canNotEdit;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String ErrorInfo) {
        this.ErrorInfo = ErrorInfo;
    }

    public long getDIII_ID() {
        return DIII_ID;
    }

    public void setDIII_ID(long DIII_ID) {
        this.DIII_ID = DIII_ID;
    }

    public long getSMT_ID() {
        return SMT_ID;
    }

    public void setSMT_ID(long SMT_ID) {
        this.SMT_ID = SMT_ID;
    }

    public long getSMM_ID() {
        return SMM_ID;
    }

    public void setSMM_ID(long SMM_ID) {
        this.SMM_ID = SMM_ID;
    }

    public long getSMLI_ID() {
        return SMLI_ID;
    }

    public void setSMLI_ID(long SMLI_ID) {
        this.SMLI_ID = SMLI_ID;
    }

    public long getEntityID() {
        return EntityID;
    }

    public void setEntityID(long EntityID) {
        this.EntityID = EntityID;
    }

    public String getEntityName() {
        return EntityName;
    }

    public void setEntityName(String EntityName) {
        this.EntityName = EntityName;
    }

    public long getLotID() {
        return LotID;
    }

    public void setLotID(long LotID) {
        this.LotID = LotID;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String LotNo) {
        this.LotNo = LotNo;
    }

    public String getLotBox() {
        String lb = LotNo + "@" + BoxName + BoxNo;
        return lb;
    }

    //托盘号+位置
    public String getIstBox() {
        String lb = IstName + "@" + BoxName + "No." + BoxNo;
        return lb;
    }

    public long getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(long Item_ID) {
        this.Item_ID = Item_ID;
    }

    public long getIV_ID() {
        return IV_ID;
    }

    public void setIV_ID(long IV_ID) {
        this.IV_ID = IV_ID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getBoxName() {
        return BoxName;
    }

    public void setBoxName(String BoxName) {
        this.BoxName = BoxName;
    }

    public String getBoxNo() {
        return BoxNo;
    }

    public void setBoxNo(String BoxNo) {
        this.BoxNo = BoxNo;
    }

    public String getBoxNameNo() {
        return BoxName + "No." + BoxNo;
    }

    public float getQty() {
        return Qty;
    }

    public void setQty(float Qty) {
        this.Qty = Qty;
    }

    public float getBoxQty() {
        return BoxQty;
    }

    public void setBoxQty(float BoxQty) {
        this.BoxQty = BoxQty;
    }

    public float getNeedMoreQty() {
        return NeedMoreQty;
    }

    public void setNeedMoreQty(float NeedMoreQty) {
        this.NeedMoreQty = NeedMoreQty;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int Bu_ID) {
        this.Bu_ID = Bu_ID;
    }

    public String getBuName() {
        return BuName;
    }

    public void setBuName(String BuName) {
        this.BuName = BuName;
    }

    public boolean getSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public long getIst_ID() {
        return Ist_ID;
    }

    public void setIst_ID(long Ist_ID) {
        this.Ist_ID = Ist_ID;
    }

    public long getSub_Ist_ID() {
        return Sub_Ist_ID;
    }

    public void setSub_Ist_ID(long Sub_Ist_ID) {
        this.Sub_Ist_ID = Sub_Ist_ID;
    }

    public String getOldIstName() {
        return OldIstName;
    }

    public void setOldIstName(String OldIstName) {
        this.OldIstName = OldIstName;
    }

    public String getIstName() {
        return IstName;
    }

    public void setIstName(String IstName) {
        this.IstName = IstName;
    }

    public String getFreezeStatus() {
        return FreezeStatus;
    }

    public void setFreezeStatus(String FreezeStatus) {
        this.FreezeStatus = FreezeStatus;
    }

    public String getLotDescription() {
        return LotDescription;
    }

    public void setLotDescription(String LotDescription) {
        this.LotDescription = LotDescription;
    }

    public String getSmlRemark() {
        return SmlRemark;
    }

    public void setSmlRemark(String SmlRemark) {
        this.SmlRemark = SmlRemark;
    }

    public String getManuLotNo() {
        return ManuLotNo;
    }

    public void setManuLotNo(String ManuLotNo) {
        this.ManuLotNo = ManuLotNo;
    }

    public WsResult getWs_result() {
        return ws_result;
    }

    public void setWs_result(WsResult w) {
        ws_result = w;
    }


}
