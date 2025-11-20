// ScheduleStats.java
public class ScheduleStats {
    private final int totalSlots;
    private final int availableSlots;
    private final int busySlots;

    public ScheduleStats(int totalSlots, int availableSlots, int busySlots) {
        this.totalSlots = totalSlots;
        this.availableSlots = availableSlots;
        this.busySlots = busySlots;
    }

    // Геттеры
    public int getTotalSlots() { return totalSlots; }
    public int getAvailableSlots() { return availableSlots; }
    public int getBusySlots() { return busySlots; }

    public double getAvailabilityPercentage() {
        return totalSlots > 0 ? (double) availableSlots / totalSlots * 100 : 0;
    }

    public double getBusyPercentage() {
        return totalSlots > 0 ? (double) busySlots / totalSlots * 100 : 0;
    }

    @Override
    public String toString() {
        return String.format("ScheduleStats{total=%d, available=%d, busy=%d, availability=%.1f%%}",
                totalSlots, availableSlots, busySlots, getAvailabilityPercentage());
    }
}