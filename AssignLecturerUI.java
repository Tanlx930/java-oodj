import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


public class AssignLecturerUI extends JPanel {

    private String leaderId;
    private userRepository userRepo;
    private ModuleRepository moduleRepo;
    
    // UI Components
    private JComboBox<String> moduleComboBox;
    private JComboBox<String> lecturerComboBox;
    private JTextArea detailsArea;
    private JTable moduleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    // Data cache
    private ArrayList<Module> myModules;
    private List<user> myLecturers;

    public AssignLecturerUI(String leaderId, userRepository userRepo, ModuleRepository moduleRepo) {
        this.leaderId = leaderId;
        this.userRepo = userRepo;
        this.moduleRepo = moduleRepo;
        
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Assign Lecturers to Modules");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(32, 42, 68));
        add(titleLabel, BorderLayout.NORTH);

        // Main content - split into left and right panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setOpaque(false);

        // Left panel - Assignment form
        JPanel leftPanel = createAssignmentFormPanel();
        splitPane.setLeftComponent(leftPanel);

        // Right panel - Module table
        JPanel rightPanel = createModuleTablePanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createAssignmentFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel formTitle = new JLabel("Assignment Form");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(41, 128, 185));

        // Form content
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Module selection
        JLabel moduleLabel = new JLabel("Select Module:");
        moduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moduleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(moduleLabel);
        formPanel.add(Box.createVerticalStrut(5));

        moduleComboBox = new JComboBox<>();
        moduleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moduleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        moduleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        moduleComboBox.addActionListener(e -> updateDetails());
        formPanel.add(moduleComboBox);
        formPanel.add(Box.createVerticalStrut(15));

