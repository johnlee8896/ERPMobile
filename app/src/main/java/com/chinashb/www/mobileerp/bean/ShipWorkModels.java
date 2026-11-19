package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货统一查询模型。与 op_Android_DP_ShipWork_Query/Action 的 Info 对应。
 */
public final class ShipWorkModels {

    private ShipWorkModels() {
    }

    public static class Customer {
        @SerializedName("CF_ID") public long cfId;
        @SerializedName("Customer_Name") public String customerName;
        @SerializedName("Order_Count") public int orderCount;
    }

    public static class InventoryLine {
        @SerializedName("DPI_ID") public long dpiId;
        @SerializedName("Product_ID") public long productId;
        @SerializedName("PS_ID") public long psId;
        @SerializedName("Product_Name") public String productName;
        @SerializedName("Customer_Part_No") public String customerPartNo;
        @SerializedName("DPI_Quantity") public double dpiQuantity;
        @SerializedName("Shipped_Quantity") public double shippedQuantity;
        @SerializedName("Remaining_Quantity") public double remainingQuantity;
        @SerializedName("Inventory_Quantity") public double inventoryQuantity;
        @SerializedName("Locked_Quantity") public double lockedQuantity;
        @SerializedName("Available_Inventory_Quantity") public double availableInventoryQuantity;
        @SerializedName("Inventory_Enough") public boolean inventoryEnough;
        @SerializedName("Inventory_Locations") public String inventoryLocations;
    }

    public static class Delivery {
        @SerializedName("Delivery_ID") public long deliveryId;
        @SerializedName("TrackNo") public String trackNo;
        @SerializedName("Delivery_Date") public String deliveryDate;
        @SerializedName("Shiping_Date") public String shipingDate;
        @SerializedName("Arrive_Date") public String arriveDate;
        @SerializedName("DriverName") public String driverName;
        @SerializedName("DriverTel") public String driverTel;
        @SerializedName("License_Plate_No") public String licensePlateNo;
        @SerializedName("ReceiverName") public String receiverName;
        @SerializedName("ReceiverTel") public String receiverTel;
        @SerializedName("Pallet_Number") public int palletNumber;
        @SerializedName("DP_Order_ID") public long dpOrderId;
    }

    public static class DeliveryDraft {
        @SerializedName("CopiedFromLast") public boolean copiedFromLast;
        @SerializedName("SourceDeliveryId") public long sourceDeliveryId;
        @SerializedName("TrackNo") public String trackNo;
        @SerializedName("DeliveryDate") public String deliveryDate;
        @SerializedName("ShippingDate") public String shippingDate;
        @SerializedName("LoadTime") public String loadTime;
        @SerializedName("ArriveDate") public String arriveDate;
        @SerializedName("DT_ID") public int dtId;
        @SerializedName("LC_ID") public int lcId;
        @SerializedName("DCNo") public String dcNo;
        @SerializedName("CheckNo") public String checkNo;
        @SerializedName("ContractNo") public String contractNo;
        @SerializedName("ETA") public int eta;
        @SerializedName("Abroad") public boolean abroad;
        @SerializedName("Mass") public boolean mass;
        @SerializedName("IsBackUp") public boolean isBackUp;
        @SerializedName("Replenish") public boolean replenish;
        @SerializedName("DriverName") public String driverName;
        @SerializedName("DriverTel") public String driverTel;
        @SerializedName("LicensePlateNo") public String licensePlateNo;
        @SerializedName("ReceiverName") public String receiverName;
        @SerializedName("ReceiverTel") public String receiverTel;
        @SerializedName("DestinationAddress") public String destinationAddress;
        @SerializedName("PalletNumber") public int palletNumber;
        @SerializedName("PalletVolume") public double palletVolume;
        @SerializedName("TotalVolume") public double totalVolume;
        @SerializedName("SingleWeight") public double singleWeight;
        @SerializedName("TotalWeight") public double totalWeight;
        @SerializedName("CarType") public int carType;
        @SerializedName("CarAllLength") public int carAllLength;
        @SerializedName("TransferBackType") public int transferBackType;
        @SerializedName("FeeMethod") public String feeMethod;
        @SerializedName("Fee") public double fee;
        @SerializedName("SenderRemark") public String senderRemark;
        @SerializedName("ReceiverRemark") public String receiverRemark;
        @SerializedName("Remark") public String remark;
    }

    public static class LookupOption {
        @SerializedName("ID") public int id;
        @SerializedName("Name") public String name;
    }

    public static class Warehouse {
        @SerializedName("Warehouse_ID") public long warehouseId;
        @SerializedName("Warehouse_Name") public String warehouseName;
    }

    public static class StageAreaSetting {
        @SerializedName("IST_ID") public long istId;
        @SerializedName("IST_Name") public String istName;
        @SerializedName("Warehouse_ID") public long warehouseId;
        @SerializedName("Warehouse_Name") public String warehouseName;
        @SerializedName("Stage_Area_ID") public long stageAreaId;
        @SerializedName("Is_Configured") public boolean isConfigured;
    }

    public static class FifoLine {
        @SerializedName("DPI_ID") public long dpiId;
        @SerializedName("Product_ID") public long productId;
        @SerializedName("PS_ID") public long psId;
        @SerializedName("DPI_Quantity") public double dpiQuantity;
        @SerializedName("Shipped_Quantity") public double shippedQuantity;
        @SerializedName("Reserved_Quantity") public double reservedQuantity;
        @SerializedName("Available_Quantity") public double availableQuantity;
        @SerializedName("Available_Pallet_Count") public int availablePalletCount;
        @SerializedName("Available_Pallet_Quantity") public double availablePalletQuantity;
        @SerializedName("Lockable_Pallet_Count") public int lockablePalletCount;
        @SerializedName("Lockable_Pallet_Quantity") public double lockablePalletQuantity;
        @SerializedName("Reserved_Pallet_Count") public int reservedPalletCount;
    }

    public static class WorkHeader {
        @SerializedName("Work_ID") public long workId;
        @SerializedName("DO_ID") public long doId;
        @SerializedName("Delivery_ID") public long deliveryId;
        @SerializedName("Warehouse_ID") public long warehouseId;
        @SerializedName("Bu_ID") public long buId;
        @SerializedName("Stage_Area_ID") public long stageAreaId;
        @SerializedName("Work_Status") public String workStatus;
        @SerializedName("Is_Active") public boolean isActive;
        @SerializedName("Started_At") public String startedAt;
        @SerializedName("Daily_Sequence") public int dailySequence;
        @SerializedName("Stage_Name") public String stageName;
        @SerializedName("TrackNo") public String trackNo;
        @SerializedName("CF_ID") public long cfId;
        @SerializedName("Customer_Name") public String customerName;
        @SerializedName("OQC_Result") public String oqcResult;
        @SerializedName("OQC_At") public String oqcAt;
        @SerializedName("OQC_By") public Integer oqcBy;
        @SerializedName("OQC_By_Name") public String oqcByName;
        @SerializedName("OQC_Remark") public String oqcRemark;
        @SerializedName("OQC_Pallet_Count") public Integer oqcPalletCount;
        @SerializedName("OQC_Quantity") public Double oqcQuantity;
        @SerializedName("OQC_Revision") public int oqcRevision;
    }

    public static class WorkDetail {
        @SerializedName("Work") public List<WorkHeader> work = new ArrayList<>();
        @SerializedName("Pallets") public List<ShipWorkPalletBean> pallets = new ArrayList<>();
    }
}
