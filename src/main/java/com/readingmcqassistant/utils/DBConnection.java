package com.readingmcqassistant.utils;

import java.sql.*;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/reading_mcq_assistant" +
        "?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
