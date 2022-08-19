package com.example.server.chat.auth;

import java.sql.*;

public class NickService {

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

        public boolean changeNick(String currentNick, String newNick) {
            try {
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE users SET nickname = ? WHERE nickname = ?");
                st.setString(1, newNick);
                st.setString(2, currentNick);
                st.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
}
