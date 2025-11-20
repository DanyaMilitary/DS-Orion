// AddTrainerInfoDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTrainerInfoDialog extends JDialog {
    private JTextField nameField;
    private JTextField specializationField;
    private JSpinner experienceSpinner;
    private JTextArea descriptionArea;

    public AddTrainerInfoDialog(JFrame parent) {
        super(parent, "Добавить информацию о тренере", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Имя тренера:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Специализация:"));
        specializationField = new JTextField();
        formPanel.add(specializationField);

        formPanel.add(new JLabel("Опыт (лет):"));
        experienceSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));
        formPanel.add(experienceSpinner);

        formPanel.add(new JLabel("Описание:"));
        descriptionArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descriptionArea));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        addButton.addActionListener(new AddAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();
            int experience = (Integer) experienceSpinner.getValue();
            String description = descriptionArea.getText().trim();

            if (name.isEmpty() || specialization.isEmpty()) {
                JOptionPane.showMessageDialog(AddTrainerInfoDialog.this,
                        "Заполните обязательные поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (TrainerDAO.addTrainer(name, specialization, experience, description)) {
                JOptionPane.showMessageDialog(AddTrainerInfoDialog.this,
                        "Информация о тренере добавлена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(AddTrainerInfoDialog.this,
                        "Ошибка добавления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}