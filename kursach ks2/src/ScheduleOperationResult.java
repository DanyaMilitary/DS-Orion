// ScheduleOperationResult.java
public class ScheduleOperationResult {
    private final boolean success;
    private final String message;
    private final TrainerSchedule schedule;
    private final String errorCode;

    private ScheduleOperationResult(boolean success, String message, TrainerSchedule schedule, String errorCode) {
        this.success = success;
        this.message = message;
        this.schedule = schedule;
        this.errorCode = errorCode;
    }

    // Фабричные методы для успеха
    public static ScheduleOperationResult success(TrainerSchedule schedule, String message) {
        return new ScheduleOperationResult(true, message, schedule, null);
    }

    public static ScheduleOperationResult success(String message) {
        return new ScheduleOperationResult(true, message, null, null);
    }

    // Фабричные методы для ошибок
    public static ScheduleOperationResult error(String message) {
        return new ScheduleOperationResult(false, message, null, "GENERAL_ERROR");
    }

    public static ScheduleOperationResult error(String message, String errorCode) {
        return new ScheduleOperationResult(false, message, null, errorCode);
    }

    // Геттеры
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public TrainerSchedule getSchedule() { return schedule; }
    public String getErrorCode() { return errorCode; }

    @Override
    public String toString() {
        return String.format("ScheduleOperationResult{success=%s, message='%s', errorCode='%s'}",
                success, message, errorCode);
    }
}