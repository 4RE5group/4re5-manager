using System;
using System.Drawing;
using System.Windows.Forms;
using System.IO;
using Microsoft.Win32;
using System.Reflection;

namespace com.ares
{
    public partial class MainWindow : Form
    {
        private string home_path;

        public static string GetEmbeddedHtml(string resourceName)
        {
            var assembly = Assembly.GetExecutingAssembly();
            using (Stream stream = assembly.GetManifestResourceStream(resourceName))
            using (StreamReader reader = new StreamReader(stream))
            {
                return reader.ReadToEnd();
            }
        }
        public MainWindow()
        {
            home_path = Environment.GetEnvironmentVariable("userprofile") + @"\4re5 group\";
            if (!Directory.Exists(home_path))
            {
                Directory.CreateDirectory(home_path);
            }
            this.Name = "4re5 manager";
            this.Text = "4re5 manager - v1.0";
            this.Icon = Icon.ExtractAssociatedIcon(System.Reflection.Assembly.GetExecutingAssembly().Location);
            this.MinimumSize = new Size(700, 450);
            InitializeWebBrowser();
        }

        private void InitializeWebBrowser()
        {
            WebBrowser webBrowser = new WebBrowser();
            webBrowser.Dock = DockStyle.Fill;
            webBrowser.Size = this.Size;
            webBrowser.ScriptErrorsSuppressed = true;
            var names = Assembly.GetExecutingAssembly().GetManifestResourceNames();
            foreach (var name in names)
            {
                Console.WriteLine(name);
            }

            string mainContent = GetEmbeddedHtml("main.html");
            webBrowser.DocumentText = mainContent;
            this.Controls.Add(webBrowser);
        }

        [STAThread]
        static void Main()
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
    }
}
