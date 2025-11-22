// TrainerDAO.java - добавляем метод поиска тренеров по специализации
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDAO {

    public static boolean addTrainer(String name, String specialization, int experience, String description) {
        String sql = "INSERT INTO trainers(name, specialization, experience, description) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, specialization);
            pstmt.setInt(3, experience);
            pstmt.setString(4, description);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка добавления тренера: " + e.getMessage());
            return false;
        }
    }

    // Новый метод: создание тренера при регистрации пользователя-тренера
    public static boolean createTrainerFromUser(int userId, String name, String specialization) {
        String sql = "INSERT INTO trainers(user_id, name, specialization, experience, description) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, specialization);
            pstmt.setInt(4, 0); // Опыт по умолчанию
            pstmt.setString(5, "Новый тренер"); // Описание по умолчанию

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка создания тренера из пользователя: " + e.getMessage());
            return false;
        }
    }

    // Новый метод: получение тренера по ID пользователя
    public static Trainer getTrainerByUserId(int userId) {
        String sql = "SELECT * FROM trainers WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Trainer(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("experience"),
                        rs.getString("description"),
                        rs.getDouble("rating"),
                        rs.getBoolean("is_active")
                );
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения тренера по user_id: " + e.getMessage());
        }
        return null;
    }

    public static List<Trainer> getAllTrainers() {
        List<Trainer> trainers = new ArrayList<>();
        String sql = "SELECT * FROM trainers WHERE is_active = TRUE ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trainers.add(new Trainer(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("experience"),
                        rs.getString("description"),
                        rs.getDouble("rating"),
                        rs.getBoolean("is_active")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения тренеров: " + e.getMessage());
        }
        return trainers;
    }

    // Новый метод: поиск тренеров по специализации
    public static List<Trainer> searchTrainersBySpecialization(String specialization) {
        List<Trainer> trainers = new ArrayList<>();
        String sql = "SELECT * FROM trainers WHERE is_active = TRUE AND specialization LIKE ? ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + specialization + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                trainers.add(new Trainer(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("experience"),
                        rs.getString("description"),
                        rs.getDouble("rating"),
                        rs.getBoolean("is_active")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска тренеров по специализации: " + e.getMessage());
        }
        return trainers;
    }

    // Новый метод: поиск тренеров по имени
    public static List<Trainer> searchTrainersByName(String name) {
        List<Trainer> trainers = new ArrayList<>();
        String sql = "SELECT * FROM trainers WHERE is_active = TRUE AND name LIKE ? ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                trainers.add(new Trainer(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("experience"),
                        rs.getString("description"),
                        rs.getDouble("rating"),
                        rs.getBoolean("is_active")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска тренеров по имени: " + e.getMessage());
        }
        return trainers;
    }

    public static Trainer getTrainerById(int trainerId) {
        String sql = "SELECT * FROM trainers WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Trainer(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("experience"),
                        rs.getString("description"),
                        rs.getDouble("rating"),
                        rs.getBoolean("is_active")
                );
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения тренера: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateTrainerProfile(int trainerId, String specialization, Integer experience, String description) {
        String sql = "UPDATE trainers SET specialization = ?, experience = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, specialization);
            if (experience != null) {
                pstmt.setInt(2, experience);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, description);
            pstmt.setInt(4, trainerId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления профиля тренера: " + e.getMessage());
            return false;
        }
    }

    public static boolean addWorkoutFeedback(int workoutId, int rating, String feedback) {
        String sql = "UPDATE workouts SET user_rating = ?, user_feedback = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rating);
            pstmt.setString(2, feedback);
            pstmt.setInt(3, workoutId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка добавления отзыва: " + e.getMessage());
            return false;
        }
    }
}