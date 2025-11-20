// Trainer.java
public class Trainer {
    private int id;
    private int userId;
    private String name;
    private String specialization;
    private Integer experience;
    private String description;
    private double rating;
    private boolean isActive;

    public Trainer(int id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.rating = 0.0;
        this.isActive = true;
    }

    public Trainer(int id, int userId, String name, String specialization,
                   Integer experience, String description, double rating, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.description = description;
        this.rating = rating;
        this.isActive = isActive;
    }

    // Геттеры
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public Integer getExperience() { return experience; }
    public String getDescription() { return description; }
    public double getRating() { return rating; }
    public boolean isActive() { return isActive; }

    @Override
    public String toString() {
        return name + " (" + specialization + ") ★" + String.format("%.1f", rating);
    }
}