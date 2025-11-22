// EditWorkoutDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditWorkoutDialog extends JDialog {
    private Workout workout;
    private JTextField dateField;
    private JTextField timeField;

    public EditWorkoutDialog(JFrame parent, Workout workout) {
        super(parent, "Изменение тренировки", true);
        this.workout = workout;
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        formPanel.add(new JLabel("Дата (ГГГГ-ММ-ДД):"));
        dateField = new JTextField(workout.getDate());
        formPanel.add(dateField);

        formPanel.add(new JLabel("Время (ЧЧ:ММ):"));
        timeField = new JTextField(workout.getTime());
        formPanel.add(timeField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Обновить");
        JButton cancelButton = new JButton("Отмена");

        updateButton.addActionListener(new UpdateAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class UpdateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();

            if (date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(EditWorkoutDialog.this,
                        "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!WorkoutDAO.isTimeSlotAvailable(workout.getTrainerId(), date, time)) {
                JOptionPane.showMessageDialog(EditWorkoutDialog.this,
                        "Это время уже занято", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (WorkoutDAO.updateWorkout(workout.getId(), date, time)) {
                JOptionPane.showMessageDialog(EditWorkoutDialog.this,
                        "Тренировка обновлена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(EditWorkoutDialog.this,
                        "Ошибка обновления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}