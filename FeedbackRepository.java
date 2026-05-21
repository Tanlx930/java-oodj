import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FeedbackRepository {
    private static final String FEEDBACK_FILE = "feedback.txt";

    public boolean saveFeedback(Feedback fb) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FEEDBACK_FILE, true)))) {
            pw.println(fb.toLine());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
            return false;
        }
    }
}