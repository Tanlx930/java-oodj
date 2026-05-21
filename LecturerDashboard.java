import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class LecturerDashboard extends JFrame {


    private Lecturer currentLecturer;

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private DashboardPanel dashboardPanel;
    private EditProfilePanel editProfilePanel;
    private DesignAssessmentPanel designAssessmentPanel;
    private MarksFeedbackPanel marksFeedbackPanel;
    private CreateQuizPanel createQuizPanel;
    private ViewQuizPanel viewQuizPanel;

    public LecturerDashboard(String lecturerId) {
        // Load lecturer
        currentLecturer = LecturerFileManager.loadLecturerById(lecturerId);
        if (currentLecturer == null) {
            currentLecturer = new Lecturer(lecturerId, "New Lecturer", "email@example.com", "12345");
            LecturerFileManager.saveOrUpdateLecturer(currentLecturer);
        }

        setTitle("APU Assessment Feedback System - Lecturer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
    // ================== TOP BAR ==================
    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setBackground(new Color(32, 42, 68));
    topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

    JLabel titleLabel = new JLabel("APU Assessment Feedback System");
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

    JLabel lecturerLabel = new JLabel("Lecturer: " + currentLecturer.getName() +
            "  (" + currentLecturer.getId() + ")");
    lecturerLabel.setForeground(Color.LIGHT_GRAY);
    lecturerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lecturerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    topBar.add(titleLabel, BorderLayout.WEST);
    topBar.add(lecturerLabel, BorderLayout.EAST);

    // ================== SIDE NAV ==================
    JPanel sideBar = new JPanel();
    sideBar.setBackground(new Color(23, 30, 52));
    sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
    sideBar.setBorder(new EmptyBorder(20, 10, 20, 10));
    sideBar.setPreferredSize(new Dimension(200, 0));

    JLabel menuTitle = new JLabel("Lecturer Menu");
    menuTitle.setForeground(Color.WHITE);
    menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    sideBar.add(menuTitle);
    sideBar.add(Box.createVerticalStrut(20));

   JButton btnDashboard = createMenuButton("Dashboard");
JButton btnProfile = createMenuButton("Profile");
JButton btnAssessments = createMenuButton("Assessments");
JButton btnCreateQuiz = createMenuButton("Create Quiz");
JButton btnViewQuiz = createMenuButton("View Quiz");
JButton btnMarksFeedback = createMenuButton("Marks & Feedback");
JButton btnExit = createMenuButton("Logout");

// Add buttons with consistent spacing
sideBar.add(btnDashboard);
sideBar.add(Box.createVerticalStrut(10));
sideBar.add(btnProfile);
sideBar.add(Box.createVerticalStrut(10));
sideBar.add(btnAssessments);
sideBar.add(Box.createVerticalStrut(10));
sideBar.add(btnCreateQuiz);
sideBar.add(Box.createVerticalStrut(10));
sideBar.add(btnViewQuiz);
sideBar.add(Box.createVerticalStrut(10));
sideBar.add(btnMarksFeedback);

sideBar.add(Box.createVerticalGlue());
sideBar.add(btnExit);


    // ================== CONTENT AREA ==================
    cardLayout = new CardLayout();

    contentPanel = new BackgroundPanel("Halo.gif");
    contentPanel.setLayout(cardLayout);
    contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
    contentPanel.setOpaque(false);

    dashboardPanel = new DashboardPanel();
    editProfilePanel = new EditProfilePanel();
    designAssessmentPanel = new DesignAssessmentPanel();
    createQuizPanel = new CreateQuizPanel(); // ✅ pass lecturer
    marksFeedbackPanel = new MarksFeedbackPanel();

    contentPanel.add(dashboardPanel, "DASHBOARD");
    contentPanel.add(editProfilePanel, "PROFILE");
    contentPanel.add(designAssessmentPanel, "ASSESSMENT");
    contentPanel.add(createQuizPanel, "QUIZ");
    contentPanel.add(marksFeedbackPanel, "MARKS");
    viewQuizPanel = new ViewQuizPanel();
    contentPanel.add(viewQuizPanel, "VIEW_QUIZ");

    // ================== MAIN LAYOUT ==================
    JPanel mainPanel = new BackgroundPanel("Halo.gif");
    mainPanel.setLayout(new BorderLayout());

    mainPanel.add(topBar, BorderLayout.NORTH);
    mainPanel.add(sideBar, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    setContentPane(mainPanel);

    // ================== BUTTON ACTIONS ==================
    btnDashboard.addActionListener(e -> {
        dashboardPanel.refreshStats();
        cardLayout.show(contentPanel, "DASHBOARD");
    });

    btnProfile.addActionListener(e -> {
        editProfilePanel.loadData();
        cardLayout.show(contentPanel, "PROFILE");
    });

    btnAssessments.addActionListener(e -> {
        designAssessmentPanel.refreshAssessmentList();
        cardLayout.show(contentPanel, "ASSESSMENT");
    });

    btnCreateQuiz.addActionListener(e -> {
        createQuizPanel.refreshAssessmentCombo();
        cardLayout.show(contentPanel, "QUIZ");
    });

   btnViewQuiz.addActionListener(e -> {
    viewQuizPanel.refreshAssessmentCombo();
    cardLayout.show(contentPanel, "VIEW_QUIZ");
});



    btnMarksFeedback.addActionListener(e -> {
        marksFeedbackPanel.refreshAssessmentCombo();
        cardLayout.show(contentPanel, "MARKS");
    });

    btnExit.addActionListener(e -> {
        dispose();
        new login().setVisible(true);
    });

    // default screen
    dashboardPanel.refreshStats();
    cardLayout.show(contentPanel, "DASHBOARD");
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

    // ================== DASHBOARD PANEL ==================
    private class DashboardPanel extends JPanel {
        private JLabel lblTotalAssessments;
        private JLabel lblTotalRecords;

        public DashboardPanel() {
            setLayout(new GridBagLayout());
            setOpaque(false); // let bg show through
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel title = new JLabel("Lecturer Dashboard");
            title.setFont(new Font("Monospaced", Font.BOLD, 28));
            title.setForeground(Color.BLACK); //
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            add(title, gbc);

            gbc.gridwidth = 1;

            lblTotalAssessments = createStatLabel("Total assessments: 0");
            lblTotalRecords = createStatLabel("Total assessment records: 0");

            gbc.gridy = 1;
            gbc.gridx = 0;
            add(lblTotalAssessments, gbc);
            gbc.gridx = 1;
            add(lblTotalRecords, gbc);
        }

        private JLabel createStatLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            return lbl;
        }

        public void refreshStats() {
            List<AssessmentType> types =
                    LecturerFileManager.loadAssessmentTypesForLecturer(currentLecturer.getId());
            List<AssessmentRecord> records =
                    LecturerFileManager.loadAssessmentRecordsForLecturer(currentLecturer.getId());

            lblTotalAssessments.setText("Total assessments: " + types.size());
            lblTotalRecords.setText("Total assessment records: " + records.size());
        }
    }

    // ================== EDIT PROFILE PANEL ==================
    private class EditProfilePanel extends JPanel {
        private JTextField txtId, txtName, txtEmail;
        private JPasswordField txtPassword;

        public EditProfilePanel() {
            setLayout(new GridBagLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;

            JLabel header = new JLabel("Edit Profile");
            header.setFont(new Font("Segoe UI", Font.BOLD, 22));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            add(header, gbc);
            gbc.gridwidth = 1;

           JLabel lblId = new JLabel("Lecturer ID:");
           JLabel lblName = new JLabel("Name:");
           JLabel lblEmail = new JLabel("Email:");
           JLabel lblPassword = new JLabel("Password:");

           // Apply bigger font to all labels
           Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
           lblId.setFont(labelFont);
           lblName.setFont(labelFont);
           lblEmail.setFont(labelFont);
           lblPassword.setFont(labelFont);

           // Optionally, set text color
           lblId.setForeground(Color.BLACK);
           lblName.setForeground(Color.BLACK);
           lblEmail.setForeground(Color.BLACK);
           lblPassword.setForeground(Color.BLACK);

           // Add labels to layout
           gbc.gridx = 0;
           gbc.gridy = 1;
           add(lblId, gbc);
           gbc.gridy = 2;
           add(lblName, gbc);
           gbc.gridy = 3;
           add(lblEmail, gbc);
           gbc.gridy = 4;
           add(lblPassword, gbc);
            

            txtId = new JTextField(25);
            txtId.setEditable(false);
            txtName = new JTextField(25);
            txtEmail = new JTextField(25);
            txtPassword = new JPasswordField(25);

            JButton btnSave = new JButton("Save");
            btnSave.setBackground(new Color(32, 142, 72));
            btnSave.setForeground(Color.BLACK);
            btnSave.setFocusPainted(false);
            btnSave.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Row 1
            gbc.gridy = 1;
            gbc.gridx = 0;
            add(lblId, gbc);
            gbc.gridx = 1;
            add(txtId, gbc);

            // Row 2
            gbc.gridy = 2;
            gbc.gridx = 0;
            add(lblName, gbc);
            gbc.gridx = 1;
            add(txtName, gbc);

            // Row 3
            gbc.gridy = 3;
            gbc.gridx = 0;
            add(lblEmail, gbc);
            gbc.gridx = 1;
            add(txtEmail, gbc);

            // Row 4
            gbc.gridy = 4;
            gbc.gridx = 0;
            add(lblPassword, gbc);
            gbc.gridx = 1;
            add(txtPassword, gbc);

            // Row 5
            gbc.gridy = 5;
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            add(btnSave, gbc);

            btnSave.addActionListener(e -> saveData());

            loadData();
        }

        public void loadData() {
            txtId.setText(currentLecturer.getId());
            txtName.setText(currentLecturer.getName());
            txtEmail.setText(currentLecturer.getEmail());
            txtPassword.setText(currentLecturer.getPassword());
        }

        private void saveData() {
            currentLecturer.setName(txtName.getText().trim());
            currentLecturer.setEmail(txtEmail.getText().trim());
            currentLecturer.setPassword(new String(txtPassword.getPassword()));

            LecturerFileManager.saveOrUpdateLecturer(currentLecturer);
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");
        }
    }

   // ================== DESIGN ASSESSMENT PANEL ==================
    private class DesignAssessmentPanel extends JPanel {
    private JTextField txtModuleCode, txtAssessmentName, txtMaxMarks, txtWeightage;
    private DefaultListModel<AssessmentType> listModel;
    private JList<AssessmentType> listAssessments;

    public DesignAssessmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        JLabel header = new JLabel("Assessments");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setOpaque(false);

        // ========== FORM PANEL ==========
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Assessment"));
        formPanel.setBackground(new Color(255, 255, 255, 200));

        txtModuleCode = new JTextField();
        txtAssessmentName = new JTextField();
        txtMaxMarks = new JTextField();
        txtWeightage = new JTextField();

        formPanel.add(new JLabel("Module / Course Code:"));
        formPanel.add(txtModuleCode);

        formPanel.add(new JLabel("Assessment Name:"));
        formPanel.add(txtAssessmentName);

        formPanel.add(new JLabel("Max Marks:"));
        formPanel.add(txtMaxMarks);

        formPanel.add(new JLabel("Weightage (%):"));
        formPanel.add(txtWeightage);

        JButton btnAdd = new JButton("Add Assessment");
        btnAdd.setBackground(new Color(0, 120, 215));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFocusPainted(false);

        formPanel.add(new JLabel());
        formPanel.add(btnAdd);

        // ========== LIST PANEL ==========
        listModel = new DefaultListModel<>();
        listAssessments = new JList<>(listModel);
        listAssessments.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(listAssessments);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Assessments"));

        // ========== BOTTOM BUTTON PANEL ==========
        JButton btnView = new JButton("View Details");
        btnView.setBackground(new Color(120, 180, 255));
        btnView.setForeground(Color.BLACK);
        btnView.setFocusPainted(false);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(200, 80, 80));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFocusPainted(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnView);
        bottomPanel.add(btnDelete);

        content.add(formPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);

        // ========== BUTTON ACTIONS ==========
        btnAdd.addActionListener(e -> addAssessmentType());
        btnDelete.addActionListener(e -> deleteSelectedAssessment());
        btnView.addActionListener(e -> viewSelectedAssessment());

        refreshAssessmentList();
    }

    private void addAssessmentType() {
        String moduleCode = txtModuleCode.getText().trim();
        String name = txtAssessmentName.getText().trim();
        String maxStr = txtMaxMarks.getText().trim();
        String weightStr = txtWeightage.getText().trim();

        if (moduleCode.isEmpty() || name.isEmpty() || maxStr.isEmpty() || weightStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            int max = Integer.parseInt(maxStr);
            double w = Double.parseDouble(weightStr);

            AssessmentType type = new AssessmentType(
                    currentLecturer.getId(), moduleCode, name, max, w);

            LecturerFileManager.saveAssessmentType(type);
            JOptionPane.showMessageDialog(this, "Assessment added.");
            clearForm();
            refreshAssessmentList();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Max marks and weightage must be numeric.");
        }
    }

    private void deleteSelectedAssessment() {
        AssessmentType selected = listAssessments.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an assessment to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this assessment?\n" + selected,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        LecturerFileManager.deleteAssessmentType(
                currentLecturer.getId(),
                selected.getModuleCode(),
                selected.getAssessmentName()
        );

        refreshAssessmentList();
        JOptionPane.showMessageDialog(this, "Assessment deleted.");
    }

   private void viewSelectedAssessment() {
    AssessmentType selected = listAssessments.getSelectedValue();
    if (selected == null) {
        JOptionPane.showMessageDialog(this, "Please select an assessment to view.");
        return;
    }

    // ===== Load student records =====
    List<AssessmentRecord> allRecords =
            LecturerFileManager.loadAssessmentRecordsForLecturer(currentLecturer.getId());

    String[] columns = {"Student ID", "Marks", "Max Marks", "Percent (%)", "Feedback"};
    List<AssessmentRecord> filtered = new ArrayList<>();

    for (AssessmentRecord r : allRecords) {
        if (r.getModuleCode().equals(selected.getModuleCode()) &&
            r.getAssessmentName().equals(selected.getAssessmentName())) {
            filtered.add(r);
        }
    }

    // if no records yet
    if (filtered.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "No student records found for this assessment yet.");
        return;
    }

    // copy list so we can modify inside listeners
    List<AssessmentRecord> filteredList = new ArrayList<>(filtered);

    Object[][] data = new Object[filteredList.size()][5];
    for (int i = 0; i < filteredList.size(); i++) {
        AssessmentRecord r = filteredList.get(i);
        double percent = (r.getMarks() / selected.getMaxMarks()) * 100.0;

        data[i][0] = r.getStudentId();
        data[i][1] = r.getMarks();
        data[i][2] = selected.getMaxMarks();
        data[i][3] = String.format("%.1f", percent);
        data[i][4] = r.getFeedback();
    }

    // ===== Table model (read-only cells) =====
    javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // use buttons, not direct editing
                }
            };

    JTable table = new JTable(model);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    table.setRowHeight(24);
    table.setForeground(Color.BLACK);
    table.setOpaque(false);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 5));

    javax.swing.table.JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 15));
    header.setOpaque(false);
    header.setBackground(new Color(255, 255, 255, 200));
    header.setForeground(Color.BLACK);

    JScrollPane tableScroll = new JScrollPane(table);
    tableScroll.setOpaque(false);
    tableScroll.getViewport().setOpaque(false);
    tableScroll.setBorder(BorderFactory.createTitledBorder("Student Progress"));

    if (tableScroll.getBorder() instanceof javax.swing.border.TitledBorder) {
        javax.swing.border.TitledBorder tb =
                (javax.swing.border.TitledBorder) tableScroll.getBorder();
        tb.setTitleColor(Color.BLACK);
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
    }

    // ===== Dialog with background =====
    JDialog dialog = new JDialog(LecturerDashboard.this, "Assessment Details", true);
    dialog.setSize(800, 550);
    dialog.setLocationRelativeTo(LecturerDashboard.this);

    BackgroundPanel bg = new BackgroundPanel("Halo.gif");
    bg.setLayout(new BorderLayout(15, 15));
    bg.setBorder(new EmptyBorder(15, 15, 15, 15));

    // === Info Panel (top) ===
    JPanel infoPanel = new JPanel(new GridBagLayout());
    infoPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    Font infoLabelFont = new Font("Segoe UI", Font.BOLD, 16);
    Font infoValueFont = new Font("Segoe UI", Font.PLAIN, 16);

    int row = 0;

    JLabel lblModuleText = new JLabel("Module / Course Code:");
    lblModuleText.setFont(infoLabelFont);
    lblModuleText.setForeground(Color.BLACK);
    JLabel lblModuleVal = new JLabel(selected.getModuleCode());
    lblModuleVal.setFont(infoValueFont);
    lblModuleVal.setForeground(Color.BLACK);

    gbc.gridx = 0; gbc.gridy = row;
    infoPanel.add(lblModuleText, gbc);
    gbc.gridx = 1;
    infoPanel.add(lblModuleVal, gbc);

    row++;
    JLabel lblAssText = new JLabel("Assessment Name:");
    lblAssText.setFont(infoLabelFont);
    lblAssText.setForeground(Color.BLACK);
    JLabel lblAssVal = new JLabel(selected.getAssessmentName());
    lblAssVal.setFont(infoValueFont);
    lblAssVal.setForeground(Color.BLACK);

    gbc.gridx = 0; gbc.gridy = row;
    infoPanel.add(lblAssText, gbc);
    gbc.gridx = 1;
    infoPanel.add(lblAssVal, gbc);

    row++;
    JLabel lblMaxText = new JLabel("Max Marks:");
    lblMaxText.setFont(infoLabelFont);
    lblMaxText.setForeground(Color.BLACK);
    JLabel lblMaxVal = new JLabel(String.valueOf(selected.getMaxMarks()));
    lblMaxVal.setFont(infoValueFont);
    lblMaxVal.setForeground(Color.BLACK);

    gbc.gridx = 0; gbc.gridy = row;
    infoPanel.add(lblMaxText, gbc);
    gbc.gridx = 1;
    infoPanel.add(lblMaxVal, gbc);

    row++;
    JLabel lblWText = new JLabel("Weightage (%):");
    lblWText.setFont(infoLabelFont);
    lblWText.setForeground(Color.BLACK);
    JLabel lblWVal = new JLabel(String.valueOf(selected.getWeightage()));
    lblWVal.setFont(infoValueFont);
    lblWVal.setForeground(Color.BLACK);

    gbc.gridx = 0; gbc.gridy = row;
    infoPanel.add(lblWText, gbc);
    gbc.gridx = 1;
    infoPanel.add(lblWVal, gbc);

    // === Bottom bar: Edit, Delete, Close ===
    JButton btnEdit = new JButton("Edit Marks");
    btnEdit.setBackground(new Color(120, 180, 255));
    btnEdit.setForeground(Color.BLACK);
    btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnEdit.setFocusPainted(false);

    JButton btnDelete = new JButton("Delete Record");
    btnDelete.setBackground(new Color(200, 80, 80));
    btnDelete.setForeground(Color.BLACK);
    btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnDelete.setFocusPainted(false);

    JButton btnClose = new JButton("Close");
    btnClose.setBackground(new Color(220, 220, 220));
    btnClose.setForeground(Color.BLACK);
    btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnClose.setFocusPainted(false);

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.setOpaque(false);
    bottomPanel.add(btnEdit);
    bottomPanel.add(btnDelete);
    bottomPanel.add(btnClose);

    // ====== BUTTON LOGIC ======

    // Edit marks for selected student
    btnEdit.addActionListener(e -> {
        int rowIndex = table.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(dialog, "Please select a student record to edit.");
            return;
        }

        AssessmentRecord rec = filteredList.get(rowIndex);

        String newMarksStr = JOptionPane.showInputDialog(
                dialog,
                "Enter new marks for student " + rec.getStudentId() + ":",
                rec.getMarks()
        );
        if (newMarksStr == null) return; // cancel

        try {
            double newMarks = Double.parseDouble(newMarksStr);
            if (newMarks < 0 || newMarks > selected.getMaxMarks()) {
                JOptionPane.showMessageDialog(dialog,
                        "Marks must be between 0 and " + selected.getMaxMarks() + ".");
                return;
            }

            String newFeedback = JOptionPane.showInputDialog(
                    dialog,
                    "Update feedback for student " + rec.getStudentId() + ":",
                    rec.getFeedback()
            );
            if (newFeedback == null) newFeedback = rec.getFeedback();

            // create updated record
            AssessmentRecord updated = new AssessmentRecord(
                    rec.getLecturerId(),
                    rec.getModuleCode(),
                    rec.getStudentId(),
                    rec.getAssessmentName(),
                    newMarks,
                    newFeedback
            );

            filteredList.set(rowIndex, updated);

            // recalc percent
            double percent = (newMarks / selected.getMaxMarks()) * 100.0;

            // update table display
            model.setValueAt(newMarks, rowIndex, 1);
            model.setValueAt(String.format("%.1f", percent), rowIndex, 3);
            model.setValueAt(newFeedback, rowIndex, 4);

            // persist to file
            LecturerFileManager.overwriteAssessmentRecordsForAssessment(
                    currentLecturer.getId(),
                    selected.getModuleCode(),
                    selected.getAssessmentName(),
                    filteredList
            );

            JOptionPane.showMessageDialog(dialog, "Marks updated successfully.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Marks must be numeric.");
        }
    });

    // Delete selected record
    btnDelete.addActionListener(e -> {
        int rowIndex = table.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(dialog, "Please select a student record to delete.");
            return;
        }

        AssessmentRecord rec = filteredList.get(rowIndex);

        int choice = JOptionPane.showConfirmDialog(
                dialog,
                "Delete record for student " + rec.getStudentId() + " ?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        filteredList.remove(rowIndex);
        model.removeRow(rowIndex);

        // persist to file
        LecturerFileManager.overwriteAssessmentRecordsForAssessment(
                currentLecturer.getId(),
                selected.getModuleCode(),
                selected.getAssessmentName(),
                filteredList
        );

        JOptionPane.showMessageDialog(dialog, "Record deleted.");
    });

    btnClose.addActionListener(e -> dialog.dispose());

    // Add components to bg panel
    bg.add(infoPanel, BorderLayout.NORTH);
    bg.add(tableScroll, BorderLayout.CENTER);
    bg.add(bottomPanel, BorderLayout.SOUTH);

    dialog.setContentPane(bg);
    dialog.setVisible(true);
}

    private void clearForm() {
        txtModuleCode.setText("");
        txtAssessmentName.setText("");
        txtMaxMarks.setText("");
        txtWeightage.setText("");
    }

    public void refreshAssessmentList() {
        listModel.clear();
        List<AssessmentType> types =
                LecturerFileManager.loadAssessmentTypesForLecturer(currentLecturer.getId());
        for (AssessmentType t : types) {
            listModel.addElement(t);
        }
    }
    
}

