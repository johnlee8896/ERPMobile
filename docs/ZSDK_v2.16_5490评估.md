# Zebra ZSDK Android v2.16.5490 评估

## 资料位置

- Demo 工程：
  `/Users/liweifeng/john/Zeraba Printer SDK/zebra-linkos-mpsdk-jan-2025/android/v2.16.5490/demos/androidstudio/ZSDK_DemosAndroid`
- 已编译 Demo APK：
  `/Users/liweifeng/john/Zeraba Printer SDK/zebra-linkos-mpsdk-jan-2025/android/v2.16.5490/demos/release/DeveloperDemo_Android-debug.apk`
- TestConnect APK：
  `/Users/liweifeng/john/Zeraba Printer SDK/zebra-linkos-mpsdk-jan-2025/android/v2.16.5490/TestConnect/TestConnect.apk`
- API 文档：
  `/Users/liweifeng/john/Zeraba Printer SDK/zebra-linkos-mpsdk-jan-2025/android/v2.16.5490/documentation/index.html`

## 结论

这套资料对当前 ZR328 调试非常有帮助，但 **不建议直接替换当前工程中的旧打印 SDK**。

原因：

- 当前 ERP 工程使用：
  - Gradle 3.1.2
  - compileSdk 27
  - minSdk 15
  - `com.android.support` 依赖
- 新 Demo 使用：
  - Gradle 8.7.2
  - compileSdk 35
  - minSdk 24
  - AndroidX

直接引入 `ZSDK_ANDROID_API.jar` 大概率会与项目现有 `ZebraPrinter_Android_SDK_1.0.jar` 的类名、依赖方式和 Android 权限处理冲突。

建议先在独立 Demo/分支验证 ZR328，再逐步抽取功能。

## 对当前问题的帮助

### 1. 蓝牙连接

官方 SDK 提供：

```java
new BluetoothConnection(macAddress)
new BluetoothConnectionInsecure(macAddress)
```

`ConnectionBuilder` 文档明确说明：

- `BT:MAC` 会优先尝试 insecure 连接。
- 成功条件包括打印机 `bluetooth.minimum_security_mode = 1`。
- insecure 失败后再尝试配对和 secure 连接。

这与当前项目将 ZR328 改为 `createInsecureRfcommSocketToServiceRecord` 优先的修复方向一致。

建议后续优先使用：

```java
Connection connection = ConnectionBuilder.build("BT:" + macAddress);
```

### 2. 打印机发现

官方 Demo 提供：

```java
BluetoothDiscoverer.findPrinters(context, discoveryHandler);
BluetoothLeDiscoverer.findPrinters(context, discoveryHandler);
```

能返回：

```java
DiscoveredPrinterBluetooth
DiscoveredPrinterBluetoothLe
```

可用于统一替换当前自定义的蓝牙扫描和配对页面。

### 3. 打印机状态

官方接口：

```java
ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
PrinterStatus status = printer.getCurrentStatus();

status.isReadyToPrint
status.isHeadOpen
status.isPaperOut
status.isPaused
status.labelsRemainingInBatch
```

可以解决当前用 `ESC h` 查询后显示“无响应”的问题。

### 4. 打印速度

官方 `SGD` 示例：

```java
String printSpeed = SGD.GET("media.speed", printerConnection);
```

Demo 中状态页也读取：

```text
media.type
media.speed
device.languages
zpl.label_length
media.printmode
```

建议在 ZR328 页面增加这些设置读取和展示，直接确认当前打印机速度与介质配置。

### 5. 图片打印

官方 ImagePrintDemo 使用：

```java
ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
printer.printImage(
    new ZebraImageAndroid(bitmap),
    0,
    0,
    width,
    height,
    false
);
```

比当前手工调用 `bitmapToBytes` + `AddBitmapImage` 更可靠。

### 6. 校准

官方接口：

```java
ToolsUtil.calibrate();
```

可以用于真正的介质校准，比当前手动调整纸张宽高更准确。

## 特别重要的说明

官方 readme 明确提示：

> 有些打印机出厂时是 line print mode，SDK 会将其识别为
> “Not a Link-OS printer”。
> 如需强制识别为 Link-OS，可使用：
>
> `device.languages = hybrid_xml_zpl`

当前 ZR328 出厂是 `line_print`，这个说明解释了：

- 旧 SDK 为什么返回“不支持的型号”。
- 为什么 `device.product_name` 握手不可靠。
- 为什么需要先明确打印机语言。

## 推荐下一步

1. 在现有手机上安装官方 `TestConnect.apk` 或 `DeveloperDemo_Android-debug.apk`。
2. 用官方工具连接同一台 ZR328。
3. 读取：
   - `media.type`
   - `media.speed`
   - `device.languages`
   - `device.product_name`
   - 打印机状态
4. 用官方工具打印一张图片，确认 ZR328 硬件和固件本身正常。
5. 官方工具验证成功后，再决定：
   - 短期：继续保留当前自研 ZR328 页面。
   - 中期：复制官方状态、设置读取逻辑。
   - 长期：升级 ERP 工程到 AndroidX/compileSdk 35 后统一使用新 SDK。
