// WorkoutDAO.java (с добавлением новых методов)
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAO {

    public static boolean scheduleWorkout(int userId, int trainerId, String date, String time) {
        String sql = "INSERT INTO workouts(user_id, trainer_id, date, time) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, trainerId);
            pstmt.setString(3, date);
            pstmt.setString(4, time);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка записи на тренировку: " + e.getMessage());
            return false;
        }
    }

    public static List<Workout> getUserWorkouts(int userId) {
        List<Workout> workouts = new ArrayList<>();
        String sql = """
            SELECT w.*, t.name as trainer_name 
            FROM workouts w 
            JOIN trainers t ON w.trainer_id = t.id 
            WHERE w.user_id = ? 
            ORDER BY w.date DESC, w.time DESC
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                workouts.add(new Workout(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("trainer_id"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getInt("duration"),
                        rs.getString("status"),
                        rs.getString("trainer_name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения тренировок пользователя: " + e.getMessage());
        }
        return workouts;
    }

    public static List<Workout> getTrainerWorkouts(int trainerId) {
        List<Workout> workouts = new ArrayList<>();
        String sql = """
            SELECT w.*, u.name as user_name 
            FROM workouts w 
            JOIN users u ON w.user_id = u.id 
            WHERE w.trainer_id = ? 
            ORDER BY w.date DESC, w.time DESC
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Workout workout = new Workout(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("trainer_id"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getInt("duration"),
                        rs.getString("status"),
                        rs.getString("user_name"), // Здесь будет имя клиента
                        true // Флаг что это для тренера
                );
                // Устанавливаем дополнительные поля
                workout.setCancelReason(rs.getString("cancel_reason"));
                workout.setNotes(rs.getString("notes"));
                workout.setUserRating(rs.getInt("user_rating"));
                if (rs.wasNull()) workout.setUserRating(null);
                workout.setUserFeedback(rs.getString("user_feedback"));

                workouts.add(workout);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения тренировок тренера: " + e.getMessage());
        }
        return workouts;
    }

    // Отмена тренировки пользователем
    public static boolean cancelWorkout(int workoutId, String reason) {
        String sql = "UPDATE workouts SET status = 'cancelled', cancel_reason = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reason);
            pstmt.setInt(2, workoutId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка отмены тренировки: " + e.getMessage());
            return false;
        }
    }

    // Отмена тренировки тренером
    public static boolean cancelWorkoutByTrainer(int workoutId, String reason) {
        String sql = "UPDATE workouts SET status = 'cancelled', cancel_reason = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "Тренер: " + reason);
            pstmt.setInt(2, workoutId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка отмены тренировки тренером: " + e.getMessage());
            return false;
        }
    }

    // Завершение тренировки тренером
    public static boolean completeWorkout(int workoutId, String notes) {
        String sql = "UPDATE workouts SET status = 'completed', notes = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (notes != null && !notes.trim().isEmpty()) {
                pstmt.setString(1, notes);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }
            pstmt.setInt(2, workoutId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка завершения тренировки: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateWorkout(int workoutId, String date, String time) {
        String sql = "UPDATE workouts SET date = ?, time = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            pstmt.setString(2, time);
            pstmt.setInt(3, workoutId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления тренировки: " + e.getMessage());
            return false;
        }
    }

    // Упрощенная проверка доступности времени
    public static boolean isTimeSlotAvailable(int trainerId, String date, String time) {
        String sql = "SELECT COUNT(*) as count FROM workouts WHERE trainer_id = ? AND date = ? AND time = ? AND status = 'scheduled'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") == 0;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка проверки доступности времени: " + e.getMessage());
        }
        return false;
    }
}