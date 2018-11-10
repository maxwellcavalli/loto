/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author maxwe
 */
public class SerialUtil {

    public static String getSerial() throws IOException {

        //Process pid = Runtime.getRuntime().exec("wmic cpu get processorId");
        Process pid = Runtime.getRuntime().exec("wmic diskdrive get serialnumber");
        BufferedReader in = new BufferedReader(new InputStreamReader(pid.getInputStream()));
        String line = "";
        while (in.read() > 0) {
            line += in.readLine();
        }
        
        line = line.substring(line.indexOf(" "), line.length());
        line = line.replaceAll(" ", "");

        return line;
    }

}
