// UserDAO.java
import java.sql.*;

public class UserDAO {

    public static boolean registerUser(String username, String password, String role, String name) {
        String sql = "INSERT INTO users(username, password, role, name) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setString(4, name);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка входа: " + e.getMessage());
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователя: " + e.getMessage());
        }
        return null;
    }
}