// ================== CREATE QUIZ PANEL ==================
private class CreateQuizPanel extends JPanel {

    private JComboBox<AssessmentType> cmbAssessments;
    private JTextField txtQuestion, txtA, txtB, txtC, txtD;
    private JComboBox<String> cmbCorrect;

    public CreateQuizPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        JLabel header = new JLabel("Create Quiz");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setForeground(Color.BLACK);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        form.setBackground(new Color(255, 255, 255, 180)); // semi transparent card

        cmbAssessments = new JComboBox<>();
        txtQuestion = new JTextField();
        txtA = new JTextField();
        txtB = new JTextField();
        txtC = new JTextField();
        txtD = new JTextField();

        cmbCorrect = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        form.add(label("Assessment Topic:"));
        form.add(cmbAssessments);

        form.add(label("Question:"));
        form.add(txtQuestion);

        form.add(label("Option A:"));
        form.add(txtA);

        form.add(label("Option B:"));
        form.add(txtB);

        form.add(label("Option C:"));
        form.add(txtC);

        form.add(label("Option D:"));
        form.add(txtD);

        form.add(label("Correct Answer:"));
        form.add(cmbCorrect);

        JButton btnSave = new JButton("Save Quiz Question");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(120, 180, 255));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);

        form.add(new JLabel());
        form.add(btnSave);

        add(form, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveQuizQuestion());

        refreshAssessmentCombo();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(Color.BLACK);
        return l;
    }

    public void refreshAssessmentCombo() {
        cmbAssessments.removeAllItems();
        List<AssessmentType> types =
                LecturerFileManager.loadAssessmentTypesForLecturer(currentLecturer.getId());
        for (AssessmentType t : types) {
            cmbAssessments.addItem(t);
        }
    }

   private void saveQuizQuestion() {
    AssessmentType selected = (AssessmentType) cmbAssessments.getSelectedItem();
    if (selected == null) {
        JOptionPane.showMessageDialog(this, "Please create/select an assessment first.");
        return;
    }

    String q = txtQuestion.getText().trim();
    String a = txtA.getText().trim();
    String b = txtB.getText().trim();
    String c = txtC.getText().trim();
    String d = txtD.getText().trim();
    String correct = (String) cmbCorrect.getSelectedItem(); // "A", "B", "C", "D"

    if (q.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in question + all options.");
        return;
    }

    // ✅ create quiz object
    QuizQuestion qq = new QuizQuestion(
            currentLecturer.getId(),
            selected.getModuleCode(),
            selected.getAssessmentName(),
            q, a, b, c, d,
            correct
    );

    // ✅ write into quiz_questions.txt
    LecturerFileManager.saveQuizQuestion(qq);

    JOptionPane.showMessageDialog(this,
            "Quiz question saved to quiz_questions.txt ✅\n\n" +
            "Module: " + selected.getModuleCode() + "\n" +
            "Assessment: " + selected.getAssessmentName() + "\n" +
            "Correct: " + correct
    );

    // clear
    txtQuestion.setText("");
    txtA.setText("");
    txtB.setText("");
    txtC.setText("");
    txtD.setText("");
    cmbCorrect.setSelectedIndex(0);
}
}

