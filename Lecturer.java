public class Lecturer {
    private String id;
    private String name;
    private String email;
    private String password;

    public Lecturer(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String toLine() {
        return id + "|" + name + "|" + email + "|" + password;
    }

    public static Lecturer fromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 4) return null;
        return new Lecturer(parts[0], parts[1], parts[2], parts[3]);
    }
}
