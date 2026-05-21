import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class AcademicDashboardUI extends JFrame {

    // UI Components
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // User data
    private String leaderId;
    private String leaderName;
    private user currentLeader;
    
    // Repositories
    private userRepository userRepo;
    private ModuleRepository moduleRepo;
    private ClassRepository classRepo;

    public AcademicDashboardUI(String leaderId, String leaderName) {
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.userRepo = new userRepository();
        this.moduleRepo = new ModuleRepository();
        this.classRepo = new ClassRepository();
        
        // Load current leader data
        loadCurrentLeader();
        
        setTitle("APU Assessment Feedback System - Academic Leader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void loadCurrentLeader() {
        List<user> allUsers = userRepo.getAllUsers();
        for (user u : allUsers) {
            if (u.getId().equals(leaderId) && "academicleader".equalsIgnoreCase(u.getRole())) {
                currentLeader = u;
                leaderName = u.getName();
                break;
            }
        }
    }

    private void initUI() {
        // ================== TOP BAR ==================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(32, 42, 68));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("APU Assessment Feedback System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel userLabel = new JLabel("Academic Leader: " + leaderName + " (ID: " + leaderId + ")");
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(userLabel, BorderLayout.EAST);

        // ================== SIDE NAV ==================
        JPanel sideBar = createSideBar();

        // ================== CONTENT AREA ==================
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(new Color(240, 240, 240));

        // Create all panels
        JPanel dashboardPanel = createDashboardPanel();
        JPanel profilePanel = new AcademicProfileUI(leaderId, leaderName, userRepo);
        JPanel moduleDirectoryPanel = new ModuleDirectoryUI(leaderId, "academicleader", moduleRepo, userRepo, classRepo);
        JPanel assignLecturerPanel = new AssignLecturerUI(leaderId, userRepo, moduleRepo);
        JPanel reportsPanel = new AcademicReportsUI(leaderId, userRepo, moduleRepo, classRepo);
        JPanel gradingSystemPanel = new GradingSystemViewUI();

        // Add panels to card layout
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(profilePanel, "PROFILE");
        contentPanel.add(moduleDirectoryPanel, "MODULES");
        contentPanel.add(assignLecturerPanel, "ASSIGN");
        contentPanel.add(reportsPanel, "REPORTS");
        contentPanel.add(gradingSystemPanel, "GRADING");
        contentPanel.add(moduleDirectoryPanel, "MODULES");
        contentPanel.add(assignLecturerPanel, "ASSIGN");
        contentPanel.add(reportsPanel, "REPORTS");

        // ================== MAIN LAYOUT ==================
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(sideBar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        
        // Show dashboard by default
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JPanel createSideBar() {
        JPanel sideBar = new JPanel();
        sideBar.setBackground(new Color(23, 30, 52));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(new EmptyBorder(20, 10, 20, 10));
        sideBar.setPreferredSize(new Dimension(200, 0));

        JLabel menuTitle = new JLabel("Leader Menu");
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sideBar.add(menuTitle);
        sideBar.add(Box.createVerticalStrut(20));

        // Navigation buttons
        JButton btnDashboard = createMenuButton("Dashboard");
        JButton btnProfile = createMenuButton("My Profile");
        JButton btnModules = createMenuButton("My Modules");
        JButton btnAssign = createMenuButton("Assign Lecturers");
        JButton btnGrading = createMenuButton("Grading System");
        JButton btnReports = createMenuButton("Reports");
        JButton btnLogout = createMenuButton("Logout");

        sideBar.add(btnDashboard);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnProfile);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnModules);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnAssign);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnGrading);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnReports);
        sideBar.add(Box.createVerticalGlue());
        sideBar.add(btnLogout);

        // Button actions
        btnDashboard.addActionListener(e -> cardLayout.show(contentPanel, "DASHBOARD"));
        btnProfile.addActionListener(e -> cardLayout.show(contentPanel, "PROFILE"));
        btnModules.addActionListener(e -> {
            // Refresh modules panel
            contentPanel.remove(contentPanel.getComponent(2));
            contentPanel.add(new ModuleDirectoryUI(leaderId, "academicleader", moduleRepo, userRepo, classRepo), "MODULES", 2);
            cardLayout.show(contentPanel, "MODULES");
        });
        btnAssign.addActionListener(e -> {
            // Refresh assign panel
            contentPanel.remove(contentPanel.getComponent(3));
            contentPanel.add(new AssignLecturerUI(leaderId, userRepo, moduleRepo), "ASSIGN", 3);
            cardLayout.show(contentPanel, "ASSIGN");
        });
        btnGrading.addActionListener(e -> cardLayout.show(contentPanel, "GRADING"));
        btnReports.addActionListener(e -> cardLayout.show(contentPanel, "REPORTS"));
        btnLogout.addActionListener(e -> {
            dispose();
            new login().setVisible(true);
        });

        return sideBar;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setMinimumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFocusPainted(false);

        btn.setBackground(new Color(45, 55, 85));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        Color normalBg = new Color(45, 55, 85);
        Color hoverBg = new Color(60, 75, 110);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverBg);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(normalBg);
            }
        });

        return btn;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Academic Leader Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(32, 42, 68));

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        // Get statistics
        ArrayList<Module> myModules = moduleRepo.getModulesByLeader(leaderId);
        List<user> allUsers = userRepo.getAllUsers();
        
        // Count lecturers under this leader
        long lecturerCount = allUsers.stream()
            .filter(u -> "lecturer".equalsIgnoreCase(u.getRole()))
            .filter(u -> String.valueOf(u.getLeaderId()).equals(leaderId))
            .count();

        // Count classes for modules under this leader
        List<ClassSession> allClasses = classRepo.getAllClasses();
        long classCount = 0;
        for (Module m : myModules) {
            for (ClassSession c : allClasses) {
                if (c.getModuleCode() != null && c.getModuleCode().equals(m.getName())) {
                    classCount++;
                }
            }
        }

        statsPanel.add(createStatCard("My Modules", String.valueOf(myModules.size()), new Color(41, 128, 185)));
        statsPanel.add(createStatCard("Lecturers", String.valueOf(lecturerCount), new Color(39, 174, 96)));
        statsPanel.add(createStatCard("Classes", String.valueOf(classCount), new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Role", "Leader", new Color(230, 126, 34)));

        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel welcomeLabel = new JLabel("<html><h2>Welcome, " + leaderName + "!</h2>" +
            "<p>As an Academic Leader, you can:</p>" +
            "<ul>" +
            "<li>View and manage modules under your supervision</li>" +
            "<li>Assign lecturers to handle your modules</li>" +
            "<li>View reports and analytics</li>" +
            "<li>Update your profile information</li>" +
            "</ul></html>");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomePanel.add(welcomeLabel);

        // Module list
        JPanel moduleListPanel = createModuleListPanel(myModules);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(welcomePanel, BorderLayout.NORTH);
        centerPanel.add(moduleListPanel, BorderLayout.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(255, 255, 255, 200));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createModuleListPanel(ArrayList<Module> modules) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Modules Under Your Supervision");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] columns = {"ID", "Module Name", "Description", "Lecturer ID"};
        Object[][] data = new Object[modules.size()][4];
        
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            data[i][0] = m.getModuleId();
            data[i][1] = m.getName();
            data[i][2] = m.getDescription();
            data[i][3] = m.getLecturerId().isEmpty() ? "Not Assigned" : m.getLecturerId();
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}