// ================== VIEW QUIZ PANEL ==================
private class ViewQuizPanel extends JPanel {

    private JComboBox<AssessmentType> cmbAssessments;
    private JTable table;
    private javax.swing.table.DefaultTableModel model;

    public ViewQuizPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        JLabel header = new JLabel("View Quiz");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setForeground(Color.BLACK);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setOpaque(false);

        JLabel lbl = new JLabel("Assessment:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.BLACK);

        cmbAssessments = new JComboBox<>();
        JButton btnLoad = new JButton("Load");
        btnLoad.setBackground(new Color(120, 180, 255));
        btnLoad.setForeground(Color.BLACK);
        btnLoad.setFocusPainted(false);

        top.add(lbl);
        top.add(cmbAssessments);
        top.add(btnLoad);

        // ✅ combine header + top into one NORTH panel
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(header, BorderLayout.NORTH);
        north.add(top, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        String[] cols = {"Module", "Assessment", "Question", "A", "B", "C", "D", "Correct"};
        model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Quiz Questions"));
        add(sp, BorderLayout.CENTER);

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.setBackground(new Color(200, 80, 80));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFocusPainted(false);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(btnDelete);

        add(bottom, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadTable());
        btnDelete.addActionListener(e -> deleteSelected());

        refreshAssessmentCombo();
    }

    public void refreshAssessmentCombo() {
        cmbAssessments.removeAllItems();
        List<AssessmentType> types =
                LecturerFileManager.loadAssessmentTypesForLecturer(currentLecturer.getId());
        for (AssessmentType t : types) cmbAssessments.addItem(t);
    }

    private void loadTable() {
        model.setRowCount(0);

        AssessmentType selected = (AssessmentType) cmbAssessments.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an assessment.");
            return;
        }

        List<QuizQuestion> list =
                LecturerFileManager.loadQuizQuestionsForLecturer(currentLecturer.getId());

        for (QuizQuestion q : list) {
            if (q.getModuleCode().equals(selected.getModuleCode()) &&
                q.getAssessmentName().equals(selected.getAssessmentName())) {

                model.addRow(new Object[]{
                        q.getModuleCode(),
                        q.getAssessmentName(),
                        q.getQuestion(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD(),
                        q.getCorrectOption()
                });
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No quiz questions found for this assessment yet.");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a question first.");
            return;
        }

        String module = (String) model.getValueAt(row, 0);
        String assessment = (String) model.getValueAt(row, 1);
        String question = (String) model.getValueAt(row, 2);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete this quiz question?\n\n" + question,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        LecturerFileManager.deleteQuizQuestion(currentLecturer.getId(), module, assessment, question);

        model.removeRow(row);
        JOptionPane.showMessageDialog(this, "Deleted ✅");
    }
}

