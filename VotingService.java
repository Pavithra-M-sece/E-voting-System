import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VotingService {
    private Map<Candidate, Integer> voteCounts = new HashMap<>();

    public void castVote(Scanner scanner, User user) {
        if (user.hasVoted()) {
            System.out.println("You have already voted.");
            return;
        }

        System.out.println("Candidates:");
        for (Candidate candidate : EVotingSystem.getCandidates()) {
            System.out.println(candidate.getName());
        }
        System.out.print("Enter the name of the candidate you want to vote for: ");
        String candidateName = scanner.nextLine();
        
        // Find the candidate and record the vote
        Candidate selectedCandidate = null;
        for (Candidate candidate : EVotingSystem.getCandidates()) {
            if (candidate.getName().equalsIgnoreCase(candidateName)) {
                selectedCandidate = candidate;
                break;
            }
        }
        
        if (selectedCandidate != null) {
            // Record the vote
            voteCounts.put(selectedCandidate, voteCounts.getOrDefault(selectedCandidate, 0) + 1);
            System.out.println("Thank you for voting for " + selectedCandidate.getName() + "!");
            user.setVoted(true);
        } else {
            System.out.println("Invalid candidate name. Please try again.");
        }
    }

    public void displayResults() {
        System.out.println("\n=== VOTING RESULTS ===");
        if (voteCounts.isEmpty()) {
            System.out.println("No votes have been cast yet.");
            return;
        }
        
        for (Map.Entry<Candidate, Integer> entry : voteCounts.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue() + " votes");
        }
    }
}
