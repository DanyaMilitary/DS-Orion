// TrainerScheduleManagerFrame.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TrainerScheduleManagerFrame extends JFrame {
    private Trainer trainer;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public TrainerScheduleManagerFrame(Trainer trainer) {
        this.trainer = trainer;

        setTitle("Управление расписанием - " + trainer.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Главная панель
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Расписание тренера: " + trainer.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        statusLabel = new JLabel("Загрузка...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLUE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Таблица расписания
        String[] columns = {"ID", "День недели", "Время начала", "Время окончания", "Статус", "Действия"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Только колонка действий редактируема
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 5 ? JButton.class : String.class;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(35);
        scheduleTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        scheduleTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Настройка ширины колонок
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        return panel;
    }




    public void updateStatus(String message) {
        statusLabel.setText(message);
    }


    // Классы для рендеринга кнопок в таблице
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = scheduleTable.getSelectedRow();
                if (row != -1) {
                    int scheduleId = (int) tableModel.getValueAt(row, 0);
                    String action = (String) tableModel.getValueAt(row, 5);

                    if (action.contains("Изменить")) {
                        editTimeSlot(scheduleId);
                    } else if (action.contains("Удалить")) {
                        deleteTimeSlot(scheduleId);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

    private void editTimeSlot(int scheduleId) {
        TrainerSchedule timeSlot = ScheduleDAO.getScheduleById(scheduleId);
        if (timeSlot != null) {
        }
    }

    private void deleteTimeSlot(int scheduleId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить этот временной слот?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (ScheduleDAO.deleteTimeSlot(scheduleId)) {
                JOptionPane.showMessageDialog(this, "Временной слот успешно удален");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ошибка удаления временного слота. Возможно, на это время есть запланированные тренировки.");
            }
        }
    }
}