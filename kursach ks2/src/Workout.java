// Workout.java
public class Workout {
    private int id;
    private int userId;
    private int trainerId;
    private String date;
    private String time;
    private int duration;
    private String status;
    private String cancelReason;
    private String notes;
    private Integer userRating;
    private String userFeedback;
    private String trainerName;
    private String userName; // Добавляем поле для имени пользователя

    // Базовый конструктор
    public Workout(int id, int userId, int trainerId, String date, String time) {
        this.id = id;
        this.userId = userId;
        this.trainerId = trainerId;
        this.date = date;
        this.time = time;
        this.duration = 60;
        this.status = "scheduled";
    }

    // Конструктор для тренировок пользователя (с именем тренера)
    public Workout(int id, int userId, int trainerId, String date, String time,
                   int duration, String status, String trainerName) {
        this.id = id;
        this.userId = userId;
        this.trainerId = trainerId;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.status = status;
        this.trainerName = trainerName;
    }

    // Конструктор для тренировок тренера (с именем пользователя)
    public Workout(int id, int userId, int trainerId, String date, String time,
                   int duration, String status, String userName, boolean isForTrainer) {
        this.id = id;
        this.userId = userId;
        this.trainerId = trainerId;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.status = status;
        this.userName = userName;
    }

    // Полный конструктор
    public Workout(int id, int userId, int trainerId, String date, String time,
                   int duration, String status, String cancelReason, String notes,
                   Integer userRating, String userFeedback, String trainerName, String userName) {
        this.id = id;
        this.userId = userId;
        this.trainerId = trainerId;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.status = status;
        this.cancelReason = cancelReason;
        this.notes = notes;
        this.userRating = userRating;
        this.userFeedback = userFeedback;
        this.trainerName = trainerName;
        this.userName = userName;
    }

    // Геттеры
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getTrainerId() { return trainerId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDuration() { return duration; }
    public String getStatus() { return status; }
    public String getCancelReason() { return cancelReason; }
    public String getNotes() { return notes; }
    public Integer getUserRating() { return userRating; }
    public String getUserFeedback() { return userFeedback; }
    public String getTrainerName() { return trainerName; }
    public String getUserName() { return userName; } // Новый геттер

    // Сеттеры
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setStatus(String status) { this.status = status; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }
    public void setUserFeedback(String userFeedback) { this.userFeedback = userFeedback; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public void setUserName(String userName) { this.userName = userName; }
}