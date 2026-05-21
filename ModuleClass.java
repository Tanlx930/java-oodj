public class ModuleClass {
    private int no;
    private String moduleAbbreviation;
    private String className;
    private String classMode;
    private String classDate;
    private String weekday;
    private String classStartTime;
    private String classEndTime;

    public ModuleClass(int no, String moduleAbbreviation, String className, String classMode, 
                       String classDate, String weekday, String classStartTime, String classEndTime) {
        this.no = no;
        this.moduleAbbreviation = moduleAbbreviation;
        this.className = className;
        this.classMode = classMode;
        this.classDate = classDate;
        this.weekday = weekday;
        this.classStartTime = classStartTime;
        this.classEndTime = classEndTime;
    }

    // Getters
    public int getNo() { return no; }
    public String getModuleAbbreviation() { return moduleAbbreviation; }
    public String getClassName() { return className; }
    public String getClassMode() { return classMode; }
    public String getClassDate() { return classDate; }
    public String getWeekday() { return weekday; }
    public String getClassStartTime() { return classStartTime; }
    public String getClassEndTime() { return classEndTime; }

    // Setters
    public void setNo(int no) { this.no = no; }
    public void setModuleAbbreviation(String moduleAbbreviation) { this.moduleAbbreviation = moduleAbbreviation; }
    public void setClassName(String className) { this.className = className; }
    public void setClassMode(String classMode) { this.classMode = classMode; }
    public void setClassDate(String classDate) { this.classDate = classDate; }
    public void setWeekday(String weekday) { this.weekday = weekday; }
    public void setClassStartTime(String classStartTime) { this.classStartTime = classStartTime; }
    public void setClassEndTime(String classEndTime) { this.classEndTime = classEndTime; }

    @Override
    public String toString() {
        return no + "," + moduleAbbreviation + "," + className + "," + classMode + "," + 
               classDate + "," + weekday + "," + classStartTime + "," + classEndTime;
    }
}
