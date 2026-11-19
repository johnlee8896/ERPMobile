# APK 升级下载优化完整说明

> 适用模块：Android 客户端「APP 升级」下载 APK 流程。
> 文档目的：记录进度条不动问题的根因、手机端已完成的全部调整、以及仍待服务端配合的部分。

## 1. 问题现象

Android 客户端检测到新版本并弹出升级提示后，开始下载 APK，但进度条长时间不动或下载速度很慢。

服务端现场分析结论：APK 文件和 IIS 下载功能本身可用，主要问题集中在 Android 端的任务管理、断点续传、多连接和进度显示。

## 2. 根因分析

当前发布方式为「每次生成新 APK 后，重命名成同一个文件名 `Shberp.apk` 并覆盖服务端文件」，下载地址始终是：

```text
http://116.236.16.218:8001/Download/Shberp.apk
```

对 Android 下载组件来说，下载任务由「URL + 目标文件路径 + 临时文件 + 已下载字节数 + 断点位置」共同识别。URL 不变但文件内容已变，会带来：

- 手机用旧断点位置续传新文件。
- 重复建立多个 Range 连接。
- 服务端返回 `416 Range Not Satisfiable`。
- 新数据被错误追加到旧临时文件。
- 多个连接共享带宽导致速度下降。
- 进度条长时间停在 0%，没有任何反馈。

## 3. 手机端已完成的调整

### 3.1 单任务控制

同一时刻只允许一个升级下载任务，避免重复点击产生多个连接：

- 新增 `isDownloading` 静态标记。
- 重复点击只提示「正在下载升级包，请稍候...」，并复用现有进度框，不新建任务。

### 3.2 URL 追加版本号

下载地址追加 `?v=<versionCode>`，改变任务地址，规避旧缓存和旧断点续传：

```text
http://116.236.16.218:8001/Download/Shberp.apk?v=2611
```

### 3.3 本地文件名版本化

本地文件名由固定的 `SHBERP.apk` 改为 `Shberp-{versionCode}.apk`，每个版本落到独立本地文件：

```text
Shberp-2611.apk
Shberp-2612.apk
```

彻底避免「旧版本临时文件续传到新版本」。

### 3.4 下载前清理旧文件

下载前删除：

- 当前版本的 `.apk`、`.apk.temp`、`.apk.part`。
- 其它历史版本的 `Shberp-*.apk`、`.apk.part`、`.apk.temp`。

保证每次从头下载，不使用旧断点位置。

### 3.5 进度显示优化

- `totalBytes <= 0` 时显示「正在连接下载服务器...」+ 不确定进度动画，不再卡在 0%。
- 百分比计算改用 `long`，避免除零和整数溢出。

### 3.6 下载到 .part + 完整性校验（SHA-256 骨架）

下载流程改为：

```text
下载到 Shberp-{versionCode}.apk.part
  -> 校验文件大小（apkSize > 0 时）
  -> 校验 SHA-256（sha256 非空时）
  -> 校验通过后改名 Shberp-{versionCode}.apk
  -> 安装
```

校验失败时删除 `.part` 文件并提示，不允许安装不完整或损坏的包。

接口字段 `apkSize` / `sha256` 已预留：服务端返回后即可直接启用校验；当前服务端未返回时，校验自动跳过，不影响现有升级流程。

## 4. 修改涉及的文件

- `app/src/main/java/com/chinashb/www/mobileerp/upgrade/APPUpgradeManager.java`
  - 单任务控制、URL 版本号、本地文件名版本化、旧文件清理、`.part` 下载、`calculateSha256()`、`verifyAndInstall()`
- `app/src/main/java/com/chinashb/www/mobileerp/upgrade/DownloadingProgressDialog.java`
  - 连接阶段提示 + 进度百分比计算
- `app/src/main/java/com/chinashb/www/mobileerp/upgrade/AppUpgradeCheckManager.java`
  - 周期检查、透传 `apkSize` / `sha256`
- `app/src/main/java/com/chinashb/www/mobileerp/upgrade/VersionBean.java`
  - 新增 `apkSize` / `sha256` 字段
- `app/src/main/java/com/chinashb/www/mobileerp/APP.java`
  - 前台周期检查的启停

## 5. 升级检查相关（关联修复）

升级提示原只在「登录/退出登录」时触发，停留页面时不检查。已增加前台周期检查：

- 每 `30s` 调用一次 `forceCheck(当前顶部 Activity)`。
- 前台启动周期检查，后台自动停止。

这样服务端推新版本后，在任意页面停留也会在 30 秒内收到升级提示。

## 6. 仍需服务端配合的部分（正式方案）

彻底解决需要服务端提供版本化实体文件和校验信息：

- 每个版本使用独立文件名，例如 `Shberp-2612.apk`，发布后不再覆盖。
- 版本接口返回 `apkSize` 和 `sha256`。

版本接口建议返回：

```json
{
  "versionName": "2.6.12",
  "versionCode": 2612,
  "apkUrl": "http://116.236.16.218:8001/Download/Shberp-2612.apk",
  "apkSize": 7133955,
  "sha256": "当前 APK 对应的 SHA-256"
}
```

当前服务端返回 `apkSize` / `sha256` 的 SQL 列名预留为：

```text
ApkSize
Sha256
```

（与现有 `Version`、`Des` 列保持 PascalCase 风格一致。）

## 7. 注意事项

- `calculateSha256` 目前在下载完成的回调里同步执行（主线程）。APK 几 MB 时可接受；正式启用校验后建议挪到后台线程。
- 只要「旧版本还在下载时覆盖了服务器文件」，仍存在极小的新旧分段混合风险；彻底消除需服务端改为版本化实体文件且不覆盖已发布文件。
- 未跑编译验证。
