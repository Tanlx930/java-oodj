
public class Module {
    private String moduleId;
    private String name;
    private String description;
    private String leaderId;      // Academic Leader ID
    private String lecturerId;    // Lecturer ID
    private String studentId;     // Student ID (for enrollment tracking)

    public Module(String moduleId, String name, String description, 
                  String leaderId, String lecturerId, String studentId) {
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.leaderId = leaderId;
        this.lecturerId = lecturerId;
        this.studentId = studentId;
    }

    // Getters
    public String getModuleId() { return moduleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLeaderId() { return leaderId; }
    public String getLecturerId() { return lecturerId; }
    public String getStudentId() { return studentId; }

    // Alias methods for compatibility with academic leader functionality
    public String getNo() { return moduleId; }
    public String getAbbrev() { return name; }  // Use name as module abbreviation
    public String getAcademicLeaderId() { return leaderId; }

    // Setters
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    // Convert to CSV line for saving
    public String toCsvLine() {
        return moduleId + "," + name + "," + description + "," + 
               leaderId + "," + lecturerId + "," + studentId;
    }

    // Parse from CSV line
    public static Module fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        return new Module(
            parts[0].trim(),
            parts[1].trim(),
            parts[2].trim(),
            parts[3].trim(),
            parts[4].trim(),
            parts[5].trim()
        );
    }

    @Override
    public String toString() {
        return name + " (" + moduleId + ")";
    }
}

