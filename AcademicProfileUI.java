import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class AcademicProfileUI extends JPanel {

    // UI Components
    private JTextField idField;
    private JTextField nameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField roleField;
    
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    
    private JButton updatePasswordBtn;
    private JLabel statusLabel;

    // Data
    private String leaderId;
    private String leaderName;
    private userRepository userRepo;
    private user currentLeader;

    public AcademicProfileUI(String leaderId, String leaderName, userRepository userRepo) {
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.userRepo = userRepo;
        
        loadCurrentLeader();
        initUI();
    }

    private void loadCurrentLeader() {
        List<user> allUsers = userRepo.getAllUsers();
        for (user u : allUsers) {
            if (u.getId().equals(leaderId)) {
                currentLeader = u;
                break;
            }
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Profile Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(32, 42, 68));
        add(titleLabel, BorderLayout.NORTH);

        // Main content - two columns
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setOpaque(false);

        // Left panel - Profile Information
        JPanel profilePanel = createProfilePanel();
        mainPanel.add(profilePanel);

        // Right panel - Change Password
        JPanel passwordPanel = createPasswordPanel();
        mainPanel.add(passwordPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar at bottom
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(statusLabel, BorderLayout.SOUTH);

        // Load data
        loadProfileData();
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel sectionTitle = new JLabel("Profile Information");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(41, 128, 185));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(20));

        // ID (read-only)
        panel.add(createLabel("Leader ID:"));
        idField = createTextField(false);
        panel.add(idField);
        panel.add(Box.createVerticalStrut(10));

        // Username (read-only)
        panel.add(createLabel("Username:"));
        usernameField = createTextField(false);
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));

        // Name (read-only)
        panel.add(createLabel("Full Name:"));
        nameField = createTextField(false);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        // Email (read-only)
        panel.add(createLabel("Email:"));
        emailField = createTextField(false);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));

        // Phone (read-only)
        panel.add(createLabel("Phone:"));
        phoneField = createTextField(false);
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(10));

        // Role (read-only)
        panel.add(createLabel("Role:"));
        roleField = createTextField(false);
        panel.add(roleField);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel sectionTitle = new JLabel("Change Password");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(41, 128, 185));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(20));

        // Old Password
        panel.add(createLabel("Current Password:"));
        oldPasswordField = createPasswordField();
        panel.add(oldPasswordField);
        panel.add(Box.createVerticalStrut(10));

        // New Password
        panel.add(createLabel("New Password:"));
        newPasswordField = createPasswordField();
        panel.add(newPasswordField);
        panel.add(Box.createVerticalStrut(10));

        // Confirm Password
        panel.add(createLabel("Confirm New Password:"));
        confirmPasswordField = createPasswordField();
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(20));

        // Update Button
        updatePasswordBtn = new JButton("Update Password");
        updatePasswordBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updatePasswordBtn.setBackground(new Color(41, 128, 185));
        updatePasswordBtn.setForeground(Color.WHITE);
        updatePasswordBtn.setFocusPainted(false);
        updatePasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updatePasswordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePasswordBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        updatePasswordBtn.addActionListener(e -> onUpdatePassword());
        panel.add(updatePasswordBtn);

        panel.add(Box.createVerticalStrut(15));

        // Info note
        JLabel noteLabel = new JLabel("<html><body style='width:200px'>" +
            "<p style='color:#666;'>Password must be at least 4 characters. " +
            "Changes are saved to user.txt.</p></body></html>");
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(noteLabel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(50, 50, 50));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setEditable(editable);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        if (!editable) {
            field.setBackground(new Color(240, 240, 240));
        }
        
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return field;
    }

    private void loadProfileData() {
        if (currentLeader != null) {
            idField.setText(currentLeader.getId());
            usernameField.setText(currentLeader.getUsername());
            nameField.setText(currentLeader.getName());
            emailField.setText(currentLeader.getEmail());
            phoneField.setText(currentLeader.getPhone());
            roleField.setText("Academic Leader");
            setStatus("Profile loaded successfully.", false);
        } else {
            setStatus("Error: Could not load profile data.", true);
        }
    }

    private void onUpdatePassword() {
        if (currentLeader == null) {
            setStatus("Error: Profile not loaded.", true);
            return;
        }

        String oldPass = new String(oldPasswordField.getPassword()).trim();
        String newPass = new String(newPasswordField.getPassword()).trim();
        String confirmPass = new String(confirmPasswordField.getPassword()).trim();

        // Validation
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            setStatus("Please fill in all password fields.", true);
            return;
        }

        if (!currentLeader.getPassword().equals(oldPass)) {
            setStatus("Current password is incorrect.", true);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            setStatus("New password and confirmation do not match.", true);
            return;
        }

        if (newPass.length() < 4) {
            setStatus("New password must be at least 4 characters.", true);
            return;
        }

        // Update password
        try {
            currentLeader.setPassword(newPass);
            
            // Save to file
            List<user> allUsers = userRepo.getAllUsers();
            for (user u : allUsers) {
                if (u.getId().equals(leaderId)) {
                    u.setPassword(newPass);
                    break;
                }
            }
            userRepo.saveAllUsers((java.util.ArrayList<user>) allUsers);

            // Clear fields
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");

            setStatus("Password updated successfully!", false);
            JOptionPane.showMessageDialog(this, 
                "Password has been updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            setStatus("Error updating password: " + ex.getMessage(), true);
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? new Color(192, 57, 43) : new Color(39, 174, 96));
    }
}

