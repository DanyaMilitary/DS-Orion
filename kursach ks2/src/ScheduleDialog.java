// ScheduleDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScheduleDialog extends JDialog {
    private final ScheduleManager scheduleManager;
    private final Trainer trainer;
    private final TrainerSchedule existingSlot;

    private boolean success = false;
    private ScheduleOperationResult operationResult;

    // UI компоненты
    private JComboBox<String> dayComboBox;
    private JSpinner startHourSpinner, startMinuteSpinner;
    private JSpinner endHourSpinner, endMinuteSpinner;
    private JCheckBox availableCheckBox;
    private JLabel statusLabel;

    public ScheduleDialog(Frame parent, String title, Trainer trainer) {
        this(parent, title, trainer, null);
    }

    public ScheduleDialog(Frame parent, String title, Trainer trainer, TrainerSchedule existingSlot) {
        super(parent, title, true);
        this.scheduleManager = ScheduleManager.getInstance();
        this.trainer = trainer;
        this.existingSlot = existingSlot;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        loadExistingData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Заголовок
        JLabel titleLabel = new JLabel(getTitle(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Статус бар
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        // Панель формы
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Параметры временного слота"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // День недели
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("День недели:*"), gbc);
        gbc.gridx = 1;
        String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
        dayComboBox = new JComboBox<>(days);
        formPanel.add(dayComboBox, gbc);

        // Время начала
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Время начала:*"), gbc);
        gbc.gridx = 1;
        JPanel startTimePanel = createTimePanel();
        startHourSpinner = (JSpinner) ((JPanel) startTimePanel.getComponent(0)).getComponent(0);
        startMinuteSpinner = (JSpinner) ((JPanel) startTimePanel.getComponent(0)).getComponent(2);
        formPanel.add(startTimePanel, gbc);

        // Время окончания
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Время окончания:*"), gbc);
        gbc.gridx = 1;
        JPanel endTimePanel = createTimePanel();
        endHourSpinner = (JSpinner) ((JPanel) endTimePanel.getComponent(0)).getComponent(0);
        endMinuteSpinner = (JSpinner) ((JPanel) endTimePanel.getComponent(0)).getComponent(2);
        formPanel.add(endTimePanel, gbc);

        // Статус доступности
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        availableCheckBox = new JCheckBox("Доступно для записи");
        availableCheckBox.setSelected(true);
        formPanel.add(availableCheckBox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = createStyledButton("💾 Сохранить", new Color(0, 150, 0));
        JButton cancelButton = createStyledButton("❌ Отмена", new Color(200, 0, 0));

        saveButton.addActionListener(e -> saveTimeSlot());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTimePanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(9, 0, 23, 1));
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 5));

        // Настройка размеров спиннеров
        hourSpinner.setPreferredSize(new Dimension(60, 25));
        minuteSpinner.setPreferredSize(new Dimension(60, 25));

        panel.add(hourSpinner);
        panel.add(new JLabel(":"));
        panel.add(minuteSpinner);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    private void loadExistingData() {
        if (existingSlot != null) {
            dayComboBox.setSelectedIndex(existingSlot.getDayOfWeek() - 1);

            String[] startParts = existingSlot.getStartTime().split(":");
            startHourSpinner.setValue(Integer.parseInt(startParts[0]));
            startMinuteSpinner.setValue(Integer.parseInt(startParts[1]));

            String[] endParts = existingSlot.getEndTime().split(":");
            endHourSpinner.setValue(Integer.parseInt(endParts[0]));
            endMinuteSpinner.setValue(Integer.parseInt(endParts[1]));

            availableCheckBox.setSelected(existingSlot.isAvailable());

            updateStatus("Редактирование существующего слота", Color.BLUE);
        } else {
            updateStatus("Создание нового временного слота", Color.GREEN);
        }
    }

    private void saveTimeSlot() {
        try {
            // Получаем данные из формы
            int dayOfWeek = dayComboBox.getSelectedIndex() + 1;
            String startTime = String.format("%02d:%02d",
                    (Integer) startHourSpinner.getValue(),
                    (Integer) startMinuteSpinner.getValue());
            String endTime = String.format("%02d:%02d",
                    (Integer) endHourSpinner.getValue(),
                    (Integer) endMinuteSpinner.getValue());
            boolean isAvailable = availableCheckBox.isSelected();

            // Выполняем операцию
            if (existingSlot != null) {
                operationResult = scheduleManager.updateTimeSlot(
                        existingSlot.getId(), dayOfWeek, startTime, endTime, isAvailable);
            } else {
                operationResult = scheduleManager.addTimeSlot(
                        trainer.getId(), dayOfWeek, startTime, endTime, isAvailable);
            }

            // Обрабатываем результат
            if (operationResult.isSuccess()) {
                success = true;
                updateStatus(operationResult.getMessage(), Color.GREEN);
                JOptionPane.showMessageDialog(this, operationResult.getMessage(), "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                updateStatus(operationResult.getMessage(), Color.RED);
                JOptionPane.showMessageDialog(this, operationResult.getMessage(), "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            String errorMsg = "Ошибка при сохранении: " + ex.getMessage();
            updateStatus(errorMsg, Color.RED);
            JOptionPane.showMessageDialog(this, errorMsg, "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // Геттеры
    public boolean isSuccess() { return success; }
    public ScheduleOperationResult getOperationResult() { return operationResult; }
    public TrainerSchedule getCreatedSchedule() {
        return operationResult != null ? operationResult.getSchedule() : null;
    }
}