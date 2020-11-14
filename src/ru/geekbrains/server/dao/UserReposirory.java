package ru.geekbrains.server.dao;

import java.awt.image.DataBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserReposirory {

    public static String getNickByLoginAndPass(String login, String password) throws SQLException {
        String sql = String.format("SELECT nickname, password FROM main WHERE login = '%s';", login);
        int myHash = password.hashCode();
        ResultSet rs = null;
        try {
            rs = DBService.getStmt().executeQuery(sql);
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

    public static List<String> getAllNicks() throws SQLException {
        List<String>  nicks = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT nickname FROM main;";
        PreparedStatement pr = DBService.getConnection().prepareStatement(sql);
        rs = pr.executeQuery();
        while (rs.next()){
            nicks.add(rs.getString(1));
        }
        pr.close();
        return nicks;
    }

    public static void addUser(String login, String password, String nick) {
        String sq2 = String.format("INSERT INTO main (login, password, nickname) VALUES  ('%s', '%s', '%s');", login, password.hashCode(), nick);
        try {
            DBService.getStmt().execute(sq2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
