
/*
 * Represents a vote in an election.
 * Indicating:
 * 1. who made the vote (Voter)
 * 2. who got the vote (Object from a class that necessarily implements the Votable interface) 
 */

public class Vote {

	private Voter voter;
	private Votable votedTo; // either MayorCandidate or MunicipalityList in our case
	
	// Constructor
	public Vote(Voter voter, Votable votedTo) {
		this.voter = voter;
		this.votedTo = votedTo;
	}
	
}
