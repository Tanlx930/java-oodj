public class QuizQuestion {
    private String lecturerId;
    private String moduleCode;
    private String assessmentName;

    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; 

    public QuizQuestion(String lecturerId, String moduleCode, String assessmentName,
                        String question, String optionA, String optionB, String optionC, String optionD,
                        String correctOption) {
        this.lecturerId = lecturerId;
        this.moduleCode = moduleCode;
        this.assessmentName = assessmentName;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    public String getLecturerId() { return lecturerId; }
    public String getModuleCode() { return moduleCode; }
    public String getAssessmentName() { return assessmentName; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectOption() { return correctOption; }

    private static String esc(String s){
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n");
    }

    private static String unesc(String s){
        if (s == null) return "";
        String out = s.replace("\\n", "\n");
        out = out.replace("\\|", "|");
        out = out.replace("\\\\", "\\");
        return out;
    }

    public String toLine() {
        return esc(lecturerId) + "|" + esc(moduleCode) + "|" + esc(assessmentName) + "|" +
               esc(question) + "|" + esc(optionA) + "|" + esc(optionB) + "|" + esc(optionC) + "|" +
               esc(optionD) + "|" + esc(correctOption);
    }

    public static QuizQuestion fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        // split by | but support escaped \|
        java.util.List<String> parts = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaped) {
                cur.append(ch);
                escaped = false;
            } else if (ch == '\\') {
                cur.append(ch);
                escaped = true;
            } else if (ch == '|') {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        parts.add(cur.toString());

        if (parts.size() < 9) return null;

        return new QuizQuestion(
                unesc(parts.get(0)),
                unesc(parts.get(1)),
                unesc(parts.get(2)),
                unesc(parts.get(3)),
                unesc(parts.get(4)),
                unesc(parts.get(5)),
                unesc(parts.get(6)),
                unesc(parts.get(7)),
                unesc(parts.get(8))
        );
    }
}
