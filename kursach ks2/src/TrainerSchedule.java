// TrainerSchedule.java
public class TrainerSchedule {
    private int id;
    private int trainerId;
    private int dayOfWeek;
    private String startTime;
    private String endTime;
    private boolean isAvailable;
    private String dayName;

    public TrainerSchedule(int id, int trainerId, int dayOfWeek, String startTime,
                           String endTime, boolean isAvailable) {
        this.id = id;
        this.trainerId = trainerId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
        this.dayName = getDayName(dayOfWeek);
    }

    private String getDayName(int dayOfWeek) {
        String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
        if (dayOfWeek >= 1 && dayOfWeek <= 7) {
            return days[dayOfWeek - 1];
        }
        return "Неизвестный день";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public int getTrainerId() { return trainerId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public boolean isAvailable() { return isAvailable; }
    public String getDayName() { return dayName; }

    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getTimeRange() {
        return startTime + " - " + endTime;
    }

    public int getDurationInMinutes() {
        try {
            String[] start = startTime.split(":");
            String[] end = endTime.split(":");
            int startMinutes = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
            int endMinutes = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
            return endMinutes - startMinutes;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("TrainerSchedule{id=%d, day=%s, time=%s-%s, available=%s}",
                id, dayName, startTime, endTime, isAvailable);
    }
}