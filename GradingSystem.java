public class GradingSystem {
    private int minMark;
    private int maxMark;
    private String grade;
    private double gpa;
    private String classification;

    public GradingSystem(int minMark, int maxMark, String grade, double gpa, String classification) {
        this.minMark = minMark;
        this.maxMark = maxMark;
        this.grade = grade;
        this.gpa = gpa;
        this.classification = classification;
    }

    public int getMinMark() {
        return minMark;
    }

    public void setMinMark(int minMark) {
        this.minMark = minMark;
    }

    public int getMaxMark() {
        return maxMark;
    }

    public void setMaxMark(int maxMark) {
        this.maxMark = maxMark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return minMark + "-" + maxMark + " : " + grade + " (" + gpa + ") - " + classification;
    }
}
