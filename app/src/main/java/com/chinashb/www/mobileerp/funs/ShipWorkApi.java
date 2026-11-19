package com.chinashb.www.mobileerp.funs;

import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.ShipWorkActiveBean;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.Customer;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.Delivery;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.DeliveryDraft;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.FifoLine;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.InventoryLine;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.LookupOption;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.StageAreaSetting;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.Warehouse;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.bean.ShipWorkOrderBean;
import com.chinashb.www.mobileerp.bean.ShipWorkStageAreaBean;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 成品发货统一业务入口。
 * 查询和动作只传当前 HR_ID、Bu_ID，不信任页面传入身份。
 */
public final class ShipWorkApi {

    public static final String QUERY_CUSTOMERS = "Customers";
    public static final String QUERY_ORDERS = "Orders";
    public static final String QUERY_DELIVERIES = "Deliveries";
    public static final String QUERY_DELIVERY_DRAFT = "DeliveryDraft";
    public static final String QUERY_DELIVERY_TYPES = "DeliveryTypes";
    public static final String QUERY_LOGISTICS_COMPANIES = "LogisticsCompanies";
    public static final String QUERY_WAREHOUSES = "Warehouses";
    public static final String QUERY_STAGE_AREAS = "StageAreas";
    public static final String QUERY_STAGE_AREA_SETTINGS = "StageAreaSettings";
    public static final String QUERY_ACTIVE_WORKS = "ActiveWorks";
    public static final String QUERY_ORDER_LINES = "OrderLines";
    public static final String QUERY_ORDER_INVENTORY = "OrderInventory";
    public static final String QUERY_WORK_DETAIL = "WorkDetail";

    public static final String ACTION_CREATE_DELIVERY = "CreateDelivery";
    public static final String ACTION_CREATE_WORK = "CreateWork";
    public static final String ACTION_SET_STAGE_AREA = "SetStageArea";
    public static final String ACTION_LOCK_FIFO = "LockFIFO";
    public static final String ACTION_PICK_SCAN = "PickScan";
    public static final String ACTION_MOVE_TO_STAGE = "MoveToStage";
    public static final String ACTION_RETURN_PALLET = "ReturnPallet";
    public static final String ACTION_RECORD_OQC = "RecordOQC";
    public static final String ACTION_SHIP_SCAN = "ShipScan";
    public static final String ACTION_POST = "Post";

    private ShipWorkApi() {
    }

    public static class Result<T> {
        public boolean success;
        public String errorInfo;
        public T data;
        public long id;
        public String info;
        public boolean networkFailure;

        private Result(boolean success, String errorInfo, T data, long id, String info,
                       boolean networkFailure) {
            this.success = success;
            this.errorInfo = errorInfo;
            this.data = data;
            this.id = id;
            this.info = info;
            this.networkFailure = networkFailure;
        }

        private static <T> Result<T> failure(String errorInfo) {
            return new Result<>(false, errorInfo, null, 0, null, false);
        }

        private static <T> Result<T> networkFailure(String errorInfo) {
            return new Result<>(false, errorInfo, null, 0, null, true);
        }
    }

    public static Result<List<Customer>> getCustomers(String searchText) {
        JsonObject params = new JsonObject();
        params.addProperty("SearchText", searchText == null ? "" : searchText);
        Type type = new TypeToken<List<Customer>>() {
        }.getType();
        return queryList(QUERY_CUSTOMERS, params, type);
    }

    public static Result<List<ShipWorkOrderBean>> getOrders(long cfId, String searchText) {
        JsonObject params = new JsonObject();
        params.addProperty("CF_ID", cfId);
        params.addProperty("SearchText", searchText == null ? "" : searchText);
        Type type = new TypeToken<List<ShipWorkOrderBean>>() {
        }.getType();
        return queryList(QUERY_ORDERS, params, type);
    }

