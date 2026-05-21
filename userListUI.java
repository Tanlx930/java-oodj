import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class userListUI extends DefaultDashboardUI {
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> genderFilter;
    private userRepository repo;
    private List<user> allUsers;

    public userListUI(String roleName, String username) {
        super("APU Assessment System - User Management", roleName, username);
        repo = new userRepository();
        allUsers = repo.getAllUsers();
        setupCustomUI();
    }

    @Override
    protected String getCurrentModuleName() {
        return "USER_MANAGEMENT";
    }

    private void setupCustomUI() {
        // Create the users management panel
        JPanel userManagementPanel = createUserManagementPanel();
        
        // Add it to the content panel
        contentPanel.add(userManagementPanel, "USER_MANAGEMENT");
        cardLayout.show(contentPanel, "USER_MANAGEMENT");
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title and Add button panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        JButton addUserButton = new JButton("Add New User");
        addUserButton = styleRoundButton(addUserButton, new Color(41, 128, 185));
        addUserButton.addActionListener(_ -> new userCreateUI().setVisible(true));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(addUserButton, BorderLayout.EAST);

        // Search and Filter panel
        JPanel searchFilterPanel = createSearchFilterPanel();

        // Combine title and search/filter panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchFilterPanel, BorderLayout.CENTER);

        // Table panel
        JScrollPane tableScrollPane = createUsersTable();

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.setToolTipText("Search by name, username, or email");

        // Add document listener for real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterPanel.setOpaque(false);

        // Role filter
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleFilter = new JComboBox<>(new String[]{"All", "Admin", "Academic Leader", "Lecturer", "Student"});
        roleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleFilter.setPreferredSize(new Dimension(140, 30));
        roleFilter.addActionListener(e -> applyFilters());

        // Gender filter
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderFilter = new JComboBox<>(new String[]{"All", "Male", "Female"});
        genderFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        genderFilter.setPreferredSize(new Dimension(100, 30));
        genderFilter.addActionListener(e -> applyFilters());

        // Clear filters button
        JButton clearFiltersBtn = new JButton("Clear Filters");
        clearFiltersBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearFiltersBtn.addActionListener(e -> clearFilters());

        filterPanel.add(roleLabel);
        filterPanel.add(roleFilter);
        filterPanel.add(genderLabel);
        filterPanel.add(genderFilter);
        filterPanel.add(clearFiltersBtn);

        // Combine search and filter
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.setOpaque(false);
        combinedPanel.add(searchPanel, BorderLayout.NORTH);
        combinedPanel.add(filterPanel, BorderLayout.CENTER);

        panel.add(combinedPanel, BorderLayout.CENTER);

        return panel;
    }

    private void clearFilters() {
        searchField.setText("");
        roleFilter.setSelectedIndex(0);
        genderFilter.setSelectedIndex(0);
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedRole = (String) roleFilter.getSelectedItem();
        String selectedGender = (String) genderFilter.getSelectedItem();

        List<user> filteredUsers = new ArrayList<>();

        for (user u : allUsers) {
            boolean matchesSearch = searchText.isEmpty() ||
                    u.getName().toLowerCase().contains(searchText) ||
                    u.getUsername().toLowerCase().contains(searchText) ||
                    u.getEmail().toLowerCase().contains(searchText);

            boolean matchesRole = selectedRole == null || selectedRole.equals("All") ||
                    formatRole(u.getRole()).equalsIgnoreCase(selectedRole);

            boolean matchesGender = selectedGender == null || selectedGender.equals("All") ||
                    u.getGender().equalsIgnoreCase(selectedGender);

            if (matchesSearch && matchesRole && matchesGender) {
                filteredUsers.add(u);
            }
        }

        updateTable(filteredUsers);
    }

    private void updateTable(List<user> users) {
        tableModel.setRowCount(0);
        for (user u : users) {
            tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getName(),
                    u.getGender(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getAge(),
                    formatRole(u.getRole()),
                    "Actions"
            });
        }
    }

    private JButton styleRoundButton(JButton button, Color backgroundColor) {
        RoundedButton roundedButton = new RoundedButton(button.getText(), backgroundColor);
        roundedButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roundedButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add hover effect
        roundedButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                roundedButton.setBackgroundColor(backgroundColor.darker());
                roundedButton.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                roundedButton.setBackgroundColor(backgroundColor);
                roundedButton.repaint();
            }
        });

        return roundedButton;
    }

    private JScrollPane createUsersTable() {
        String[] columnNames = {"ID", "Username", "Name", "Gender", "Email", "Phone", "Age", "Role", "Actions"};
        tableModel = new DefaultTableModel(new Object[0][0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only "Actions" column is editable
            }
        };

        JTable usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usersTable.setRowHeight(40);
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set custom renderer and editor for "Actions" column
        usersTable.getColumnModel().getColumn(8).setCellRenderer(new UserActionButtonRenderer());
        usersTable.getColumnModel().getColumn(8).setCellEditor(new UserActionButtonEditor(new JCheckBox(), usersTable, this));

        // Set column widths
        int[] columnWidths = {20, 100, 100, 50, 150, 84, 40, 100, 160};
        for (int i = 0; i < columnWidths.length; i++) {
            usersTable.getColumnModel().getColumn(i).setMinWidth(columnWidths[i]);
        }

        loadUsersFromRepository();

        return new JScrollPane(usersTable);
    }

    private void loadUsersFromRepository() {
        repo = new userRepository();
        allUsers = repo.getAllUsers();
        updateTable(allUsers);
    }

    private String formatRole(String role) {
        switch(role.toLowerCase()) {
            case "admin":
                return "Admin";
            case "academicleader":
                return "Academic Leader";
            case "lecturer":
                return "Lecturer";
            case "student":
                return "Student";
            default:
                return role;
        }
    }

    void refreshTable() {
        loadUsersFromRepository();
        // Re-apply current filters after refresh
        applyFilters();
    }

    boolean deleteUserFromRepository(int userId) {
        userRepository repo = new userRepository();
        List<user> users = repo.getAllUsers();

        boolean removed = users.removeIf(u -> Integer.parseInt(u.getId()) == userId);

        if (removed) {
            repo.saveAllUsers(users);
        }

        return removed;
    }

    boolean deleteUserFromRepository(String userId) {
        userRepository repo = new userRepository();
        List<user> users = repo.getAllUsers();

        boolean removed = users.removeIf(u -> u.getId().equals(userId));

        if (removed) {
            repo.saveAllUsers(users);
        }

        return removed;
    }
}

