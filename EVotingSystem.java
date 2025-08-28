import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EVotingSystem {
    private static Map<String, User> users = new HashMap<>();
    private static List<Candidate> candidates = new ArrayList<>();
    
    public static List<Candidate> getCandidates() {
        return candidates;
    }
    private static VotingService votingService = new VotingService();

    private static void initializeAdmin() {
        User admin = new User("admin", "admin", "admin123", "admin");
        users.put("admin", admin);
        System.out.println("Default admin user created.");
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeCandidates();
        initializeAdmin();
        
        while (true) {
            System.out.println("Welcome to the E-Voting System");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. View Results");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser(scanner);
                    break;
                case 2:
                    loginUser(scanner);
                    break;
                case 3:
                    votingService.displayResults();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeCandidates() {
        candidates.add(new Candidate("Alice"));
        candidates.add(new Candidate("Bob"));
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Enter ID number: ");
        String id = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        User user = new User(id, username, password, "voter");
        users.put(username, user);
        System.out.println("Registration successful!");
    }

    private static void loginUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful!");
            votingService.castVote(scanner, user);
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }
}
