public class Feedback {
    private String studentId;
    private String feedbackType;
    private String message;
    private int rating;

    public Feedback(String studentId, String feedbackType, String message, int rating) {
        this.studentId = studentId;
        this.feedbackType = feedbackType;
        this.message = message;
        this.rating = rating;
    }

    public String toLine() {
        // Format: StudentID|Type|Message|Rating
        return studentId + "|" + feedbackType + "|" + message.replace("\n", " ") + "|" + rating;
    }
}