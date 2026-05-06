<%@ WebHandler Language="C#" Class="UploadApk" %>

using System;
using System.IO;
using System.Web;

public class UploadApk : IHttpHandler
{
    public void ProcessRequest(HttpContext context)
    {
        context.Response.ContentType = "text/plain";

        if (!string.Equals(context.Request.HttpMethod, "POST", StringComparison.OrdinalIgnoreCase))
        {
            context.Response.StatusCode = 405;
            context.Response.Write("Only POST is allowed.");
            return;
        }

        if (context.Request.Files.Count == 0)
        {
            context.Response.StatusCode = 400;
            context.Response.Write("No file uploaded.");
            return;
        }

        HttpPostedFile postedFile = context.Request.Files["file"] ?? context.Request.Files[0];
        if (postedFile == null || postedFile.ContentLength <= 0)
        {
            context.Response.StatusCode = 400;
            context.Response.Write("Uploaded file is empty.");
            return;
        }

        string requestedFileName = context.Request["fileName"];
        string safeFileName = string.IsNullOrWhiteSpace(requestedFileName)
                ? "app-release.apk"
                : Path.GetFileName(requestedFileName);

        if (!safeFileName.EndsWith(".apk", StringComparison.OrdinalIgnoreCase))
        {
            context.Response.StatusCode = 400;
            context.Response.Write("Only .apk files are allowed.");
            return;
        }

        string downloadDirectory = context.Server.MapPath("~/Download/");
        if (!Directory.Exists(downloadDirectory))
        {
            Directory.CreateDirectory(downloadDirectory);
        }

        string targetFilePath = Path.Combine(downloadDirectory, safeFileName);
        string tempFilePath = targetFilePath + ".uploading";

        postedFile.SaveAs(tempFilePath);

        if (File.Exists(targetFilePath))
        {
            File.Delete(targetFilePath);
        }

        File.Move(tempFilePath, targetFilePath);

        context.Response.StatusCode = 200;
        context.Response.Write("Upload success: /Download/" + safeFileName);
    }

    public bool IsReusable
    {
        get { return false; }
    }
}
