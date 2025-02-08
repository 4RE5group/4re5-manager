using System.Drawing.Drawing2D;
using static System.Runtime.InteropServices.JavaScript.JSType;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;

namespace _4re5_manager
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }
        private void ApplyRoundedCorners(int radius)
        {
            GraphicsPath path = new GraphicsPath();
            int w = this.Width;
            int h = this.Height;
            int d = radius * 2; // Diamètre des coins

            // Ajout des coins arrondis
            path.AddArc(0, 0, d, d, 180, 90);           // Haut-gauche
            path.AddArc(w - d, 0, d, d, 270, 90);       // Haut-droite
            path.AddArc(w - d, h - d, d, d, 0, 90);     // Bas-droite
            path.AddArc(0, h - d, d, d, 90, 90);        // Bas-gauche
            path.CloseFigure();

            this.Region = new Region(path); // Applique la forme
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            updateRepo = new System.Windows.Forms.Button();
            title = new Label();
            exit = new PictureBox();
            minimize = new PictureBox();
            panel1 = new Panel();
            appsList = new Panel();
            panel3 = new Panel();
            searchBox = new System.Windows.Forms.TextBox();
            search = new PictureBox();
            panel2 = new Panel();
            appBtnUninstall = new System.Windows.Forms.Button();
            appBtnInstall = new System.Windows.Forms.Button();
            appVersion = new Label();
            appDesc = new Label();
            label2 = new Label();
            appName = new Label();
            appIcon = new PictureBox();
            available = new Label();
            Installed = new Label();
            PictureBox appBanner = new PictureBox();
            ((System.ComponentModel.ISupportInitialize)exit).BeginInit();
            panel1.SuspendLayout();
            panel3.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)search).BeginInit();
            panel2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)appIcon).BeginInit();
            SuspendLayout();
            // 
            // title
            // 
            title.AutoSize = true;
            title.BackColor = Color.Transparent;
            title.ForeColor = Color.White;
            title.Location = new Point(12, 9);
            title.Name = "title";
            title.Size = new Size(101, 20);
            title.TabIndex = 1;
            title.Text = "4re5 manager";
            // 
            // exit
            // 
            using (MemoryStream ms = new MemoryStream(Convert.FromBase64String(Data.exitIcon)))
            {
                exit.Image = Image.FromStream(ms);
            }
            exit.Location = new Point(775, 10);
            exit.Name = "exit";
            exit.Size = new Size(15, 15);
            exit.SizeMode = PictureBoxSizeMode.StretchImage;
            exit.TabIndex = 2;
            exit.TabStop = false;
            exit.Click += exit_Click;

            // 
            // minimize
            // 
            using (MemoryStream ms = new MemoryStream(Convert.FromBase64String(Data.minimizeIcon)))
            {
                minimize.Image = Image.FromStream(ms);
            }
            minimize.Location = new Point(735, 10);
            minimize.Name = "minimize";
            minimize.Size = new Size(15, 15);
            minimize.SizeMode = PictureBoxSizeMode.StretchImage;
            minimize.TabIndex = 2;
            minimize.TabStop = false;
            minimize.Click += minimize_Click;


            // app banner
            using (MemoryStream ms = new MemoryStream(Convert.FromBase64String(Data.banner)))
            {
                appBanner.Image = Image.FromStream(ms);
            }
            appBanner.Location = new Point(12, 9);
            appBanner.Name = "title";
            appBanner.Size = new Size(100, 65);
            appBanner.SizeMode = PictureBoxSizeMode.Zoom; 
            appBanner.TabIndex = 2;
            this.Controls.Add(appBanner);

            // update button
            updateRepo.Text = "Update";
            updateRepo.ForeColor = Color.Black;
            updateRepo.BackColor = Color.White;
            updateRepo.Size = new Size(80, 30);
            updateRepo.Location = new Point(710, 60);
            updateRepo.Click += UpdateRepo_Click;




            // 
            // panel1
            //
            panel1.Controls.Add(panel3);
            panel1.Controls.Add(appsList);
            
            panel1.Location = new Point(7, 115);
            panel1.Margin = new Padding(8, 8, 8, 8);
            panel1.Name = "appListDisplay";
            panel1.Size = new Size(476, 373);
            panel1.TabIndex = 4;
            // 
            // appsList
            // 
            appsList.AutoScroll = true;
            appsList.Location = new Point(3, 50);
            appsList.Name = "appsList";
            appsList.Padding = new Padding(8, 50, 8, 8);
            appsList.Size = new Size(470, 328);
            appsList.TabIndex = 9;
            // 
            // panel3
            // 
            panel3.BackColor = Color.FromArgb(64, 64, 64);
            panel3.Controls.Add(searchBox);
            panel3.Controls.Add(search);
            panel3.Location = new Point(3, 3);
            panel3.Name = "panel3";
            panel3.Size = new Size(470, 36);
            panel3.TabIndex = 8;
            // 
            // searchBox
            // 
            searchBox.Location = new Point(3, 3);
            searchBox.Name = "searchBox";
            searchBox.Size = new Size(432, 30);
            searchBox.TabIndex = 6;
            //searchBox.TextChanged += searchApp;
            searchBox.KeyDown += searchApp;
            // 
            // search
            // 
            using (MemoryStream ms = new MemoryStream(Convert.FromBase64String(Data.searchIcon)))
            {
                search.Image = Image.FromStream(ms);
            }
            search.Location = new Point(435, 3);
            search.Name = "search";
            search.Size = new Size(30, 30);
            search.SizeMode = PictureBoxSizeMode.StretchImage;
            search.TabIndex = 7;
            search.TabStop = false;
            //search.Click += searchApp;
            // 
            // panel2
            // 
            panel2.BackColor = Color.FromArgb(64, 64, 64);
            panel2.Controls.Add(appBtnUninstall);
            panel2.Controls.Add(appBtnInstall);
            panel2.Controls.Add(appVersion);
            panel2.Controls.Add(appDesc);
            panel2.Controls.Add(label2);
            panel2.Controls.Add(appName);
            panel2.Controls.Add(appIcon);
            panel2.Location = new Point(494, 115);
            panel2.Name = "panel2";
            panel2.Padding = new Padding(8);
            panel2.Size = new Size(290, 373);
            panel2.TabIndex = 5;
            // 
            // appBtnUninstall
            // 
            appBtnUninstall.Location = new Point(178, 338);
            appBtnUninstall.Name = "appBtnUninstall";
            appBtnUninstall.Size = new Size(94, 29);
            appBtnUninstall.TabIndex = 6;
            appBtnUninstall.Text = "Uninstall";
            appBtnUninstall.UseVisualStyleBackColor = true;
            // 
            // appBtnInstall
            // 
            appBtnInstall.Location = new Point(21, 338);
            appBtnInstall.Name = "appBtnInstall";
            appBtnInstall.Size = new Size(94, 29);
            appBtnInstall.TabIndex = 5;
            appBtnInstall.Text = "Install";
            appBtnInstall.UseVisualStyleBackColor = true;
            // 
            // appVersion
            // 
            appVersion.AutoSize = true;
            appVersion.Font = new Font("Segoe UI", 9F, FontStyle.Bold, GraphicsUnit.Point, 0);
            appVersion.ForeColor = Color.White;
            appVersion.Location = new Point(21, 315);
            appVersion.Name = "appVersion";
            appVersion.Size = new Size(90, 20);
            appVersion.TabIndex = 4;
            appVersion.Text = "version: 1.0";
            // 
            // appDesc
            // 
            appDesc.ForeColor = Color.White;
            appDesc.Location = new Point(13, 197);
            appDesc.Name = "appDesc";
            appDesc.Size = new Size(259, 110);
            appDesc.TabIndex = 3;
            appDesc.Text = "sample desc";
            // 
            // label2
            // 
            label2.AutoSize = true;
            label2.Font = new Font("Segoe UI", 9F, FontStyle.Bold, GraphicsUnit.Point, 0);
            label2.ForeColor = Color.White;
            label2.Location = new Point(21, 177);
            label2.Name = "label2";
            label2.Size = new Size(87, 20);
            label2.TabIndex = 2;
            label2.Text = "description";
            // 
            // appName
            // 
            appName.BackColor = Color.Transparent;
            appName.Font = new Font("Segoe UI", 12F);
            appName.ForeColor = Color.White;
            appName.Location = new Point(0, 133);
            appName.Name = "appName";
            appName.Size = new Size(290, 36);
            appName.TabIndex = 1;
            appName.Text = "AppName";
            appName.TextAlign = ContentAlignment.TopCenter;
            // 
            // appIcon
            // 
            appIcon.Location = new Point(0, 15);
            appIcon.Name = "appIcon";
            appIcon.Size = new Size(290, 115);
            appIcon.SizeMode = PictureBoxSizeMode.Zoom;
            appIcon.TabIndex = 0;
            appIcon.TabStop = false;
            // 
            // available
            // 
            available.AutoSize = true;
            available.BackColor = Color.White;
            available.ForeColor = Color.Black;
            available.Location = new Point(12, 92);
            available.Name = "available";
            available.Size = new Size(71, 20);
            available.TabIndex = 7;
            available.Text = "Available";
            available.Click += available_and_installed_Click;
            // 
            // Installed
            // 
            Installed.AutoSize = true;
            Installed.ForeColor = Color.White;
            Installed.Location = new Point(89, 92);
            Installed.Name = "Installed";
            Installed.Size = new Size(65, 20);
            Installed.TabIndex = 8;
            Installed.Text = "Installed";
            Installed.Click += available_and_installed_Click;
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(8F, 20F);
            AutoScaleMode = AutoScaleMode.Font;
            BackColor = Color.Black;
            ClientSize = new Size(800, 500);
            Controls.Add(Installed);
            Controls.Add(available);
            Controls.Add(panel2);
            Controls.Add(panel1);
            Controls.Add(exit);
            Controls.Add(minimize);
            Controls.Add(title);
            Controls.Add(updateRepo);
            FormBorderStyle = FormBorderStyle.None;
            MaximumSize = new Size(800, 500);
            MinimumSize = new Size(800, 500);


            using (MemoryStream ms = new MemoryStream(Convert.FromBase64String(Data.appIcon)))
            {
                Icon = new Icon(ms);
            }
            MouseDown += window_MouseDown;
            MouseUp += window_MouseUp;
            MouseMove += window_MouseMove;
            Load += (s, e) => ApplyRoundedCorners(20);



            Name = "4re5 manager";
            StartPosition = FormStartPosition.CenterScreen;
            Text = "4re5 manager";
            ((System.ComponentModel.ISupportInitialize)exit).EndInit();
            panel1.ResumeLayout(false);
            panel3.ResumeLayout(false);
            panel3.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)search).EndInit();
            panel2.ResumeLayout(false);
            panel2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)appIcon).EndInit();
            ResumeLayout(false);
            PerformLayout();
        }

        private void UpdateRepo_Click(object sender, EventArgs e)
        {
            FetchRepo(true);
        }

        #endregion
        private System.Windows.Forms.Button updateRepo;
        private Label title;
        private PictureBox exit;
        private PictureBox minimize;
        private Panel panel1;
        private Panel panel2;
        private PictureBox appIcon;
        private System.Windows.Forms.TextBox searchBox;
        private Panel panel3;
        private PictureBox search;
        private Label available;
        private Label Installed;
        private Panel appsList;
        private Label appDesc;
        private Label label2;
        private Label appName;
        private System.Windows.Forms.Button appBtnUninstall;
        private System.Windows.Forms.Button appBtnInstall;
        private Label appVersion;
    }
}
