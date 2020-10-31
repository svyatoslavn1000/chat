package ru.geekbrains.server;

import java.io.Serializable;

public class User implements Serializable {
    int id;
    String login;
    String password;
    String nick;

    public User() {
    }

    public User(int id, String login, String password, String nick) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
