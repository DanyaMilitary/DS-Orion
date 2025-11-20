// RegistrationFrame.java - обновляем для автоматического создания тренера
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JComboBox<String> roleComboBox;
    private JTextField specializationField; // Добавляем поле специализации для тренера

    public RegistrationFrame() {
        setTitle("Регистрация");
        setSize(400, 400); // Увеличиваем высоту для нового поля
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Регистрация", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // Увеличиваем до 6 строк

        formPanel.add(new JLabel("Логин:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Подтвердите пароль:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        formPanel.add(new JLabel("Имя:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Роль:"));
        roleComboBox = new JComboBox<>(new String[]{"user", "trainer"});
        roleComboBox.addActionListener(e -> toggleSpecializationField());
        formPanel.add(roleComboBox);

        formPanel.add(new JLabel("Специализация:"));
        specializationField = new JTextField();
        specializationField.setEnabled(false); // По умолчанию выключено
        formPanel.add(specializationField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton registerButton = new JButton("Зарегистрироваться");
        JButton cancelButton = new JButton("Отмена");

        registerButton.addActionListener(new RegisterAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void toggleSpecializationField() {
        String role = (String) roleComboBox.getSelectedItem();
        specializationField.setEnabled("trainer".equals(role));
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String name = nameField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();
            String specialization = specializationField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.length() < 3) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Логин должен содержать минимум 3 символа", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 4) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Пароль должен содержать минимум 4 символа", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Пароли не совпадают", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("trainer".equals(role) && specialization.isEmpty()) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Для тренера необходимо указать специализацию", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (UserDAO.usernameExists(username)) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Пользователь с таким логином уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Регистрируем пользователя
            if (UserDAO.registerUser(username, password, role, name)) {
                // Если это тренер, создаем запись в таблице тренеров
                if ("trainer".equals(role)) {
                    User user = UserDAO.loginUser(username, password);
                    if (user != null) {
                        if (TrainerDAO.createTrainerFromUser(user.getId(), name, specialization)) {
                            JOptionPane.showMessageDialog(RegistrationFrame.this,
                                    "Регистрация тренера успешна!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(RegistrationFrame.this,
                                    "Пользователь создан, но возникла ошибка при создании профиля тренера",
                                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "Регистрация успешна!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Ошибка регистрации", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}