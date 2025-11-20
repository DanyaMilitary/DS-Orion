// DatabaseManager.java (убираем таблицу сообщений)
import java.sql.*;
import java.io.File;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:sports.db";
    private static boolean isInitialized = false;

    static {
        initializeDriver();
    }

    private static void initializeDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite драйвер успешно загружен");
            new File(".").mkdirs();
        } catch (ClassNotFoundException e) {
            System.err.println("ОШИБКА: SQLite драйвер не найден!");
            e.printStackTrace();
        }
    }

    public static void initializeDatabase() {
        if (isInitialized) {
            System.out.println("База данных уже инициализирована");
            return;
        }

        System.out.println("Инициализация базы данных...");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Создаем таблицы если они не существуют
            createTablesIfNotExist(stmt);

            System.out.println("База данных успешно инициализирована");
            isInitialized = true;

        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist(Statement stmt) throws SQLException {
        // Таблица пользователей
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                name TEXT NOT NULL,
                phone TEXT,
                email TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // Таблица тренеров
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS trainers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER UNIQUE,
                name TEXT NOT NULL,
                specialization TEXT NOT NULL,
                experience INTEGER,
                description TEXT,
                rating DECIMAL(3,2) DEFAULT 0.0,
                is_active BOOLEAN DEFAULT TRUE,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users (id)
            )
        """);

        // Таблица тренировок
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS workouts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                trainer_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                duration INTEGER DEFAULT 60,
                status TEXT DEFAULT 'scheduled',
                cancel_reason TEXT,
                notes TEXT,
                user_rating INTEGER,
                user_feedback TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users (id),
                FOREIGN KEY (trainer_id) REFERENCES trainers (id)
            )
        """);

        System.out.println("Все таблицы созданы или уже существуют");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Метод для сброса базы данных (только для разработки)
    public static void resetDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS workouts");
            stmt.execute("DROP TABLE IF EXISTS trainers");
            stmt.execute("DROP TABLE IF EXISTS users");

            isInitialized = false;
            initializeDatabase();

            System.out.println("База данных сброшена и переинициализирована");
        } catch (SQLException e) {
            System.err.println("Ошибка сброса базы данных: " + e.getMessage());
        }
    }
}