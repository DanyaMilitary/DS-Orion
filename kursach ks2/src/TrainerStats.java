// TrainerStats.java
public class TrainerStats {
    private int totalWorkouts;
    private int completedWorkouts;
    private int scheduledWorkouts;
    private int cancelledWorkouts;
    private double averageRating;
    private int ratedWorkouts;

    public TrainerStats(int totalWorkouts, int completedWorkouts, int scheduledWorkouts,
                        int cancelledWorkouts, double averageRating, int ratedWorkouts) {
        this.totalWorkouts = totalWorkouts;
        this.completedWorkouts = completedWorkouts;
        this.scheduledWorkouts = scheduledWorkouts;
        this.cancelledWorkouts = cancelledWorkouts;
        this.averageRating = averageRating;
        this.ratedWorkouts = ratedWorkouts;
    }

    // Геттеры
    public int getTotalWorkouts() { return totalWorkouts; }
    public int getCompletedWorkouts() { return completedWorkouts; }
    public int getScheduledWorkouts() { return scheduledWorkouts; }
    public int getCancelledWorkouts() { return cancelledWorkouts; }
    public double getAverageRating() { return averageRating; }
    public int getRatedWorkouts() { return ratedWorkouts; }

    public double getCompletionRate() {
        return totalWorkouts > 0 ? (double) completedWorkouts / totalWorkouts * 100 : 0;
    }

    public double getCancellationRate() {
        return totalWorkouts > 0 ? (double) cancelledWorkouts / totalWorkouts * 100 : 0;
    }

    public double getRatingPercentage() {
        return completedWorkouts > 0 ? (double) ratedWorkouts / completedWorkouts * 100 : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Статистика: Всего %d тренировок | Завершено: %d (%.1f%%) | Запланировано: %d | Отменено: %d (%.1f%%) | Рейтинг: %.1f/5 (оценок: %d, %.1f%%)",
                totalWorkouts, completedWorkouts, getCompletionRate(), scheduledWorkouts,
                cancelledWorkouts, getCancellationRate(), averageRating, ratedWorkouts, getRatingPercentage()
        );
    }
}