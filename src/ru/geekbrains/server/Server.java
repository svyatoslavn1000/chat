package ru.geekbrains.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;


public class Server {

    private final Vector<ClientHandler> clients;
    DBService DBService;

    public Server() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;
        this.DBService = new DBService();

        try {
            DBService.connect();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");


            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DBService.disconnect();
        }
    }

    public void broadcastMsg(ClientHandler from, String msg) {
        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsg(msg);
            }
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    public void unSubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastClientList();
    }

    public boolean isNickUsed(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNickBizy(String nick) throws SQLException {
        List<String> nicks = DBService.getAllNicks();
        for(String o: nicks){
            if(o.equals(nick)){
                return true;
            }
        }
        return false;
    }

    public void sendPersonalMessage(ClientHandler from, String nickTo, String msg) {
        if (from.nick.equals(nickTo)) {
            from.sendMsg("Не спамьте в чат!");
            return;
        }
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден");
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist");
        for (ClientHandler o : clients) {
            sb.append(" " + o.getNick());
        }
        String out = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }
}

