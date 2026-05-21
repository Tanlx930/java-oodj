import java.util.*;


public class ReportsService {

    private ModuleRepository moduleRepo;
    private userRepository userRepo;
    private ClassRepository classRepo;

    public ReportsService() {
        this.moduleRepo = new ModuleRepository();
        this.userRepo = new userRepository();
        this.classRepo = new ClassRepository();
    }

    public ReportsService(ModuleRepository moduleRepo, userRepository userRepo, ClassRepository classRepo) {
        this.moduleRepo = moduleRepo;
        this.userRepo = userRepo;
        this.classRepo = classRepo;
    }

    /**
     * Get module summary for an academic leader.
     * Returns rows: [Module ID, Name, Lecturer, #Classes, #Students]
     */
    public List<Object[]> getModuleSummaryForLeader(String leaderId) {
        List<Object[]> rows = new ArrayList<>();
        
        try {
            ArrayList<Module> modules = moduleRepo.getModulesByLeader(leaderId);
            ArrayList<ClassSession> allClasses = classRepo.loadAllClasses();
            List<user> allUsers = userRepo.getAllUsers();

            for (Module m : modules) {
                String lecturerName = "Not Assigned";
                if (m.getLecturerId() != null && !m.getLecturerId().isEmpty()) {
                    for (user u : allUsers) {
                        if (u.getId().equals(m.getLecturerId())) {
                            lecturerName = u.getName();
                            break;
                        }
                    }
                }

                int classCount = 0;
                for (ClassSession c : allClasses) {
                    if (c.getModuleAbbreviation() != null && 
                        c.getModuleAbbreviation().equalsIgnoreCase(m.getModuleId())) {
                        classCount++;
                    }
                }

                int studentCount = 0;
                if (m.getStudentId() != null && !m.getStudentId().isEmpty()) {
                    String[] studentIds = m.getStudentId().split(";");
                    for (String id : studentIds) {
                        if (!id.trim().isEmpty()) studentCount++;
                    }
                }

                rows.add(new Object[]{
                    m.getModuleId(),
                    m.getName(),
                    lecturerName,
                    classCount,
                    studentCount
                });
            }

            rows.sort(Comparator.comparing(o -> String.valueOf(o[0])));

        } catch (Exception ex) {
            System.err.println("Error generating module summary: " + ex.getMessage());
        }

        return rows;
    }

    /**
     * Get lecturer workload for an academic leader.
     * Returns rows: [Lecturer ID, Lecturer Name, #Modules, #Total Classes]
     */
    public List<Object[]> getLecturerWorkloadForLeader(String leaderId) {
        List<Object[]> rows = new ArrayList<>();

        try {
            ArrayList<Module> modules = moduleRepo.getModulesByLeader(leaderId);
            ArrayList<ClassSession> allClasses = classRepo.loadAllClasses();
            List<user> allUsers = userRepo.getAllUsers();

            List<user> lecturers = new ArrayList<>();
            for (user u : allUsers) {
                if ("lecturer".equalsIgnoreCase(u.getRole()) &&
                    String.valueOf(u.getLeaderId()).equals(leaderId)) {
                    lecturers.add(u);
                }
            }

            for (user lecturer : lecturers) {
                int moduleCount = 0;
                Set<String> lecturerModules = new HashSet<>();
                for (Module m : modules) {
                    if (lecturer.getId().equals(m.getLecturerId())) {
                        moduleCount++;
                        lecturerModules.add(m.getModuleId().toUpperCase());
                    }
                }

                int classCount = 0;
                for (ClassSession c : allClasses) {
                    if (c.getModuleAbbreviation() != null &&
                        lecturerModules.contains(c.getModuleAbbreviation().toUpperCase())) {
                        classCount++;
                    }
                }

                rows.add(new Object[]{
                    lecturer.getId(),
                    lecturer.getName(),
                    moduleCount,
                    classCount
                });
            }

            rows.sort(Comparator.comparing(o -> String.valueOf(o[0])));

        } catch (Exception ex) {
            System.err.println("Error generating lecturer workload: " + ex.getMessage());
        }

        return rows;
    }

    /**
     * Get class schedule summary for an academic leader.
     * Returns rows: [Module ID, Class Name, Day, Time]
     */
    public List<Object[]> getClassScheduleForLeader(String leaderId) {
        List<Object[]> rows = new ArrayList<>();

        try {
            ArrayList<Module> modules = moduleRepo.getModulesByLeader(leaderId);
            ArrayList<ClassSession> allClasses = classRepo.loadAllClasses();

            Set<String> leaderModules = new HashSet<>();
            for (Module m : modules) {
                leaderModules.add(m.getModuleId().toUpperCase());
            }

            for (ClassSession c : allClasses) {
                if (c.getModuleAbbreviation() != null &&
                    leaderModules.contains(c.getModuleAbbreviation().toUpperCase())) {
                    
                    String time = c.getClassStartTime() + " - " + c.getClassEndTime();
                    
                    rows.add(new Object[]{
                        c.getModuleAbbreviation(),
                        c.getClassName(),
                        c.getWeekdays(),
                        time
                    });
                }
            }

            rows.sort((a, b) -> {
                int cmp = String.valueOf(a[0]).compareToIgnoreCase(String.valueOf(b[0]));
                if (cmp != 0) return cmp;
                return String.valueOf(a[1]).compareToIgnoreCase(String.valueOf(b[1]));
            });

        } catch (Exception ex) {
            System.err.println("Error generating class schedule: " + ex.getMessage());
        }

        return rows;
    }

    /**
     * Get summary statistics for an academic leader.
     * Returns a map with keys: totalModules, totalLecturers, totalClasses, totalStudents
     */
    public Map<String, Integer> getSummaryStatisticsForLeader(String leaderId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalModules", 0);
        stats.put("totalLecturers", 0);
        stats.put("totalClasses", 0);
        stats.put("totalStudents", 0);

        try {
            ArrayList<Module> modules = moduleRepo.getModulesByLeader(leaderId);
            ArrayList<ClassSession> allClasses = classRepo.loadAllClasses();
            List<user> allUsers = userRepo.getAllUsers();

            stats.put("totalModules", modules.size());

            int lecturerCount = 0;
            for (user u : allUsers) {
                if ("lecturer".equalsIgnoreCase(u.getRole()) &&
                    String.valueOf(u.getLeaderId()).equals(leaderId)) {
                    lecturerCount++;
                }
            }
            stats.put("totalLecturers", lecturerCount);

            Set<String> leaderModules = new HashSet<>();
            for (Module m : modules) {
                leaderModules.add(m.getModuleId().toUpperCase());
            }

            int classCount = 0;
            for (ClassSession c : allClasses) {
                if (c.getModuleAbbreviation() != null &&
                    leaderModules.contains(c.getModuleAbbreviation().toUpperCase())) {
                    classCount++;
                }
            }
            stats.put("totalClasses", classCount);

            Set<String> uniqueStudents = new HashSet<>();
            for (Module m : modules) {
                if (m.getStudentId() != null && !m.getStudentId().isEmpty()) {
                    String[] studentIds = m.getStudentId().split(";");
                    for (String id : studentIds) {
                        if (!id.trim().isEmpty()) {
                            uniqueStudents.add(id.trim());
                        }
                    }
                }
            }
            stats.put("totalStudents", uniqueStudents.size());

        } catch (Exception ex) {
            System.err.println("Error generating summary statistics: " + ex.getMessage());
        }

        return stats;
    }
}
