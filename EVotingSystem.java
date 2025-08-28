import java.util.*;

/**
 * Phase I – Console E-Voting System
 * - Voter registration (name, id, password) with duplicate prevention
 * - Login / logout
 * - Candidate management (preload defaults or add manually)
 * - Voting (one vote per voter)
 * - View results (totals + winner / tie)
 * - Exit anytime
 * - All data stored in-memory using Collections
 */
public class EVotingSystem {

    // ==== Models ====
    static class Voter {
        private final String voterId;
        private final String name;
        private final String password;
        private boolean hasVoted;

        Voter(String voterId, String name, String password) {
            this.voterId = voterId;
            this.name = name;
            this.password = password;
            this.hasVoted = false;
        }

        public String getVoterId() { return voterId; }
        public String getName() { return name; }
        public String getPassword() { return password; }
        public boolean hasVoted() { return hasVoted; }
        public void markVoted() { this.hasVoted = true; }
    }

    static class Candidate {
        private final String candidateId;
        private final String name;
        private int voteCount;

        Candidate(String candidateId, String name) {
            this.candidateId = candidateId;
            this.name = name;
            this.voteCount = 0;
        }

        public String getCandidateId() { return candidateId; }
        public String getName() { return name; }
        public int getVoteCount() { return voteCount; }
        public void addVote() { voteCount++; }
    }

    // ==== In-memory storage ====
    private static final Map<String, Voter> votersById = new HashMap<>();
    private static final Map<String, Candidate> candidatesById = new LinkedHashMap<>(); // keeps insertion order
    private static final Map<String, String> votesByVoterId = new HashMap<>(); // voterId -> candidateId

    // Logged-in voter session (simple)
    private static Voter currentVoter = null;

    // ==== Console helpers ====
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> registerVoter();
                case 2 -> loginVoter();
                case 3 -> candidateManagementMenu();
                case 4 -> showCandidates();
                case 5 -> votingMenu();
                case 6 -> viewResults();
                case 7 -> { System.out.println("Exiting... Thank you!"); return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n====== E-VOTING SYSTEM (Phase I) ======");
        System.out.println("1. Register Voter (Sign Up)");
        System.out.println("2. Login Voter");
        System.out.println("3. Candidate Management");
        System.out.println("4. Show Candidates");
        System.out.println("5. Cast Vote");
        System.out.println("6. View Results");
        System.out.println("7. Exit");
    }

    // ==== Registration / Login ====
    private static void registerVoter() {
        System.out.println("\n--- Voter Registration ---");
        String name = readNonEmpty("Enter Name: ");
        String voterId = readNonEmpty("Enter Voter ID: ");

        if (votersById.containsKey(voterId)) {
            System.out.println("❌ Registration failed: Voter ID already exists.");
            return;
        }

        String password = readNonEmpty("Create Password: ");
        votersById.put(voterId, new Voter(voterId, name, password));
        System.out.println("✅ Registration successful for " + name + " (ID: " + voterId + ")");
    }

    private static void loginVoter() {
        System.out.println("\n--- Voter Login ---");
        String voterId = readNonEmpty("Enter Voter ID: ");
        String password = readNonEmpty("Enter Password: ");

        Voter v = votersById.get(voterId);
        if (v == null) {
            System.out.println("❌ Login failed: Voter not found. Please register first.");
            return;
        }
        if (!v.getPassword().equals(password)) {
            System.out.println("❌ Login failed: Incorrect password.");
            return;
        }
        currentVoter = v;
        System.out.println("✅ Login successful. Welcome, " + currentVoter.getName() + "!");
        // Offer quick actions after login
        voterSessionMenu();
    }

