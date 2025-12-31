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
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Threading.Tasks;
using System.Threading;

namespace com.ares
{
    public class aresAPI
    {
        private CoreWebView2 webview;
        private string app_version;
        private SynchronizationContext context;
        public string APP_DIR;
        public static string REPO_URL = "https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/repo.json";
        private Dictionary<string, Process> runningProcesses = new Dictionary<string, Process>();

        public void LoadMain()
        {
            string mainContent = MainWindow.GetEmbeddedFile("4re5-manager.app.main.html");
            webview.NavigateToString(mainContent);
            sendMessage("LoadMain", "success");
        }

        public void GetManagerVersion()
        {
            sendMessage("GetManagerVersion", app_version);
        }

        public void GetPackageVersion(string package)
        {
            if (Security.Sanitize(package) != package)
            {
                sendMessage("GetPackageVersion", "error: path inejction protection trigger");
                return;
            }
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
            Task.Run(() =>
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

                string executablePath = Path.Combine(APP_DIR, "packages", package, app_manifest.startUpFile);

                // launch startup command
                Process process = new Process();
                ProcessStartInfo startInfo = new ProcessStartInfo();
                
                process.StartInfo.UseShellExecute = false;
                startInfo.FileName = "cmd.exe";
                startInfo.WorkingDirectory = Path.Combine(APP_DIR, "packages", package);
                if (app_manifest.isCmdTool == "true")
                {
                    startInfo.WindowStyle = ProcessWindowStyle.Normal;
                    if (File.Exists(executablePath)) 
                        startInfo.Arguments = "/K \"" + executablePath + "\"";
                    else
                        startInfo.Arguments = "/K \"" + app_manifest.startUpFile + "\"";
                }
                else
                {
                    startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                    startInfo.Arguments = "/C \"" + executablePath + "\"";
                }
                process.StartInfo = startInfo;
                process.Start();

                lock (runningProcesses)
                {
                    runningProcesses[package] = process;
                }

                process.WaitForExit();

                lock (runningProcesses)
                {
                    if (runningProcesses.ContainsKey(package) && runningProcesses[package] == process)
                        runningProcesses.Remove(package);
                }

                int exitCode = process.ExitCode;
                if (exitCode == 0)
                    sendMessage("launchPackage", "success");
                else
                    sendMessage("launchPackage", "error: could not launch package");
            });
        }

        public void Uninstall(string package)
        {
            Task.Run(() =>
            {
                package = Security.Sanitize(package);

                lock (runningProcesses)
                {
                    if (runningProcesses.ContainsKey(package))
                    {
                        try
                        {
                            runningProcesses[package].Kill(true);
                        }
                        catch { }
                        runningProcesses.Remove(package);
                    }
                }
                System.Threading.Thread.Sleep(500);

                string package_path = Path.Combine(APP_DIR, "packages", package);
                if (Directory.Exists(package_path))
                {
                    try
                    {
                        Directory.Delete(package_path, true);
                        sendMessage("Uninstall", "success");
                    }
                    catch (Exception ex)
                    {
                        sendMessage("Uninstall", "error: " + ex.Message);
                    }
                }
                else
                {
                    sendMessage("Uninstall", "error: package not found");
                }
            });
        }

