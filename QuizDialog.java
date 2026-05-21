import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class QuizDialog extends JDialog {
    private List<QuizQuestion> questions;
    private int currentIndex = 0;
    private int score = 0;
    private String moduleCode;
    private String studentId; 
    
    // UI Components
    private JLabel lblQuestionNum;
    private JProgressBar progressBar;
    private JTextArea txtQuestion;
    private JRadioButton rbA, rbB, rbC, rbD;
    private ButtonGroup optionsGroup;
    private JButton btnNext;

    public QuizDialog(Frame parent, String studentId, String moduleCode, List<QuizQuestion> questions) {
        super(parent, "Quiz Session: " + moduleCode, true);
        this.studentId = studentId;
        this.moduleCode = moduleCode;
        this.questions = questions;

        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        initUI();
        loadQuestion();
    }

    private void initUI() {
        // --- Main Background ---
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 247, 250)); 
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30)); 
        add(mainPanel);

        // --- Header Section (Progress & Title) ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        lblQuestionNum = new JLabel("Question 1 of " + questions.size());
        lblQuestionNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQuestionNum.setForeground(new Color(80, 80, 80));

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(100, 8));
        progressBar.setForeground(new Color(40, 167, 69)); 
        progressBar.setBackground(new Color(220, 220, 220));

        headerPanel.add(lblQuestionNum, BorderLayout.WEST);
        headerPanel.add(progressBar, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Question Card (Center) ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true), 
            new EmptyBorder(30, 40, 30, 40) 
        ));

        // Question Text
        txtQuestion = new JTextArea();
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setLineWrap(true);
        txtQuestion.setEditable(false);
        txtQuestion.setOpaque(false);
        txtQuestion.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtQuestion.setForeground(new Color(30, 30, 30));
        txtQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtQuestion);
        
        cardPanel.add(Box.createVerticalStrut(25)); 

        // Options
        optionsGroup = new ButtonGroup();
        rbA = createStyledRadioButton();
        rbB = createStyledRadioButton();
        rbC = createStyledRadioButton();
        rbD = createStyledRadioButton();

        optionsGroup.add(rbA); optionsGroup.add(rbB);
        optionsGroup.add(rbC); optionsGroup.add(rbD);

        cardPanel.add(rbA); cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(rbB); cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(rbC); cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(rbD);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // --- Footer Section (Button) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        btnNext = new JButton("Next Question");
        btnNext.setPreferredSize(new Dimension(160, 45));
        btnNext.setBackground(new Color(0, 120, 215)); 
        btnNext.setForeground(Color.WHITE);
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext.setFocusPainted(false);
        btnNext.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnNext.addActionListener(e -> nextQuestion());
        
        bottomPanel.add(btnNext);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JRadioButton createStyledRadioButton() {
        JRadioButton rb = new JRadioButton();
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rb.setForeground(new Color(50, 50, 50));
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rb;
    }

    private void loadQuestion() {
        if (currentIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        QuizQuestion q = questions.get(currentIndex);
        
        // Update Header
        lblQuestionNum.setText("Question " + (currentIndex + 1) + " of " + questions.size());
        progressBar.setValue(currentIndex);

        // Update Text
        txtQuestion.setText(q.getQuestion());
        
        rbA.setText(q.getOptionA());
        rbB.setText(q.getOptionB());
        rbC.setText(q.getOptionC());
        rbD.setText(q.getOptionD());
        
        optionsGroup.clearSelection();
        
        if (currentIndex == questions.size() - 1) {
            btnNext.setText("Submit Quiz");
            btnNext.setBackground(new Color(40, 167, 69)); 
        } else {
            btnNext.setText("Next Question");
            btnNext.setBackground(new Color(0, 120, 215));
        }
    }

    private void nextQuestion() {
        if (optionsGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Please select an answer to proceed.", "Answer Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        QuizQuestion q = questions.get(currentIndex);
        String selected = null;
        if (rbA.isSelected()) selected = "A";
        else if (rbB.isSelected()) selected = "B";
        else if (rbC.isSelected()) selected = "C";
        else if (rbD.isSelected()) selected = "D";

        if (selected != null && selected.equalsIgnoreCase(q.getCorrectOption())) {
            score++;
        }

        currentIndex++;
        loadQuestion();
    }

    private void finishQuiz() {
        progressBar.setValue(questions.size());
        
        // 1. Calculate Score
        double percentage = (double) score / questions.size() * 100;
        
        // 2. Get details
        QuizQuestion meta = questions.get(0);
        String lecturerId = meta.getLecturerId();
        String assessmentName = meta.getAssessmentName();

        // 3. Save to Lecturer's System
        AssessmentRecord record = new AssessmentRecord(
            lecturerId,
            moduleCode,
            studentId,
            assessmentName,
            percentage,
            "Auto-graded Quiz"
        );
        
        LecturerFileManager.saveAssessmentRecord(record);

        // 4. Show Beautiful Result Message
        String msg = String.format("<html><body style='width: 250px; text-align: center;'>" +
                                   "<h2 style='color: #0078D7;'>Quiz Completed!</h2>" +
                                   "<p style='font-size: 14px;'>You scored <b>%d / %d</b></p>" +
                                   "<h1 style='color: %s;'>%.1f%%</h1>" +
                                   "</body></html>", 
                                   score, questions.size(), 
                                   (percentage >= 50 ? "#28a745" : "#dc3545"), 
                                   percentage);
        
        JOptionPane.showMessageDialog(this, msg, "Quiz Result", JOptionPane.PLAIN_MESSAGE);
        
        dispose();
    }
}