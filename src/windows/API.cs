using System;
using System.Drawing;
using System.Windows.Forms;
using System.IO;
using Microsoft.Win32;
using System.Reflection;
using System.Net.Http;
using System.Text.Json;
using Microsoft.Web.WebView2.Core;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;

namespace com.ares
{
    public class aresAPI
    {
        private CoreWebView2 webview;
        public string APP_DIR;
        public static string REPO_URL = "https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/repo.json";

        public void LoadMain()
        {
            string mainContent = MainWindow.GetEmbeddedFile("4re5-manager.app.main.html");
            webview.NavigateToString(mainContent);
            sendMessage("LoadMain", "success");
        }

        public void GetPackageVersion(string package)
        {
            string manifest = Path.Combine(APP_DIR, "packages", package, "manifest");
            if (!File.Exists(manifest))
                sendMessage("GetPackageVersion", "-1.0");
            else
            {
                Manifest manifest_content = JsonSerializer.Deserialize<Manifest>(File.ReadAllText(manifest));
                // transform version format to float format
                // ex: 1.0.2.3 => 1.023
                var regex = new Regex(Regex.Escape("."));
                string version = regex.Replace(manifest_content.version, "#", 1);
                version = version.Replace(".", "");
                version = version.Replace("#", ".");
                sendMessage("GetPackageVersion", version);
            }
        }

        public void launchPackage(string package)
        {
            if (Security.Sanitize(package) != package)
            {
                sendMessage("launchPackage", "error: path inejction protection trigger");
                return;
            }
            string manifest = Path.Combine(APP_DIR, "packages", package, "manifest");
            Manifest app_manifest = JsonSerializer.Deserialize<Manifest>(File.ReadAllText(manifest), new JsonSerializerOptions
            {
                IncludeFields = true
            });

            // launch startup command
            Process process = new Process();
            ProcessStartInfo startInfo = new ProcessStartInfo();
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.RedirectStandardError = true;
            startInfo.WindowStyle = ProcessWindowStyle.Hidden;
            startInfo.FileName = "cmd.exe";
            startInfo.Arguments = "/C " + app_manifest.startUpFile;
            process.StartInfo = startInfo;
            process.Start();
            process.WaitForExit();

            int exitCode = process.ExitCode;
            string errors = process.StandardError.ReadToEnd();
            if (exitCode == 0)
                sendMessage("launchPackage", "success");
            else
                sendMessage("launchPackage", "error: "+errors);
        }

        public void Install(string json)
        {
            // load app json
            App current_app = JsonSerializer.Deserialize<App>(json, new JsonSerializerOptions
            {
                IncludeFields = true
            });
            if (current_app == null)
            {
                sendMessage("Install", "error: could not parse json into class");
                return;
            }
            Platform[] platforms;
            Platform platform = null;
            try
            {
                platforms = current_app.platform;
                foreach (Platform p in platforms)
                {
                    if (p.platformid == 0)
                        platform = p;
                }
                if (platform == null)
                {
                    sendMessage("Install", "error: platform is null");
                    return;
                }
            }
            catch (Exception e)
            {
                sendMessage("Install", "error: " + e);
                return;
            }

            string repo_path = Path.Combine(APP_DIR, "repo.json");
            string package_path = Path.Combine(APP_DIR, "packages");

            if (!Directory.Exists(package_path))
                Directory.CreateDirectory(package_path);

            // path injection protection
            current_app.package = Security.Sanitize(current_app.package);

            string this_package_path = Path.Combine(package_path, current_app.package);
            string[] urlSplitted = platform.url.Split("/");
            if (urlSplitted.Length < 3)
            {
                sendMessage("Install", "error: invalid url");
                return;
            }
            string startupItem = Path.Combine(this_package_path, urlSplitted[urlSplitted.Length - 1]);
            if (!Directory.Exists(this_package_path))
                Directory.CreateDirectory(this_package_path);
            // download file
            using (var client = new HttpClient())
            {
                using (var s = client.GetStreamAsync(platform.url))
                {
                    using (var fs = new FileStream(startupItem, FileMode.OpenOrCreate))
                    {
                        s.Result.CopyTo(fs);
                        sendMessage("log", "Binary has been downloaded!");
                    }
                }
            }
            if (platform.isInstaller == "true")
            {
                Process process = new Process();
                ProcessStartInfo startInfo = new ProcessStartInfo();
                process.StartInfo.UseShellExecute = false;
                process.StartInfo.RedirectStandardError = true;
                startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                startInfo.FileName = "cmd.exe";
                startInfo.Arguments = "/C call \"" + startupItem + "\"";
                process.StartInfo = startInfo;
                process.Start();
                process.WaitForExit();

                int exitCode = process.ExitCode;
                string errors = process.StandardError.ReadToEnd();
                if (exitCode == 0)
                    sendMessage("log", "Installer command has been executed!");
                else
                    sendMessage("log", "error: "+errors);
                
            }
            // execute command
            if (platform.installProcess != "none" && platform.installProcess != "")
            {
                Process process = new Process();
                ProcessStartInfo startInfo = new ProcessStartInfo();
                process.StartInfo.UseShellExecute = false;
                process.StartInfo.RedirectStandardError = true;
                startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                startInfo.FileName = "cmd.exe";
                startInfo.Arguments = "/C " + platform.installProcess;
                process.StartInfo = startInfo;
                process.Start();
                process.WaitForExit();

                int exitCode = process.ExitCode;
                string errors = process.StandardError.ReadToEnd();
                if (exitCode == 0)
                    sendMessage("log", "post-installation command has been executed!");
                else
                    sendMessage("log", "error: "+errors);
            }
            // write manifest
            string manifest = Path.Combine(APP_DIR, "packages", current_app.package, "manifest");
            sendMessage("log", "manifest path: " + manifest);
            JsonParser.ParseManifest(current_app, platform, manifest);
            sendMessage("log", "manifest written!");
            sendMessage("Install", "success");
        }

        public void openLink(string url)
        {
            if (url.StartsWith("http")) // avoid other programs to be opened
            {
                try
                {
                    Process.Start(url);
                }
                catch
                {
                    // hack because of this: https://github.com/dotnet/corefx/issues/10361
                    if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
                    {
                        url = url.Replace("&", "^&");
                        Process.Start(new ProcessStartInfo(url) { UseShellExecute = true });
                    }
                    else if (RuntimeInformation.IsOSPlatform(OSPlatform.Linux))
                    {
                        Process.Start("xdg-open", url);
                    }
                    else if (RuntimeInformation.IsOSPlatform(OSPlatform.OSX))
                    {
                        Process.Start("open", url);
                    }
                    else
                    {
                        sendMessage("openLink", "an error occured");
                        return;
                    }
                }
            }
            sendMessage("openLink", "success");
        }

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
            sendMessage("GetRepo", repo_content);
        }

        private void sendMessage(string name, string message)
        {
            webview.PostWebMessageAsString(name + ":" + message);
        }

        public aresAPI(CoreWebView2 _webview)
        {
            APP_DIR = Path.Combine(Environment.GetEnvironmentVariable("SystemDrive"), @"Program Files\4re5 group");
            webview = _webview;
        }
    }
}