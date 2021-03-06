package ru.geekbrains.server;

import ru.geekbrains.server.dao.DBService;
import ru.geekbrains.server.dao.MessageRepository;
import ru.geekbrains.server.dao.UserMessage;
import ru.geekbrains.server.dao.UserReposirory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    Socket socket = null;
    DataInputStream in;
    DataOutputStream out;
    Server server;
    String nick;
    ArrayList<String> blacklist;
    ru.geekbrains.server.dao.DBService DBService;
    Boolean isAuthorized = false;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.DBService = new DBService();
            this.blacklist = new ArrayList<>();

            Thread thread = new Thread(() -> {
                try {
                    while (!isAuthorized) {
                        String str = in.readUTF();
                        if (str.startsWith("/registration")) {
                            registration(str);
                        }
                        else if (str.startsWith("/auth")) {
                            isAuthorized = authorizationOk(str);
                        }else{
                            sendMsg("Пожалуйста, авторизуйтесь или пройдите регистрацию.");
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                server.broadcastMsg(ClientHandler.this, "Пользователь " + nick + " покинул чат");
                                out.writeUTF("/serverClosed");
                            }
                            if (str.startsWith("/w")) {
                                String[] tokens = str.split(" ", 3);
                                server.sendPersonalMessage(ClientHandler.this, tokens[1], tokens[2]);
                            }
                            if (str.startsWith("/blacklist ")) {
                                String[] tokens = str.split(" ");
                                blacklist.add(tokens[1]);
                                sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                            }
                        } else {
                            server.broadcastMsg(ClientHandler.this, nick + " : " + str);
                            MessageRepository.addMsg(nick, str);
                        }
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unSubscribe(ClientHandler.this);
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean checkBlackList(String nick) {
        return blacklist.contains(nick);
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registration(String registrationMessage) throws SQLException {
        String[] tokens = registrationMessage.split(" ", 4);
        if(server.isNickBizy(tokens[3])){
            System.out.println(tokens[3]);
            sendMsg("Ник "  + tokens[3] + " уже используется" );
        }else{
            UserReposirory.addUser(tokens[1],tokens[2],tokens[3]);
            sendMsg("Регистрация прошла успешно");}
    }

    private boolean authorizationOk(String authMessage) throws SQLException {
        String[] tokens = authMessage.split(" ");
        String newNick = UserReposirory.getNickByLoginAndPass(tokens[1], tokens[2]);
        if (newNick != null) {
            if (!server.isNickUsed(newNick)) {
                sendMsg("/authok" + " " + newNick);
                nick = newNick;
                server.subscribe(ClientHandler.this);
                server.broadcastMsg(ClientHandler.this, "Пользователь " + nick + " вошел в чат");
                List<UserMessage> messages = MessageRepository.getAllMessages();
                for(UserMessage o: messages){
                    sendMsg(o.getNick() + " : " + o.getMessage());
                    System.out.println(o.getNick() + " : " + o.getMessage());
                    return true;
                }
            } else {
                sendMsg("Учетная запись " + newNick + " уже используется");
                return false;
            }
        } else {
            sendMsg("неверный логин/пароль");
            return false;
        }
        return false;
    }
}
