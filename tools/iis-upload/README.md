# IIS APK Upload

Files in this folder:

- `UploadApk.ashx`
- `web.config`

Deploy them to the IIS site root that currently serves:

- `http://116.236.16.218:8001/Download/`

Expected result after deployment:

- upload endpoint:
  - `http://116.236.16.218:8001/UploadApk.ashx?fileName=app-release.apk`
- download path:
  - `http://116.236.16.218:8001/Download/app-release.apk`

What the handler does:

- accepts `POST`
- accepts multipart form upload field `file`
- saves uploaded apk into `~/Download/`
- overwrites the target file if it already exists

Suggested `local.properties` settings:

```properties
release.upload.url=http://116.236.16.218:8001/UploadApk.ashx?fileName=app-release.apk
release.upload.user=你的账号
release.upload.password=你的密码
release.upload.enabled=true
release.upload.mode=multipart
release.upload.formField=file
```

Example manual test:

```bash
curl --anyauth --user "user:password" \
  -F "file=@app/build/outputs/apk/release/app-release.apk;filename=app-release.apk" \
  "http://116.236.16.218:8001/UploadApk.ashx?fileName=app-release.apk"
```

Notes:

- `web.config` raises upload limits to 100 MB.
- Authentication is expected to be handled by IIS itself.
- If the IIS site is not an ASP.NET site, enable ASP.NET/.NET Framework support first.
