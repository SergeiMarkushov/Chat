package com.example.server.chat.auth;


import java.util.Set;

public class AuthService implements InterfaceAuthService {

    private static Set<User> USERS = Set.of(
            new User("login1", "pass1", "username1"),
            new User("login2", "pass2", "username2"),
            new User("login3", "pass3", "username3")
    );

    public String getUsernameByLoginAndPassword(String login, String password) {
        User requiredUser = new User(login, password);
        for (User user : USERS) {
            if (requiredUser.equals(user)) {
                return user.getUsername();
            }
        }

        return null;
    }

    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        User requireUser = new User(login, password);
        for (User user : USERS) {
            if (requireUser.equals(user)){
                return user.getUsername();
            }
        }
        return null;
    }

    @Override
    public void updateUsername(String currentUsername, String newUsername) {
        User user = getUserByUsername(currentUsername);
        if (user != null) {
            user.setUsername(newUsername);
        }
    }

    private User getUserByUsername(String username) {
        for (User user : USERS) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

}