        public void Install(string json)
        {
            Task.Run(async () =>
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
                        if (p.platform == "windows")
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
                
                bool fileExists = File.Exists(startupItem);
                bool checksumMatch = false;

                if (fileExists)
                {
                    using (var stream = File.OpenRead(startupItem))
                    {
                        using (var sha256 = SHA256.Create())
                        {
                            byte[] hash = sha256.ComputeHash(stream);
                            string hashString = BitConverter.ToString(hash).Replace("-", "").ToLowerInvariant();
                            if ("sha256:" + hashString == platform.checksum)
                                checksumMatch = true;
                            else
                                sendMessage("log", "Checksum mismatch, redownloading...");
                        }
                    }
                }

                if (!fileExists || !checksumMatch)
                {
                    if (fileExists)
                        File.Delete(startupItem);

                    // download file
                    using (var client = new HttpClient())
                    {
                        using (var s = await client.GetStreamAsync(platform.url))
                        {
                            using (var fs = new FileStream(startupItem, FileMode.OpenOrCreate))
                            {
                                await s.CopyToAsync(fs);
                                sendMessage("log", "Binary has been downloaded!");
                            }
                        }
                    }

                    // Verify checksum after download
                    using (var stream = File.OpenRead(startupItem))
                    {
                        using (var sha256 = SHA256.Create())
                        {
                            byte[] hash = sha256.ComputeHash(stream);
                            string hashString = "sha256:" + BitConverter.ToString(hash).Replace("-", "").ToLowerInvariant();
                            if (hashString != platform.checksum)
                            {
                                stream.Close();
                                File.Delete(startupItem);
                                sendMessage("log", "error: checksum mismatch   '"+hashString+"' vs '"+platform.checksum+"'");
                                sendMessage("Install", "error: checksum mismatch after download");
                                return;
                            }
                        }
                    }

                    if (platform.artifactType == "exe" || platform.artifactType == "msi")
                    {
                        sendMessage("Install", "downloaded");
                        return;
                    }
                }

                if (platform.artifactType == "exe" || platform.artifactType == "msi")
                {
                    //platform.startUpFile = urlSplitted[urlSplitted.Length - 1];

                    Process process = new Process();
                    ProcessStartInfo startInfo = new ProcessStartInfo();
                    process.StartInfo.UseShellExecute = false;
                    //startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                    startInfo.FileName = startupItem;
                    //startInfo.Arguments = "/C call \"" + startupItem + "\"";
                    process.StartInfo = startInfo;
                    process.Start();
                    process.WaitForExit();

                    // Assume success after installer closes
                    int exitCode = process.ExitCode;
                    if (exitCode != 0)
                    {
                        sendMessage("Install", "error: installer failed with code " + exitCode);
                        return;
                    }
                }

                // write manifest
                string manifest = Path.Combine(APP_DIR, "packages", current_app.package, "manifest");
                sendMessage("log", "manifest path: " + manifest);
                JsonParser.ParseManifest(current_app, platform, manifest);
                sendMessage("log", "manifest written!");
                sendMessage("Install", "success");
            });
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
                        Process.Start("xdg-open", url);
                    else if (RuntimeInformation.IsOSPlatform(OSPlatform.OSX))
                        Process.Start("open", url);
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
            Task.Run(async () =>
            {
                // check if APP dir exists
                if (!Directory.Exists(APP_DIR))
                    Directory.CreateDirectory(APP_DIR);

                string repo_path = Path.Combine(APP_DIR, "repo.json");
                if (!File.Exists(repo_path) || force_updating)
                {
                    using (var client = new HttpClient())
                    {
                        using (var s = await client.GetStreamAsync(REPO_URL))
                        {
                            using (var fs = new FileStream(repo_path, FileMode.OpenOrCreate))
                                await s.CopyToAsync(fs);
                        }
                    }
                }
                string repo_content = await File.ReadAllTextAsync(repo_path);
                sendMessage("GetRepo", repo_content);
            });
        }

        private void sendMessage(string name, string message)
        {
            if (context != null)
            {
                context.Post((state) => {
                    webview.PostWebMessageAsString(name + ":" + message);
                }, null);
            }
            else
                webview.PostWebMessageAsString(name + ":" + message);
        }

        public aresAPI(CoreWebView2 _webview, string appversion)
        {
            app_version = appversion;
            APP_DIR = Path.Combine(Environment.GetEnvironmentVariable("SystemDrive"), @"Program Files\4re5 group");
            webview = _webview;
            context = SynchronizationContext.Current;
        }
    }
}