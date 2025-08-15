package com.ares;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class RootExec {
    public static boolean canRunRootCommands() {
        boolean ret = false;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();

            int exitCode = suProcess.waitFor();
            if (exitCode != 255) {
                ret = true;
            } else {
                ret = false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    public static String runCommand(String command) {
        StringBuilder output = new StringBuilder();
        Process suProcess;
        DataOutputStream os;
        BufferedReader reader;

        try {
            suProcess = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(suProcess.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));

            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            suProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
