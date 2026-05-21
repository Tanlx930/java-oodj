import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class GradingSystemViewUI extends JPanel {

    private GradingSystemRepository repository;
    private List<GradingSystem> gradesList;
    private JTable gradesTable;
    private DefaultTableModel tableModel;

    public GradingSystemViewUI() {
        this.repository = new GradingSystemRepository();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Grading System Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(32, 42, 68));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshBtn.setBackground(new Color(41, 128, 185));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshBtn, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);

        // Description panel
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(new Color(248, 249, 250));
        descPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel descLabel = new JLabel("<html>" +
            "<h3 style='color:#2c3e50;'>About the Grading System</h3>" +
            "<p>This grading system defines how student marks are converted to grades and GPA values. " +
            "Each tier shows the mark range, corresponding grade letter, GPA points, and classification.</p>" +
            "<p style='color:#7f8c8d; font-size:11px;'><i>Note: This is a read-only view. " +
            "Contact the administrator to make changes to the grading system.</i></p>" +
            "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descPanel.add(descLabel, BorderLayout.CENTER);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel tableTitle = new JLabel("Grade Tiers");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(41, 128, 185));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Create table
        String[] columns = {"Grade", "GPA", "Classification", "Min Mark", "Max Mark"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setRowHeight(35);
        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gradesTable.setShowGrid(true);
        gradesTable.setGridColor(new Color(230, 230, 230));
        gradesTable.setIntercellSpacing(new Dimension(10, 5));

        // Style header
        JTableHeader header = gradesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center align all columns
        javax.swing.table.DefaultTableCellRenderer centerRenderer = 
            new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < gradesTable.getColumnCount(); i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = createSummaryPanel();

        mainPanel.add(descPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Calculate statistics when data is loaded
        panel.add(createStatCard("Total Tiers", "0", new Color(41, 128, 185)));
        panel.add(createStatCard("Highest GPA", "0.0", new Color(39, 174, 96)));
        panel.add(createStatCard("Pass Grades", "0", new Color(243, 156, 18)));
        panel.add(createStatCard("Fail Grades", "0", new Color(231, 76, 60)));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setName("value"); // For updating later

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(255, 255, 255, 200));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        gradesList = repository.getAllGradingSystems();

        // Populate table
        for (GradingSystem gs : gradesList) {
            tableModel.addRow(new Object[]{
                gs.getGrade(),
                String.format("%.2f", gs.getGpa()),
                gs.getClassification(),
                gs.getMinMark(),
                gs.getMaxMark()
            });
        }

        // Update summary statistics
        updateSummaryStats();
    }

    private void updateSummaryStats() {
        int totalTiers = gradesList.size();
        double highestGpa = 0.0;
        int passCount = 0;
        int failCount = 0;

        for (GradingSystem gs : gradesList) {
            if (gs.getGpa() > highestGpa) {
                highestGpa = gs.getGpa();
            }
            
            // Count pass/fail based on GPA or classification
            String classification = gs.getClassification().toLowerCase();
            if (classification.contains("fail") || gs.getGpa() == 0.0) {
                failCount++;
            } else {
                passCount++;
            }
        }

        // Update the stat cards - find the summary panel and update labels
        Component[] components = getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                updateStatCardsRecursively((JPanel) c, totalTiers, highestGpa, passCount, failCount);
            }
        }
    }

    private void updateStatCardsRecursively(JPanel panel, int total, double highestGpa, int pass, int fail) {
        Component[] components = panel.getComponents();
        int cardIndex = 0;
        
        for (Component c : components) {
            if (c instanceof JPanel && ((JPanel) c).getLayout() instanceof BorderLayout) {
                JPanel card = (JPanel) c;
                Component[] cardComponents = card.getComponents();
                for (Component cc : cardComponents) {
                    if (cc instanceof JLabel && "value".equals(cc.getName())) {
                        switch (cardIndex) {
                            case 0: ((JLabel) cc).setText(String.valueOf(total)); break;
                            case 1: ((JLabel) cc).setText(String.format("%.2f", highestGpa)); break;
                            case 2: ((JLabel) cc).setText(String.valueOf(pass)); break;
                            case 3: ((JLabel) cc).setText(String.valueOf(fail)); break;
                        }
                        cardIndex++;
                    }
                }
            } else if (c instanceof JPanel) {
                updateStatCardsRecursively((JPanel) c, total, highestGpa, pass, fail);
            }
        }
    }
}
