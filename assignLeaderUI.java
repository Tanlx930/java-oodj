import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class assignLeaderUI extends DefaultDashboardUI {
    private DefaultTableModel tableModel;
    private List<user> lecturers;

    public assignLeaderUI(String roleName, String username) {
        super("APU Assessment System - Assign Academic Leader", roleName, username);
        setupCustomUI();
    }

    @Override
    protected String getCurrentModuleName() {
        return "ASSIGN_LEADER";
    }

    private void setupCustomUI() {
        // Create the assign leader panel
        JPanel assignLeaderPanel = createAssignLeaderPanel();
        
        // Add it to the content panel
        contentPanel.add(assignLeaderPanel, "ASSIGN_LEADER");
        cardLayout.show(contentPanel, "ASSIGN_LEADER");
    }

    private JPanel createAssignLeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Assign Academic Leader to Lecturers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Table panel
        JScrollPane tableScrollPane = createLecturersTable();

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createLecturersTable() {
        String[] columnNames = {"ID", "Username", "Name", "Email", "Phone", "Assigned Leader", "Actions"};
        tableModel = new DefaultTableModel(new Object[0][0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only "Actions" column is editable
            }
        };

        JTable lecturersTable = new JTable(tableModel);
        lecturersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        lecturersTable.setRowHeight(40);
        lecturersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set custom renderer and editor for "Actions" column
        lecturersTable.getColumnModel().getColumn(6).setCellRenderer(new AssignLeaderButtonRenderer());
        lecturersTable.getColumnModel().getColumn(6).setCellEditor(new AssignLeaderButtonEditor(new JCheckBox(), lecturersTable, this));

        // Set column widths
        int[] columnWidths = {20, 100, 100, 150, 84, 150, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            lecturersTable.getColumnModel().getColumn(i).setMinWidth(columnWidths[i]);
        }

        loadLecturersFromRepository();

        return new JScrollPane(lecturersTable);
    }

    private void loadLecturersFromRepository() {
        tableModel.setRowCount(0); // Clear the table
        userRepository repo = new userRepository();
        List<user> allUsers = repo.getAllUsers();
        
        // Filter only lecturers
        lecturers = allUsers.stream()
                .filter(u -> "lecturer".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        for (user lecturer : lecturers) {
            tableModel.addRow(new Object[]{
                    lecturer.getId(),
                    lecturer.getUsername(),
                    lecturer.getName(),
                    lecturer.getEmail(),
                    lecturer.getPhone(),
                    getAssignedLeaderName(lecturer),
                    getButtonLabel(lecturer)
            });
        }
    }

    private String getAssignedLeaderName(user lecturer) {
        // Check if lecturer has an assigned leader
        if (lecturer.getLeaderId() == -1) {
            return "Not Assigned";
        }
        
        // Get the leader's name from the repository
        userRepository repo = new userRepository();
        List<user> allUsers = repo.getAllUsers();
        
        for (user u : allUsers) {
            if (u.getId().equals(String.valueOf(lecturer.getLeaderId()))) {
                return u.getName() + " (" + u.getUsername() + ")";
            }
        }
        
        return "Not Assigned";
    }

    private String getButtonLabel(user lecturer) {
        return "Set";  // Single action for both assign and edit
    }

    void refreshTable() {
        loadLecturersFromRepository();
    }

    public List<user> getAcademicLeaders() {
        userRepository repo = new userRepository();
        List<user> allUsers = repo.getAllUsers();
        
        // Filter only academic leaders
        return allUsers.stream()
                .filter(u -> "academicleader".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());
    }

    public user getLecturerById(String lecturerId) {
        return lecturers.stream()
                .filter(u -> u.getId().equals(lecturerId))
                .findFirst()
                .orElse(null);
    }
}

// Assign Leader Button Renderer
class AssignLeaderButtonRenderer extends JPanel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(createRoundButton("Set", new Color(41, 128, 185)));
        return panel;
    }

    private JButton createRoundButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }
}

// Assign Leader Button Editor
class AssignLeaderButtonEditor extends DefaultCellEditor {
    private JTable table;
    private final assignLeaderUI parentFrame;
    private boolean isProcessing = false;

