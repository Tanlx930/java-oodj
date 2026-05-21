import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuizRepository {
    private static final String QUIZ_FILE = "quiz_questions.txt";

    public List<QuizQuestion> getQuestionsByModule(String moduleCode) {
        List<QuizQuestion> questions = new ArrayList<>();
        File file = new File(QUIZ_FILE);

        if (!file.exists()) return questions;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[") || line.trim().isEmpty()) continue;

                try {
                    QuizQuestion q = QuizQuestion.fromLine(line);
                    // Case-insensitive check for module code
                    if (q != null && q.getModuleCode().equalsIgnoreCase(moduleCode)) {
                        questions.add(q);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }
}