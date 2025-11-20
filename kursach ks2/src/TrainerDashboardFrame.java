// TrainerDashboardFrame.java (с функцией выхода)
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TrainerDashboardFrame extends JFrame {
    private User currentUser;
    private Trainer currentTrainer;
    private JTable workoutsTable;
    private DefaultTableModel workoutsModel;

    public TrainerDashboardFrame(User user) {
        this.currentUser = user;
        this.currentTrainer = TrainerDAO.getTrainerByUserId(user.getId());

        if (currentTrainer == null) {
            JOptionPane.showMessageDialog(this,
                    "Профиль тренера не найден. Обратитесь к администратору.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("Кабинет тренера - " + currentTrainer.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Добавляем обработчик закрытия окна
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });

        initComponents();
        loadWorkouts();
    }

    private void initComponents() {
        // Основная панель
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок с информацией о тренере
        JLabel titleLabel = new JLabel(
                "<html><center>Кабинет тренера<br>" +
                        currentTrainer.getName() + " - " + currentTrainer.getSpecialization() +
                        "</center></html>", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Обновить");
        JButton profileButton = new JButton("Редактировать профиль");
        JButton logoutButton = new JButton("Выйти");

        refreshButton.addActionListener(e -> loadWorkouts());
        profileButton.addActionListener(e -> editProfile());
        logoutButton.addActionListener(e -> logout());

        // Устанавливаем цвет для кнопки выхода
        logoutButton.setBackground(new Color(197, 11, 11));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);

        buttonPanel.add(refreshButton);
        buttonPanel.add(profileButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Таблица тренировок
        String[] columns = {"ID", "Клиент", "Дата", "Время", "Статус", "Оценка", "Действия"};
        workoutsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Только колонка с действиями редактируемая
            }
        };
        workoutsTable = new JTable(workoutsModel);
        workoutsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        workoutsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(workoutsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Статистика тренера
        JLabel statsLabel = new JLabel(" ", JLabel.CENTER);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(statsLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите выйти из личного кабинета?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Показываем сообщение о выходе
            JOptionPane.showMessageDialog(this,
                    "Вы успешно вышли из системы",
                    "Выход",
                    JOptionPane.INFORMATION_MESSAGE);

            // Открываем окно авторизации
            new LoginFrame().setVisible(true);

            // Закрываем текущее окно
            dispose();
        }
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        } else {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    private void loadWorkouts() {
        workoutsModel.setRowCount(0);
        List<Workout> workouts = WorkoutDAO.getTrainerWorkouts(currentTrainer.getId());

        int scheduledCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;
        double totalRating = 0;
        int ratedWorkouts = 0;

        for (Workout workout : workouts) {
            Object[] row = {
                    workout.getId(),
                    workout.getUserName(),
                    workout.getDate(),
                    workout.getTime(),
                    getStatusText(workout.getStatus()),
                    workout.getUserRating() != null ? workout.getUserRating() + "★" : "—",
                    "Действия"
            };
            workoutsModel.addRow(row);

            // Считаем статистику
            switch (workout.getStatus()) {
                case "scheduled": scheduledCount++; break;
                case "completed": completedCount++; break;
                case "cancelled": cancelledCount++; break;
            }

            // Считаем рейтинг
            if (workout.getUserRating() != null) {
                totalRating += workout.getUserRating();
                ratedWorkouts++;
            }
        }

        // Обновляем статистику
        double averageRating = ratedWorkouts > 0 ? totalRating / ratedWorkouts : 0;
        JLabel statsLabel = (JLabel) ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (statsLabel != null) {
            statsLabel.setText(String.format(
                    "Статистика: Запланировано: %d | Завершено: %d | Отменено: %d | Средний рейтинг: %.1f★",
                    scheduledCount, completedCount, cancelledCount, averageRating
            ));
        }
    }

    private String getStatusText(String status) {
        switch (status) {
            case "scheduled": return "Запланирована";
            case "completed": return "Завершена";
            case "cancelled": return "Отменена";
            default: return status;
        }
    }

    private void editProfile() {
        new EditTrainerProfileDialog(this, currentTrainer).setVisible(true);
    }

    // Классы для рендеринга кнопок в таблице
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Действия");
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                handleButtonClick(currentRow);
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            button.setText("Действия");
            return button;
        }

        private void handleButtonClick(int row) {
            int workoutId = (Integer) workoutsModel.getValueAt(row, 0);
            String status = workoutsModel.getValueAt(row, 4).toString();
            String clientName = workoutsModel.getValueAt(row, 1).toString();
            String date = workoutsModel.getValueAt(row, 2).toString();
            String time = workoutsModel.getValueAt(row, 3).toString();

            JPopupMenu popupMenu = new JPopupMenu();

            if ("Запланирована".equals(status)) {
                JMenuItem completeItem = new JMenuItem("Завершить тренировку");
                completeItem.addActionListener(e -> completeWorkout(workoutId, clientName, date, time));
                popupMenu.add(completeItem);

                JMenuItem cancelItem = new JMenuItem("Отменить тренировку");
                cancelItem.addActionListener(e -> cancelWorkout(workoutId, clientName, date, time));
                popupMenu.add(cancelItem);
            }

            // Добавляем информацию о тренировке для всех статусов
            JMenuItem infoItem = new JMenuItem("Информация о тренировке");
            infoItem.addActionListener(e -> showWorkoutInfo(workoutId));
            popupMenu.add(infoItem);

            if (popupMenu.getComponentCount() > 0) {
                popupMenu.show(workoutsTable, workoutsTable.getCellRect(row, 6, true).x,
                        workoutsTable.getCellRect(row, 6, true).y);
            }
        }
    }

    private void completeWorkout(int workoutId, String clientName, String date, String time) {
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Подтвердите завершение тренировки:\n\nКлиент: %s\nДата: %s\nВремя: %s",
                        clientName, date, time),
                "Завершение тренировки",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String notes = JOptionPane.showInputDialog(this,
                    "Добавьте заметки о тренировке (необязательно):",
                    "Заметки о тренировке",
                    JOptionPane.QUESTION_MESSAGE);

            if (WorkoutDAO.completeWorkout(workoutId, notes)) {
                JOptionPane.showMessageDialog(this,
                        "Тренировка успешно завершена!",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
                loadWorkouts();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при завершении тренировки",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelWorkout(int workoutId, String clientName, String date, String time) {
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Вы уверены, что хотите отменить тренировку?\n\nКлиент: %s\nДата: %s\nВремя: %s",
                        clientName, date, time),
                "Отмена тренировки",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String reason = JOptionPane.showInputDialog(this,
                    "Укажите причину отмены для клиента:",
                    "Причина отмены",
                    JOptionPane.QUESTION_MESSAGE);

            if (reason != null && !reason.trim().isEmpty()) {
                if (WorkoutDAO.cancelWorkoutByTrainer(workoutId, reason)) {
                    JOptionPane.showMessageDialog(this,
                            "Тренировка отменена",
                            "Успех",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadWorkouts();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка при отмене тренировки",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Отмена не выполнена. Необходимо указать причину для клиента.",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showWorkoutInfo(int workoutId) {
        Workout workout = getWorkoutById(workoutId);
        if (workout != null) {
            String info = String.format(
                    "Информация о тренировке:\n\n" +
                            "Клиент: %s\n" +
                            "Дата: %s\n" +
                            "Время: %s\n" +
                            "Статус: %s\n" +
                            "Продолжительность: %d мин.\n" +
                            "Оценка: %s\n" +
                            "Отзыв: %s\n" +
                            "Причина отмены: %s\n" +
                            "Заметки: %s",
                    workout.getUserName(),
                    workout.getDate(),
                    workout.getTime(),
                    getStatusText(workout.getStatus()),
                    workout.getDuration(),
                    workout.getUserRating() != null ? workout.getUserRating() + "★" : "не оценена",
                    workout.getUserFeedback() != null ? workout.getUserFeedback() : "нет",
                    workout.getCancelReason() != null ? workout.getCancelReason() : "нет",
                    workout.getNotes() != null ? workout.getNotes() : "нет"
            );

            JOptionPane.showMessageDialog(this, info, "Информация о тренировке", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Workout getWorkoutById(int workoutId) {
        List<Workout> workouts = WorkoutDAO.getTrainerWorkouts(currentTrainer.getId());
        for (Workout workout : workouts) {
            if (workout.getId() == workoutId) {
                return workout;
            }
        }
        return null;
    }
}