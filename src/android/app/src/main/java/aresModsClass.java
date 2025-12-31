package com.ares;

public class aresModsClass {
    // Declare the native method
    public native int memInjector(String pid2, String address2, String type2, String value2);

    // Load the library
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("4re5mods");
    }
}
