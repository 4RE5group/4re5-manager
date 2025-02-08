using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace _4re5_manager
{
    public class ListGenerator
    {
        private static int maxTextLength = 20;
        private static string SetLabelText(string text)
        {
            if (text.Length > maxTextLength)
                return text.Substring(0, maxTextLength) + "...";
            else
                return text;
        }
        public static void AddAppPanel(Control parent, int pos, string appName, string description, string platform, Image icon, EventHandler onDownloadClick, EventHandler onAppRowClick)
        {
            Panel panel = new Panel
            {
                Name = "panel_"+appName,
                BackColor = Color.FromArgb(50, 50, 50), // Gris foncé
                Size = new Size(parent.Width, 50),
                Margin = new Padding(5),
                Location = new Point(0, pos * 50),
            };

            // Label App Name
            Label lblAppName = new Label
            {
                Name = "appName_"+appName,
                Text = SetLabelText(appName),
                ForeColor = Color.White,
                Font = new Font("Arial", 10, FontStyle.Bold),
                Location = new Point(40, 5),
                AutoSize = true
            };

            // Label Description
            Label lblDescription = new Label
            {
                Name = "appDesc_"+appName,
                Text = SetLabelText(description),
                ForeColor = Color.LightGray,
                Font = new Font("Arial", 8),
                Location = new Point(40, 25),
                AutoSize = true
            };

            // Label Platform
            Label lblPlatform = new Label // platform -> version
            {
                Name = "appPlatform_" + appName,
                Text = SetLabelText(platform),
                ForeColor = Color.White,
                Font = new Font("Arial", 9),
                Location = new Point((parent.Width/2)-40, 15),
                AutoSize = true
            };

            // Platform Icon 
            PictureBox picPlatform = new PictureBox
            {
                Name = "appPic_" + appName,
                Image = icon, // Remplace par une icône valide
                Size = new Size(20, 20),
                Location = new Point(15, 15),
                SizeMode = PictureBoxSizeMode.Zoom
            };

            // Download Button
            Button btnDownload = new Button
            {
                Name="downloadbtn_" + appName,
                Text = "Download",
                BackColor = Color.White,
                ForeColor = Color.Black,
                FlatStyle = FlatStyle.Flat,
                Size = new Size(90, 30),
                Location = new Point(parent.Width-110, 10)
            };
            btnDownload.Click += onDownloadClick;
            panel.Click += onAppRowClick;

            // Ajout des contrôles au panel
            panel.Controls.Add(lblAppName);
            panel.Controls.Add(lblDescription);
            panel.Controls.Add(lblPlatform);
            panel.Controls.Add(picPlatform);
            panel.Controls.Add(btnDownload);

            // Ajout du panel au parent
            parent.Controls.Add(panel);
        }
    }
}
