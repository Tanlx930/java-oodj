import java.io.*;
import java.util.*;

public class ModuleClassRepository {

    private static final String FILE_PATH = "class.txt";
    private final List<ModuleClass> classes = new ArrayList<>();

    // Constructor - load classes when repository is created
    public ModuleClassRepository() {
        loadClasses();
    }

    public List<ModuleClass> getAllClasses() {
        return new ArrayList<>(classes);
    }

    public void addClass(ModuleClass moduleClass) {
        classes.add(moduleClass);
        saveAllClasses();
    }

    public void saveAllClasses(List<ModuleClass> updatedList) {
        classes.clear();
        classes.addAll(updatedList);
        saveAllClasses();
    }

    public void saveAllClasses() {
        writeToFile(classes);
    }

    // Get unique module abbreviations for dropdown
    public List<String> getUniqueModuleAbbreviations() {
        Set<String> uniqueModules = new LinkedHashSet<>();
        for (ModuleClass mc : classes) {
            uniqueModules.add(mc.getModuleAbbreviation());
        }
        return new ArrayList<>(uniqueModules);
    }

    // Get unique class modes for dropdown
    public List<String> getUniqueClassModes() {
        Set<String> uniqueModes = new LinkedHashSet<>();
        for (ModuleClass mc : classes) {
            uniqueModes.add(mc.getClassMode());
        }
        return new ArrayList<>(uniqueModes);
    }

    // Get unique weekdays for dropdown
    public List<String> getUniqueWeekdays() {
        Set<String> uniqueWeekdays = new LinkedHashSet<>();
        for (ModuleClass mc : classes) {
            uniqueWeekdays.add(mc.getWeekday());
        }
        return new ArrayList<>(uniqueWeekdays);
    }

    // Get next available ID
    public int getNextId() {
        if (classes.isEmpty()) {
            return 1;
        }
        int maxId = 0;
        for (ModuleClass mc : classes) {
            if (mc.getNo() > maxId) {
                maxId = mc.getNo();
            }
        }
        return maxId + 1;
    }

    // -----------------------------
    // LOAD CLASSES FROM FILE
    // -----------------------------
    private void loadClasses() {
        classes.clear();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("class.txt not found. Creating new file...");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            // Skip header
            String firstLine = br.readLine();
            if (firstLine == null) return;

            // Check if first line is header
            if (!firstLine.toLowerCase().startsWith("no,")) {
                // First line is data, not header
                processLine(firstLine);
            }

            while ((line = br.readLine()) != null) {
                processLine(line);
            }

        } catch (Exception e) {
            System.err.println("Error loading classes: " + e.getMessage());
        }
    }

    private void processLine(String line) {
        line = line.trim();
        if (line.isEmpty()) return;

        ModuleClass mc = parseLine(line);
        if (mc != null) {
            classes.add(mc);
        }
    }

    // -----------------------------
    // PARSE CSV → ModuleClass
    // -----------------------------
    private ModuleClass parseLine(String line) {
        String[] p = line.split(",", -1);

        if (p.length < 8) {
            System.err.println("Invalid line: " + line);
            return null;
        }

        try {
            return new ModuleClass(
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(),
                    p[2].trim(),
                    p[3].trim(),
                    p[4].trim(),
                    p[5].trim(),
                    p[6].trim(),
                    p[7].trim()
            );

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Parse error: " + e.getMessage());
            return null;
        }
    }

    // -----------------------------
    // SAVE CLASSES TO FILE
    // -----------------------------
    private void writeToFile(List<ModuleClass> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {

            // Write header
            pw.println("No,Module Abbreviation,Class Name,Class Mode,Class Date,Weekdays,Class Start Time,Class End Time");

            for (ModuleClass mc : list) {
                pw.println(
                    mc.getNo() + "," +
                    mc.getModuleAbbreviation() + "," +
                    mc.getClassName() + "," +
                    mc.getClassMode() + "," +
                    mc.getClassDate() + "," +
                    mc.getWeekday() + "," +
                    mc.getClassStartTime() + "," +
                    mc.getClassEndTime()
                );
            }

        } catch (IOException e) {
            System.err.println("Error writing classes: " + e.getMessage());
        }
    }
}
