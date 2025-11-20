// ScheduleDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    // Получение расписания тренера
    public static List<TrainerSchedule> getTrainerSchedule(int trainerId) {
        List<TrainerSchedule> schedule = new ArrayList<>();
        String sql = "SELECT * FROM trainer_schedule WHERE trainer_id = ? ORDER BY day_of_week, start_time";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                schedule.add(new TrainerSchedule(
                        rs.getInt("id"),
                        rs.getInt("trainer_id"),
                        rs.getInt("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения расписания тренера: " + e.getMessage());
        }
        return schedule;
    }

    // Добавление временного слота
    public static boolean addTimeSlot(int trainerId, int dayOfWeek, String startTime, String endTime, boolean isAvailable) {
        // Проверяем, нет ли конфликтующего расписания
        if (hasScheduleConflict(trainerId, dayOfWeek, startTime, endTime)) {
            System.err.println("Обнаружен конфликт расписания");
            return false;
        }

        String sql = "INSERT INTO trainer_schedule (trainer_id, day_of_week, start_time, end_time, is_available) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            pstmt.setInt(2, dayOfWeek);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            pstmt.setBoolean(5, isAvailable);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка добавления временного слота: " + e.getMessage());
            return false;
        }
    }

    // Перегруженный метод для обратной совместимости
    public static boolean addTimeSlot(int trainerId, int dayOfWeek, String startTime, String endTime) {
        return addTimeSlot(trainerId, dayOfWeek, startTime, endTime, true);
    }

    // Обновление временного слота
    public static boolean updateTimeSlot(int scheduleId, int dayOfWeek, String startTime, String endTime, boolean isAvailable) {
        // Получаем текущий слот для проверки конфликтов
        TrainerSchedule currentSlot = getScheduleById(scheduleId);
        if (currentSlot == null) return false;

        // Проверяем конфликты, исключая текущий слот
        if (hasScheduleConflict(currentSlot.getTrainerId(), dayOfWeek, startTime, endTime, scheduleId)) {
            System.err.println("Обнаружен конфликт расписания при обновлении");
            return false;
        }

        String sql = "UPDATE trainer_schedule SET day_of_week = ?, start_time = ?, end_time = ?, is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dayOfWeek);
            pstmt.setString(2, startTime);
            pstmt.setString(3, endTime);
            pstmt.setBoolean(4, isAvailable);
            pstmt.setInt(5, scheduleId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления временного слота: " + e.getMessage());
            return false;
        }
    }

    // Удаление временного слота
    public static boolean deleteTimeSlot(int scheduleId) {
        // Проверяем, нет ли запланированных тренировок в этом слоте
        if (hasScheduledWorkouts(scheduleId)) {
            System.err.println("Нельзя удалить слот с запланированными тренировками");
            return false;
        }

        String sql = "DELETE FROM trainer_schedule WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, scheduleId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка удаления временного слота: " + e.getMessage());
            return false;
        }
    }

    // Получение слотa по ID
    public static TrainerSchedule getScheduleById(int scheduleId) {
        String sql = "SELECT * FROM trainer_schedule WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, scheduleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new TrainerSchedule(
                        rs.getInt("id"),
                        rs.getInt("trainer_id"),
                        rs.getInt("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения слотa по ID: " + e.getMessage());
        }
        return null;
    }

    // Проверка конфликтов расписания
    public static boolean hasScheduleConflict(int trainerId, int dayOfWeek, String startTime, String endTime) {
        return hasScheduleConflict(trainerId, dayOfWeek, startTime, endTime, -1);
    }

    public static boolean hasScheduleConflict(int trainerId, int dayOfWeek, String startTime, String endTime, int excludeScheduleId) {
        String sql = """
            SELECT id FROM trainer_schedule 
            WHERE trainer_id = ? AND day_of_week = ? 
            AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?))
            AND id != ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            pstmt.setInt(2, dayOfWeek);
            pstmt.setString(3, endTime);
            pstmt.setString(4, startTime);
            pstmt.setString(5, endTime);
            pstmt.setString(6, startTime);
            pstmt.setInt(7, excludeScheduleId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Ошибка проверки конфликтов расписания: " + e.getMessage());
            return true; // В случае ошибки считаем, что конфликт есть
        }
    }

    // Проверка наличия запланированных тренировок в слоте
    private static boolean hasScheduledWorkouts(int scheduleId) {
        // Получаем информацию о слоте
        TrainerSchedule slot = getScheduleById(scheduleId);
        if (slot == null) return false;

        // Упрощенная проверка - ищем любые запланированные тренировки у этого тренера
        String sql = """
            SELECT COUNT(*) as workout_count FROM workouts 
            WHERE trainer_id = ? AND status = 'scheduled'
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, slot.getTrainerId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("workout_count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка проверки тренировок в слоте: " + e.getMessage());
        }
        return false;
    }

    // Получение доступного времени тренера на конкретный день
    public static List<String> getAvailableTimeSlots(int trainerId, String date) {
        List<String> timeSlots = new ArrayList<>();

        // Определяем день недели для даты
        String sql = """
            SELECT start_time, end_time FROM trainer_schedule 
            WHERE trainer_id = ? AND day_of_week = ? AND is_available = TRUE
            ORDER BY start_time
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int dayOfWeek = getDayOfWeekFromDate(date);
            pstmt.setInt(1, trainerId);
            pstmt.setInt(2, dayOfWeek);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                timeSlots.add(startTime + " - " + endTime);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения доступного времени: " + e.getMessage());
        }
        return timeSlots;
    }

    // Вспомогательный метод для получения дня недели из даты
    private static int getDayOfWeekFromDate(String date) {
        try {
            // Простая реализация - в реальном приложении нужно использовать Date
            // Для демонстрации возвращаем понедельник (1)
            return 1;
        } catch (Exception e) {
            System.err.println("Ошибка определения дня недели: " + e.getMessage());
        }
        return 1; // По умолчанию понедельник
    }
}