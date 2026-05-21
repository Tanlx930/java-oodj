import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


public class AcademicReportsUI extends JPanel {

    private String leaderId;
    private userRepository userRepo;
    private ModuleRepository moduleRepo;
    private ClassRepository classRepo;
    
    private JTabbedPane tabbedPane;

    public AcademicReportsUI(String leaderId, userRepository userRepo, 
                             ModuleRepository moduleRepo, ClassRepository classRepo) {
        this.leaderId = leaderId;
        this.userRepo = userRepo;
        this.moduleRepo = moduleRepo;
        this.classRepo = classRepo;
        
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(32, 42, 68));
        add(titleLabel, BorderLayout.NORTH);

        // Tabbed pane with reports
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        tabbedPane.addTab("Module Overview", createModuleOverviewPanel());
        tabbedPane.addTab("Lecturer Assignments", createLecturerAssignmentsPanel());
        tabbedPane.addTab("Class Schedule", createClassSchedulePanel());
        tabbedPane.addTab("Summary Statistics", createSummaryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createModuleOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Modules Under Your Supervision");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(41, 128, 185));

        // Get modules for this leader
        ArrayList<Module> myModules = moduleRepo.getModulesByLeader(leaderId);
        
        String[] columns = {"Module ID", "Module Name", "Description", "Lecturer ID", "Status"};
        Object[][] data = new Object[myModules.size()][5];
        
        for (int i = 0; i < myModules.size(); i++) {
            Module m = myModules.get(i);
            data[i][0] = m.getModuleId();
            data[i][1] = m.getName();
            data[i][2] = m.getDescription();
            data[i][3] = m.getLecturerId().isEmpty() ? "Not Assigned" : m.getLecturerId();
            data[i][4] = m.getLecturerId().isEmpty() ? "Needs Lecturer" : "Active";
        }

        JTable table = createStyledTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshBtn = createRefreshButton(() -> refreshModuleOverview(panel));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLecturerAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Lecturers Under Your Leadership");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(41, 128, 185));

        // Get lecturers assigned to this leader
        List<user> allUsers = userRepo.getAllUsers();
        List<user> myLecturers = allUsers.stream()
            .filter(u -> "lecturer".equalsIgnoreCase(u.getRole()))
            .filter(u -> String.valueOf(u.getLeaderId()).equals(leaderId))
            .collect(Collectors.toList());

        // Get module assignments
        ArrayList<Module> allModules = moduleRepo.loadAll();
        
        String[] columns = {"Lecturer ID", "Name", "Email", "Assigned Modules"};
        Object[][] data = new Object[myLecturers.size()][4];
        
        for (int i = 0; i < myLecturers.size(); i++) {
            user lec = myLecturers.get(i);
            
            // Count modules for this lecturer
            long moduleCount = allModules.stream()
                .filter(m -> m.getLecturerId().equals(lec.getId()))
                .count();
            
            data[i][0] = lec.getId();
            data[i][1] = lec.getName();
            data[i][2] = lec.getEmail();
            data[i][3] = moduleCount + " module(s)";
        }

        JTable table = createStyledTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshBtn = createRefreshButton(() -> refreshLecturerAssignments(panel));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createClassSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Class Schedule for Your Modules");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(41, 128, 185));

        // Get modules for this leader
        ArrayList<Module> myModules = moduleRepo.getModulesByLeader(leaderId);
        List<String> myModuleNames = myModules.stream()
            .map(Module::getName)
            .collect(Collectors.toList());

        // Get classes for those modules
        List<ClassSession> allClasses = classRepo.getAllClasses();
        List<ClassSession> myClasses = allClasses.stream()
            .filter(c -> myModuleNames.contains(c.getModuleCode()))
            .collect(Collectors.toList());

        String[] columns = {"Class ID", "Module", "Class Name", "Mode", "Date", "Day", "Time"};
        Object[][] data = new Object[myClasses.size()][7];
        
        for (int i = 0; i < myClasses.size(); i++) {
            ClassSession c = myClasses.get(i);
            data[i][0] = c.getId();
            data[i][1] = c.getModuleCode();
            data[i][2] = c.getClassName();
            data[i][3] = c.getMode();
            data[i][4] = c.getDate();
            data[i][5] = c.getDay();
            data[i][6] = c.getStartTime() + " - " + c.getEndTime();
        }

        JTable table = createStyledTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshBtn = createRefreshButton(() -> refreshClassSchedule(panel));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Summary Statistics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(41, 128, 185));

        // Calculate statistics
        ArrayList<Module> myModules = moduleRepo.getModulesByLeader(leaderId);
        List<user> allUsers = userRepo.getAllUsers();
        List<ClassSession> allClasses = classRepo.getAllClasses();

        // Lecturers under this leader
        long lecturerCount = allUsers.stream()
            .filter(u -> "lecturer".equalsIgnoreCase(u.getRole()))
            .filter(u -> String.valueOf(u.getLeaderId()).equals(leaderId))
            .count();

        // Modules with lecturers assigned
        long assignedModules = myModules.stream()
            .filter(m -> !m.getLecturerId().isEmpty())
            .count();

        // Classes for my modules
        List<String> myModuleNames = myModules.stream()
            .map(Module::getName)
            .collect(Collectors.toList());
        long classCount = allClasses.stream()
            .filter(c -> myModuleNames.contains(c.getModuleCode()))
            .count();

        // Class modes breakdown
        Map<String, Long> modeBreakdown = allClasses.stream()
            .filter(c -> myModuleNames.contains(c.getModuleCode()))
            .collect(Collectors.groupingBy(ClassSession::getMode, Collectors.counting()));

        // Create stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatCard("Total Modules", String.valueOf(myModules.size()), new Color(41, 128, 185)));
        statsPanel.add(createStatCard("Assigned Modules", String.valueOf(assignedModules), new Color(39, 174, 96)));
        statsPanel.add(createStatCard("Unassigned", String.valueOf(myModules.size() - assignedModules), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Lecturers", String.valueOf(lecturerCount), new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Total Classes", String.valueOf(classCount), new Color(230, 126, 34)));
        statsPanel.add(createStatCard("Physical Classes", 
            String.valueOf(modeBreakdown.getOrDefault("Physical", 0L)), new Color(52, 73, 94)));

        // Mode breakdown details
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        modePanel.setBackground(new Color(248, 249, 250));
        modePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel modeTitle = new JLabel("Class Mode Breakdown:");
        modeTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modePanel.add(modeTitle);
        modePanel.add(Box.createVerticalStrut(10));

        for (Map.Entry<String, Long> entry : modeBreakdown.entrySet()) {
            JLabel modeLabel = new JLabel("• " + entry.getKey() + ": " + entry.getValue() + " classes");
            modeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            modePanel.add(modeLabel);
        }

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(modePanel, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable(Object[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(189, 215, 238));
        
        return table;
    }

    private JPanel createStatCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(255, 255, 255, 200));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createRefreshButton(Runnable action) {
        JButton btn = new JButton("Refresh");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    // Refresh methods
    private void refreshModuleOverview(JPanel panel) {
        int index = tabbedPane.indexOfComponent(panel);
        tabbedPane.setComponentAt(index, createModuleOverviewPanel());
    }

    private void refreshLecturerAssignments(JPanel panel) {
        int index = tabbedPane.indexOfComponent(panel);
        tabbedPane.setComponentAt(index, createLecturerAssignmentsPanel());
    }

    private void refreshClassSchedule(JPanel panel) {
        int index = tabbedPane.indexOfComponent(panel);
        tabbedPane.setComponentAt(index, createClassSchedulePanel());
    }
}