        // Lecturer selection
        JLabel lecturerLabel = new JLabel("Select Lecturer:");
        lecturerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lecturerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lecturerLabel);
        formPanel.add(Box.createVerticalStrut(5));

        lecturerComboBox = new JComboBox<>();
        lecturerComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lecturerComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        lecturerComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        lecturerComboBox.addActionListener(e -> updateDetails());
        formPanel.add(lecturerComboBox);
        formPanel.add(Box.createVerticalStrut(15));

        // Details area
        JLabel detailsLabel = new JLabel("Assignment Details:");
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(detailsLabel);
        formPanel.add(Box.createVerticalStrut(5));

        detailsArea = new JTextArea(8, 20);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBackground(new Color(248, 249, 250));
        
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(detailsScroll);
        formPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton assignBtn = new JButton("Assign Lecturer");
        assignBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignBtn.setBackground(new Color(41, 128, 185));
        assignBtn.setForeground(Color.WHITE);
        assignBtn.setFocusPainted(false);
        assignBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        assignBtn.addActionListener(e -> onAssign());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());

        buttonPanel.add(assignBtn);
        buttonPanel.add(refreshBtn);
        formPanel.add(buttonPanel);

        // Notes
        formPanel.add(Box.createVerticalStrut(15));
        JLabel noteLabel = new JLabel("<html><body style='width:350px'>" +
            "<p style='color:#666;'><b>Rules:</b></p>" +
            "<ul style='color:#666;'>" +
            "<li>You can only assign modules under your leadership</li>" +
            "<li>Lecturers shown are those assigned to you</li>" +
            "<li>Changes are saved to module.txt</li>" +
            "</ul></body></html>");
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(noteLabel);

        panel.add(formTitle, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createModuleTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel tableTitle = new JLabel("Current Module Assignments");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(41, 128, 185));

        String[] columns = {"Module ID", "Module Name", "Current Lecturer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        moduleTable = new JTable(tableModel);
        moduleTable.setRowHeight(30);
        moduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        moduleTable.getTableHeader().setBackground(new Color(41, 128, 185));
        moduleTable.getTableHeader().setForeground(Color.WHITE);
        moduleTable.setSelectionBackground(new Color(189, 215, 238));

        // Click on table row to select module
        moduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = moduleTable.getSelectedRow();
                if (row >= 0 && row < myModules.size()) {
                    moduleComboBox.setSelectedIndex(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(moduleTable);

        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        // Clear existing data
        moduleComboBox.removeAllItems();
        lecturerComboBox.removeAllItems();
        tableModel.setRowCount(0);

        // Load modules for this leader
        myModules = moduleRepo.getModulesByLeader(leaderId);

        // Load lecturers under this leader
        List<user> allUsers = userRepo.getAllUsers();
        myLecturers = allUsers.stream()
            .filter(u -> "lecturer".equalsIgnoreCase(u.getRole()))
            .filter(u -> String.valueOf(u.getLeaderId()).equals(leaderId))
            .collect(Collectors.toList());

        // Populate module combo box
        for (Module m : myModules) {
            moduleComboBox.addItem(m.getModuleId() + " - " + m.getName());
        }

        // Populate lecturer combo box
        lecturerComboBox.addItem("-- Select Lecturer --");
        for (user lec : myLecturers) {
            lecturerComboBox.addItem(lec.getId() + " - " + lec.getName());
        }

        // Populate table
        for (Module m : myModules) {
            String lecturerDisplay = m.getLecturerId().isEmpty() ? 
                "Not Assigned" : getLecturerName(m.getLecturerId());
            
            tableModel.addRow(new Object[]{
                m.getModuleId(),
                m.getName(),
                lecturerDisplay
            });
        }

        setStatus("Loaded " + myModules.size() + " module(s) and " + 
                  myLecturers.size() + " lecturer(s)", false);
        
        updateDetails();
    }

    private String getLecturerName(String lecturerId) {
        for (user lec : myLecturers) {
            if (lec.getId().equals(lecturerId)) {
                return lec.getId() + " - " + lec.getName();
            }
        }
        // Check in all users
        List<user> allUsers = userRepo.getAllUsers();
        for (user u : allUsers) {
            if (u.getId().equals(lecturerId)) {
                return u.getId() + " - " + u.getName();
            }
        }
        return lecturerId;
    }

    private void updateDetails() {
        if (myModules == null || myModules.isEmpty()) {
            detailsArea.setText("No modules available.");
            return;
        }

        int moduleIndex = moduleComboBox.getSelectedIndex();
        if (moduleIndex < 0 || moduleIndex >= myModules.size()) {
            return;
        }

        Module selectedModule = myModules.get(moduleIndex);
        
        StringBuilder sb = new StringBuilder();
        sb.append("MODULE INFORMATION\n");
        sb.append("==================\n");
        sb.append("ID: ").append(selectedModule.getModuleId()).append("\n");
        sb.append("Name: ").append(selectedModule.getName()).append("\n");
        sb.append("Description: ").append(selectedModule.getDescription()).append("\n\n");
        
        sb.append("CURRENT ASSIGNMENT\n");
        sb.append("==================\n");
        if (selectedModule.getLecturerId().isEmpty()) {
            sb.append("Lecturer: Not Assigned\n");
        } else {
            sb.append("Lecturer: ").append(getLecturerName(selectedModule.getLecturerId())).append("\n");
        }
        
        sb.append("\nSELECTED NEW LECTURER\n");
        sb.append("=====================\n");
        int lecIndex = lecturerComboBox.getSelectedIndex();
        if (lecIndex > 0 && lecIndex <= myLecturers.size()) {
            user selectedLec = myLecturers.get(lecIndex - 1);
            sb.append("ID: ").append(selectedLec.getId()).append("\n");
            sb.append("Name: ").append(selectedLec.getName()).append("\n");
            sb.append("Email: ").append(selectedLec.getEmail()).append("\n");
        } else {
            sb.append("No lecturer selected\n");
        }

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    private void onAssign() {
        int moduleIndex = moduleComboBox.getSelectedIndex();
        int lecturerIndex = lecturerComboBox.getSelectedIndex();

        if (moduleIndex < 0 || moduleIndex >= myModules.size()) {
            setStatus("Please select a module.", true);
            return;
        }

        if (lecturerIndex <= 0) {
            setStatus("Please select a lecturer.", true);
            return;
        }

        Module selectedModule = myModules.get(moduleIndex);
        user selectedLecturer = myLecturers.get(lecturerIndex - 1);

        // Check if already assigned to same lecturer
        if (selectedModule.getLecturerId().equals(selectedLecturer.getId())) {
            setStatus("Module is already assigned to this lecturer.", true);
            return;
        }

        // Confirm assignment
        int confirm = JOptionPane.showConfirmDialog(this,
            "Assign lecturer '" + selectedLecturer.getName() + "' to module '" + 
            selectedModule.getName() + "'?",
            "Confirm Assignment",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Perform assignment
        try {
            selectedModule.setLecturerId(selectedLecturer.getId());
            boolean success = moduleRepo.updateLecturerForModule(
                selectedModule.getModuleId(), 
                selectedLecturer.getId()
            );

            if (success) {
                setStatus("Successfully assigned " + selectedLecturer.getName() + 
                          " to " + selectedModule.getName(), false);
                loadData(); // Refresh
                JOptionPane.showMessageDialog(this,
                    "Lecturer assigned successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                setStatus("Failed to assign lecturer. Module not found.", true);
            }
        } catch (Exception ex) {
            setStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? new Color(192, 57, 43) : new Color(39, 174, 96));
    }
}