    private static void voterSessionMenu() {
        while (currentVoter != null) {
            System.out.println("\n--- Voter Session (" + currentVoter.getName() + ") ---");
            System.out.println("1. Show Candidates");
            System.out.println("2. Cast Vote");
            System.out.println("3. Logout");
            int ch = readInt("Choose: ");
            switch (ch) {
                case 1 -> showCandidates();
                case 2 -> castVoteForLoggedInVoter();
                case 3 -> { System.out.println("Logged out."); currentVoter = null; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==== Candidate Management ====
    private static void candidateManagementMenu() {
        while (true) {
            System.out.println("\n--- Candidate Management ---");
            System.out.println("1. Preload Default Candidates");
            System.out.println("2. Add Candidate Manually");
            System.out.println("3. Show Candidates");
            System.out.println("4. Back to Main Menu");
            int ch = readInt("Choose: ");
            switch (ch) {
                case 1 -> preloadDefaultCandidates();
                case 2 -> addCandidate();
                case 3 -> showCandidates();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void preloadDefaultCandidates() {
        if (!candidatesById.isEmpty()) {
            System.out.println("Candidates already exist. Skipping preload.");
            return;
        }
        candidatesById.put("C101", new Candidate("C101", "Alice Johnson"));
        candidatesById.put("C102", new Candidate("C102", "Bharat Kumar"));
        candidatesById.put("C103", new Candidate("C103", "Chen Li"));
        System.out.println("✅ Default candidates loaded.");
    }

    private static void addCandidate() {
        String cid = readNonEmpty("Enter Candidate ID: ");
        if (candidatesById.containsKey(cid)) {
            System.out.println("❌ Candidate ID already exists.");
            return;
        }
        String name = readNonEmpty("Enter Candidate Name: ");
        candidatesById.put(cid, new Candidate(cid, name));
        System.out.println("✅ Candidate added: " + cid + " - " + name);
    }

    private static void showCandidates() {
        System.out.println("\n--- Candidates ---");
        if (candidatesById.isEmpty()) {
            System.out.println("No candidates found. Use Candidate Management to add or preload.");
            return;
        }
        for (Candidate c : candidatesById.values()) {
            System.out.printf("%s - %s (Votes: %d)%n", c.getCandidateId(), c.getName(), c.getVoteCount());
        }
    }

    // ==== Voting ====
    private static void votingMenu() {
        System.out.println("\n--- Voting ---");
        if (currentVoter == null) {
            System.out.println("Please log in to cast your vote.");
            return;
        }
        castVoteForLoggedInVoter();
    }

    private static void castVoteForLoggedInVoter() {
        if (currentVoter == null) {
            System.out.println("No voter is logged in.");
            return;
        }
        if (candidatesById.isEmpty()) {
            System.out.println("No candidates available. Please ask admin to add candidates first.");
            return;
        }
        if (currentVoter.hasVoted()) {
            System.out.println("❌ You have already voted. One vote per voter is enforced.");
            return;
        }

        showCandidates();
        String cid = readNonEmpty("Enter Candidate ID to vote: ");

        Candidate chosen = candidatesById.get(cid);
        if (chosen == null) {
            System.out.println("❌ Invalid Candidate ID.");
            return;
        }

        // record vote
        chosen.addVote();
        currentVoter.markVoted();
        votesByVoterId.put(currentVoter.getVoterId(), cid);
        System.out.println("✅ Vote cast successfully for " + chosen.getName());
    }

    // ==== Results ====
    private static void viewResults() {
        System.out.println("\n--- Voting Results ---");
        if (candidatesById.isEmpty()) {
            System.out.println("No candidates to show.");
            return;
        }
        int max = -1;
        List<Candidate> leaders = new ArrayList<>();

        for (Candidate c : candidatesById.values()) {
            System.out.printf("%s : %d votes%n", c.getName(), c.getVoteCount());
            if (c.getVoteCount() > max) {
                max = c.getVoteCount();
                leaders.clear();
                leaders.add(c);
            } else if (c.getVoteCount() == max) {
                leaders.add(c);
            }
        }

        if (leaders.isEmpty()) {
            System.out.println("No votes cast yet.");
            return;
        }

        if (leaders.size() == 1) {
            System.out.println("Winner: " + leaders.get(0).getName());
        } else {
            System.out.print("Winner: Tie between ");
            for (int i = 0; i < leaders.size(); i++) {
                System.out.print(leaders.get(i).getName());
                if (i < leaders.size() - 1) System.out.print(", ");
            }
            System.out.println();
        }
    }

    // ==== Input helpers ====
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = sc.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input cannot be empty. Try again.");
        }
    }
}
