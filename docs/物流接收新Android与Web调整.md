# 物流接收（新）Android 与 Web 调整说明

## 1. 背景

对应 Excel《物流待入库拣货20261021V1-未完成.xlsx》中“物流接收（新）”页：

- 扫码后无法直观核对物料型号、供应商和数量。
- “发出方”区域信息对现场操作无必要。
- 明细清单原先递归展示原始 JSON，无法快速核对数量。

## 2. Android 已完成调整

### 2.1 隐藏“发出方”

布局文件：

`app/src/main/res/layout/activity_new_logistics_manage.xml`

给“发出方”整个区块增加 ID：

```xml
android:id="@+id/new_logistics_sender_section"
```

`NewLogisticsManageActivity` 在 `onCreate` 中直接隐藏该区块：

```java
senderSection.setVisibility(View.GONE);
```

### 2.2 明细清单按业务字段显示

当 `op_Check_Logistics_Supplier_Manifest_Barcode` 返回的 `Info` 中包含 `Items` 数组时，不再递归展示全部原始字段，而是按以下顺序显示：

- `ITEM-ID`
- 名称
- 数量
- 件数
- 要求日期

当前服务端字段映射：

| Android 显示 | 当前服务端字段 |
| --- | --- |
| ITEM-ID | `Item_ID` |
| 名称 | `名称` |
| 数量 | `发货数量` |
| 件数 | `件数` |
| 要求日期 | `发货日期` |

Android 同时兼容以下英文别名：

- 数量：`Qty`、`Quantity`、`ReceiveQuantity`、`ReceivedQuantity`
- 件数：`PackageCount`、`BoxCount`、`Count`
- 要求日期：`RequestDate`、`RequireDate`、`DeliveryDate`

### 2.3 初始空态隐藏

进入页面时以下空信息区域不显示，扫描到对应对象后再自动显示：

- 扫描信息
- 接收方
- 物流信息
- 明细清单

“发出方”始终隐藏。

## 3. Web 端结论

### 3.1 当前结论

现有接口：

```text
op_Check_Logistics_Supplier_Manifest_Barcode
```

已经返回 Android 所需的全部业务字段，因此**本轮不强制新增 Web 接口**。

实际探测的 `Info` 示例结构：

```json
{
  "Result": true,
  "DSIM_ID": 471819,
  "Manifest": [
    {
      "DSIM_ID": 471819,
      "Supplier_Name": "德瑞灵特种材料（上海）有限公司",
      "Des_Address": "上海市嘉定区安亭镇百安公路898号"
    }
  ],
  "Items": [
    {
      "Item_ID": 160059,
      "KisCode": "3.08.020.0005",
      "名称": "塑料粒子",
      "规格": "100P NC010本色",
      "发货数量": 10000,
      "件数": 1,
      "发货日期": "/Date(1792080000000+0800)/"
    }
  ]
}
```

### 3.2 建议的兼容优化，非必需

为了减少 Android 对中文字段名的依赖，建议 Web 端在 `Items` 中补充以下等价字段：

```json
{
  "Item_ID": 160059,
  "ItemName": "塑料粒子",
  "Quantity": 10000,
  "PieceCount": 1,
  "RequiredDate": "2026-10-17"
}
```

建议保留原字段，不要删除：

- `Item_ID`
- `名称`
- `发货数量`
- `件数`
- `发货日期`

如果新增字段，建议继续使用同一 `Info` 返回，不需要新 SOAP 方法。

## 4. 验收点

- 扫码成功且返回 `Items` 后，Android 明细列表仅显示 ITEM-ID、名称、数量、件数、要求日期。
- “发出方”整个区域不可见。
- 数量 `10000.0000` 显示为 `10000`。
- `/Date(...)/` 日期显示为 `yyyy-MM-dd`。
- 物流接收、整单移位、逐托盘移位原有操作不受影响。
