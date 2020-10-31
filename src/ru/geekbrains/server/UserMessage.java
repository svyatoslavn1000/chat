package ru.geekbrains.server;

public class UserMessage {
    private String nick;
    private String message;

    public UserMessage() {
    }

    public UserMessage(String nick, String message) {
        this.nick = nick;
        this.message = message;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString(){return (nick + " " + message); }
}