    // ================== MARKS & FEEDBACK PANEL ==================
    private class MarksFeedbackPanel extends JPanel {
        private JComboBox<AssessmentType> cmbAssessments;
        private JTextField txtStudentId, txtMarks;
        private JTextArea txtFeedback;

        public MarksFeedbackPanel() {
            setLayout(new BorderLayout(10, 10));
            setOpaque(false);

            JLabel header = new JLabel("Marks & Feedback");
            header.setFont(new Font("Segoe UI", Font.BOLD, 22));
            header.setBorder(new EmptyBorder(10, 10, 10, 10));
            add(header, BorderLayout.NORTH);

            JPanel topPanel = new JPanel(new GridLayout(4, 2, 8, 8));
            topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            topPanel.setBackground(new Color(255, 255, 255, 200));

            cmbAssessments = new JComboBox<>();
            txtStudentId = new JTextField();
            txtMarks = new JTextField();

            topPanel.add(new JLabel("Assessment:"));
            topPanel.add(cmbAssessments);
            topPanel.add(new JLabel("Student ID:"));
            topPanel.add(txtStudentId);
            topPanel.add(new JLabel("Marks:"));
            topPanel.add(txtMarks);

            JButton btnSave = new JButton("Save");
            btnSave.setBackground(new Color(0, 120, 215));
            btnSave.setForeground(Color.BLUE);
            btnSave.setFocusPainted(false);

            topPanel.add(new JLabel());
            topPanel.add(btnSave);

            txtFeedback = new JTextArea(6, 30);
            txtFeedback.setLineWrap(true);
            txtFeedback.setWrapStyleWord(true);
            JScrollPane feedbackScroll = new JScrollPane(txtFeedback);
            feedbackScroll.setBorder(BorderFactory.createTitledBorder("Feedback"));

            add(topPanel, BorderLayout.CENTER);
            add(feedbackScroll, BorderLayout.SOUTH);

            btnSave.addActionListener(e -> saveRecord());
        }

