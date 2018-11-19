/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv;

import br.com.loto.core.util.JdbcUtil;
import br.com.loto.crypto.EncriptaDecriptaRSA;
import br.com.loto.server.tv.thread.ServerThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwe
 */
public class MainApp {

    static Properties properties;

    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    private static void initConnection() {
        String dbUrl = properties.getProperty("db.connection.url");
        Properties props = new Properties();
        props.setProperty("user", properties.getProperty("db.connection.user"));
        props.setProperty("password", properties.getProperty("db.connection.pwd"));

//        String dbUrl = "jdbc:hsqldb:file:/home/mcavalli/hsqldb/dbloto";
//        Properties props = new Properties();
//        props.setProperty("user", "SA");
//        props.setProperty("password", "");
//        props.setProperty("ssl", "true");
        try {
            JdbcUtil.getInstance().init(dbUrl, props);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    public static void main(String args[]) {
        byte[] password = null;
        try {
            String configFilePath = System.getProperty("config.file");
            properties = new Properties();
            properties.load(new FileInputStream(new File(configFilePath)));

            byte[] bytes = Files.readAllBytes(new File(properties.getProperty("password.file")).toPath());

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(properties.getProperty("private.key")))) {
                PrivateKey chavePrivate = (PrivateKey) inputStream.readObject();
                password = EncriptaDecriptaRSA.decriptografaToBytes(bytes, chavePrivate);
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(0);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        initConnection();

        ServerSocket myServerSocket = null;
        boolean ServerOn = true;

        try {
            myServerSocket = new ServerSocket(Integer.parseInt(properties.getProperty("server.port")));
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "Could not create server socket on port {0}. Quitting.", properties.getProperty("server.port"));
            System.exit(-1);
        }

        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        LOG.log(Level.INFO, "Server is online {0}", formatter.format(now.getTime()));

        while (ServerOn) {
            try {
                Socket serverSocket = myServerSocket.accept();
                ServerThread serverThread = new ServerThread(serverSocket, password, properties);
                serverThread.start();
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, null, ioe);
            }
        }
        try {
            myServerSocket.close();
            LOG.log(Level.INFO, "Server is offline");
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, null, ioe);
            System.exit(-1);
        }

    }

}
