import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class userDetailsEditUI extends JDialog {
    private final JTextField usernameField;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField ageField;
    private final JComboBox<String> genderField;
    private final JComboBox<String> roleField;
    private JCheckBox resetPasswordCheckbox;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private boolean confirmed;
    private boolean passwordReset;
    private String newPassword;
    private String userId;

    public userDetailsEditUI(JFrame parent, String userId, String username, String name, String gender, String email, String phone, int age, String role) {
        super(parent, "Edit User", true);
        this.userId = userId;
        setSize(500, 700);
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

        JLabel titleLabel = new JLabel("Edit User");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Form panel with padding
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Username Field (Non-editable)
        formPanel.add(createFieldPanel("Username:", usernameField = new JTextField(username, 20)));
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(230, 230, 230));
        formPanel.add(Box.createVerticalStrut(12));

        // Name Field
        formPanel.add(createFieldPanel("Name:", nameField = new JTextField(name, 20)));
        formPanel.add(Box.createVerticalStrut(12));

        // Gender Field - Dropdown
        genderField = new JComboBox<>(new String[]{"male", "female"});
        genderField.setSelectedItem(gender.toLowerCase());
        formPanel.add(createFieldPanel("Gender:", genderField));
        formPanel.add(Box.createVerticalStrut(12));

        // Email Field
        formPanel.add(createFieldPanel("Email:", emailField = new JTextField(email, 20)));
        formPanel.add(Box.createVerticalStrut(12));

        // Phone Field
        formPanel.add(createFieldPanel("Phone:", phoneField = new JTextField(phone, 20)));
        formPanel.add(Box.createVerticalStrut(12));

        // Age Field
        formPanel.add(createFieldPanel("Age:", ageField = new JTextField(String.valueOf(age), 20)));
        formPanel.add(Box.createVerticalStrut(12));

        // Role Field - Dropdown
        roleField = new JComboBox<>(new String[]{"Admin", "Academic Leader", "Lecturer", "Student"});
        // Map the incoming role to display format
        roleField.setSelectedItem(formatRoleForDisplay(role));
        formPanel.add(createFieldPanel("Role:", roleField));
        formPanel.add(Box.createVerticalStrut(18));

        // ====== Password Reset Section ======
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(400, 2));
        separator.setForeground(new Color(180, 180, 180));
        formPanel.add(separator);
        formPanel.add(Box.createVerticalStrut(12));

        JLabel passwordSectionLabel = new JLabel("Password Reset");
        passwordSectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordSectionLabel.setForeground(new Color(50, 50, 50));
        passwordSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordSectionLabel);
        formPanel.add(Box.createVerticalStrut(8));

        // Reset password checkbox
        resetPasswordCheckbox = new JCheckBox("Reset password for this user");
        resetPasswordCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resetPasswordCheckbox.setBackground(new Color(240, 240, 240));
        resetPasswordCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetPasswordCheckbox.setMaximumSize(new Dimension(400, 30));
        formPanel.add(resetPasswordCheckbox);
        formPanel.add(Box.createVerticalStrut(8));

        // New Password Field
        newPasswordField = new JPasswordField(20);
        newPasswordField.setEnabled(false);
        JPanel newPwPanel = createFieldPanel("New Password:", newPasswordField);
        formPanel.add(newPwPanel);
        formPanel.add(Box.createVerticalStrut(8));

        // Confirm Password Field
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setEnabled(false);
        JPanel confirmPwPanel = createFieldPanel("Confirm Password:", confirmPasswordField);
        formPanel.add(confirmPwPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Toggle password fields when checkbox is clicked
        resetPasswordCheckbox.addActionListener(_ -> {
            boolean selected = resetPasswordCheckbox.isSelected();
            newPasswordField.setEnabled(selected);
            confirmPasswordField.setEnabled(selected);
            if (!selected) {
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton confirmButton = createStyledButton("Confirm", new Color(41, 128, 185));
        JButton cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));

        confirmButton.addActionListener(_ -> {
            if (validateForm()) {
                confirmed = true;
                if (resetPasswordCheckbox.isSelected()) {
                    passwordReset = true;
                    newPassword = new String(newPasswordField.getPassword()).trim();
                }
                dispose();
            }
        });

        cancelButton.addActionListener(_ -> dispose());

        buttonPanel.add(confirmButton);
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

    private boolean validateForm() {
        // Validate Name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Email
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if email is taken by another user
        if (isEmailTakenByOther(emailField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Email already exists for another user.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Phone
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isValidMalaysiaPhoneNumber(phoneField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Malaysia phone number (e.g., 0123456789 or 012-3456789).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Age
        if (ageField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Age cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 13 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be between 13 and 120 years old.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate Password Reset
        if (resetPasswordCheckbox.isSelected()) {
            String newPw = new String(newPasswordField.getPassword()).trim();
            String confirmPw = new String(confirmPasswordField.getPassword()).trim();

            if (newPw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "New password cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (newPw.length() < 4) {
                JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (!newPw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidMalaysiaPhoneNumber(String phone) {
        String phoneRegex = "^(01)[0-9]{1}[-]?[0-9]{7,8}$";
        return phone.matches(phoneRegex);
    }

    private boolean isEmailTakenByOther(String email) {
        userRepository repo = new userRepository();
        List<user> users = repo.getAllUsers();
        for (user u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private String formatRoleForDisplay(String role) {
        switch (role.toLowerCase()) {
            case "admin": return "Admin";
            case "academicleader": case "academic leader": return "Academic Leader";
            case "lecturer": return "Lecturer";
            case "student": return "Student";
            default: return role;
        }
    }

    private String formatRoleForStorage(String displayRole) {
        switch (displayRole) {
            case "Admin": return "admin";
            case "Academic Leader": return "academicLeader";
            case "Lecturer": return "lecturer";
            case "Student": return "student";
            default: return displayRole;
        }
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setPreferredSize(new Dimension(120, 35));
        label.setForeground(new Color(50, 50, 50));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
            ));
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ((JPasswordField) field).setBorder(BorderFactory.createCompoundBorder(
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isPasswordReset() {
        return passwordReset;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getGender() {
        return (String) genderField.getSelectedItem();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public int getAge() {
        return Integer.parseInt(ageField.getText().trim());
    }

    public String getRole() {
        return formatRoleForStorage((String) roleField.getSelectedItem());
    }
}