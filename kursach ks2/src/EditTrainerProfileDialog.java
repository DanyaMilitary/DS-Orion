// EditTrainerProfileDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditTrainerProfileDialog extends JDialog {
    private Trainer trainer;
    private JTextField specializationField;
    private JTextField experienceField;
    private JTextArea descriptionArea;

    public EditTrainerProfileDialog(JFrame parent, Trainer trainer) {
        super(parent, "Редактирование профиля", true);
        this.trainer = trainer;
        setSize(500, 400);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Имя:"));
        JTextField nameField = new JTextField(trainer.getName());
        nameField.setEditable(false);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Специализация:"));
        specializationField = new JTextField(trainer.getSpecialization());
        formPanel.add(specializationField);

        formPanel.add(new JLabel("Опыт (лет):"));
        experienceField = new JTextField(trainer.getExperience() != null ?
                trainer.getExperience().toString() : "");
        formPanel.add(experienceField);

        formPanel.add(new JLabel("Описание:"));
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setText(trainer.getDescription() != null ? trainer.getDescription() : "");
        formPanel.add(new JScrollPane(descriptionArea));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(new SaveAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class SaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String specialization = specializationField.getText().trim();
            String expText = experienceField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (specialization.isEmpty()) {
                JOptionPane.showMessageDialog(EditTrainerProfileDialog.this,
                        "Специализация обязательна");
                return;
            }

            Integer experience = null;
            if (!expText.isEmpty()) {
                try {
                    experience = Integer.parseInt(expText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(EditTrainerProfileDialog.this,
                            "Опыт должен быть числом");
                    return;
                }
            }

            if (TrainerDAO.updateTrainerProfile(trainer.getId(), specialization, experience, description)) {
                JOptionPane.showMessageDialog(EditTrainerProfileDialog.this,
                        "Профиль обновлен");
                dispose();
            } else {
                JOptionPane.showMessageDialog(EditTrainerProfileDialog.this,
                        "Ошибка обновления профиля");
            }
        }
    }
}