// LoginFrame.java (без кнопки чата)
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public LoginFrame() {
        setTitle("ДС Орин - Авторизация");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("ДС Орион", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Логин:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Роль:"));
        roleComboBox = new JComboBox<>(new String[]{"user", "trainer"});
        formPanel.add(roleComboBox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton loginButton = new JButton("Войти");
        JButton registerButton = new JButton("Регистрация");

        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(e -> new RegistrationFrame().setVisible(true));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = UserDAO.loginUser(username, password);
            if (user != null && user.getRole().equals(role)) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Успешный вход!", "Успех", JOptionPane.INFORMATION_MESSAGE);

                if ("user".equals(role)) {
                    new UserDashboardFrame(user).setVisible(true);
                } else {
                    new TrainerDashboardFrame(user).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Неверные данные или роль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}