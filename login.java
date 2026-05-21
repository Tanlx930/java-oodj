import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// ==========================================
// 1. ENCAPSULATION: The User Object
// ==========================================
class UserSession {
    private String id;
    private String username;
    private String name;
    private String role;

    public UserSession(String id, String username, String name, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
}

// ==========================================
// 2. ABSTRACTION: Authentication Service
// ==========================================
class AuthenticationService {
    private static final String FILE_NAME = "user.txt";

    public UserSession authenticate(String inputUser, String inputPass) {
        File file = new File(FILE_NAME);
        if (!file.exists()) file = new File("src/" + FILE_NAME);

        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                // Check Username
                if (parts[1].trim().equals(inputUser) && parts[2].trim().equals(inputPass)) {
                    
                    // --- ROBUST ROLE FINDER ---
                    String role = "unknown";
                    
                    // Look at the last 3 parts of the line (For varying column counts)
                    int startCheck = Math.max(0, parts.length - 3);
                    for (int i = startCheck; i < parts.length; i++) {
                        String p = parts[i].trim().toLowerCase();
                        if (p.equals("student") || p.equals("lecturer") || p.equals("admin")) {
                            role = p;
                            break;
                        }
                    }
                    
                    // If scanning failed, fallback to hardcoded indices
                    if (role.equals("unknown")) {
                        if (parts.length >= 9) role = parts[parts.length - 2].trim(); 
                    }

                    // Return User (ID=0, Username=1, Name=3, Role=FoundRole)
                    return new UserSession(parts[0], parts[1], parts[3], role);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; 
    }
}

// ==========================================
// 3. UI IMPLEMENTATION
// ==========================================
public class login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JCheckBox chkShowPassword;
    
    private AuthenticationService authService = new AuthenticationService();

    public login() {
        setTitle("APU Assessment Feedback System - Login");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
        setVisible(true);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserSession user = authService.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!\nWelcome, " + user.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
            routeUser(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }

    private void routeUser(UserSession user) {
        String role = user.getRole().toLowerCase();

        switch (role) {
            case "student":
                new studentDashboard(user.getName(), user.getId()).setVisible(true);
                this.dispose();
                break;

            case "lecturer":
                 new LecturerDashboard(user.getId()).setVisible(true);
                 this.dispose();
                 break;

            case "admin":
                new userListUI(user.getRole(), user.getName()).setVisible(true);
                this.dispose();
                break;

            case "academicleader":
                new AcademicDashboardUI(user.getId(), user.getName()).setVisible(true);
                this.dispose();
                break;    

            default:
                JOptionPane.showMessageDialog(this, "Unknown Role Type: " + role, "Configuration Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private void initUI() {
        BackgroundPanel bgPanel = new BackgroundPanel("Image.gif");
        bgPanel.setLayout(new GridBagLayout());
        setContentPane(bgPanel);

        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(new Color(0, 0, 0, 160));
        loginCard.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SYSTEM LOGIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 30, 0);
        loginCard.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.CYAN);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; loginCard.add(lblUser, gbc);
        gbc.gridx = 1; loginCard.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.CYAN);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.addActionListener(e -> performLogin());
        gbc.gridx = 0; gbc.gridy = 2; loginCard.add(lblPass, gbc);
        gbc.gridx = 1; loginCard.add(txtPassword, gbc);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setOpaque(false);
        chkShowPassword.setForeground(Color.WHITE);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) txtPassword.setEchoChar((char) 0);
            else txtPassword.setEchoChar('\u2022');
        });
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        loginCard.add(chkShowPassword, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(100, 35));
        btnLogin.addActionListener(e -> performLogin());

        btnClear = new JButton("Clear");
        btnClear.setBackground(new Color(220, 50, 50));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClear.setFocusPainted(false);
        btnClear.setPreferredSize(new Dimension(100, 35));
        btnClear.addActionListener(e -> { txtUsername.setText(""); txtPassword.setText(""); });

        btnPanel.add(btnLogin);
        btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(20, 0, 0, 0);
        loginCard.add(btnPanel, gbc);

        bgPanel.add(loginCard);
    }

    class BackgroundPanel extends JPanel {
        private Image img;
        public BackgroundPanel(String path) {
            try { img = new ImageIcon(path).getImage(); } catch (Exception e) {}
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            else { g.setColor(new Color(30, 30, 40)); g.fillRect(0, 0, getWidth(), getHeight()); }
        }
    }
}