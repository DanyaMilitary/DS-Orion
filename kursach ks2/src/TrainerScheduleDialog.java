// TrainerScheduleDialog.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrainerScheduleDialog extends JDialog {
    private Trainer trainer;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;

    public TrainerScheduleDialog(JFrame parent, Trainer trainer) {
        super(parent, "Расписание тренера", true);
        this.trainer = trainer;
        setSize(600, 400);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("Расписание тренера " + trainer.getName(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Таблица расписания
        String[] columns = {"День недели", "Начало", "Конец", "Действия"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Только колонка действий редактируема
            }
        };

        scheduleTable = new JTable(tableModel);
        loadSchedule();

        mainPanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Добавить время");
        JButton refreshButton = new JButton("Обновить");

        addButton.addActionListener(e -> addTimeSlot());
        refreshButton.addActionListener(e -> loadSchedule());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadSchedule() {
        tableModel.setRowCount(0);
        // Здесь можно загрузить расписание из базы данных
        // Временные данные для примера
        String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
        String[][] schedule = {
                {"Понедельник", "09:00", "18:00"},
                {"Вторник", "10:00", "19:00"},
                {"Среда", "09:00", "18:00"},
                {"Четверг", "10:00", "19:00"},
                {"Пятница", "09:00", "17:00"},
                {"Суббота", "10:00", "15:00"}
        };

        for (String[] row : schedule) {
            tableModel.addRow(new Object[]{row[0], row[1], row[2], "Удалить"});
        }
    }

    private void addTimeSlot() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JComboBox<String> dayCombo = new JComboBox<>(new String[]{
                "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"
        });

        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("18:00");

        panel.add(new JLabel("День недели:"));
        panel.add(dayCombo);
        panel.add(new JLabel("Начало (ЧЧ:ММ):"));
        panel.add(startField);
        panel.add(new JLabel("Конец (ЧЧ:ММ):"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Добавить время",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Сохранить в базу данных
            JOptionPane.showMessageDialog(this, "Время добавлено в расписание");
            loadSchedule();
        }
    }
}