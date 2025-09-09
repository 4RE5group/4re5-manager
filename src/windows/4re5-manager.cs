using System;
using System.Drawing;
using System.Windows.Forms;
using System.IO;
using Microsoft.Win32;
using System.Reflection;
using System.Collections.Generic;
using Microsoft.Web.WebView2.Core;
using Microsoft.Web.WebView2.WinForms;

namespace com.ares
{
    public partial class MainWindow : Form
    {
        private string app_version;
        private WebView2 webView;

        public static string GetEmbeddedFile(string resourceName)
        {
            var assembly = Assembly.GetExecutingAssembly();
            using (Stream stream = assembly.GetManifestResourceStream(resourceName))
            {
                if (stream == null)
                    throw new FileNotFoundException($"Embedded resource '{resourceName}' not found.");

                using (StreamReader reader = new StreamReader(stream))
                {
                    return reader.ReadToEnd();
                }
            }
        }
        public static Icon GetEmbeddedIcon(string resourceName)
        {
            var assembly = Assembly.GetExecutingAssembly();
            using (Stream stream = assembly.GetManifestResourceStream(resourceName))
            {
                if (stream == null)
                    throw new FileNotFoundException($"Embedded resource '{resourceName}' not found.");

                return new Icon(stream);
            }
        }

        public MainWindow()
        {
            // Get app version
            var executable = Assembly.GetExecutingAssembly();
            var versionInfo = executable?.GetCustomAttribute<AssemblyInformationalVersionAttribute>();
            app_version = versionInfo?.InformationalVersion.Split('+')[0].Replace("-", " ") ?? "1.0.0";

            this.Name = "4re5 manager";
            this.Text = $"4re5 manager - v{app_version}";
            this.Icon = GetEmbeddedIcon("4re5-manager.icon.ico");
            this.MinimumSize = new Size(700, 450);
            this.MaximumSize = this.MinimumSize;
            this.BackColor = Color.Black;

            InitializeWebView2Async();
        }

        private async void InitializeWebView2Async()
        {
            webView = new WebView2
            {
                Dock = DockStyle.Fill,
            };
            this.Controls.Add(webView);

            try
            {
                await webView.EnsureCoreWebView2Async();
                // load 4re5-manager api
                webView.CoreWebView2.AddHostObjectToScript("aresAPI", new aresAPI(webView.CoreWebView2));
                
                string mainContent = GetEmbeddedFile("4re5-manager.app.main.html");
                webView.CoreWebView2.NavigateToString(mainContent);
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Failed to load UI: {ex.Message}");
                this.Close();
            }
        }

        [STAThread]
        static int Main(string[] argv)
        {
            try
            {
                // Set AUMID for current process
                string regPath = @"Software\Classes\AppUserModelId\com.ares";
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey(regPath, true))
                {
                    if (key == null)
                    {
                        using (RegistryKey newKey = Registry.CurrentUser.CreateSubKey(regPath))
                        {
                            newKey.SetValue("DisplayName", "4re5 manager");
                            newKey.SetValue("ApplicationName", "4re5 manager");
                        }
                    }
                }

                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new MainWindow());
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Could not launch UI: {ex.Message}");
                return 1;
            }
            return 0;
        }
    }
}
