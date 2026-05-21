import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * StudentDashboard class extends DefaultDashboardUI.
 * Features:
 * - Register for Classes
 * - View Schedule
 * - Take Quizzes (One attempt only)
 * - Check Results
 * - Submit Feedback
 * - Edit Profile
 */

public class studentDashboard extends DefaultDashboardUI {
    
    // User Data
    private String studentName;
    private String studentId;
    private String studentUsername;
    private String studentPassword;
    private String studentEmail;
    private String studentPhone;
    
    // Services & Repositories (Composition)
    private ProfileEditService profileEditService;
    private userRepository userRepo;
    private ClassRepository classRepo; 
    private QuizRepository quizRepo; 
    private StudentAssessmentRepository assessmentRepo; 
    private FeedbackRepository feedbackRepo;  

    // UI Components that need refreshing
    private JTable scheduleTable;
    private DefaultTableModel scheduleTableModel;

    public studentDashboard(String userName, String userId) {
        // 1. Call Parent Constructor (Inheritance)
        super("APU Student Services", "Student", userName);
        this.studentName = userName;
        this.studentId = userId;
        
        // 2. Initialize Dependencies (Composition)
        this.userRepo = new userRepository();
        this.profileEditService = new ProfileEditService(userRepo);
        this.classRepo = new ClassRepository(); 
        this.quizRepo = new QuizRepository(); 
        this.assessmentRepo = new StudentAssessmentRepository();
        this.feedbackRepo = new FeedbackRepository();
        
        // 3. Load Data & UI
        loadUserData();
        initializeStudentUI();
    }


