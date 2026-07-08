package com.chinashb.www.mobileerp.bean;

/***
 * @date 创建时间 6/17/26 10:57 AM
 * @author 作者: liweifeng
 * @description 投料出库类，主要用于一键出库的list
 */

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
public class MWIssueItemOutParam implements KvmSerializable {

    // ===== 字段（必须和 VB DataMember 同名、大小写一致）=====
    public Long Item_ID;
    public Long IV_ID;
    public Long LotID;
    public String LotNo;
    public Long Ist_ID;
    public Long Sub_Ist_ID;
    public Long SMLI_ID;
    public Long SMM_ID;
    public Long SMT_ID;
    public String Qty;
    public String AcDate;
    public String Scan_Code;

    // ===== kSOAP2 必须实现的 4 个方法 =====
    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0: return Item_ID;
            case 1: return IV_ID;
            case 2: return LotID;
            case 3: return LotNo;
            case 4: return Ist_ID;
            case 5: return Sub_Ist_ID;
            case 6: return SMLI_ID;
            case 7: return SMM_ID;
            case 8: return SMT_ID;
            case 9: return Qty;
            case 10: return AcDate;
            case 11: return Scan_Code;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 12;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0: Item_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 1: IV_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 2: LotID = value == null ? null : Long.valueOf(value.toString()); break;
            case 3: LotNo = (String) value; break;
            case 4: Ist_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 5: Sub_Ist_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 6: SMLI_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 7: SMM_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 8: SMT_ID = value == null ? null : Long.valueOf(value.toString()); break;
            case 9: Qty = (String) value; break;
            case 10: AcDate = (String) value; break;
            case 11: Scan_Code = (String) value; break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable hashtable, PropertyInfo info) {
        switch (index) {
            case 0: info.type = PropertyInfo.LONG_CLASS; info.name = "Item_ID"; break;
            case 1: info.type = PropertyInfo.LONG_CLASS; info.name = "IV_ID"; break;
            case 2: info.type = PropertyInfo.LONG_CLASS; info.name = "LotID"; break;
            case 3: info.type = PropertyInfo.STRING_CLASS; info.name = "LotNo"; break;
            case 4: info.type = PropertyInfo.LONG_CLASS; info.name = "Ist_ID"; break;
            case 5: info.type = PropertyInfo.LONG_CLASS; info.name = "Sub_Ist_ID"; break;
            case 6: info.type = PropertyInfo.LONG_CLASS; info.name = "SMLI_ID"; break;
            case 7: info.type = PropertyInfo.LONG_CLASS; info.name = "SMM_ID"; break;
            case 8: info.type = PropertyInfo.LONG_CLASS; info.name = "SMT_ID"; break;
            case 9: info.type = PropertyInfo.STRING_CLASS; info.name = "Qty"; break;
            case 10: info.type = PropertyInfo.STRING_CLASS; info.name = "AcDate"; break;
            case 11: info.type = PropertyInfo.STRING_CLASS; info.name = "Scan_Code"; break;
        }
    }

    // ===== Java Bean Setter / Getter（你刚才缺的就是这些）=====
    public Long getItem_ID() { return Item_ID; }
    public void setItem_ID(Long item_ID) { Item_ID = item_ID; }

    public Long getIV_ID() { return IV_ID; }
    public void setIV_ID(Long IV_ID) { this.IV_ID = IV_ID; }

    public Long getLotID() { return LotID; }
    public void setLotID(Long lotID) { LotID = lotID; }

    public String getLotNo() { return LotNo; }
    public void setLotNo(String lotNo) { LotNo = lotNo; }

    public Long getIst_ID() { return Ist_ID; }
    public void setIst_ID(Long ist_ID) { Ist_ID = ist_ID; }

    public Long getSub_Ist_ID() { return Sub_Ist_ID; }
    public void setSub_Ist_ID(Long sub_Ist_ID) { Sub_Ist_ID = sub_Ist_ID; }

    public Long getSMLI_ID() { return SMLI_ID; }
    public void setSMLI_ID(Long SMLI_ID) { this.SMLI_ID = SMLI_ID; }

    public Long getSMM_ID() { return SMM_ID; }
    public void setSMM_ID(Long SMM_ID) { this.SMM_ID = SMM_ID; }

    public Long getSMT_ID() { return SMT_ID; }
    public void setSMT_ID(Long SMT_ID) { this.SMT_ID = SMT_ID; }

    public String getQty() { return Qty; }
    public void setQty(String qty) { Qty = qty; }

    public String getAcDate() { return AcDate; }
    public void setAcDate(String acDate) { AcDate = acDate; }

    public String getScan_Code() { return Scan_Code; }
    public void setScan_Code(String scan_Code) { Scan_Code = scan_Code; }
}


