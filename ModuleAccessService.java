import java.util.ArrayList;


public final class ModuleAccessService {
    private ModuleAccessService() {}

    /**
     * Filter modules based on user role and ID
     */
    public static ArrayList<Module> filterModulesByRole(
            ArrayList<Module> all,
            String role,
            String userId
    ) {
        ArrayList<Module> out = new ArrayList<>();
        if (all == null) return out;

        // Normalize role for comparison
        String normalizedRole = role != null ? role.toLowerCase().trim() : "";

        // Admin or Student: can see all modules
        if (normalizedRole.equals("admin") || normalizedRole.equals("student")) {
            out.addAll(all);
            return out;
        }

        // Lecturer: only modules they teach
        if (normalizedRole.equals("lecturer")) {
            for (Module m : all) {
                if (m.getLecturerId() != null && m.getLecturerId().equals(userId)) {
                    out.add(m);
                }
            }
            return out;
        }

        // Academic Leader: only modules under them
        if (normalizedRole.equals("academicleader")) {
            for (Module m : all) {
                if (m.getLeaderId() != null && m.getLeaderId().equals(userId)) {
                    out.add(m);
                }
            }
            return out;
        }

        // Default: return empty list
        return out;
    }

    /**
     * Check if a user can access a specific module
     */
    public static boolean canAccessModule(Module module, String role, String userId) {
        if (module == null) return false;
        
        String normalizedRole = role != null ? role.toLowerCase().trim() : "";

        // Admin and Student can access all
        if (normalizedRole.equals("admin") || normalizedRole.equals("student")) {
            return true;
        }

        // Lecturer can access modules they teach
        if (normalizedRole.equals("lecturer")) {
            return module.getLecturerId() != null && module.getLecturerId().equals(userId);
        }

        // Academic Leader can access modules under them
        if (normalizedRole.equals("academicleader")) {
            return module.getLeaderId() != null && module.getLeaderId().equals(userId);
        }

        return false;
    }
}
