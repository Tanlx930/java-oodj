public class user {
    private String id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String email;
    private String phone;
    private int age;
    private String role;
    private int leaderId;

    public user(String newId, String username, String password, String name, String gender, String email, String phone, int age, String role, int leaderId) {
        this.id = newId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.role = role;
        this.leaderId = leaderId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getLeaderId() { return leaderId; }
    public void setLeaderId(int leaderId) { this.leaderId = leaderId; }


    public String toLine() {
        return id + "|" + name + "|" + email + "|" + password;
    }

    public static user fromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 4) return null;
        return new user(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Integer.parseInt(parts[7]), parts[8], Integer.parseInt(parts[9]));
    }
}
