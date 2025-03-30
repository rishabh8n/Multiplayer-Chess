package controller;

import model.User;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    public static boolean registerUser(String username, String password) {
        try{
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement st = con.prepareStatement("insert into users(username, password) values(?,?)");
            st.setString(1, username);
            st.setString(2, password);
           return st.executeUpdate() > 0;
        }catch (SQLException e) {
            System.out.println("Error registering user");
            return false;
        }
    }

    public static User authenticateUser(String username, String password) {
        System.out.println("Authenticating user " + username+ " with password " + password);
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement st = con.prepareStatement("select * from users where username = ? and password = ?");
            st.setString(1,username);
            st.setString(2,password);
            ResultSet rs = st.executeQuery();
//            System.out.println("Execution Status: "+rs.next());
            if(rs.next()) {
                System.out.println(rs.getInt("id"));
                User authenticatedUser = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("games_played"),
                        rs.getInt("games_won")
                );
                System.out.println("User authenticated: "+authenticatedUser.getUsername());
                return authenticatedUser;
            }
            return null;
        }catch (SQLException e) {
            System.out.println("Error authenticating user");
            return null;
        }
    }

    public static boolean updateGameStats(int userId, boolean won) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(
                     "update users set games_played = games_played + 1, games_won = games_won + ? where id = ?")) {

            st.setInt(1, won ? 1 : 0);
            st.setInt(2, userId);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating game stats: " + e.getMessage());
            return false;
        }
    }
}
