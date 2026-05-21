import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class classListUI extends DefaultDashboardUI {
    private DefaultTableModel tableModel;
    private JTable classTable;
    private JTextField searchField;
    private JComboBox<String> moduleFilter;
    private JComboBox<String> modeFilter;
    private JComboBox<String> weekdayFilter;
    private ModuleClassRepository repo;
    private List<ModuleClass> allClasses;

    public classListUI(String roleName, String username) {
        super("APU Assessment System - Class Management", roleName, username);
        repo = new ModuleClassRepository();
        allClasses = repo.getAllClasses();
        setupCustomUI();
    }

    @Override
    protected String getCurrentModuleName() {
        return "CLASS_MANAGEMENT";
    }

    private void setupCustomUI() {
        // Create the class management panel
        JPanel classManagementPanel = createClassManagementPanel();
        
        // Add it to the content panel
        contentPanel.add(classManagementPanel, "CLASS_MANAGEMENT");
        cardLayout.show(contentPanel, "CLASS_MANAGEMENT");
    }

    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title and Add button panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Manage Classes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        JButton addClassButton = styleRoundButton(new JButton("Add New Class"), new Color(41, 128, 185));
        addClassButton.addActionListener(e -> openCreateClassDialog());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(addClassButton, BorderLayout.EAST);

        // Search and Filter panel
        JPanel searchFilterPanel = createSearchFilterPanel();

        // Combine title and search/filter panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchFilterPanel, BorderLayout.CENTER);

        // Table panel
        JScrollPane tableScrollPane = createClassesTable();

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.setToolTipText("Search by class name or module abbreviation");

        // Add document listener for real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setOpaque(false);

        // Module filter
        JLabel moduleLabel = new JLabel("Module:");
        moduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleFilter = new JComboBox<>();
        moduleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        moduleFilter.setPreferredSize(new Dimension(100, 30));
        populateModuleFilter();
        moduleFilter.addActionListener(e -> applyFilters());

        // Mode filter
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeFilter = new JComboBox<>();
        modeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        modeFilter.setPreferredSize(new Dimension(100, 30));
        populateModeFilter();
        modeFilter.addActionListener(e -> applyFilters());

        // Weekday filter
        JLabel weekdayLabel = new JLabel("Weekday:");
        weekdayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weekdayFilter = new JComboBox<>();
        weekdayFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        weekdayFilter.setPreferredSize(new Dimension(110, 30));
        populateWeekdayFilter();
        weekdayFilter.addActionListener(e -> applyFilters());

        // Clear filters button
        JButton clearFiltersBtn = new JButton("Clear Filters");
        clearFiltersBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearFiltersBtn.addActionListener(e -> clearFilters());

        filterPanel.add(moduleLabel);
        filterPanel.add(moduleFilter);
        filterPanel.add(modeLabel);
        filterPanel.add(modeFilter);
        filterPanel.add(weekdayLabel);
        filterPanel.add(weekdayFilter);
        filterPanel.add(clearFiltersBtn);

        // Combine search and filter
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.setOpaque(false);
        combinedPanel.add(searchPanel, BorderLayout.NORTH);
        combinedPanel.add(filterPanel, BorderLayout.CENTER);

        panel.add(combinedPanel, BorderLayout.CENTER);

        return panel;
    }

    private void populateModuleFilter() {
        moduleFilter.removeAllItems();
        moduleFilter.addItem("All");
        for (String module : repo.getUniqueModuleAbbreviations()) {
            moduleFilter.addItem(module);
        }
    }

    private void populateModeFilter() {
        modeFilter.removeAllItems();
        modeFilter.addItem("All");
        for (String mode : repo.getUniqueClassModes()) {
            modeFilter.addItem(mode);
        }
    }

    private void populateWeekdayFilter() {
        weekdayFilter.removeAllItems();
        weekdayFilter.addItem("All");
        for (String weekday : repo.getUniqueWeekdays()) {
            weekdayFilter.addItem(weekday);
        }
    }

    private void clearFilters() {
        searchField.setText("");
        moduleFilter.setSelectedIndex(0);
        modeFilter.setSelectedIndex(0);
        weekdayFilter.setSelectedIndex(0);
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedModule = (String) moduleFilter.getSelectedItem();
        String selectedMode = (String) modeFilter.getSelectedItem();
        String selectedWeekday = (String) weekdayFilter.getSelectedItem();

        List<ModuleClass> filteredClasses = new ArrayList<>();

        for (ModuleClass mc : allClasses) {
            boolean matchesSearch = searchText.isEmpty() ||
                    mc.getClassName().toLowerCase().contains(searchText) ||
                    mc.getModuleAbbreviation().toLowerCase().contains(searchText);

            boolean matchesModule = selectedModule == null || selectedModule.equals("All") ||
                    mc.getModuleAbbreviation().equals(selectedModule);

            boolean matchesMode = selectedMode == null || selectedMode.equals("All") ||
                    mc.getClassMode().equals(selectedMode);

            boolean matchesWeekday = selectedWeekday == null || selectedWeekday.equals("All") ||
                    mc.getWeekday().equals(selectedWeekday);

            if (matchesSearch && matchesModule && matchesMode && matchesWeekday) {
                filteredClasses.add(mc);
            }
        }

        updateTable(filteredClasses);
    }

    private void updateTable(List<ModuleClass> classes) {
        tableModel.setRowCount(0);
        for (ModuleClass mc : classes) {
            tableModel.addRow(new Object[]{
                    mc.getNo(),
                    mc.getModuleAbbreviation(),
                    mc.getClassName(),
                    mc.getClassMode(),
                    mc.getClassDate(),
                    mc.getWeekday(),
                    mc.getClassStartTime(),
                    mc.getClassEndTime(),
                    "Actions"
            });
        }
    }

    private JButton styleRoundButton(JButton button, Color backgroundColor) {
        RoundedButton roundedButton = new RoundedButton(button.getText(), backgroundColor);
        roundedButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roundedButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        roundedButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                roundedButton.setBackgroundColor(backgroundColor.darker());
                roundedButton.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                roundedButton.setBackgroundColor(backgroundColor);
                roundedButton.repaint();
            }
        });

        return roundedButton;
    }

    private JScrollPane createClassesTable() {
        String[] columnNames = {"No", "Module", "Class Name", "Mode", "Date", "Weekday", "Start Time", "End Time", "Actions"};
        tableModel = new DefaultTableModel(new Object[0][0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only "Actions" column is editable
            }
        };

        classTable = new JTable(tableModel);
        classTable.setFont(new Font("Arial", Font.PLAIN, 12));
        classTable.setRowHeight(40);
        classTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set custom renderer and editor for "Actions" column
        classTable.getColumnModel().getColumn(8).setCellRenderer(new ClassActionButtonRenderer());
        classTable.getColumnModel().getColumn(8).setCellEditor(new ClassActionButtonEditor(new JCheckBox(), classTable, this));

        // Set column widths
        int[] columnWidths = {40, 70, 140, 80, 100, 90, 80, 80, 160};
        for (int i = 0; i < columnWidths.length; i++) {
            classTable.getColumnModel().getColumn(i).setMinWidth(columnWidths[i]);
        }

        loadClassesFromRepository();

        return new JScrollPane(classTable);
    }

    private void loadClassesFromRepository() {
        allClasses = repo.getAllClasses();
        updateTable(allClasses);
        // Refresh filter options
        populateModuleFilter();
        populateModeFilter();
        populateWeekdayFilter();
    }

    private void openCreateClassDialog() {
        classCreateUI dialog = new classCreateUI(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            refreshTable();
        }
    }

    void refreshTable() {
        repo = new ModuleClassRepository();
        loadClassesFromRepository();
    }

    boolean deleteClassFromRepository(int classNo) {
        List<ModuleClass> classes = repo.getAllClasses();

        boolean removed = classes.removeIf(mc -> mc.getNo() == classNo);

        if (removed) {
            repo.saveAllClasses(classes);
        }

        return removed;
    }
}

