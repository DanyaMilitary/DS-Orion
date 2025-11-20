// ScheduleManager.java
import java.util.List;

public class ScheduleManager {
    private static ScheduleManager instance;

    private ScheduleManager() {}

    public static ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public ScheduleOperationResult addTimeSlot(int trainerId, int dayOfWeek, String startTime, String endTime, boolean isAvailable) {
        try {
            if (ScheduleDAO.addTimeSlot(trainerId, dayOfWeek, startTime, endTime, isAvailable)) {
                return ScheduleOperationResult.success("Временной слот успешно добавлен");
            } else {
                return ScheduleOperationResult.error("Ошибка добавления временного слота");
            }
        } catch (Exception e) {
            return ScheduleOperationResult.error("Ошибка: " + e.getMessage());
        }
    }

    public ScheduleOperationResult updateTimeSlot(int scheduleId, int dayOfWeek, String startTime, String endTime, boolean isAvailable) {
        try {
            if (ScheduleDAO.updateTimeSlot(scheduleId, dayOfWeek, startTime, endTime, isAvailable)) {
                return ScheduleOperationResult.success("Временной слот успешно обновлен");
            } else {
                return ScheduleOperationResult.error("Ошибка обновления временного слота");
            }
        } catch (Exception e) {
            return ScheduleOperationResult.error("Ошибка: " + e.getMessage());
        }
    }

    public ScheduleOperationResult deleteTimeSlot(int scheduleId) {
        try {
            if (ScheduleDAO.deleteTimeSlot(scheduleId)) {
                return ScheduleOperationResult.success("Временной слот успешно удален");
            } else {
                return ScheduleOperationResult.error("Ошибка удаления временного слота");
            }
        } catch (Exception e) {
            return ScheduleOperationResult.error("Ошибка: " + e.getMessage());
        }
    }

    public List<TrainerSchedule> getTrainerSchedule(int trainerId) {
        return ScheduleDAO.getTrainerSchedule(trainerId);
    }
}