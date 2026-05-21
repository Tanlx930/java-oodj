import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentAssessmentRepository {
    private static final String RECORD_FILE = "assessment_records.txt";
    private static final String TYPE_FILE = "assessment_types.txt";

    // 1. Get all records for a specific student ID (For "Check Result")
    public List<AssessmentRecord> getStudentRecords(String studentId) {
        List<AssessmentRecord> records = new ArrayList<>();
        File file = new File(RECORD_FILE);

        if (!file.exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("[source")) continue;
                
                AssessmentRecord r = AssessmentRecord.fromLine(line);
                
                if (r != null && r.getStudentId().equals(studentId)) {
                    records.add(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    // 2. Find Max Marks (For percentage calculation)
    public int getMaxMarks(String moduleCode, String assessmentName) {
        File file = new File(TYPE_FILE);
        if (!file.exists()) return 100;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("[source")) continue;

                AssessmentType t = AssessmentType.fromLine(line);
                
                if (t != null && t.getModuleCode().equals(moduleCode) 
                        && t.getAssessmentName().equals(assessmentName)) {
                    return t.getMaxMarks();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 100;
    }

    // 3. Check if student already attempted the quiz
    public boolean hasCompletedAssessment(String studentId, String moduleCode, String assessmentName) {
        File file = new File(RECORD_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("[source")) continue;

                AssessmentRecord r = AssessmentRecord.fromLine(line);
                
                // Check if this student already has a record for this exact Module + Assessment
                if (r != null && 
                    r.getStudentId().equals(studentId) && 
                    r.getModuleCode().equalsIgnoreCase(moduleCode) && 
                    r.getAssessmentName().equalsIgnoreCase(assessmentName)) {
                    return true; 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}