package ru.geekbrains.server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    public static void addMsg(String nick, String msg) {
        String sql = String.format("INSERT INTO messages (nick, message) VALUES  ('%s', '%s');", nick, msg);
        try {
            PreparedStatement pr = DBService.getConnection().prepareStatement(sql);
            pr.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static List<UserMessage> getAllMessages() throws SQLException {
        List<UserMessage>  messages = new ArrayList<>();
        UserMessage message = null;
        ResultSet rs = null;
        String sql = "SELECT nick, message FROM messages;";
        PreparedStatement pr =  DBService.getConnection().prepareStatement(sql);
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
