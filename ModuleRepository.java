import java.io.*;
import java.util.ArrayList;


public class ModuleRepository {

    private static final String FILE_PATH = "module.txt";
    private static final String HEADER = "moduleId,name,description,leaderId,lecturerId,studentId";

    /**
     * Load all modules from file
     */
    public ArrayList<Module> loadAll() {
        ArrayList<Module> modules = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            System.out.println("module.txt not found.");
            return modules;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    if (line.toLowerCase().startsWith("moduleid,")) continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                Module m = Module.fromCsvLine(line);
                if (m != null) {
                    modules.add(m);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading modules: " + e.getMessage());
        }
        
        return modules;
    }

    /**
     * Save all modules to file
     */
    public void saveAll(ArrayList<Module> modules) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println(HEADER);
            for (Module m : modules) {
                pw.println(m.toCsvLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving modules: " + e.getMessage());
        }
    }

    /**
     * Update lecturer assignment for a module
     */
    public boolean updateLecturerForModule(String moduleId, String newLecturerId) {
        ArrayList<Module> modules = loadAll();
        boolean found = false;

        for (Module m : modules) {
            if (m.getModuleId().equals(moduleId)) {
                m.setLecturerId(newLecturerId);
                found = true;
                break;
            }
        }

        if (found) {
            saveAll(modules);
        }
        return found;
    }

    /**
     * Add a new module
     */
    public void addModule(Module module) {
        ArrayList<Module> modules = loadAll();
        modules.add(module);
        saveAll(modules);
    }

    /**
     * Delete a module by ID
     */
    public boolean deleteModule(String moduleId) {
        ArrayList<Module> modules = loadAll();
        boolean removed = modules.removeIf(m -> m.getModuleId().equals(moduleId));
        if (removed) {
            saveAll(modules);
        }
        return removed;
    }

    /**
     * Find module by ID
     */
    public Module findById(String moduleId) {
        ArrayList<Module> modules = loadAll();
        for (Module m : modules) {
            if (m.getModuleId().equals(moduleId)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Get modules by leader ID
     */
    public ArrayList<Module> getModulesByLeader(String leaderId) {
        ArrayList<Module> all = loadAll();
        ArrayList<Module> result = new ArrayList<>();
        for (Module m : all) {
            if (m.getLeaderId() != null && m.getLeaderId().equals(leaderId)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Get modules by lecturer ID
     */
    public ArrayList<Module> getModulesByLecturer(String lecturerId) {
        ArrayList<Module> all = loadAll();
        ArrayList<Module> result = new ArrayList<>();
        for (Module m : all) {
            if (m.getLecturerId() != null && m.getLecturerId().equals(lecturerId)) {
                result.add(m);
            }
        }
        return result;
    }
}