// User Action Button Renderer
class UserActionButtonRenderer extends JPanel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(createRoundButton("Edit", new Color(41, 128, 185)));
        panel.add(createRoundButton("Delete", new Color(231, 76, 60)));
        return panel;
    }

    private JButton createRoundButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, backgroundColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }
}

// User Action Button Editor
class UserActionButtonEditor extends DefaultCellEditor {
    private JPanel panel;
    private final JTable table;
    private final userListUI parentFrame;

    public UserActionButtonEditor(JCheckBox checkBox, JTable table, userListUI parentFrame) {
        super(checkBox);
        this.table = table;
        this.parentFrame = parentFrame;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // "Edit" button
        JButton editButton = createRoundStyledButton("Edit", new Color(41, 128, 185));
        editButton.setPreferredSize(new Dimension(15, 30));
        editButton.setMinimumSize(new Dimension(15, 30));
        editButton.addActionListener(_ -> handleEditAction(row));

        // "Delete" button
        JButton deleteButton = createRoundStyledButton("Delete", new Color(231, 76, 60));
        deleteButton.setPreferredSize(new Dimension(40, 30));
        deleteButton.setMinimumSize(new Dimension(40, 30));
        deleteButton.addActionListener(_ -> handleDeleteAction(row));

        panel.add(editButton);
        panel.add(deleteButton);
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

    private void handleEditAction(int row) {
        // Cancel cell editing first to prevent ArrayIndexOutOfBoundsException
        fireEditingCanceled();
        
        String userId = (String) table.getValueAt(row, 0);
        String username = (String) table.getValueAt(row, 1);
        String name = (String) table.getValueAt(row, 2);
        String gender = (String) table.getValueAt(row, 3);
        String email = (String) table.getValueAt(row, 4);
        String phone = (String) table.getValueAt(row, 5);
        int age = (int) table.getValueAt(row, 6);
        String role = (String) table.getValueAt(row, 7);

        userDetailsEditUI dialog = new userDetailsEditUI(parentFrame, userId, username, name, gender, email, phone, age, role);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            userRepository repo = new userRepository();
            List<user> users = repo.getAllUsers();

            for (user u : users) {
                if (u.getId().equals(userId)) {
                    u.setName(dialog.getName());
                    u.setGender(dialog.getGender());
                    u.setEmail(dialog.getEmail());
                    u.setPhone(dialog.getPhone());
                    u.setAge(dialog.getAge());
                    u.setRole(dialog.getRole());
                    // Handle password reset
                    if (dialog.isPasswordReset()) {
                        u.setPassword(dialog.getNewPassword());
                    }
                    break;
                }
            }

            repo.saveAllUsers(users);
            parentFrame.refreshTable();
            JOptionPane.showMessageDialog(parentFrame, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteAction(int row) {
        // Cancel cell editing first to prevent ArrayIndexOutOfBoundsException
        fireEditingCanceled();
        
        String userId = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (parentFrame.deleteUserFromRepository(userId)) {
                parentFrame.refreshTable();
                JOptionPane.showMessageDialog(parentFrame, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class EditUserDialog extends userDetailsEditUI {
    public EditUserDialog(JFrame parent, String userId, String username, String name, String gender, String email, String phone, int age, String role) {
        super(parent, userId, username, name, gender, email, phone, age, role);
    }
}

// Custom Rounded Button Class
class RoundedButton extends JButton {
    private int cornerRadius = 15;
    private Color backgroundColor;

    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.backgroundColor = bgColor;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Draw text
        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}