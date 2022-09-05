package com.example.server.chat.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class PersistentDbAuthService implements InterfaceAuthService{

    private static final Logger LOGGER = LogManager.getLogger(PersistentDbAuthService.class);

    private static final String DB = "jdbc:sqlite:chatUsers.db";
    private Connection connection;
    private PreparedStatement getUsernameStatement;
    private PreparedStatement updateUsernameStatement;


    @Override
    public void start() {
        try {
            LOGGER.info("Creating DB connection....");
            connection = DriverManager.getConnection(DB);
            LOGGER.info("DB connection successfully");
            getUsernameStatement = createGetUsernameStatement();
            updateUsernameStatement =createUpdateUsernameStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOGGER.error("Filed to connect to DB {}", DB);
            throw new RuntimeException("Filed to start auth service");
        }
    }


    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        String username = null;
        try{
            getUsernameStatement.setString(1,login);
            getUsernameStatement.setString(2,password);
            ResultSet resultSet = getUsernameStatement.executeQuery();
            while (resultSet.next()){
                username = resultSet.getString("username");
                break;
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOGGER.error("Filed to fetch username from DB");
        }
        return username;
    }

    @Override
    public void stop() {
        if(connection != null){
            try{
                LOGGER.info("Closing connection");
                connection.close();
                LOGGER.info("Connection is closed");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LOGGER.error("Filed to close connection");
                throw new RuntimeException("Filed to stop auth service");
            }
        }
    }

    @Override
    public void updateUsername(String currentUsername, String newUsername) {
        try{
            updateUsernameStatement.setString(1, newUsername);
            updateUsernameStatement.setString(2,currentUsername);
            int result = updateUsernameStatement.executeUpdate();
            LOGGER.info("Update username. Update rows {}", result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOGGER.error("Filed to update username. Current name: {}; New username: {}%n", currentUsername, newUsername);
        }

    }


    private PreparedStatement createUpdateUsernameStatement() throws SQLException {
        return connection.prepareStatement("UPDATE users SET username = ? WHERE username = ? ");
    }

    private PreparedStatement createGetUsernameStatement() throws SQLException {
        return connection.prepareStatement("SELECT username FROM users WHERE login = ? AND password = ? ");
    }
}