    // Load specific student details from user.txt

    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].equals(studentId)) {
                    studentUsername = parts[1];
                    studentPassword = parts[2];
                    studentName = parts[3];
                    studentEmail = parts[5];
                    studentPhone = parts[6];
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
    }

    
    //Initialize the specific UI layout for students

    private void initializeStudentUI() {
        // Create Panels
        JPanel dashboardPanel = createDashboardPanel(studentName);
        JPanel registerClassPanel = createRegisterClassPanel();
        JPanel viewClassesPanel = createViewClassesPanel();
        JPanel checkResultPanel = createCheckResultPanel(); 
        JPanel feedbackPanel = createFeedbackPanel();       
        JPanel profilePanel = createProfilePanel();

        // Add to CardLayout
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(registerClassPanel, "REGISTER_CLASS");
        contentPanel.add(viewClassesPanel, "VIEW_CLASSES");
        contentPanel.add(checkResultPanel, "CHECK_RESULT");
        contentPanel.add(feedbackPanel, "FEEDBACK");
        contentPanel.add(profilePanel, "PROFILE");

        // Setup Sidebar
        setupStudentSidebar();
        
        // Default View
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    // =================================================================================
    // 1. REGISTER CLASS PANEL
    // =================================================================================
    private JPanel createRegisterClassPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Register for Class");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(titleLabel, gbc);

        // Label
        gbc.gridy = 1;
        JLabel selectLabel = new JLabel("Select a Class to Register:");
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(selectLabel, gbc);

        // Combo Box
        gbc.gridy = 2;
        JComboBox<ClassSession> classCombo = new JComboBox<>();
        List<ClassSession> availableClasses = classRepo.getAllClasses();
        for (ClassSession c : availableClasses) {
            classCombo.addItem(c);
        }
        classCombo.setPreferredSize(new Dimension(500, 35));
        panel.add(classCombo, gbc);

        // Register Button
        gbc.gridy = 3;
        JButton registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(150, 40));
        registerBtn.setBackground(new Color(0, 120, 215));
        registerBtn.setForeground(Color.WHITE);
        
        registerBtn.addActionListener(e -> {
            ClassSession selected = (ClassSession) classCombo.getSelectedItem();
            if (selected != null) {
                boolean success = classRepo.registerStudent(studentId, selected.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Successfully registered for:\n" + selected.getClassName());
                    refreshScheduleTable();
                } else {
                    JOptionPane.showMessageDialog(this, "You are already registered for this class!", "Duplicate Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        panel.add(registerBtn, gbc);

        return panel;
    }

    // =================================================================================
    // 2. VIEW REGISTERED CLASSES PANEL 
    // =================================================================================
    private JPanel createViewClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("My Class Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton btnQuiz = new JButton("Do Quiz");
        btnQuiz.setBackground(new Color(255, 140, 0)); // Orange
        btnQuiz.setForeground(Color.WHITE);
        btnQuiz.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuiz.setFocusPainted(false);
        
        JButton btnRefresh = new JButton("Refresh Schedule");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> refreshScheduleTable());
        
        btnPanel.add(btnQuiz);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Class Name", "Module", "Day", "Time", "Mode", "Date"};
        scheduleTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        scheduleTable = new JTable(scheduleTableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        scheduleTable.getTableHeader().setBackground(new Color(23, 30, 52));
        scheduleTable.getTableHeader().setForeground(Color.WHITE);
        
        refreshScheduleTable();

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Quiz Button Action
        btnQuiz.addActionListener(e -> {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a class from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String moduleCode = (String) scheduleTableModel.getValueAt(selectedRow, 1);
            
            List<QuizQuestion> questions = quizRepo.getQuestionsByModule(moduleCode);
            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No quiz available for module: " + moduleCode, "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Check if already attempted
                String assessmentName = questions.get(0).getAssessmentName();
                if (assessmentRepo.hasCompletedAssessment(studentId, moduleCode, assessmentName)) {
                     JOptionPane.showMessageDialog(this, 
                        "You have already attempted the quiz: " + assessmentName + "\nYou cannot take it again.", 
                        "Access Denied", 
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    new QuizDialog((JFrame) SwingUtilities.getWindowAncestor(this), studentId, moduleCode, questions).setVisible(true);
                }
            }
        });

        return panel;
    }

    private void refreshScheduleTable() {
        if (scheduleTableModel == null) return;
        scheduleTableModel.setRowCount(0);
        List<ClassSession> myClasses = classRepo.getClassesForStudent(studentId);
        for (ClassSession c : myClasses) {
            Object[] row = {
                c.getClassName(),
                c.getModuleCode(),
                c.getDay(),
                c.getStartTime() + " - " + c.getEndTime(),
                c.getMode(),
                c.getDate()
            };
            scheduleTableModel.addRow(row);
        }
    }

    // =================================================================================
    // 3. CHECK RESULT PANEL
    // =================================================================================
    private JPanel createCheckResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("My Assessment Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        
        JButton btnRefresh = new JButton("Refresh Results");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Module", "Assessment", "Score", "Max Marks", "Percentage", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable resultTable = new JTable(model);
        resultTable.setRowHeight(30);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultTable.getTableHeader().setBackground(new Color(23, 30, 52));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        
        Runnable loadData = () -> {
            model.setRowCount(0);
            List<AssessmentRecord> records = assessmentRepo.getStudentRecords(studentId);
            for (AssessmentRecord r : records) {
                int maxMarks = assessmentRepo.getMaxMarks(r.getModuleCode(), r.getAssessmentName());
                double percentage = (r.getMarks() / maxMarks) * 100.0;
                Object[] row = {
                    r.getModuleCode(),
                    r.getAssessmentName(),
                    r.getMarks(),
                    maxMarks,
                    String.format("%.1f%%", percentage),
                    r.getFeedback()
                };
                model.addRow(row);
            }
        };

        loadData.run();
        btnRefresh.addActionListener(e -> loadData.run());

        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // =================================================================================
    // 4. FEEDBACK PANEL
    // =================================================================================
    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Provide Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(titleLabel, gbc);

        // Feedback Type
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel typeLabel = new JLabel("Feedback Context:");
        // CHANGED TO BOLD HERE
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        panel.add(typeLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>();
        typeCombo.addItem("General Feedback");
        typeCombo.addItem("System Issue");
        
        // Add Registered Classes
        List<ClassSession> myClasses = classRepo.getClassesForStudent(studentId);
        for (ClassSession c : myClasses) {
            typeCombo.addItem("Module: " + c.getModuleCode() + " - " + c.getClassName());
        }
        
        typeCombo.setPreferredSize(new Dimension(300, 30));
        panel.add(typeCombo, gbc);

        // Rating Slider
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel rateLabel = new JLabel("Rate Satisfaction:");
        // CHANGED TO BOLD
        rateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(rateLabel, gbc);

        gbc.gridx = 1;
        JSlider ratingSlider = new JSlider(1, 5, 5); 
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setOpaque(false);
        ratingSlider.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(ratingSlider, gbc);

        // Message Area
        gbc.gridx = 0; gbc.gridy = 3; 
        JLabel msgLabel = new JLabel("Your Message:");
        // CHANGED TO BOLD
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        panel.add(msgLabel, gbc);

        gbc.gridx = 1;
        JTextArea msgArea = new JTextArea(8, 30);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(msgArea);
        panel.add(scroll, gbc);

        // Submit Button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST; 
        gbc.fill = GridBagConstraints.NONE;
        
        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.setBackground(new Color(40, 167, 69));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setPreferredSize(new Dimension(160, 40));
        
        submitBtn.addActionListener(e -> {
            String message = msgArea.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a message.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String context = (String) typeCombo.getSelectedItem();
            int rating = ratingSlider.getValue(); 

            Feedback fb = new Feedback(studentId, context, message, rating);
            
            boolean success = feedbackRepo.saveFeedback(fb);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Thank you! Your feedback (Rating: " + rating + "/5) has been submitted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                msgArea.setText("");
                typeCombo.setSelectedIndex(0);
                ratingSlider.setValue(5); 
            } else {
                JOptionPane.showMessageDialog(this, "Error saving feedback.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(submitBtn, gbc);
        return panel;
    }

    // =================================================================================
    // 5. PROFILE PANEL
    // =================================================================================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Edit Personal Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        
        addProfileRow(panel, gbc, "Full Name (Read-Only):", studentName, false, null);
        
        JTextField usernameField = new JTextField(studentUsername, 20);
        addProfileRow(panel, gbc, "Username:", null, true, usernameField);
        
        JPasswordField passwordField = new JPasswordField(studentPassword, 20);
        addProfileRow(panel, gbc, "Password:", null, true, passwordField);
        
        addProfileRow(panel, gbc, "Email (Read-Only):", studentEmail, false, null);

        gbc.gridx = 1; gbc.gridy++;
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(40, 167, 69));
        saveBtn.setForeground(Color.WHITE);
        
        saveBtn.addActionListener(e -> {
            String newInfoUser = usernameField.getText();
            String newInfoPass = new String(passwordField.getPassword());
            
            ProfileEditService.UpdateResult result = profileEditService.updateStudentProfile(
                studentId, newInfoUser, newInfoPass
            );

            if (result.isSuccess()) {
                studentUsername = newInfoUser;
                studentPassword = newInfoPass;
                JOptionPane.showMessageDialog(this, result.getMessage());
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                usernameField.setText(studentUsername);
                passwordField.setText(studentPassword);
            }
        });
        panel.add(saveBtn, gbc);

        return panel;
    }

    private void addProfileRow(JPanel p, GridBagConstraints gbc, String label, String value, boolean editable, JComponent field) {
        gbc.gridx = 0; 
        p.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        if (editable) {
            p.add(field, gbc);
        } else {
            JTextField tf = new JTextField(value, 20);
            tf.setEditable(false);
            tf.setBackground(new Color(230, 230, 230));
            p.add(tf, gbc);
        }
        gbc.gridy++;
    }

    // =================================================================================
    // 6. SIDEBAR NAVIGATION
    // =================================================================================
    private void setupStudentSidebar() {
        Container contentPane = getContentPane();
        JPanel mainPanel = (JPanel) contentPane;
        
        JPanel studentSideBar = new JPanel();
        studentSideBar.setBackground(new Color(23, 30, 52));
        studentSideBar.setLayout(new BoxLayout(studentSideBar, BoxLayout.Y_AXIS));
        studentSideBar.setBorder(new EmptyBorder(20, 10, 20, 10));
        studentSideBar.setPreferredSize(new Dimension(200, 650));

        JLabel menuTitle = new JLabel("Student Menu");
        menuTitle.setForeground(Color.WHITE);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        studentSideBar.add(menuTitle);
        studentSideBar.add(Box.createVerticalStrut(20));

        addButton(studentSideBar, "Dashboard", "DASHBOARD");
        studentSideBar.add(Box.createVerticalStrut(10));
        addButton(studentSideBar, "Register Class", "REGISTER_CLASS");
        studentSideBar.add(Box.createVerticalStrut(10));
        addButton(studentSideBar, "My Schedule", "VIEW_CLASSES"); 
        studentSideBar.add(Box.createVerticalStrut(10));
        addButton(studentSideBar, "Check Result", "CHECK_RESULT");
        studentSideBar.add(Box.createVerticalStrut(10));
        addButton(studentSideBar, "Feedback", "FEEDBACK");
        studentSideBar.add(Box.createVerticalStrut(10));
        addButton(studentSideBar, "Edit Profile", "PROFILE");
        studentSideBar.add(Box.createVerticalGlue());
        
        JButton btnLogout = createMenuButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new login().setVisible(true);
        });
        studentSideBar.add(btnLogout);

        mainPanel.add(studentSideBar, BorderLayout.WEST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void addButton(JPanel panel, String text, String cardName) {
        JButton btn = createMenuButton(text);
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if(cardName.equals("VIEW_CLASSES")) refreshScheduleTable();
        });
        panel.add(btn);
    }

    // =================================================================================
    // 7. DASHBOARD PANEL
    // =================================================================================
    private JPanel createDashboardPanel(String studentName) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 10, 20);

        JLabel welcome = new JLabel("Welcome, " + studentName + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcome.setForeground(new Color(0, 51, 102));
        panel.add(welcome, gbc);

        gbc.gridy = 1;
        JLabel sub = new JLabel("Select an option from the menu to begin.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(sub, gbc);
        return panel;
    }
}