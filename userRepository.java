import java.io.*;
import java.util.*;

public class userRepository {

    private static final String FILE_PATH = "user.txt";
    private final List<user> users = new ArrayList<>();

    // Constructor - load users when repository is created
    public userRepository() {
        loadUsers();
    }

    public List<user> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(user u) {
        users.add(u);
        saveAllUsers();
    }

    public void saveAllUsers(List<user> updatedList) {
        users.clear();
        users.addAll(updatedList);
        saveAllUsers();
    }

    public void saveAllUsers() {
        writeToFile(users);
    }

    // -----------------------------
    // LOAD USERS FROM FILE
    // -----------------------------
    private void loadUsers() {
        users.clear();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("user.txt not found. Creating new file...");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            // Skip header if exists
            String firstLine = br.readLine();
            if (firstLine == null) return;

            if (!firstLine.toLowerCase().startsWith("id,")) {
                // First line is actually data, not header
                processLine(firstLine);
            }

            while ((line = br.readLine()) != null) {
                processLine(line);
            }

        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void processLine(String line) {
        line = line.trim();
        if (line.isEmpty()) return;

        user u = parseLine(line);
        if (u != null) {
            users.add(u);
        }
    }

    // -----------------------------
    // PARSE CSV → USER
    // -----------------------------
    private user parseLine(String line) {
        String[] p = line.split(",", -1);

        if (p.length < 10) {
            System.err.println("Invalid line: " + line);
            return null;
        }

        try {
            return new user(
                    p[0].trim(),
                    p[1].trim(),
                    p[2].trim(),
                    p[3].trim(),
                    p[4].trim(),
                    p[5].trim(),
                    p[6].trim(),
                    Integer.parseInt(p[7].trim()),
                    p[8].trim(),
                    Integer.parseInt(p[9].trim())
            );

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Parse error: " + e.getMessage());
            return null;
        }
    }

    // -----------------------------
    // SAVE USERS TO FILE
    // -----------------------------
    private void writeToFile(List<user> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {

            // Write header
            pw.println("id,username,password,name,gender,email,phone,age,role,leaderId");

            for (user u : list) {
                pw.println(
                    u.getId() + "," +
                    u.getUsername() + "," +
                    u.getPassword() + "," +
                    u.getName() + "," +
                    u.getGender() + "," +
                    u.getEmail() + "," +
                    u.getPhone() + "," +
                    u.getAge() + "," +
                    u.getRole() + "," +
                    u.getLeaderId()
                );
            }

        } catch (IOException e) {
            System.err.println("Error writing users: " + e.getMessage());
        }
    }
}
