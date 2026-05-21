
public class ProfileEditService {
    
    private userRepository userRepo;
    
    /**
     * Constructor - Demonstrates DEPENDENCY INJECTION
     */
    public ProfileEditService(userRepository userRepo) {
        this.userRepo = userRepo;
    }
    
    /**
     * Validate if a username is available 
     * Demonstrates ABSTRACTION and business logic encapsulation
     * 
     * @param newUsername the username to check
     * @param currentUserId the ID of the user trying to change their username (to exclude from check)
     * @return null if username is available, error message if taken
     */
    public String validateUsernameAvailability(String newUsername, String currentUserId) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return "Username cannot be empty";
        }
        
        if (newUsername.length() < 3) {
            return "Username must be at least 3 characters";
        }
        
        if (newUsername.length() > 50) {
            return "Username is too long (max 50 characters)";
        }
        
        // Check if username already exists (excluding current user)
        for (user u : userRepo.getAllUsers()) {
            if (u.getUsername().equals(newUsername) && !u.getId().equals(currentUserId)) {
                return "Username '" + newUsername + "' is already taken by another user";
            }
        }
        
        return null; // Username is available
    }
    
    /**
     * Validate password
     * @return null if valid, error message if invalid
     */
    public String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        
        if (password.length() > 100) {
            return "Password is too long";
        }
        
        return null; // Password is valid
    }
    
    /**
     * Update student profile with new username and password
     * Only these two fields can be changed by students.
     * Demonstrates ABSTRACTION by delegating to userRepository
     * 
     * @param userId the student's ID
     * @param newUsername the new username
     * @param newPassword the new password
     * @return UpdateResult object with success flag and error message if any
     */
    public UpdateResult updateStudentProfile(String userId, String newUsername, String newPassword) {
        // Validate username availability
        String usernameError = validateUsernameAvailability(newUsername, userId);
        if (usernameError != null) {
            return new UpdateResult(false, usernameError);
        }
        
        // Validate password
        String passwordError = validatePassword(newPassword);
        if (passwordError != null) {
            return new UpdateResult(false, passwordError);
        }
        
        // Find the user and update
        for (user u : userRepo.getAllUsers()) {
            if (u.getId().equals(userId)) {
                u.setUsername(newUsername);
                u.setPassword(newPassword);
                
                // Save all users back to file
                userRepo.saveAllUsers();
                return new UpdateResult(true, "Profile updated successfully!");
            }
        }
        
        return new UpdateResult(false, "User not found");
    }
    
    /**
     * Inner class to represent the result of an update operation
     * Demonstrates encapsulation of operation result with error messages
     */
    public static class UpdateResult {
        private boolean success;
        private String message;
        
        public UpdateResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
