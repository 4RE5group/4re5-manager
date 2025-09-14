using System;
using System.Text.Json;
using System.IO;

namespace com.ares
{
    public static class JsonParser
    {
        public static void ParseManifest(App app, Platform platform, string path)
        {
            Manifest manifest = new Manifest();
            manifest.disabled = app.disabled;
            manifest.name = app.name;
            manifest.description = app.description;
            manifest.image = app.image;
            manifest.first_date = app.first_date;
            manifest.author = app.author;
            manifest.price = app.price;
            manifest.docs = app.docs;
            manifest.github = app.github;
            manifest.license = app.license;
            manifest.package = app.package;
            manifest.version = platform.version;
            manifest.isCmdTool = platform.isCmdTool;
            manifest.startUpFile = platform.startUpFile;

            string jsonString = JsonSerializer.Serialize(manifest, new JsonSerializerOptions
            {
                WriteIndented = true //for pretty-printing
            });
            File.WriteAllText(path, jsonString);
        }
    }
    public class Manifest
    {
        public string disabled { get; set; }
        public string name { get; set; }
        public string description { get; set; }
        public string image { get; set; }
        public string first_date { get; set; }
        public string author { get; set; }
        public string price { get; set; }
        public string docs { get; set; }
        public string github { get; set; }
        public string license { get; set; }
        public string package { get; set; }
        public string version { get; set; }
        public string isCmdTool { get; set; }
        public string startUpFile { get; set; }
    }
    public class Platform
    {
        public string name { get; set; }
        public int platformid { get; set; }
        public string url { get; set; }
        public string version { get; set; }
        public string[] install_requires { get; set; }
        public string isCmdTool { get; set; }
        public string startUpFile { get; set; }
        public string isZipped { get; set; }
        public string installProcess { get; set; }
        public string isInstaller { get; set; }
    }
    public class App
    {
        public string disabled { get; set; }
        public string name { get; set; }
        public string description { get; set; }
        public string image { get; set; }
        public string first_date { get; set; }
        public string author { get; set; }
        public string type { get; set; }
        public string price { get; set; }
        public string docs { get; set; }
        public string github { get; set; }
        public string license { get; set; }
        public string package { get; set; }
        public Platform[] platform { get; set; }
    }
}