    public static Result<List<InventoryLine>> getOrderInventory(long doId) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        Type type = new TypeToken<List<InventoryLine>>() {
        }.getType();
        return queryList(QUERY_ORDER_INVENTORY, params, type);
    }

    public static Result<List<Delivery>> getDeliveries(long doId, String searchText) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        params.addProperty("SearchText", searchText == null ? "" : searchText);
        Type type = new TypeToken<List<Delivery>>() {
        }.getType();
        return queryList(QUERY_DELIVERIES, params, type);
    }

    public static Result<DeliveryDraft> getDeliveryDraft(long doId, boolean copyLast) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        params.addProperty("CopyLast", copyLast);
        WsResult wsResult = query(QUERY_DELIVERY_DRAFT, params);
        if (wsResult == null || !wsResult.getResult()) {
            return wsResult == null
                    ? Result.networkFailure("网络请求失败")
                    : Result.failure(error(wsResult));
        }
        try {
            DeliveryDraft draft = parseSingleInfo(wsResult, DeliveryDraft.class);
            return new Result<>(true, null, draft,
                    wsResult.getID() == null ? 0 : wsResult.getID(), wsResult.getInfo(), false);
        } catch (Exception e) {
            return Result.failure("物流单草稿解析失败");
        }
    }

    public static Result<List<LookupOption>> getDeliveryTypes() {
        Type type = new TypeToken<List<LookupOption>>() {
        }.getType();
        return queryList(QUERY_DELIVERY_TYPES, new JsonObject(), type);
    }

    public static Result<List<LookupOption>> getLogisticsCompanies() {
        Type type = new TypeToken<List<LookupOption>>() {
        }.getType();
        return queryList(QUERY_LOGISTICS_COMPANIES, new JsonObject(), type);
    }

    public static Result<List<Warehouse>> getWarehouses(long doId) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        Type type = new TypeToken<List<Warehouse>>() {
        }.getType();
        return queryList(QUERY_WAREHOUSES, params, type);
    }

    public static Result<List<ShipWorkStageAreaBean>> getStageAreas(long doId, long warehouseId) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        params.addProperty("Warehouse_ID", warehouseId);
        Type type = new TypeToken<List<ShipWorkStageAreaBean>>() {
        }.getType();
        return queryList(QUERY_STAGE_AREAS, params, type);
    }

    public static Result<List<ShipWorkStageAreaBean>> getStageAreas(long doId) {
        Result<List<Warehouse>> warehouses = getWarehouses(doId);
        if (!warehouses.success || warehouses.data == null || warehouses.data.isEmpty()) {
            return Result.failure(warehouses.errorInfo == null ? "未获取到仓库" : warehouses.errorInfo);
        }
        if (warehouses.data.size() != 1) {
            return Result.failure("当前指令匹配到多个仓库，请检查成品账配置");
        }
        return getStageAreas(doId, warehouses.data.get(0).warehouseId);
    }

    public static Result<List<StageAreaSetting>> getStageAreaSettings(long doId) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        Type type = new TypeToken<List<StageAreaSetting>>() {
        }.getType();
        return queryList(QUERY_STAGE_AREA_SETTINGS, params, type);
    }

    public static Result<List<ShipWorkActiveBean>> getActiveWorks() {
        Type type = new TypeToken<List<ShipWorkActiveBean>>() {
        }.getType();
        return queryList(QUERY_ACTIVE_WORKS, new JsonObject(), type);
    }

    public static Result<List<FifoLine>> getOrderLines(long workId) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        Type type = new TypeToken<List<FifoLine>>() {
        }.getType();
        return queryList(QUERY_ORDER_LINES, params, type);
    }

    public static Result<WorkDetail> getWorkDetail(long workId) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        WsResult wsResult = query(QUERY_WORK_DETAIL, params);
        if (wsResult == null || !wsResult.getResult()) {
            return wsResult == null
                    ? Result.networkFailure("网络请求失败")
                    : Result.failure(error(wsResult));
        }
        try {
            WorkDetail detail = parseInfo(wsResult, WorkDetail.class);
            return new Result<>(true, null, detail, wsResult.getID() == null ? 0 : wsResult.getID(),
                    wsResult.getInfo(), false);
        } catch (Exception e) {
            return Result.failure("作业详情解析失败");
        }
    }

    public static Result<Void> createDelivery(long doId, DeliveryDraft draft) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        putString(params, "TrackNo", draft.trackNo);
        putString(params, "DeliveryDate", draft.deliveryDate);
        putString(params, "ShippingDate", draft.shippingDate);
        putString(params, "LoadTime", draft.loadTime);
        putString(params, "ArriveDate", draft.arriveDate);
        params.addProperty("DT_ID", draft.dtId);
        params.addProperty("LC_ID", draft.lcId);
        putString(params, "DCNo", draft.dcNo);
        putString(params, "CheckNo", draft.checkNo);
        putString(params, "ContractNo", draft.contractNo);
        params.addProperty("ETA", draft.eta);
        putString(params, "DriverName", draft.driverName);
        putString(params, "DriverTel", draft.driverTel);
        putString(params, "LicensePlateNo", draft.licensePlateNo);
        putString(params, "ReceiverName", draft.receiverName);
        putString(params, "ReceiverTel", draft.receiverTel);
        putString(params, "DestinationAddress", draft.destinationAddress);
        params.addProperty("PalletNumber", draft.palletNumber);
        params.addProperty("PalletVolume", draft.palletVolume);
        params.addProperty("TotalVolume", draft.totalVolume);
        params.addProperty("SingleWeight", draft.singleWeight);
        params.addProperty("TotalWeight", draft.totalWeight);
        params.addProperty("CarType", draft.carType);
        params.addProperty("CarAllLength", draft.carAllLength);
        params.addProperty("TransferBackType", draft.transferBackType);
        putString(params, "FeeMethod", draft.feeMethod);
        params.addProperty("Fee", draft.fee);
        putString(params, "SenderRemark", draft.senderRemark);
        putString(params, "ReceiverRemark", draft.receiverRemark);
        putString(params, "Remark", draft.remark);
        params.addProperty("Replenish", draft.replenish);
        return action(ACTION_CREATE_DELIVERY, params);
    }

    public static Result<Void> createWork(long doId, long deliveryId, long stageAreaId) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        params.addProperty("Delivery_ID", deliveryId);
        params.addProperty("Stage_Area_ID", stageAreaId);
        return action(ACTION_CREATE_WORK, params);
    }

    public static Result<Void> setStageArea(long doId, long istId) {
        JsonObject params = new JsonObject();
        params.addProperty("DO_ID", doId);
        params.addProperty("IST_ID", istId);
        return action(ACTION_SET_STAGE_AREA, params);
    }

    public static Result<Void> lockFifo(long workId, long dpiId, double quantity) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("DPI_ID", dpiId);
        params.addProperty("Quantity", quantity);
        return action(ACTION_LOCK_FIFO, params);
    }

    public static Result<Void> pickScan(long workId, String barcode) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("Barcode", barcode);
        return action(ACTION_PICK_SCAN, params);
    }

    public static Result<Void> moveToStage(long workId, String locationBarcode) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("LocationBarcode", locationBarcode);
        return action(ACTION_MOVE_TO_STAGE, params);
    }

    public static Result<Void> returnPallet(long workId, String barcode) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("Barcode", barcode);
        return action(ACTION_RETURN_PALLET, params);
    }

    public static Result<Void> recordOqc(long workId, boolean passed, String remark) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("Passed", passed);
        putString(params, "Remark", remark);
        return action(ACTION_RECORD_OQC, params);
    }

    public static Result<Void> shipScan(long workId, String barcode) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("Barcode", barcode);
        return action(ACTION_SHIP_SCAN, params);
    }

    public static Result<Void> post(long workId) {
        JsonObject params = new JsonObject();
        params.addProperty("Work_ID", workId);
        params.addProperty("SheetNo", "");
        return action(ACTION_POST, params);
    }

    private static <T> Result<List<T>> queryList(String queryType, JsonObject params, Type type) {
        WsResult wsResult = query(queryType, params);
        if (wsResult == null || !wsResult.getResult()) {
            return wsResult == null
                    ? Result.networkFailure("网络请求失败")
                    : Result.failure(error(wsResult));
        }
        try {
            List<T> list = parseInfo(wsResult, type);
            return new Result<>(true, null, list == null ? new ArrayList<T>() : list,
                    wsResult.getID() == null ? 0 : wsResult.getID(), wsResult.getInfo(), false);
        } catch (Exception e) {
            return Result.failure("返回数据解析失败");
        }
    }

    private static WsResult query(String queryType, JsonObject params) {
        // 使用 Android 普通 SOAP 入口，页面 JSON 只包含业务参数
        Identity identity = identity();
        if (identity == null) {
            WsResult result = new WsResult();
            result.setResult(false);
            result.setErrorInfo("请先确认登录人员和BU");
            return result;
        }
        return WebServiceUtil.opAndroidDpShipWorkQuery(
                identity.hrId,
                identity.buId,
                queryType,
                params == null ? "{}" : params.toString());
    }

    private static Result<Void> action(String actionType, JsonObject params) {
        // 动作身份统一由登录信息和当前 BU 提供
        Identity identity = identity();
        if (identity == null) {
            return Result.failure("请先确认登录人员和BU");
        }
        WsResult result = WebServiceUtil.opAndroidDpShipWorkAction(
                identity.hrId,
                identity.buId,
                actionType,
                params == null ? "{}" : params.toString());
        if (result == null || !result.getResult()) {
            return result == null
                    ? Result.networkFailure("网络请求失败")
                    : Result.failure(error(result));
        }
        return new Result<>(true, null, null, result.getID() == null ? 0 : result.getID(),
                result.getInfo(), false);
    }

    private static Identity identity() {
        UserInfoEntity userInfo = UserSingleton.get().getUserInfo();
        if (userInfo == null) {
            return null;
        }
        int hrId = UserSingleton.get().getHRID();
        if (hrId <= 0) {
            hrId = userInfo.getHR_ID();
        }
        if (hrId <= 0 || userInfo.getBu_ID() <= 0) {
            return null;
        }
        return new Identity(hrId, userInfo.getBu_ID());
    }

    private static class Identity {
        final int hrId;
        final int buId;

        Identity(int hrId, int buId) {
            this.hrId = hrId;
            this.buId = buId;
        }
    }

    private static void putString(JsonObject object, String key, String value) {
        object.addProperty(key, value == null ? "" : value);
    }

    private static String error(WsResult result) {
        String errorInfo = result.getErrorInfo();
        if (errorInfo != null && !errorInfo.isEmpty()) {
            return errorInfo;
        }
        String info = result.getInfo();
        return info == null || info.isEmpty() ? "操作失败" : info;
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseInfo(WsResult result, Type type) throws Exception {
        String info = result.getInfo();
        if (info == null || info.isEmpty() || "null".equalsIgnoreCase(info)) {
            if (type == String.class) {
                return (T) "";
            }
            return null;
        }
        JsonElement element = new JsonParser().parse(info);
        if (type instanceof Class && WorkDetail.class.equals(type)) {
            return JsonUtil.parseJsonToObject(element.toString(), type);
        }
        return JsonUtil.parseJsonToObject(element.toString(), type);
    }

    private static <T> T parseSingleInfo(WsResult result, Class<T> type) throws Exception {
        String info = result.getInfo();
        if (info == null || info.isEmpty() || "null".equalsIgnoreCase(info)) {
            return null;
        }
        JsonElement element = new JsonParser().parse(info);
        if (element.isJsonArray()) {
            if (element.getAsJsonArray().size() == 0) {
                return null;
            }
            element = element.getAsJsonArray().get(0);
        }
        return JsonUtil.parseJsonToObject(element.toString(), type);
    }
}