//public class MWIssueItemOutParam implements KvmSerializable {
//
//    public Long Item_ID;
//    public Long IV_ID;
//    public Long LotID;
//    public String LotNo;
//    public Long Ist_ID;
//    public Long Sub_Ist_ID;
//    public Long SMLI_ID;
//    public Long SMM_ID;
//    public Long SMT_ID;
//    public String Qty;
//    public String AcDate;
//    public String Scan_Code;
//
//    @Override
//    public Object getProperty(int index) {
//        switch (index) {
//            case 0: return Item_ID;
//            case 1: return IV_ID;
//            case 2: return LotID;
//            case 3: return LotNo;
//            case 4: return Ist_ID;
//            case 5: return Sub_Ist_ID;
//            case 6: return SMLI_ID;
//            case 7: return SMM_ID;
//            case 8: return SMT_ID;
//            case 9: return Qty;
//            case 10: return AcDate;
//            case 11: return Scan_Code;
//        }
//        return null;
//    }
//
//    @Override
//    public int getPropertyCount() {
//        return 12;
//    }
//
//    @Override
//    public void setProperty(int index, Object value) {
//        switch (index) {
//            case 0: Item_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 1: IV_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 2: LotID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 3: LotNo = (String) value; break;
//            case 4: Ist_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 5: Sub_Ist_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 6: SMLI_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 7: SMM_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 8: SMT_ID = value == null ? null : Long.valueOf(value.toString()); break;
//            case 9: Qty = (String) value; break;
//            case 10: AcDate = (String) value; break;
//            case 11: Scan_Code = (String) value; break;
//        }
//    }
//
//    @Override
//    public void getPropertyInfo(int index, Hashtable hashtable, PropertyInfo info) {
//        switch (index) {
//            case 0: info.type = PropertyInfo.LONG_CLASS; info.name = "Item_ID"; break;
//            case 1: info.type = PropertyInfo.LONG_CLASS; info.name = "IV_ID"; break;
//            case 2: info.type = PropertyInfo.LONG_CLASS; info.name = "LotID"; break;
//            case 3: info.type = PropertyInfo.STRING_CLASS; info.name = "LotNo"; break;
//            case 4: info.type = PropertyInfo.LONG_CLASS; info.name = "Ist_ID"; break;
//            case 5: info.type = PropertyInfo.LONG_CLASS; info.name = "Sub_Ist_ID"; break;
//            case 6: info.type = PropertyInfo.LONG_CLASS; info.name = "SMLI_ID"; break;
//            case 7: info.type = PropertyInfo.LONG_CLASS; info.name = "SMM_ID"; break;
//            case 8: info.type = PropertyInfo.LONG_CLASS; info.name = "SMT_ID"; break;
//            case 9: info.type = PropertyInfo.STRING_CLASS; info.name = "Qty"; break;
//            case 10: info.type = PropertyInfo.STRING_CLASS; info.name = "AcDate"; break;
//            case 11: info.type = PropertyInfo.STRING_CLASS; info.name = "Scan_Code"; break;
//        }
//    }
//}

//public class MWIssueItemOutParam {
//
//    // 字段名必须与 VB DataMember 完全一致（大小写敏感）
//    private Long Item_ID;
//    private Long IV_ID;
//    private Long LotID;
//    private String LotNo;
//    private Long Ist_ID;
//    private Long Sub_Ist_ID;
//    private Long SMLI_ID;
//    private Long SMM_ID;
//    private Long SMT_ID;
//    private String Qty;
//    private Date AcDate;
//    private String Scan_Code;
//
//    // Getters and Setters
//    public Long getItem_ID() {
//        return Item_ID;
//    }
//
//    public void setItem_ID(Long item_ID) {
//        Item_ID = item_ID;
//    }
//
//    public Long getIV_ID() {
//        return IV_ID;
//    }
//
//    public void setIV_ID(Long iV_ID) {
//        IV_ID = iV_ID;
//    }
//
//    public Long getLotID() {
//        return LotID;
//    }
//
//    public void setLotID(Long lotID) {
//        LotID = lotID;
//    }
//
//    public String getLotNo() {
//        return LotNo;
//    }
//
//    public void setLotNo(String lotNo) {
//        LotNo = lotNo;
//    }
//
//    public Long getIst_ID() {
//        return Ist_ID;
//    }
//
//    public void setIst_ID(Long ist_ID) {
//        Ist_ID = ist_ID;
//    }
//
//    public Long getSub_Ist_ID() {
//        return Sub_Ist_ID;
//    }
//
//    public void setSub_Ist_ID(Long sub_Ist_ID) {
//        Sub_Ist_ID = sub_Ist_ID;
//    }
//
//    public Long getSMLI_ID() {
//        return SMLI_ID;
//    }
//
//    public void setSMLI_ID(Long sMLI_ID) {
//        SMLI_ID = sMLI_ID;
//    }
//
//    public Long getSMM_ID() {
//        return SMM_ID;
//    }
//
//    public void setSMM_ID(Long sMM_ID) {
//        SMM_ID = sMM_ID;
//    }
//
//    public Long getSMT_ID() {
//        return SMT_ID;
//    }
//
//    public void setSMT_ID(Long sMT_ID) {
//        SMT_ID = sMT_ID;
//    }
//
//    public String getQty() {
//        return Qty;
//    }
//
//    public void setQty(String qty) {
//        Qty = qty;
//    }
//
//    public Date getAcDate() {
//        return AcDate;
//    }
//
//    public void setAcDate(Date acDate) {
//        AcDate = acDate;
//    }
//
//    public String getScan_Code() {
//        return Scan_Code;
//    }
//
//    public void setScan_Code(String scan_Code) {
//        Scan_Code = scan_Code;
//    }
//}