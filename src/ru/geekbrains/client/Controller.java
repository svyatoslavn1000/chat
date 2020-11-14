package ru.geekbrains.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    ListView<Object> listView;

    @FXML
    TextField textField;

    @FXML
    Button buttonSubmit;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    TextField loginFieldReg;

    @FXML
    PasswordField passwordFieldReg1;

    @FXML
    PasswordField passwordFieldReg2;

    @FXML
    TextField nickFieldReg;

    @FXML
    ListView<String> clientsList;

    Socket socket;
    DataInputStream in;

    DataOutputStream out;

    private boolean isAuthorized;
    String nick = null;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    List<TextArea> textAreas;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        upperPanel.setVisible(!isAuthorized);
        upperPanel.setManaged(!isAuthorized);
        bottomPanel.setVisible(isAuthorized);
        bottomPanel.setManaged(isAuthorized);
        clientsList.setVisible(isAuthorized);
        clientsList.setManaged(isAuthorized);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
        textAreas = new ArrayList<>();
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread(() -> {

                    try {
                        while (true) {
                            String msg = in.readUTF();
                            String[] str = msg.split(" ");
                            if (str[0].equals("/authok")) {
                                setAuthorized(true);
                                nick = str[1];
                                break;
                            } else {
                                Platform.runLater(() -> listView.getItems().add(msg + "\n"));
                            }
                        }
                        while (true) {
                            String str = in.readUTF();
                            if(str.startsWith("/")){
                                if (str.equals("/serverClosed")) {
                                    break;
                                }
                                if (str.startsWith("/clientlist")) {
                                    String[] tokens = str.split(" ");
                                    Platform.runLater(() -> {
                                    clientsList.getItems().clear();
                                    for(int i = 1; i < tokens.length; i++){
                                        clientsList.getItems().add(tokens[i]);
                                    }});
                                }
                            }else{
                                String nickIn = str.split(" ")[0];
                                Platform.runLater(() -> {
                                    Label msg = new Label(str);
                                    VBox msgBox = new VBox(msg);
                                    if (nickIn.equals(nick)) {
                                        msgBox.setAlignment(Pos.CENTER_RIGHT);
                                        msg.getStyleClass().add("chat-bubble1");
                                    }
                                    msg.getStyleClass().add("chat-bubble");
                                    listView.getItems().add(msgBox);
                                    textField.clear();
                                    textField.requestFocus();
                                });
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void sendMsg() throws IOException {
        out.writeUTF(textField.getText());
        textField.clear();
        textField.requestFocus();
    }

    public void tryToAuth(ActionEvent actionEvent) {

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToReg(ActionEvent actionEvent) {

        if (socket == null || socket.isClosed()) {
            connect();
        }
        if(passwordFieldReg1.getText().equals(passwordFieldReg2.getText())) {
            try {
                out.writeUTF("/registration " + loginFieldReg.getText() + " "
                        + passwordFieldReg1.getText() + " " + nickFieldReg.getText());
                loginFieldReg.clear();
                passwordFieldReg1.clear();
                passwordFieldReg2.clear();
                nickFieldReg.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Значения полей Пароль и Подтвердить должны совпадать");
            alert.showAndWait();
        }
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            MiniStage ms = new MiniStage(clientsList.getSelectionModel().getSelectedItem(), out, textAreas);
            ms.show();
        }
    }
}
