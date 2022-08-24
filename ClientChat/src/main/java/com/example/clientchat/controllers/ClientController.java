package com.example.clientchat.controllers;

import com.example.clientchat.ClientChat;
import com.example.clientchat.dialogs.Dialogs;
import com.example.clientchat.model.Network;
import com.example.clientchat.model.ReadMessageListener;
import com.example.clientchat.service.ChatHistory;
import com.example.command.Command;
import com.example.command.CommandType;
import com.example.command.commands.commands.ClientMessageCommandData;
import com.example.command.commands.commands.UpdateUserListCommandData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Optional;

public class ClientController {

    private static final int LAST_HISTORY_ROWS_NUMBER = 100;


    @FXML
    public TextField messageTextArea;

    @FXML
    public TextArea chatTextArea;

    @FXML
    public ListView<String> userList;

    @FXML
    public Button sendMassageButton;

    private ClientChat application;
    private ChatHistory chatHistoryService;

    public void sendMessage() {
        String message = messageTextArea.getText().trim();

        if (message.isEmpty()) {
            messageTextArea.clear();
            return;
        }

        String sender = null;
        if (!userList.getSelectionModel().isEmpty()) {
            sender = userList.getSelectionModel().getSelectedItem().toString();
        }

        try {
            if (sender != null) {
                Network.getInstance().sendPrivateMessage(sender, message);
            } else {
                Network.getInstance().sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
//            Dialogs.NetworkError.SEND_MESSAGE.show();
            application.showErrorDialog("Error sending data by network");
        }

        appendMessageToChat("Я", message);
        requestFocusForTextArea();
        messageTextArea.clear();
    }

    public void createChatHistory(){
        this.chatHistoryService = new ChatHistory(Network.getInstance().getCurrentUsername());
    }

    public void appendMessageToChat(String sender, String message) {
        String currentText = chatTextArea.getText();

        chatTextArea.appendText(DateFormat.getInstance().format(new Date()));
        chatTextArea.appendText(System.lineSeparator());

        if (sender != null) {
            chatTextArea.appendText(sender + ":");
            chatTextArea.appendText(System.lineSeparator());
        }

        chatTextArea.appendText(message);
        chatTextArea.appendText(System.lineSeparator());
        chatTextArea.appendText(System.lineSeparator());
        chatTextArea.setFocusTraversable(true);
        chatTextArea.clear();

        String newMessage = chatTextArea.getText(currentText.length(), chatTextArea.getLength());
        chatHistoryService.appendText(newMessage);


    }

    public void setApplication(ClientChat application){
        this.application = application;
    }

    private void requestFocusForTextArea() {
        Platform.runLater(() -> messageTextArea.requestFocus());
    }

    public void initializeMessageHandler() {
        Network.getInstance().addReadMessageListener(new ReadMessageListener() {
            @Override
            public void processReceivedCommand(Command command) {
                if(chatHistoryService == null){
                    createChatHistory();
                    loadChatHistory();
                }
                if (command.getType() == CommandType.CLIENT_MESSAGE) {
                    ClientMessageCommandData data = (ClientMessageCommandData) command.getData();
                    appendMessageToChat(data.getSender(), data.getMessage());
                } else if (command.getType() == CommandType.UPDATE_USERS_LIST) {
                    UpdateUserListCommandData data = (UpdateUserListCommandData) command.getData();
                    Platform.runLater(() -> {
                        userList.setItems(FXCollections.observableArrayList(data.getUsers()));
                    });
                }

            }
        });
    }
    public void changeUserName (ActionEvent actionEvent){
        TextInputDialog editDialog = new TextInputDialog();
        editDialog.setTitle("Change nickname");
        editDialog.setHeaderText("Enter new username");
        editDialog.setContentText("Username: ");

        Optional<String> result = editDialog.showAndWait();
        if (result.isPresent()){
            try{
                Network.getInstance().changeUsername(result.get());
            } catch (IOException e) {
                e.printStackTrace();
                Dialogs.NetworkError.SEND_MESSAGE.show();
            }
        }
    }

    private void loadChatHistory(){
        String rows = chatHistoryService.loadLastRows(LAST_HISTORY_ROWS_NUMBER);
        chatTextArea.clear();
        chatTextArea.setText(rows);
    }

    public void about (ActionEvent actionEvent){
        Dialogs.AboutDialog.INFO.show();
    }

    public void closeChat(ActionEvent actionEvent){
        chatHistoryService.close();
        ClientChat.INSTANCE.getChatStage().close();
    }
}