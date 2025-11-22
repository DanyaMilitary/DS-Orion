// User.java
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String name;
    private String phone;
    private String email;

    public User(int id, String username, String password, String role, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    // Геттеры
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}