    public AssignLeaderButtonEditor(JCheckBox checkBox, JTable table, assignLeaderUI parentFrame) {
        super(checkBox);
        this.table = table;
        this.parentFrame = parentFrame;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isProcessing) {
            return new JPanel(); // Return empty panel if already processing to prevent re-entry
        }

        this.table = table;
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton actionButton = createRoundStyledButton("Set", new Color(41, 128, 185));
        final int editingRow = row; // Capture row value
        actionButton.addActionListener(e -> {
            if (isProcessing) return;
            isProcessing = true;
            
            // Handle on a separate invocation to prevent table corruption
            SwingUtilities.invokeLater(() -> {
                try {
                    handleAssignAction(editingRow);
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        stopCellEditing();
                        isProcessing = false;
                    });
                }
            });
        });

        panel.add(actionButton, BorderLayout.CENTER);
        return panel;
    }

    private JButton createRoundStyledButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackgroundColor(backgroundColor.darker());
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackgroundColor(backgroundColor);
                button.repaint();
            }
        });

        return button;
    }

    private void handleAssignAction(int row) {
        // Validate row is still valid
        if (row < 0 || row >= table.getRowCount()) {
            return;
        }

        try {
            String lecturerId = (String) table.getValueAt(row, 0);
            String lecturerName = (String) table.getValueAt(row, 2);

            // Get list of academic leaders
            List<user> academicLeaders = parentFrame.getAcademicLeaders();

            if (academicLeaders.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No academic leaders available!", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create dropdown list
            String[] leaderNames = new String[academicLeaders.size() + 1];
            leaderNames[0] = "No Assignment";
            for (int i = 0; i < academicLeaders.size(); i++) {
                leaderNames[i + 1] = academicLeaders.get(i).getName() + " (" + academicLeaders.get(i).getUsername() + ")";
            }

            // Show selection dialog
            JComboBox<String> leaderCombo = new JComboBox<>(leaderNames);
            
            // Get current assignment if exists
            user lecturer = parentFrame.getLecturerById(lecturerId);
            if (lecturer != null && lecturer.getLeaderId() != -1) {
                // Set the currently assigned leader as selected
                String currentLeader = (String) table.getValueAt(row, 5);
                leaderCombo.setSelectedItem(currentLeader);
            }

            JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
            dialogPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            dialogPanel.add(new JLabel("Select Academic Leader for: " + lecturerName), BorderLayout.NORTH);
            dialogPanel.add(leaderCombo, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(parentFrame, dialogPanel, "Set Academic Leader", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String selectedLeader = (String) leaderCombo.getSelectedItem();
                
                // Save the assignment and update user.txt
                saveAssignment(lecturerId, selectedLeader, academicLeaders);
                
                // Schedule table refresh and message on EDT
                SwingUtilities.invokeLater(() -> {
                    try {
                        parentFrame.refreshTable();
                        JOptionPane.showMessageDialog(parentFrame, "Academic leader assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        System.err.println("Error refreshing table: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception ex) {
            System.err.println("Exception in handleAssignAction: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void saveAssignment(String lecturerId, String assignedLeader, List<user> academicLeaders) {
        try {
            userRepository repo = new userRepository();
            List<user> allUsers = repo.getAllUsers();
            
            // Determine the leader ID
            int leaderId = -1;
            if (!assignedLeader.equals("No Assignment")) {
                // Extract leader ID from the selection
                for (user leader : academicLeaders) {
                    if (assignedLeader.contains(leader.getUsername())) {
                        leaderId = Integer.parseInt(leader.getId());
                        break;
                    }
                }
            }
            
            // Update the lecturer's leaderId in the users list
            for (user u : allUsers) {
                if (u.getId().equals(lecturerId)) {
                    u.setLeaderId(leaderId);
                    break;
                }
            }
            
            // Save all users back to file
            repo.saveAllUsers(allUsers);
            System.out.println("Lecturer " + lecturerId + " assigned to leader " + leaderId);
        } catch (Exception ex) {
            System.err.println("Error saving assignment: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Error saving assignment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}