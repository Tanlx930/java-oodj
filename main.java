public class main {
    public static void main(String[] args) {
        // Launch the login page on the EDT (Event Dispatch Thread)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new login();
        });
    }
}