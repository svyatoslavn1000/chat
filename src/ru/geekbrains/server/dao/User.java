package ru.geekbrains.server.dao;

import java.io.Serializable;

public class User implements Serializable {
    Integer id;
    String login;
    String password;
    String nick;

    public User() {
    }

    public User(Integer id, String login, String password, String nick) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Override
    public boolean equals(Object other) {
        return (other instanceof User) && (id != null)
                ? id.equals(((User) other).id)
                : (other == this);
    }

    @Override
    public int hashCode() {
        return (id != null)
                ? (this.getClass().hashCode() + id.hashCode())
                : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("User[id=%d,login=%s,nick=%s]",
                id, login, nick);
    }
}
