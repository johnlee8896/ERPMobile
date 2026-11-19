# APK 上传认证调整最终说明

## 结论

服务器返回 403 的原因不是 `Download` 目录没有写权限，而是：

- Android 使用了 `--anyauth --user`
- 服务器 `UploadApk.ashx` 不识别这种 HTTP 认证
- 服务器只接受 `X-Upload-Password` 请求头

因此需要修改 Android 上传请求，不需要修改 IIS 目录权限。

## 当前最可能的服务端阻塞原因

Android 已按要求发送 `X-Upload-Password`、`file`、`versionCode`、`versionName`，但仍返回 IIS 原生 403 页面，而不是 Handler 自定义的口令错误正文。

最可能是以下任一原因：

1. 当前出口公网 IP 不在服务器上传白名单中。
2. 服务器实际部署的 `UploadApk.ashx` 还是旧版本。
3. `local.properties` 中的 `release.upload.password` 不是新的上传口令，而是旧 IIS 账号密码。

当前 Android 构建机的公网出口 IP：

```text
160.202.167.53
```

请服务器管理员确认是否已加入上传白名单。

## Android 最终请求方式

```bash
curl --fail-with-body \
  -H "X-Upload-Password: ${UPLOAD_PASSWORD}" \
  -F "file=@app-release.apk;filename=app-release.apk" \
  -F "versionCode=${VERSION_CODE}" \
  -F "versionName=${VERSION_NAME}" \
  "http://116.236.16.218:8001/Download/UploadApk.ashx"
```

要求：

- 不要使用 `--anyauth`
- 不要使用 `--user`
- 文件字段必须为 `file`
- 必须提交当前真实 `versionCode`
- 必须提交当前真实 `versionName`
- 密码只从 `local.properties` 等本地配置读取，不能写死在 `build.gradle`

## 当前 Gradle 配置

`app/build.gradle` 已调整：

- 使用 `--fail-with-body`
- 增加 `X-Upload-Password`
- 增加 `uploadPassword` multipart 字段
- 保留 multipart 文件字段 `file`
- 增加 `versionCode`
- 增加 `versionName`
- 自动把上传基础地址补为：

```text
http://116.236.16.218:8001/UploadApk.ashx?fileName=app-release.apk
```

## local.properties

```properties
release.upload.url=http://116.236.16.218:8001/Download/UploadApk.ashx
release.upload.password=实际上传口令
release.upload.enabled=true
```

`release.upload.user` 不再用于 APK 上传。

`release.upload.password` 必须与你在 `/Download/Upload.html` 页面手动输入的“上传口令”完全一致，不能使用旧的 IIS 登录密码。

同时必须确认：

- `release.upload.password` 使用的是服务器管理员提供的最新上传口令。
- 服务器 Handler 是文档所述支持 `X-Upload-Password` 的版本。
- 当前公网 IP `160.202.167.53` 已加入可信来源名单。

## 上传成功后的服务器行为

服务器会根据 `versionCode` 和 `versionName` 自动生成：

- `Shberp-{versionCode}.apk`
- `Shberp.apk`
- `latest-apk.json`

其中 `latest-apk.json` 包含 URL、文件大小、SHA-256 和发布时间。

## 校验

上传成功后检查：

```text
http://116.236.16.218:8001/Download/latest-apk.json
```

同时确认：

- `Shberp-{versionCode}.apk`
- `Shberp.apk`

都存在且 APK 大小正确。
