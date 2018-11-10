/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv;

import br.com.loto.core.util.JdbcUtil;
import br.com.loto.server.tv.thread.ServerThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;

/**
 *
 * @author maxwe
 */
public class MainApp {

    private static void initConnection() {
        String dbUrl = "jdbc:postgresql://localhost/loto";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "apollo");

//        String dbUrl = "jdbc:hsqldb:file:/home/mcavalli/hsqldb/dbloto";
//        Properties props = new Properties();
//        props.setProperty("user", "SA");
//        props.setProperty("password", "");
//        props.setProperty("ssl", "true");
        try {
            JdbcUtil.getInstance().init(dbUrl, props);
        } catch (SQLException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        initConnection();

        ServerSocket myServerSocket = null;
        boolean ServerOn = true;

        try {
            myServerSocket = new ServerSocket(12033);
        } catch (IOException ioe) {
            System.out.println("Could not create server socket on port 8888. Quitting.");
            System.exit(-1);
        }

        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        System.out.println("It is now : " + formatter.format(now.getTime()));

        while (ServerOn) {
            try {
                Socket serverSocket = myServerSocket.accept();
                ServerThread serverThread = new ServerThread(serverSocket);
                serverThread.start();
            } catch (IOException ioe) {
                System.out.println("Exception found on accept. Ignoring. Stack Trace :");
                ioe.printStackTrace();
            }
        }
        try {
            myServerSocket.close();
            System.out.println("Server Stopped");
        } catch (Exception ioe) {
            System.out.println("Error Found stopping server socket");
            System.exit(-1);
        }

    }

}
