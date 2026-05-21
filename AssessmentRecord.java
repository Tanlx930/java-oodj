public class AssessmentRecord {
    private String lecturerId;
    private String moduleCode;
    private String studentId;
    private String assessmentName;
    private double marks;
    private String feedback;

    public AssessmentRecord(String lecturerId, String moduleCode, String studentId,
                            String assessmentName, double marks, String feedback) {
        this.lecturerId = lecturerId;
        this.moduleCode = moduleCode;
        this.studentId = studentId;
        this.assessmentName = assessmentName;
        this.marks = marks;
        this.feedback = feedback;
    }

    public String getLecturerId() { return lecturerId; }
    public String getModuleCode() { return moduleCode; }
    public String getStudentId() { return studentId; }
    public String getAssessmentName() { return assessmentName; }
    public double getMarks() { return marks; }
    public String getFeedback() { return feedback; }

    public String toLine() {
        return lecturerId + "|" + moduleCode + "|" + studentId + "|" +
                assessmentName + "|" + marks + "|" + feedback.replace("|", "/");
    }

    public static AssessmentRecord fromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 6) return null;
        try {
            double m = Double.parseDouble(parts[4]);
            return new AssessmentRecord(parts[0], parts[1], parts[2],
                    parts[3], m, parts[5]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
