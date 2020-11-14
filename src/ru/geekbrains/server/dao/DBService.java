package ru.geekbrains.server.dao;

import ru.geekbrains.server.dao.UserMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBService {

    private static Connection connection;
    private static Statement stmt;

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        DBService.connection = connection;
    }

    public static Statement getStmt() {
        return stmt;
    }

    public static void setStmt(Statement stmt) {
        DBService.stmt = stmt;
    }

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwables) {

        }
    }
}
