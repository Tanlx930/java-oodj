import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GradingSystemRepository {
    private static final String FILE_PATH = "gradingSystem.txt";
    private List<GradingSystem> gradingSystems;

    public GradingSystemRepository() {
        this.gradingSystems = new ArrayList<>();
        loadFromFile();
    }

    private void loadFromFile() {
        gradingSystems.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            // Skip header line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    GradingSystem gs = parseLine(line);
                    if (gs != null) {
                        gradingSystems.add(gs);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading grading system file: " + e.getMessage());
        }
    }

    private GradingSystem parseLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                int minMark = Integer.parseInt(parts[0].trim());
                int maxMark = Integer.parseInt(parts[1].trim());
                String grade = parts[2].trim();
                double gpa = Double.parseDouble(parts[3].trim());
                String classification = parts[4].trim();
                return new GradingSystem(minMark, maxMark, grade, gpa, classification);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing line: " + line);
        }
        return null;
    }

    public List<GradingSystem> getAllGradingSystems() {
        return new ArrayList<>(gradingSystems);
    }

    public GradingSystem getGradingSystemByGrade(String grade) {
        for (GradingSystem gs : gradingSystems) {
            if (gs.getGrade().equalsIgnoreCase(grade)) {
                return gs;
            }
        }
        return null;
    }

    public GradingSystem getGradingSystemByMark(int mark) {
        for (GradingSystem gs : gradingSystems) {
            if (mark >= gs.getMinMark() && mark <= gs.getMaxMark()) {
                return gs;
            }
        }
        return null;
    }

    public void updateGradingSystem(int index, GradingSystem gs) {
        if (index >= 0 && index < gradingSystems.size()) {
            gradingSystems.set(index, gs);
            saveToFile();
        }
    }

    public void addGradingSystem(GradingSystem gs) {
        gradingSystems.add(gs);
        // Sort by minMark descending (highest grade first)
        gradingSystems.sort((a, b) -> b.getMinMark() - a.getMinMark());
        saveToFile();
    }

    public void deleteGradingSystem(int index) {
        if (index >= 0 && index < gradingSystems.size()) {
            gradingSystems.remove(index);
            saveToFile();
        }
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("minMark,maxMark,grade,gpa,classification\n");
            for (GradingSystem gs : gradingSystems) {
                writer.write(gs.getMinMark() + "," + gs.getMaxMark() + "," + 
                            gs.getGrade() + "," + gs.getGpa() + "," + 
                            gs.getClassification() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving grading system file: " + e.getMessage());
        }
    }

    public void reload() {
        loadFromFile();
    }
}
