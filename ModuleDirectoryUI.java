import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ModuleDirectoryUI extends JPanel {

    private String userId;
    private String userRole;
    private ModuleRepository moduleRepo;
    private userRepository userRepo;
    private ClassRepository classRepo;

    // Table
    private DefaultTableModel tableModel;
    private JTable moduleTable;

    // Detail panel
    private JTextArea detailArea;
    private JLabel statusLabel;

    // Cached data
    private ArrayList<Module> visibleModules = new ArrayList<>();
    private ArrayList<ClassSession> allClasses = new ArrayList<>();
    private List<user> allUsers = new ArrayList<>();

    public ModuleDirectoryUI(String userId, String userRole, 
                             ModuleRepository moduleRepo, 
                             userRepository userRepo, 
                             ClassRepository classRepo) {
        this.userId = userId;
        this.userRole = userRole;
        this.moduleRepo = moduleRepo;
        this.userRepo = userRepo;
        this.classRepo = classRepo;
        
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Module Directory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(32, 42, 68));
        add(titleLabel, BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setOpaque(false);

        // Left - Table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Right - Details
        JPanel detailsPanel = createDetailsPanel();
        splitPane.setRightComponent(detailsPanel);

        add(splitPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel tableTitle = new JLabel("Modules");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(41, 128, 185));

        String[] columns = {"Module ID", "Module Name", "Lecturer", "Classes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        moduleTable = new JTable(tableModel);
        moduleTable.setRowHeight(30);
        moduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        moduleTable.getTableHeader().setBackground(new Color(41, 128, 185));
        moduleTable.getTableHeader().setForeground(Color.WHITE);
        moduleTable.setSelectionBackground(new Color(189, 215, 238));

        // Selection listener
        moduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = moduleTable.getSelectedRow();
                if (row >= 0 && row < visibleModules.size()) {
                    showDetails(visibleModules.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(moduleTable);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(new Color(41, 128, 185));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshBtn);

        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel detailsTitle = new JLabel("Module Details");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsTitle.setForeground(new Color(41, 128, 185));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setBackground(new Color(248, 249, 250));
        detailArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailArea.setText("Select a module from the table to view details.");

        JScrollPane scrollPane = new JScrollPane(detailArea);

        panel.add(detailsTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        detailArea.setText("Select a module from the table to view details.");

        try {
            // Load all data
            ArrayList<Module> allModules = moduleRepo.loadAll();
            allClasses = classRepo.loadAllClasses();
            allUsers = userRepo.getAllUsers();

            // Filter modules based on role
            visibleModules = ModuleAccessService.filterModulesByRole(allModules, userRole, userId);

            // Populate table
            for (Module m : visibleModules) {
                String lecturerDisplay = getLecturerDisplay(m.getLecturerId());
                int classCount = countClassesForModule(m.getModuleId());

                tableModel.addRow(new Object[]{
                    m.getModuleId(),
                    m.getName(),
                    lecturerDisplay,
                    classCount
                });
            }

            setStatus("Loaded " + visibleModules.size() + " module(s)", false);

            // Auto-select first row
            if (tableModel.getRowCount() > 0) {
                moduleTable.setRowSelectionInterval(0, 0);
            }

        } catch (Exception ex) {
            setStatus("Failed to load modules: " + ex.getMessage(), true);
        }
    }

    private String getLecturerDisplay(String lecturerId) {
        if (lecturerId == null || lecturerId.isEmpty()) {
            return "Not Assigned";
        }
        for (user u : allUsers) {
            if (u.getId().equals(lecturerId)) {
                return lecturerId + " - " + u.getName();
            }
        }
        return lecturerId;
    }

    private String getLeaderDisplay(String leaderId) {
        if (leaderId == null || leaderId.isEmpty()) {
            return "Not Assigned";
        }
        for (user u : allUsers) {
            if (u.getId().equals(leaderId)) {
                return leaderId + " - " + u.getName();
            }
        }
        return leaderId;
    }

    private int countClassesForModule(String moduleId) {
        int count = 0;
        for (ClassSession c : allClasses) {
            // Check if class belongs to this module (by module abbreviation)
            if (c.getModuleAbbreviation() != null && 
                c.getModuleAbbreviation().equalsIgnoreCase(moduleId)) {
                count++;
            }
        }
        return count;
    }

    private void showDetails(Module m) {
        if (m == null) {
            detailArea.setText("No module selected.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MODULE INFORMATION\n");
        sb.append("==================\n\n");
        
        sb.append("Module ID: ").append(m.getModuleId()).append("\n");
        sb.append("Name: ").append(m.getName()).append("\n");
        sb.append("Description: ").append(m.getDescription()).append("\n\n");

        sb.append("ASSIGNMENTS\n");
        sb.append("===========\n");
        sb.append("Lecturer: ").append(getLecturerDisplay(m.getLecturerId())).append("\n");
        sb.append("Academic Leader: ").append(getLeaderDisplay(m.getLeaderId())).append("\n\n");

        sb.append("ENROLLED STUDENTS\n");
        sb.append("=================\n");
        String studentIds = m.getStudentId();
        if (studentIds == null || studentIds.isEmpty()) {
            sb.append("No students enrolled.\n");
        } else {
            String[] ids = studentIds.split(";");
            int studentCount = 0;
            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    String studentName = "Unknown";
                    for (user u : allUsers) {
                        if (u.getId().equals(id.trim())) {
                            studentName = u.getName();
                            break;
                        }
                    }
                    studentCount++;
                    sb.append(studentCount).append(". ").append(id.trim())
                      .append(" - ").append(studentName).append("\n");
                }
            }
            if (studentCount == 0) {
                sb.append("No students enrolled.\n");
            }
        }

        sb.append("\nCLASS SCHEDULE\n");
        sb.append("==============\n");
        int classCount = 0;
        for (ClassSession c : allClasses) {
            if (c.getModuleAbbreviation() != null && 
                c.getModuleAbbreviation().equalsIgnoreCase(m.getModuleId())) {
                classCount++;
                sb.append(classCount).append(". ");
                sb.append(c.getClassName()).append("\n");
                sb.append("   Mode: ").append(c.getClassMode()).append("\n");
                sb.append("   Date: ").append(c.getClassDate()).append("\n");
                sb.append("   Day: ").append(c.getWeekdays()).append("\n");
                sb.append("   Time: ").append(c.getClassStartTime())
                  .append(" - ").append(c.getClassEndTime()).append("\n\n");
            }
        }
        if (classCount == 0) {
            sb.append("No classes scheduled.\n");
        }

        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? new Color(192, 57, 43) : new Color(39, 174, 96));
    }
}