// Class Action Button Renderer
class ClassActionButtonRenderer extends JPanel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(createRoundButton("Edit", new Color(41, 128, 185)));
        panel.add(createRoundButton("Delete", new Color(231, 76, 60)));
        return panel;
    }

    private JButton createRoundButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }
}

// Class Action Button Editor
class ClassActionButtonEditor extends DefaultCellEditor {
    private JPanel panel;
    private final JTable table;
    private final classListUI parentFrame;

    public ClassActionButtonEditor(JCheckBox checkBox, JTable table, classListUI parentFrame) {
        super(checkBox);
        this.table = table;
        this.parentFrame = parentFrame;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // "Edit" button
        JButton editButton = createRoundStyledButton("Edit", new Color(41, 128, 185));
        editButton.setPreferredSize(new Dimension(15, 30));
        editButton.setMinimumSize(new Dimension(15, 30));
        editButton.addActionListener(e -> handleEditAction(row));

        // "Delete" button
        JButton deleteButton = createRoundStyledButton("Delete", new Color(231, 76, 60));
        deleteButton.setPreferredSize(new Dimension(40, 30));
        deleteButton.setMinimumSize(new Dimension(40, 30));
        deleteButton.addActionListener(e -> handleDeleteAction(row));

        panel.add(editButton);
        panel.add(deleteButton);
        return panel;
    }

    private JButton createRoundStyledButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackgroundColor(backgroundColor.darker());
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackgroundColor(backgroundColor);
                button.repaint();
            }
        });

        return button;
    }

    private void handleEditAction(int row) {
        int classNo = (int) table.getValueAt(row, 0);
        String module = (String) table.getValueAt(row, 1);
        String className = (String) table.getValueAt(row, 2);
        String mode = (String) table.getValueAt(row, 3);
        String date = (String) table.getValueAt(row, 4);
        String weekday = (String) table.getValueAt(row, 5);
        String startTime = (String) table.getValueAt(row, 6);
        String endTime = (String) table.getValueAt(row, 7);

        classEditUI dialog = new classEditUI(parentFrame, classNo, module, className, mode, date, weekday, startTime, endTime);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ModuleClassRepository repo = new ModuleClassRepository();
            List<ModuleClass> classes = repo.getAllClasses();

            for (ModuleClass mc : classes) {
                if (mc.getNo() == classNo) {
                    mc.setModuleAbbreviation(dialog.getModuleAbbreviation());
                    mc.setClassName(dialog.getClassName());
                    mc.setClassMode(dialog.getClassMode());
                    mc.setClassDate(dialog.getClassDate());
                    mc.setWeekday(dialog.getWeekday());
                    mc.setClassStartTime(dialog.getClassStartTime());
                    mc.setClassEndTime(dialog.getClassEndTime());
                    break;
                }
            }

            repo.saveAllClasses(classes);
            parentFrame.refreshTable();
            JOptionPane.showMessageDialog(parentFrame, "Class updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteAction(int row) {
        int classNo = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this class?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (parentFrame.deleteClassFromRepository(classNo)) {
                ((DefaultTableModel) table.getModel()).removeRow(row);
                JOptionPane.showMessageDialog(parentFrame, "Class deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Failed to delete class.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
