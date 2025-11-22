// RateWorkoutDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RateWorkoutDialog extends JDialog {
    private Workout workout;
    private JComboBox<Integer> ratingComboBox;
    private JTextArea feedbackArea;

    public RateWorkoutDialog(JFrame parent, Workout workout) {
        super(parent, "Оценка тренировки", true);
        this.workout = workout;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        formPanel.add(new JLabel("Тренер:"));
        formPanel.add(new JLabel(workout.getTrainerName()));

        formPanel.add(new JLabel("Оценка (1-5):"));
        ratingComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        formPanel.add(ratingComboBox);

        formPanel.add(new JLabel("Отзыв:"));
        feedbackArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(feedbackArea));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("Отправить отзыв");
        JButton cancelButton = new JButton("Отмена");

        submitButton.addActionListener(new SubmitAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private class SubmitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int rating = (Integer) ratingComboBox.getSelectedItem();
            String feedback = feedbackArea.getText().trim();

            if (TrainerDAO.addWorkoutFeedback(workout.getId(), rating, feedback)) {
                JOptionPane.showMessageDialog(RateWorkoutDialog.this,
                        "Спасибо за ваш отзыв!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(RateWorkoutDialog.this,
                        "Ошибка отправки отзыва", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}