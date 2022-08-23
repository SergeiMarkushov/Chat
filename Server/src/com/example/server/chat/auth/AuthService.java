package com.example.server.chat.auth;


import java.sql.*;

public class AuthService {

    private static Connection connection;
    private static Statement statement;

    private static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chatDB.db");
            statement = connection.createStatement();
        } catch (Exception e) {
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

    public static void newUser(String login, String password){
        try{
            connect();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, login);
            preparedStatement.execute();
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static String getUsernameByLoginAndPassword(String login, String password){
        String sql = String.format("SELECT nickname FROM users WHERE login='%s' and password='%s", login, password);
        try{
            connect();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                return resultSet.getString(4);
            }
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }



    public static void changeNickname (String nickname, String s){
        PreparedStatement preparedStatement = null;
        try{
            connect();
            preparedStatement = connection.prepareStatement("UPDATE users SET nickname=? WHERE nickname=?;");
            preparedStatement.setString(1,nickname);
            preparedStatement.setString(2,s);
            preparedStatement.execute();
            disconnect();
        }catch (SQLException e){
            throw new RuntimeException(e);
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
}