import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class gradingsystemUI extends DefaultDashboardUI {
    private DefaultTableModel tableModel;
    private final GradingSystemRepository repository;
    private List<GradingSystem> gradesList;

    public gradingsystemUI(String appTitle, String roleName, String username) {
        super(appTitle, roleName, username);
        this.repository = new GradingSystemRepository();
        this.gradesList = repository.getAllGradingSystems();
        setupCustomUI();
    }

    @Override
    protected String getCurrentModuleName() {
        return "GRADING_MANAGEMENT";
    }

    private void setupCustomUI() {
        // Create the grading system management panel
        JPanel gradingManagementPanel = createGradingManagementPanel();
        
        // Add it to the content panel
        contentPanel.add(gradingManagementPanel, "GRADING_MANAGEMENT");
        cardLayout.show(contentPanel, "GRADING_MANAGEMENT");
    }

    private JPanel createGradingManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0)); // Add bottom padding

        JLabel titleLabel = new JLabel("Manage Grading System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        JButton addGradeButton = styleRoundButton(new JButton("Add New Grade"), new Color(39, 174, 96));
        addGradeButton.addActionListener(e -> openAddGradeDialog());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(addGradeButton, BorderLayout.EAST);

        // Table panel
        JScrollPane tableScrollPane = createGradingTable();

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
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

    private void openAddGradeDialog() {
        GradingAddDialog dialog = new GradingAddDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            GradingSystem newGs = new GradingSystem(
                dialog.getMinMark(), dialog.getMaxMark(),
                dialog.getGrade(), dialog.getGpa(), dialog.getClassification()
            );
            repository.addGradingSystem(newGs);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Grade tier added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JScrollPane createGradingTable() {
        String[] columnNames = {"Grade", "GPA", "Classification", "Min Mark", "Max Mark", "Actions"};
        tableModel = new DefaultTableModel(new Object[0][0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only "Actions" column is editable
            }
        };

        JTable gradingTable = new JTable(tableModel);
        gradingTable.setFont(new Font("Arial", Font.PLAIN, 12));
        gradingTable.setRowHeight(40);
        gradingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set custom renderer and editor for "Actions" column
        gradingTable.getColumnModel().getColumn(5).setCellRenderer(new GradingActionButtonRenderer());
        gradingTable.getColumnModel().getColumn(5).setCellEditor(new GradingActionButtonEditor(new JCheckBox(), gradingTable, this));

        // Set column widths
        int[] columnWidths = {80, 80, 120, 100, 100, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            gradingTable.getColumnModel().getColumn(i).setMinWidth(columnWidths[i]);
        }

        loadGradesFromRepository();

        return new JScrollPane(gradingTable);
    }

    private void loadGradesFromRepository() {
        tableModel.setRowCount(0);
        gradesList = repository.getAllGradingSystems();
        for (GradingSystem gs : gradesList) {
            tableModel.addRow(new Object[]{
                    gs.getGrade(),
                    String.format("%.2f", gs.getGpa()),
                    gs.getClassification(),
                    gs.getMinMark(),
                    gs.getMaxMark(),
                    "Edit"
            });
        }
    }

    void refreshTable() {
        repository.reload();
        loadGradesFromRepository();
    }

    GradingSystemRepository getRepository() {
        return repository;
    }
}

// Grading Action Button Renderer
class GradingActionButtonRenderer extends JPanel implements TableCellRenderer {
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

// Grading Action Button Editor
class GradingActionButtonEditor extends DefaultCellEditor {
    private JPanel panel;
    private final JTable table;
    private final gradingsystemUI parentFrame;

    public GradingActionButtonEditor(JCheckBox checkBox, JTable table, gradingsystemUI parentFrame) {
        super(checkBox);
        this.table = table;
        this.parentFrame = parentFrame;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton editButton = createRoundStyledButton("Edit", new Color(41, 128, 185));
        editButton.setPreferredSize(new Dimension(60, 30));
        editButton.setMinimumSize(new Dimension(60, 30));
        editButton.addActionListener(_ -> handleEditAction(row));

        JButton deleteButton = createRoundStyledButton("Delete", new Color(231, 76, 60));
        deleteButton.setPreferredSize(new Dimension(60, 30));
        deleteButton.setMinimumSize(new Dimension(60, 30));
        deleteButton.addActionListener(_ -> handleDeleteAction(row));

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
        String grade = (String) table.getValueAt(row, 0);
        String gpaStr = (String) table.getValueAt(row, 1);
        String classification = (String) table.getValueAt(row, 2);
        int minMark = (int) table.getValueAt(row, 3);
        int maxMark = (int) table.getValueAt(row, 4);
        double gpa = Double.parseDouble(gpaStr);

        // Create edit dialog
        GradingEditDialog dialog = new GradingEditDialog(parentFrame, row, grade, minMark, maxMark, gpa, classification);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            GradingSystemRepository repo = new GradingSystemRepository();
            GradingSystem gs = new GradingSystem(dialog.getMinMark(), dialog.getMaxMark(), grade, dialog.getGpa(), classification);
            repo.updateGradingSystem(row, gs);
            parentFrame.refreshTable();
            JOptionPane.showMessageDialog(parentFrame, "Grade updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteAction(int row) {
        String grade = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete grade tier '" + grade + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            GradingSystemRepository repo = parentFrame.getRepository();
            repo.deleteGradingSystem(row);
            parentFrame.refreshTable();
            JOptionPane.showMessageDialog(parentFrame, "Grade tier deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public Object getCellEditorValue() {
        return "Edit";
    }
}

// Grading Edit Dialog with +/- buttons
class GradingEditDialog extends JDialog {
    private int minMark;
    private int maxMark;
    private double gpa;
    private boolean confirmed = false;
    private final int rowIndex;
    private final List<GradingSystem> gradesList;

    public GradingEditDialog(gradingsystemUI parentFrame, int rowIndex, String grade, int minMark, int maxMark, double gpa, String classification) {
        super((JFrame) SwingUtilities.getWindowAncestor(parentFrame), "Edit Grade: " + grade, true);
        this.rowIndex = rowIndex;
        this.minMark = minMark;
        this.maxMark = maxMark;
        this.gpa = gpa;
        this.gradesList = new GradingSystemRepository().getAllGradingSystems();

        setSize(400, 300);
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(parentFrame));
        initializeUI(grade, classification);
    }

    private void initializeUI(String grade, String classification) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        contentPanel.setBackground(Color.WHITE);

        // Min Mark Panel
        JPanel minPanel = createMarkPanel("Min Mark:", minMark);
        contentPanel.add(minPanel);

        // Max Mark Panel
        JPanel maxPanel = createMarkPanel("Max Mark:", maxMark);
        contentPanel.add(maxPanel);

        // GPA Panel
        JPanel gpaPanel = createGPAPanel("GPA:");
        contentPanel.add(gpaPanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveBtn = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));

        saveBtn.addActionListener(_ -> handleSave());
        cancelBtn.addActionListener(_ -> dispose());

        buttonPanel.add(saveBtn, BorderLayout.EAST);
        buttonPanel.add(cancelBtn, BorderLayout.WEST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createMarkPanel(String label, int value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel markControlPanel = new JPanel(new BorderLayout(5, 0));
        markControlPanel.setBackground(Color.WHITE);

        JLabel markValue = new JLabel(String.valueOf(value));
        markValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        markValue.setHorizontalAlignment(JLabel.CENTER);

        JButton decBtn = createSmallRoundButton("-", new Color(231, 76, 60));
        JButton incBtn = createSmallRoundButton("+", new Color(41, 128, 185));

        if (label.equals("Min Mark:")) {
            decBtn.addActionListener(_ -> {
                if (minMark > 0) {
                    minMark--;
                    markValue.setText(String.valueOf(minMark));
                }
            });
            incBtn.addActionListener(_ -> {
                if (minMark < maxMark) {
                    minMark++;
                    markValue.setText(String.valueOf(minMark));
                }
            });
        } else {
            decBtn.addActionListener(_ -> {
                if (maxMark > minMark) {
                    maxMark--;
                    markValue.setText(String.valueOf(maxMark));
                }
            });
            incBtn.addActionListener(_ -> {
                if (maxMark < 100) {
                    maxMark++;
                    markValue.setText(String.valueOf(maxMark));
                }
            });
        }

        markControlPanel.add(decBtn, BorderLayout.WEST);
        markControlPanel.add(markValue, BorderLayout.CENTER);
        markControlPanel.add(incBtn, BorderLayout.EAST);

        panel.add(labelComp, BorderLayout.WEST);
        panel.add(markControlPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGPAPanel(String label) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel gpaControlPanel = new JPanel(new BorderLayout(5, 0));
        gpaControlPanel.setBackground(Color.WHITE);

        JLabel gpaValue = new JLabel(String.format("%.2f", gpa));
        gpaValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gpaValue.setHorizontalAlignment(JLabel.CENTER);

        JButton decBtn = createSmallRoundButton("-", new Color(231, 76, 60));
        JButton incBtn = createSmallRoundButton("+", new Color(41, 128, 185));

        decBtn.addActionListener(_ -> {
            if (gpa >= 0.10) {
                gpa -= 0.10;
                gpaValue.setText(String.format("%.2f", gpa));
            }
        });
        incBtn.addActionListener(_ -> {
            if (gpa < 4.00) {
                gpa += 0.10;
                gpaValue.setText(String.format("%.2f", gpa));
            }
        });

        gpaControlPanel.add(decBtn, BorderLayout.WEST);
        gpaControlPanel.add(gpaValue, BorderLayout.CENTER);
        gpaControlPanel.add(incBtn, BorderLayout.EAST);

        panel.add(labelComp, BorderLayout.WEST);
        panel.add(gpaControlPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createSmallRoundButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isArmed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(Math.min(bgColor.getRed() + 30, 255),
                            Math.min(bgColor.getGreen() + 30, 255),
                            Math.min(bgColor.getBlue() + 30, 255)));
                } else {
                    g2d.setColor(bgColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(35, 35);
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
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

    private void handleSave() {
        // Validate marks
        if (minMark > maxMark) {
            JOptionPane.showMessageDialog(this, "Min mark cannot exceed max mark!", "Invalid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate boundaries
        if (rowIndex < gradesList.size() - 1) {
            GradingSystem lowerGrade = gradesList.get(rowIndex + 1);
            if (minMark <= lowerGrade.getMaxMark()) {
                JOptionPane.showMessageDialog(this, 
                        "Min mark must be > " + lowerGrade.getMaxMark() + " (max of " + lowerGrade.getGrade() + ")", 
                        "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (rowIndex > 0) {
            GradingSystem higherGrade = gradesList.get(rowIndex - 1);
            if (maxMark >= higherGrade.getMinMark()) {
                JOptionPane.showMessageDialog(this, 
                        "Max mark must be < " + higherGrade.getMinMark() + " (min of " + higherGrade.getGrade() + ")", 
                        "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getMinMark() {
        return minMark;
    }

    public int getMaxMark() {
        return maxMark;
    }

    public double getGpa() {
        return gpa;
    }
}

// Dialog for adding a new grading tier
class GradingAddDialog extends JDialog {
    private JTextField gradeField;
    private JTextField classificationField;
    private JSpinner minMarkSpinner;
    private JSpinner maxMarkSpinner;
    private JSpinner gpaSpinner;
    private boolean confirmed = false;

    public GradingAddDialog(gradingsystemUI parentFrame) {
        super((JFrame) SwingUtilities.getWindowAncestor(parentFrame), "Add New Grade Tier", true);
        setSize(450, 400);
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(parentFrame));
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Add New Grade Tier");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(32, 42, 68));
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Form Content
        JPanel contentPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        contentPanel.setBackground(Color.WHITE);

        // Grade
        contentPanel.add(createLabel("Grade (e.g. A+):"));
        gradeField = new JTextField(10);
        gradeField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(gradeField);

        // Classification
        contentPanel.add(createLabel("Classification:"));
        classificationField = new JTextField(10);
        classificationField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(classificationField);

        // Min Mark
        contentPanel.add(createLabel("Min Mark:"));
        minMarkSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        minMarkSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(minMarkSpinner);

        // Max Mark
        contentPanel.add(createLabel("Max Mark:"));
        maxMarkSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        maxMarkSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentPanel.add(maxMarkSpinner);

        // GPA
        contentPanel.add(createLabel("GPA:"));
        gpaSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 4.0, 0.1));
        gpaSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JSpinner.NumberEditor gpaEditor = new JSpinner.NumberEditor(gpaSpinner, "0.00");
        gpaSpinner.setEditor(gpaEditor);
        contentPanel.add(gpaSpinner);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton saveBtn = createStyledButton("Save", new Color(39, 174, 96));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));

        saveBtn.addActionListener(_ -> handleSave());
        cancelBtn.addActionListener(_ -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setPreferredSize(new Dimension(100, 35));

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

    private void handleSave() {
        // Validate grade name
        if (gradeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Grade cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate classification
        if (classificationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Classification cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int minMark = (int) minMarkSpinner.getValue();
        int maxMark = (int) maxMarkSpinner.getValue();

        if (minMark > maxMark) {
            JOptionPane.showMessageDialog(this, "Min mark cannot exceed max mark.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate grade name
        GradingSystemRepository repo = new GradingSystemRepository();
        if (repo.getGradingSystemByGrade(gradeField.getText().trim()) != null) {
            JOptionPane.showMessageDialog(this, "A grade tier with this name already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for overlapping mark ranges
        for (GradingSystem gs : repo.getAllGradingSystems()) {
            if (minMark <= gs.getMaxMark() && maxMark >= gs.getMinMark()) {
                JOptionPane.showMessageDialog(this,
                        "Mark range overlaps with existing grade '" + gs.getGrade() + "' (" + gs.getMinMark() + "-" + gs.getMaxMark() + ").",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public String getGrade() { return gradeField.getText().trim(); }
    public String getClassification() { return classificationField.getText().trim(); }
    public int getMinMark() { return (int) minMarkSpinner.getValue(); }
    public int getMaxMark() { return (int) maxMarkSpinner.getValue(); }
    public double getGpa() { return (double) gpaSpinner.getValue(); }
}
