import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class classCreateUI extends JDialog {

    private JComboBox<String> moduleField;
    private JTextField classNameField;
    private JComboBox<String> classModeField;
    private JTextField classDateField;
    private JComboBox<String> weekdayField;
    private JComboBox<String> startTimeField;
    private JComboBox<String> endTimeField;
    private JButton saveButton, cancelButton;
    private ModuleClassRepository repo = new ModuleClassRepository();
    private boolean confirmed = false;

    // Predefined options
    private static final String[] CLASS_MODES = {"Physical", "Online", "Hybrid"};
    private static final String[] WEEKDAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String[] TIME_SLOTS = {
        "08:30", "09:00", "09:30", "10:00", "10:30", "10:45", "11:00", "11:30",
        "12:00", "12:30", "12:45", "13:00", "13:30", "14:00", "14:30", "15:00",
        "15:30", "15:45", "16:00", "16:30", "17:00", "17:30", "17:45", "18:00",
        "18:30", "18:45", "19:00", "19:30", "20:00", "20:30", "20:45", "21:00"
    };

    public classCreateUI(JFrame parent) {
        super(parent, "Create New Class", true);
        setSize(550, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(32, 42, 68));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Create New Class");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Form panel with padding
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Module Abbreviation dropdown
        moduleField = new JComboBox<>();
        moduleField.addItem(null);
        for (String module : repo.getUniqueModuleAbbreviations()) {
            moduleField.addItem(module);
        }
        // Add common modules if not already present
        String[] commonModules = {"ARS", "DTIN", "IS", "IVIP", "PEP", "CC1", "DBM", "DM", "OSCA", "PRCOM", 
                                   "ACAL", "EIM", "FUUD", "IAI", "PWP", "FEP", "ISWE", "OOP", "RWDD", "SYAD", 
                                   "CP", "CSF", "DLD", "NWT"};
        for (String module : commonModules) {
            boolean exists = false;
            for (int i = 0; i < moduleField.getItemCount(); i++) {
                if (module.equals(moduleField.getItemAt(i))) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                moduleField.addItem(module);
            }
        }
        formPanel.add(createFieldPanel("Module:", moduleField));
        formPanel.add(Box.createVerticalStrut(15));

        // Class Name
        formPanel.add(createFieldPanel("Class Name:", classNameField = new JTextField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        // Class Mode dropdown
        classModeField = new JComboBox<>();
        classModeField.addItem(null);
        for (String mode : CLASS_MODES) {
            classModeField.addItem(mode);
        }
        formPanel.add(createFieldPanel("Class Mode:", classModeField));
        formPanel.add(Box.createVerticalStrut(15));

        // Class Date
        formPanel.add(createFieldPanel("Class Date (YYYY-MM-DD):", classDateField = new JTextField(20)));
        classDateField.setToolTipText("Enter date in format: YYYY-MM-DD (e.g., 2026-01-19)");
        formPanel.add(Box.createVerticalStrut(15));

        // Weekday dropdown
        weekdayField = new JComboBox<>();
        weekdayField.addItem(null);
        for (String weekday : WEEKDAYS) {
            weekdayField.addItem(weekday);
        }
        formPanel.add(createFieldPanel("Weekday:", weekdayField));
        formPanel.add(Box.createVerticalStrut(15));

        // Auto-update weekday when date changes
        classDateField.addActionListener(e -> autoFillWeekday());
        classDateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                autoFillWeekday();
            }
        });

        // Start Time dropdown
        startTimeField = new JComboBox<>();
        startTimeField.addItem(null);
        for (String time : TIME_SLOTS) {
            startTimeField.addItem(time);
        }
        formPanel.add(createFieldPanel("Start Time:", startTimeField));
        formPanel.add(Box.createVerticalStrut(15));

        // End Time dropdown
        endTimeField = new JComboBox<>();
        endTimeField.addItem(null);
        for (String time : TIME_SLOTS) {
            endTimeField.addItem(time);
        }
        formPanel.add(createFieldPanel("End Time:", endTimeField));
        formPanel.add(Box.createVerticalStrut(30));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        saveButton = createStyledButton("Save", new Color(41, 128, 185));
        cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));

        saveButton.addActionListener(e -> saveClass());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        formPanel.add(buttonPanel);

        // Scroll pane for form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(240, 240, 240));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void autoFillWeekday() {
        String dateText = classDateField.getText().trim();
        if (!dateText.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                String weekdayName = dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase();
                weekdayField.setSelectedItem(weekdayName);
            } catch (Exception ex) {
                // Invalid date format, ignore
            }
        }
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setMaximumSize(new Dimension(450, 40));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setPreferredSize(new Dimension(160, 35));
        label.setForeground(new Color(50, 50, 50));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
            ));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void saveClass() {
        // Validation
        if (moduleField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a module.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (classNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (classModeField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a class mode.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (classDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class date cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate date format
        String dateText = classDateField.getText().trim();
        try {
            LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (weekdayField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a weekday.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (startTimeField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a start time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endTimeField.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an end time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate time order
        String startTime = (String) startTimeField.getSelectedItem();
        String endTime = (String) endTimeField.getSelectedItem();
        if (startTime.compareTo(endTime) >= 0) {
            JOptionPane.showMessageDialog(this, "End time must be after start time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new class
        int newId = repo.getNextId();
        ModuleClass newClass = new ModuleClass(
            newId,
            (String) moduleField.getSelectedItem(),
            classNameField.getText().trim(),
            (String) classModeField.getSelectedItem(),
            classDateField.getText().trim(),
            (String) weekdayField.getSelectedItem(),
            startTime,
            endTime
        );

        List<ModuleClass> classes = repo.getAllClasses();
        classes.add(newClass);
        repo.saveAllClasses(classes);

        confirmed = true;
        JOptionPane.showMessageDialog(this, "Class created successfully!");
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}