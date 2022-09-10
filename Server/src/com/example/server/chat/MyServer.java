package com.example.server.chat;

import com.example.command.Command;
import com.example.server.chat.auth.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class MyServer {

    private static final Logger LOGGER = LogManager.getLogger(MyServer.class);

    private AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server has been started");
            authService = new AuthService();
            while (true) {
                waitAndProcessClientConnection(serverSocket);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to bind port {}", port);
        }
    }

    private void waitAndProcessClientConnection(ServerSocket serverSocket) throws IOException {
        LOGGER.info("Waiting for new client connection");
        Socket clientSocket = serverSocket.accept();
        LOGGER.info("Client has been connected");
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendCommand(Command.clientMessageCommand(sender.getUserName(), message));
            }
        }
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String recipient, String privateMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender && client.getUserName().equals(recipient)) {
                client.sendCommand(Command.clientMessageCommand(sender.getUserName(), privateMessage));
            }
        }
    }

    public void notifyUserListUpdated() throws IOException {
        List<String> users = new ArrayList<>();
        for (ClientHandler client : clients) {
            users.add(client.getUserName());
        }

        for (ClientHandler client : clients) {
            client.sendCommand(Command.updateUserListCommand(users));
        }
    }

    public synchronized boolean isUserNameBusy(String userName) {
        for (ClientHandler client : clients) {
            if (client.getUserName().equals(userName)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        notifyUserListUpdated();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        notifyUserListUpdated();
    }

    public AuthService getAuthService() {
        return authService;
    }
}