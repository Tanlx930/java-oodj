import java.util.Objects;

public class AssessmentType {
    private String lecturerId;
    private String moduleCode;
    private String assessmentName;
    private int maxMarks;
    private double weightage;

    public AssessmentType(String lecturerId, String moduleCode, String assessmentName,
                          int maxMarks, double weightage) {
        this.lecturerId = lecturerId;
        this.moduleCode = moduleCode;
        this.assessmentName = assessmentName;
        this.maxMarks = maxMarks;
        this.weightage = weightage;
    }

    public String getLecturerId() { return lecturerId; }
    public String getModuleCode() { return moduleCode; }
    public String getAssessmentName() { return assessmentName; }
    public int getMaxMarks() { return maxMarks; }
    public double getWeightage() { return weightage; }

    public String toLine() {
        return lecturerId + "|" + moduleCode + "|" + assessmentName + "|" + maxMarks + "|" + weightage;
    }

    public static AssessmentType fromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) return null;
        try {
            int max = Integer.parseInt(parts[3]);
            double w = Double.parseDouble(parts[4]);
            return new AssessmentType(parts[0], parts[1], parts[2], max, w);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return moduleCode + " - " + assessmentName + " (" + maxMarks + " marks, " + weightage + "%)";
    }

    // 👉 add these:
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentType)) return false;
        AssessmentType that = (AssessmentType) o;
        return maxMarks == that.maxMarks &&
               Double.compare(that.weightage, weightage) == 0 &&
               Objects.equals(lecturerId, that.lecturerId) &&
               Objects.equals(moduleCode, that.moduleCode) &&
               Objects.equals(assessmentName, that.assessmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturerId, moduleCode, assessmentName, maxMarks, weightage);
    }
}
