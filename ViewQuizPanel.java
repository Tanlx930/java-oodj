import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ViewQuizPanel extends JPanel {

    private JComboBox<AssessmentType> cmbAssessments;
    private JTable table;
    private javax.swing.table.DefaultTableModel model;
    private Lecturer currentLecturer; 

    // Constructor accepts the Lecturer object
    public ViewQuizPanel(Lecturer lecturer) {
        this.currentLecturer = lecturer;
        
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

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(header, BorderLayout.NORTH);
        north.add(top, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        String[] cols = {"Module", "Assessment", "Question", "A", "B", "C", "D", "Correct"};
        model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
        List<AssessmentType> types = LecturerFileManager.loadAssessmentTypesForLecturer(currentLecturer.getId());
        for (AssessmentType t : types) cmbAssessments.addItem(t);
    }

    private void loadTable() {
        model.setRowCount(0);
        AssessmentType selected = (AssessmentType) cmbAssessments.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an assessment.");
            return;
        }

        List<QuizQuestion> list = LecturerFileManager.loadQuizQuestionsForLecturer(currentLecturer.getId());

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

        int choice = JOptionPane.showConfirmDialog(this, "Delete this quiz question?\n\n" + question, "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        LecturerFileManager.deleteQuizQuestion(currentLecturer.getId(), module, assessment, question);
        model.removeRow(row);
        JOptionPane.showMessageDialog(this, "Deleted ✅");
    }
}