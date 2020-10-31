package ru.geekbrains.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String password) throws SQLException {
        String sq1 = String.format("SELECT nickname, password FROM main WHERE login = '%s';", login);
        int myHash = password.hashCode();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sq1);
            if (rs.next()) {
                String nick = rs.getString(1);
                int dbHash = rs.getInt(2);
                if (myHash == dbHash) {
                    return nick;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException throwables) {

        }
    }

    public static void addUser(String login, String password, String nick) {
        String sq2 = String.format("INSERT INTO main (login, password, nickname) VALUES  ('%s', '%s', '%s');", login, password.hashCode(), nick);
        try {
            stmt.execute(sq2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void addMsg(String nick, String msg) {
        String sql = String.format("INSERT INTO messages (nick, message) VALUES  ('%s', '%s');", nick, msg);
        try {
            PreparedStatement pr = connection.prepareStatement(sql);
            pr.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<String> getAllNicks() throws SQLException {
        List<String>  nicks = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT nickname FROM main;";
        PreparedStatement pr = connection.prepareStatement(sql);
        rs = pr.executeQuery();
        while (rs.next()){
            nicks.add(rs.getString(1));
        }
        return nicks;
    }

    public List<UserMessage> getAllMessages() throws SQLException {
        List<UserMessage>  messages = new ArrayList<>();
        UserMessage message = null;
        ResultSet rs = null;
        String sql = "SELECT nick, message FROM messages;";
        PreparedStatement pr = connection.prepareStatement(sql);
        rs = pr.executeQuery();
        while (rs.next()){
            message = new UserMessage();
            message.setNick(rs.getString(1));
            message.setMessage(rs.getString(2));
            messages.add(message);
        }
        return messages;
    }
}
