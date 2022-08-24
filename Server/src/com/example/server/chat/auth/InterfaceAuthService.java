package com.example.server.chat.auth;

public interface InterfaceAuthService {
    default void start(){
    }

    String getUserNameByLoginAndPassword(String login, String password);

    default void stop(){
    }

    void updateUsername(String currentUsername, String newUsername);
}