        public void refreshAssessmentCombo() {
            cmbAssessments.removeAllItems();
            List<AssessmentType> types =
                    LecturerFileManager.loadAssessmentTypesForLecturer(currentLecturer.getId());
            for (AssessmentType t : types) {
                cmbAssessments.addItem(t);
            }
        }

        private void saveRecord() {
            AssessmentType selected = (AssessmentType) cmbAssessments.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an assessment.");
                return;
            }

            String studentId = txtStudentId.getText().trim();
            String marksStr = txtMarks.getText().trim();
            String feedback = txtFeedback.getText().trim();

            if (studentId.isEmpty() || marksStr.isEmpty() || feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            try {
                double marks = Double.parseDouble(marksStr);
                if (marks < 0 || marks > selected.getMaxMarks()) {
                    JOptionPane.showMessageDialog(this,
                            "Marks must be between 0 and " + selected.getMaxMarks());
                    return;
                }

                AssessmentRecord record = new AssessmentRecord(
                        currentLecturer.getId(),
                        selected.getModuleCode(),
                        studentId,
                        selected.getAssessmentName(),
                        marks,
                        feedback
                );

                LecturerFileManager.saveAssessmentRecord(record);
                JOptionPane.showMessageDialog(this, "Record saved.");
                txtStudentId.setText("");
                txtMarks.setText("");
                txtFeedback.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Marks must be numeric.");
            }
        }
    }
    // ---------- Background panel class ----------
    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
