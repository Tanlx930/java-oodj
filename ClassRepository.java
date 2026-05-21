import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassRepository {
    private static final String CLASS_FILE = "class.txt";
    private static final String REGISTRATION_FILE = "student_registration.txt";

    // 1. GET ALL CLASSES
    public List<ClassSession> getAllClasses() {
        List<ClassSession> classes = new ArrayList<>();
        File file = new File(CLASS_FILE);

        if (!file.exists()) return classes;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("[source") || line.startsWith("No,")) continue;
                String[] p = line.split(",");
                if (p.length >= 8) {
                    classes.add(new ClassSession(
                        p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
                        p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim()
                    ));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return classes;
    }

    // 2. REGISTER STUDENT
    public boolean registerStudent(String studentId, String classId) {
        if (isAlreadyRegistered(studentId, classId)) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(REGISTRATION_FILE, true))) {
            bw.write(studentId + "," + classId);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. CHECK DUPLICATES
    private boolean isAlreadyRegistered(String studentId, String classId) {
        File file = new File(REGISTRATION_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(studentId) && parts[1].trim().equals(classId)) {
                    return true;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    // 4. GET CLASSES FOR STUDENT (This was missing!)
    public List<ClassSession> getClassesForStudent(String studentId) {
        Set<String> registeredIds = new HashSet<>();
        File regFile = new File(REGISTRATION_FILE);
        
        // 1. Find all Class IDs this student registered for
        if (regFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(regFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && parts[0].trim().equals(studentId)) {
                        registeredIds.add(parts[1].trim());
                    }
                }
            } catch (IOException e) { e.printStackTrace(); }
        }

        // 2. Filter the main class list
        List<ClassSession> all = getAllClasses();
        List<ClassSession> studentClasses = new ArrayList<>();
        
        for (ClassSession c : all) {
            if (registeredIds.contains(c.getId())) {
                studentClasses.add(c);
            }
        }
        return studentClasses;
    }
    
    // 5. HELPER FOR POLYMORPHISM (If needed by other methods)
    // Alias method to match any potential previous naming
    public List<ClassSession> getStudentClasses(String studentId) {
        return getClassesForStudent(studentId);
    }

    // 6. ALIAS METHOD - loadAllClasses() for compatibility
    public ArrayList<ClassSession> loadAllClasses() {
        return new ArrayList<>(getAllClasses());
    }
}