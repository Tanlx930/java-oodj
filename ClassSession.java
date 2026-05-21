public class ClassSession {
    private String id;
    private String moduleCode;
    private String className;
    private String mode;
    private String date;
    private String day;
    private String startTime;
    private String endTime;

    public ClassSession(String id, String moduleCode, String className, String mode, 
                        String date, String day, String startTime, String endTime) {
        this.id = id;
        this.moduleCode = moduleCode;
        this.className = className;
        this.mode = mode;
        this.date = date;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public String getId() { return id; }
    public String getModuleCode() { return moduleCode; }
    public String getClassName() { return className; }
    public String getMode() { return mode; }
    public String getDate() { return date; }
    public String getDay() { return day; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    // Alias methods for compatibility with different naming conventions
    public String getModuleAbbreviation() { return moduleCode; }
    public String getClassMode() { return mode; }
    public String getClassDate() { return date; }
    public String getWeekdays() { return day; }
    public String getClassStartTime() { return startTime; }
    public String getClassEndTime() { return endTime; }

    @Override
    public String toString() {
        return className + " (" + day + " " + startTime + ")";
    }
}