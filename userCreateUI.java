import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class userCreateUI extends JFrame {

    private JTextField usernameField, nameField, emailField, phoneField, ageField;
    private JPasswordField passwordField;
    private JComboBox<String> genderField = new JComboBox<>(new String[]{null, "male", "female"});
    private JComboBox<String> roleField = new JComboBox<>(new String[]{null, "admin", "academicLeader", "lecturer", "student"});
    private JButton saveButton, cancelButton;
    private userRepository repo = new userRepository();

    public userCreateUI() {
        setTitle("Create New User");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(32, 42, 68));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Create New User");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Form panel with padding
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Form fields
        formPanel.add(createFieldPanel("Username:", usernameField = new JTextField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Password:", passwordField = new JPasswordField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Name:", nameField = new JTextField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Gender:", genderField));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Phone:", phoneField = new JTextField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Age:", ageField = new JTextField(20)));
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(createFieldPanel("Role:", roleField));
        formPanel.add(Box.createVerticalStrut(30));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        saveButton = createStyledButton("Save", new Color(41, 128, 185));
        cancelButton = createStyledButton("Cancel", new Color(231, 76, 60));

        saveButton.addActionListener(e -> saveUser());
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

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setPreferredSize(new Dimension(100, 35));
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

    private void saveUser() {
        try {
            if (usernameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username cannot be empty.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            }else if (new String(passwordField.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Password cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
    
            }else if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Name cannot be empty.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (genderField.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a gender.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (isUsernameExists(usernameField.getText().trim())) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose a different username.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Email cannot be empty.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (!isValidEmail(emailField.getText().trim())) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (isEmailExists(emailField.getText().trim())) {
                JOptionPane.showMessageDialog(this,
                        "Email already exists. Please use a different email.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Phone cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
    
            } else if (!isValidMalaysiaPhoneNumber(phoneField.getText().trim())) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid Malaysia phone number (e.g., 0123456789 or 012-3456789).",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (ageField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Age cannot be empty.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (!isValidAge(Integer.parseInt(ageField.getText().trim()))) {
                JOptionPane.showMessageDialog(this,
                        "Age must be between 13 and 120 years old.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (roleField.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a role.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age = Integer.parseInt(ageField.getText().trim());
            int leaderId = -1;

            List<user> users = repo.getAllUsers();

            // Generate ID
            int newId = users.isEmpty() ? 1 : Integer.parseInt(users.get(users.size() - 1).getId()) + 1;

            user newUser = new user(
                String.valueOf(newId),
                usernameField.getText().trim(),
                new String(passwordField.getPassword()).trim(),
                nameField.getText().trim(),
                (String) genderField.getSelectedItem(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                age,
                (String) roleField.getSelectedItem(),
                leaderId
            );

            users.add(newUser);


            repo.saveAllUsers(users);

            JOptionPane.showMessageDialog(this, "User created successfully!");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Age, Leader ID, Lecturer ID, and Student ID must be valid numbers.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidMalaysiaPhoneNumber(String phone) {
        // Malaysian phone numbers: 01x-xxxxxxxx or 01xxxxxxxxx (with or without hyphen)
        String phoneRegex = "^(01)[0-9]{1}[-]?[0-9]{7,8}$";
        return phone.matches(phoneRegex);
    }

    private boolean isValidAge(int age) {
        // Age should be between 13 and 120 years old
        return age >= 13 && age <= 120;
    }

    private boolean isUsernameExists(String username) {
        List<user> users = repo.getAllUsers();
        for (user u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmailExists(String email) {
        List<user> users = repo.getAllUsers();
        for (user u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

}
