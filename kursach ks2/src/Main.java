import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Устанавливаем красивый вид для Swing компонентов
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Инициализируем базу данных
            DatabaseManager.initializeDatabase();

            // Показываем окно авторизации
            new LoginFrame().setVisible(true);

            System.out.println("Приложение спортивного комплекса запущено");
        });
    }
}