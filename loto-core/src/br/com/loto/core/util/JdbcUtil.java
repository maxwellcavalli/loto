/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author maxwe
 */
public class JdbcUtil {

    private static JdbcUtil instance;
    private static Connection conn;

    public static JdbcUtil getInstance() {
        if (instance == null) {
            instance = new JdbcUtil();
        }

        return instance;
    }

    public void init(String url, Properties props) throws SQLException {
//        Properties props = new Properties()
//        props.setProperty("user", "fred");
//        props.setProperty("password", "secret");
//        props.setProperty("ssl", "true");
        conn = DriverManager.getConnection(url, props);
        conn.setAutoCommit(false);
    }

    public Connection getConnection() throws SQLException {
        return conn;
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
    
    public void commit() throws SQLException{
        conn.commit();
    }
    
    public void rollback() throws SQLException{
        conn.rollback();
    }
    
}
