// ScheduleWorkoutDialog.java (с валидацией даты и времени)
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleWorkoutDialog extends JDialog {
    private User currentUser;
    private JComboBox<Trainer> trainerComboBox;
    private JTextField dateField;
    private JTextField timeField;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public ScheduleWorkoutDialog(JFrame parent, User user) {
        super(parent, "Запись на тренировку", true);
        this.currentUser = user;

        // Инициализация форматов даты и времени
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Строгая проверка даты
        timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setLenient(false); // Строгая проверка времени

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadTrainers();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Тренер:"));
        trainerComboBox = new JComboBox<>();
        formPanel.add(trainerComboBox);

        formPanel.add(new JLabel("Дата (ГГГГ-ММ-ДД):"));
        dateField = new JTextField();
        // Добавляем подсказку
        dateField.setToolTipText("Формат: 2024-01-15");
        formPanel.add(dateField);

        formPanel.add(new JLabel("Время (ЧЧ:ММ):"));
        timeField = new JTextField();
        timeField.setToolTipText("Формат: 14:30 (от 09:00 до 21:00)");
        formPanel.add(timeField);

        // Панель с примерами форматов
        JPanel examplesPanel = new JPanel(new FlowLayout());
        examplesLabel = new JLabel("<html><font color='gray'>Примеры: Дата: 2024-12-25, Время: 14:30</font></html>");
        examplesPanel.add(examplesLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(examplesPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton scheduleButton = new JButton("Записаться");
        JButton cancelButton = new JButton("Отмена");

        scheduleButton.addActionListener(new ScheduleAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(scheduleButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadTrainers() {
        List<Trainer> trainers = TrainerDAO.getAllTrainers();
        for (Trainer trainer : trainers) {
            trainerComboBox.addItem(trainer);
        }
    }

    // Метод для проверки корректности даты
    private boolean isValidDate(String dateStr) {
        try {
            Date date = dateFormat.parse(dateStr);

            // Проверяем, что дата не в прошлом
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();

            if (date.before(today)) {
                JOptionPane.showMessageDialog(this,
                        "Нельзя записаться на прошедшую дату",
                        "Ошибка даты", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Проверяем, что дата не слишком далеко в будущем (максимум 3 месяца)
            calendar.add(Calendar.MONTH, 3);
            Date maxDate = calendar.getTime();

            if (date.after(maxDate)) {
                JOptionPane.showMessageDialog(this,
                        "Можно записываться максимум на 3 месяца вперед",
                        "Ошибка даты", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Неверный формат даты!\nПравильный формат: ГГГГ-ММ-ДД\nПример: 2024-12-25",
                    "Ошибка формата даты", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Метод для проверки корректности времени
    private boolean isValidTime(String timeStr) {
        try {
            Date time = timeFormat.parse(timeStr);

            // Проверяем, что время в рабочих часах (9:00 - 21:00)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Проверяем границы времени
            if (hour < 9 || hour > 21) {
                JOptionPane.showMessageDialog(this,
                        "Время должно быть между 09:00 и 21:00",
                        "Ошибка времени", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Проверяем, что минуты кратны 5 (для удобства расписания)
            if (minute % 5 != 0) {
                JOptionPane.showMessageDialog(this,
                        "Время должно быть кратно 5 минутам\nПример: 09:00, 14:30, 18:45",
                        "Ошибка времени", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Проверяем, что время не раньше текущего, если дата сегодняшняя
            String dateStr = dateField.getText().trim();
            if (!dateStr.isEmpty()) {
                try {
                    Date selectedDate = dateFormat.parse(dateStr);
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    // Если выбрана сегодняшняя дата, проверяем время
                    if (selectedDate.equals(today.getTime())) {
                        Calendar now = Calendar.getInstance();
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.setTime(time);

                        // Сравниваем только время, если дата сегодня
                        if (selectedTime.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY) ||
                                (selectedTime.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY) &&
                                        selectedTime.get(Calendar.MINUTE) <= now.get(Calendar.MINUTE))) {
                            JOptionPane.showMessageDialog(this,
                                    "Нельзя записаться на прошедшее время сегодня",
                                    "Ошибка времени", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                } catch (ParseException e) {
                    // Дата еще не проверена, пропускаем проверку времени
                }
            }

            return true;

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Неверный формат времени!\nПравильный формат: ЧЧ:ММ\nПример: 14:30",
                    "Ошибка формата времени", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Метод для проверки доступности времени у тренера
    private boolean isTimeSlotAvailable(int trainerId, String date, String time) {
        return WorkoutDAO.isTimeSlotAvailable(trainerId, date, time);
    }

    private class ScheduleAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Trainer selectedTrainer = (Trainer) trainerComboBox.getSelectedItem();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();

            // Проверяем заполнение полей
            if (selectedTrainer == null) {
                JOptionPane.showMessageDialog(ScheduleWorkoutDialog.this,
                        "Выберите тренера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(ScheduleWorkoutDialog.this,
                        "Заполните дату и время", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Валидация даты
            if (!isValidDate(date)) {
                dateField.requestFocus();
                return;
            }

            // Валидация времени
            if (!isValidTime(time)) {
                timeField.requestFocus();
                return;
            }

            // Проверка доступности времени
            if (!isTimeSlotAvailable(selectedTrainer.getId(), date, time)) {
                JOptionPane.showMessageDialog(ScheduleWorkoutDialog.this,
                        "Это время уже занято у выбранного тренера\nВыберите другое время",
                        "Время занято", JOptionPane.WARNING_MESSAGE);
                timeField.requestFocus();
                return;
            }

            // Все проверки пройдены, записываем на тренировку
            if (WorkoutDAO.scheduleWorkout(currentUser.getId(), selectedTrainer.getId(), date, time)) {
                JOptionPane.showMessageDialog(ScheduleWorkoutDialog.this,
                        "Вы успешно записаны на тренировку!\n\nТренер: " + selectedTrainer.getName() +
                                "\nДата: " + date + "\nВремя: " + time,
                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(ScheduleWorkoutDialog.this,
                        "Ошибка записи на тренировку", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Добавляем поле для примера форматов
    private JLabel examplesLabel;
}