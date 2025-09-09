using System;
using System.Drawing;
using System.Windows.Forms;
using System.IO;
using Microsoft.Win32;
using System.Reflection;
using System.Net.Http;
using System.Text.Json;
using Microsoft.Web.WebView2.Core;


namespace com.ares
{
    public class aresAPI
    {
        private CoreWebView2 webview;
        public string APP_DIR;
        public static string REPO_URL = "https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/repo.json";

        public void GetRepo(bool force_updating)
        {
            // check if APP dir exists
            if (!Directory.Exists(APP_DIR))
                Directory.CreateDirectory(APP_DIR);

            string repo_path = Path.Combine(APP_DIR, "repo.json");
            if (!File.Exists(repo_path) || force_updating)
            {
                using (var client = new HttpClient())
                {
                    using (var s = client.GetStreamAsync(REPO_URL))
                    {
                        using (var fs = new FileStream(repo_path, FileMode.OpenOrCreate))
                        {
                            s.Result.CopyTo(fs);
                        }
                    }
                }
            }
            string repo_content = File.ReadAllText(repo_path);
            sendMessage("GetRepo",repo_content);
        }

        private void sendMessage(string name, string message)
        {
            webview.PostWebMessageAsString(name+":"+message);
        }

        public aresAPI(CoreWebView2 _webview)
        {
            APP_DIR = Path.Combine(Environment.GetEnvironmentVariable("SystemDrive"), @"Program Files\4re5 group");
            webview = _webview;
        }
    }
}