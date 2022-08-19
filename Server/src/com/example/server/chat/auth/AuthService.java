package com.example.server.chat.auth;


import java.sql.*;

public class AuthService {

    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
    private static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void newUser(String login, String password, String username){
        connect();
        String sql = String.format("INSERT INTO users (login, password, nickname) VALUES ('%s', '%d', '%s')", login, password, username);

        try {
            boolean rs = statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getNickByLoginAndPass(String login, String password) {
        String sql = String.format("SELECT nickname FROM users where login = '%s' and password = '%s'", login, password);

        try {
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                return rs.getString(4);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



}
//    private static Set<User> USERS = Set.of(
//            new User("login1", "pass1", "username1"),
//            new User("login2", "pass2", "username2"),
//            new User("login3", "pass3", "username3")
//    );
//
//    public String getUsernameByLoginAndPassword(String login, String password) {
//        User requiredUser = new User(login, password);
//        for (User user : USERS) {
//            if (requiredUser.equals(user)) {
//                return user.getUsername();
//            }
//        }
//
//        return null;
//    }
//}