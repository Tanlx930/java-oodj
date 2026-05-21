import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class editProfile extends JFrame {
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    public editProfile(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        
        // Load user data from file
        loadUserData();
        
        // Initialize UI
        initializeUI();
    }

    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].equals(studentId)) {
                    studentName = parts[3];
                    studentEmail = parts[5];
                    studentPhone = parts[6];
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
            studentEmail = "";
            studentPhone = "";
        }
    }

    private void initializeUI() {
        setTitle("Edit Profile - " + studentName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Edit Your Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(20, 20, 20));
        mainPanel.add(titleLabel, gbc);

        // Full Name Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(nameLabel, gbc);

        // Full Name Field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(25);
        nameField.setText(studentName);
        nameField.setEditable(false); // Name is read-only
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameField.setBackground(new Color(230, 230, 230));
        mainPanel.add(nameField, gbc);

        // Email Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(emailLabel, gbc);

        // Email Field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(25);
        emailField.setText(studentEmail);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(emailField, gbc);

        // Phone Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(phoneLabel, gbc);

        // Phone Field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        phoneField = new JTextField(25);
        phoneField.setText(studentPhone);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(phoneField, gbc);

        // Button Panel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(120, 40));
        saveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        saveBtn.setBackground(new Color(100, 200, 100));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> saveChanges());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setBackground(new Color(200, 100, 100));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private void saveChanges() {
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();

        // Validation
        if (newEmail.isEmpty() || newPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and Phone cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation (simple check)
        if (!newEmail.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Phone validation (simple check)
        if (newPhone.length() < 10) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the file
        if (updateUserFile(newEmail, newPhone)) {
            studentEmail = newEmail;
            studentPhone = newPhone;
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean updateUserFile(String newEmail, String newPhone) {
        try {
            File inputFile = new File("user.txt");
            File tempFile = new File("src/user_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                // Check if this is the current student's record
                if (parts.length >= 7 && parts[0].equals(studentId)) {
                    // Update email and phone
                    parts[5] = newEmail;
                    parts[6] = newPhone;

                    // Write updated line
                    writer.write(String.join(",", parts));
                    found = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (found) {
                // Replace original file with updated temp file
                if (inputFile.delete()) {
                    return tempFile.renameTo(inputFile);
                }
            } else {
                tempFile.delete();
            }

        } catch (IOException e) {
            System.out.println("Error updating user file: " + e.getMessage());
        }

        return false;
    }
}
