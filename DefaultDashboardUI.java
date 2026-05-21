import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DefaultDashboardUI extends JFrame {

    // Shared components
    protected CardLayout cardLayout;
    protected JPanel contentPanel;
    protected String currentRole;
    protected String currentUsername;

    public DefaultDashboardUI(String appTitle, String roleName, String username) {
        this.currentRole = roleName;
        this.currentUsername = username;
        setTitle(appTitle + " - " + roleName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(null);

        initUI(appTitle, roleName, username);

        // Auto-navigate to User Management on startup (only for the base dashboard)
        if (this.getClass() == DefaultDashboardUI.class) {
            SwingUtilities.invokeLater(() -> navigateTo("USER_MANAGEMENT"));
        }
    }

    private void initUI(String appTitle, String roleName, String username) {
        // ========== TOP BAR ==========
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(32, 42, 68));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(appTitle);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel userLabel = new JLabel(roleName + ": " + username);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(userLabel, BorderLayout.EAST);

        // ========== SIDE BAR ==========
        JPanel sideBar = createSideBar();

        // ========== CONTENT AREA (with background image) ==========
        cardLayout = new CardLayout();

        // Use our custom panel that paints the background image
        contentPanel = new BackgroundPanel("Image2.gif"); 
        contentPanel.setLayout(cardLayout);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setOpaque(false);

        JPanel welcomePanel = createSimplePanel("Welcome to APU Assessment System");

        contentPanel.add(welcomePanel, "WELCOME");

        // ========== MAIN LAYOUT ==========
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(sideBar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    protected JPanel createSideBar() {
        JPanel sideBar = new JPanel();
        sideBar.setBackground(new Color(23, 30, 52));
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(new EmptyBorder(20, 10, 20, 10));
        sideBar.setPreferredSize(new Dimension(200, 0));

        JLabel menuTitle = new JLabel("Admin Menu");
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sideBar.add(menuTitle);
        sideBar.add(Box.createVerticalStrut(20));

        // Navigation buttons
        JButton btnUserManagement = createMenuButton("User Management");
        JButton btnClassManagement = createMenuButton("Class Management");
        JButton btnGradingSystem = createMenuButton("Grading System");
        JButton btnAssignLeader = createMenuButton("Assign Leader");

        // Highlight current active module
        String currentModule = getCurrentModuleName();
        highlightActiveButton(btnUserManagement, currentModule.equals("USER_MANAGEMENT"));
        highlightActiveButton(btnClassManagement, currentModule.equals("CLASS_MANAGEMENT"));
        highlightActiveButton(btnGradingSystem, currentModule.equals("GRADING_MANAGEMENT"));
        highlightActiveButton(btnAssignLeader, currentModule.equals("ASSIGN_LEADER"));

        sideBar.add(btnUserManagement);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnClassManagement);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnGradingSystem);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(btnAssignLeader);

        sideBar.add(Box.createVerticalGlue());

        JButton btnLogout = createMenuButton("Logout");
        sideBar.add(btnLogout);

        // ========== BUTTON ACTIONS ==========
        btnUserManagement.addActionListener(e -> navigateTo("USER_MANAGEMENT"));
        btnClassManagement.addActionListener(e -> navigateTo("CLASS_MANAGEMENT"));
        btnGradingSystem.addActionListener(e -> navigateTo("GRADING_MANAGEMENT"));
        btnAssignLeader.addActionListener(e -> navigateTo("ASSIGN_LEADER"));
        btnLogout.addActionListener(e -> {
            dispose();
            new login().setVisible(true);
        });

        return sideBar;
    }

    // Override this method in child classes to indicate which module is active
    protected String getCurrentModuleName() {
        return "";
    }

    // Highlight the active button
    protected void highlightActiveButton(JButton btn, boolean isActive) {
        if (isActive) {
            btn.setBackground(new Color(41, 128, 185));  // Blue highlight for active
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }

    protected void navigateTo(String moduleName) {
        // Close current window and open the appropriate UI
        dispose();
        SwingUtilities.invokeLater(() -> {
            switch (moduleName) {
                case "USER_MANAGEMENT":
                    new userListUI(currentRole, currentUsername).setVisible(true);
                    break;
                case "CLASS_MANAGEMENT":
                    new classListUI(currentRole, currentUsername).setVisible(true);
                    break;
                case "GRADING_MANAGEMENT":
                    new gradingsystemUI("APU Assessment System - Grading System", currentRole, currentUsername).setVisible(true);
                    break;
                case "ASSIGN_LEADER":
                    new assignLeaderUI(currentRole, currentUsername).setVisible(true);
                    break;
                default:
                    new DefaultDashboardUI("APU Assessment System", currentRole, currentUsername).setVisible(true);
                    break;
            }
        });
    }

    // ======= Shared button style for everyone =======
    protected JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setMinimumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFocusPainted(false);

        btn.setBackground(new Color(45, 55, 85));  // darker blue bg
        btn.setForeground(Color.WHITE);             // white text
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

    // Simple placeholder panel (transparent so bg shows through)
    protected JPanel createSimplePanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // let background image be visible

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(Color.BLACK);

        panel.add(lbl);
        return panel;
    }

    // ======= Background panel that paints the image =======
    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            // Load image from file path (relative to working directory)
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Scale image to fill the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
