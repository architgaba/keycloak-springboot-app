package com.dotcms.master_control.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static String urlString;
    private static String driverName = "org.postgresql.Driver";
    private static String username ;
    private static String password ;
    private static Connection con;

    public static Connection getConnection(String urlString,String username,String password) {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(urlString, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
        return con;
    }
}