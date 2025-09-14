using System;
using System.Collections.Generic;

namespace com.ares
{
    public static class Security
    {
        public static string forbiddenList = "/~$:;!?%*^`\\";
        public static char repalcementChar = '-';
        public static string Sanitize(string name)
        {
            foreach (char character in forbiddenList)
            {
                name = name.Replace(character, repalcementChar);
            }
            // remove all '..' to avoid parent directory access
            while (name.Contains(".."))
                name = name.Replace("..", repalcementChar + ".");
            return name;
        }
    }
}