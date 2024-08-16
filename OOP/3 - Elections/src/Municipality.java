import java.util.Collections;
import java.util.Vector;

public class Municipality {

	private String name;
	private Vector<MunicipalityList> municipality_lists;
	private Vector<MayorCandidate> mayor_candidates;
	private Vector<Voter> voters;
	
	// Constructor 1, in case we had some of the election-related data in the city, listed in vectors
	public Municipality(String name, Vector<MayorCandidate> mayor_candidates, Vector<Voter> voters,
			Vector<MunicipalityList> municipality_list) {
		this.name = name;
		this.mayor_candidates = mayor_candidates;
		this.voters = voters;
		this.municipality_lists = municipality_list;
	}
	
	// Constructor 2, no preliminary work was made
	public Municipality(String name) {
		this(name, new Vector<MayorCandidate>(), new Vector<Voter>(), new Vector<MunicipalityList>());
	}
	
	public String getName() {
		return this.name;
	}
	
	public Vector<MayorCandidate> getMayorCandidates() {
		return this.mayor_candidates;
	}
	
	public Vector<MunicipalityList> getMuncipalityLists(){
		return this.municipality_lists;
	}
	
	public Vector<Voter> getVoters() {
		return this.voters;
	}
	
	// counting the number of voters who voted already, in order to calculate the voting ratio
	public int actualVoters() {
		int votes = 0;
		for(Voter v : voters)
			if(v.getVotedMayor() || v.getVotedList())
				votes++;
		return votes;
	}
	
	public void addVoter(Voter v) {
		this.voters.add(v);
	}
	
	public void addMayorCandidates(MayorCandidate mc) {
		this.mayor_candidates.add(mc);
	}
	
	public void addListCandidates(MunicipalityList ml) {
		this.municipality_lists.add(ml);
	}
	
	// secondary way of sorting a city's mayor candidates --> by votes (higher to lower)
	public void sortMayorCandidatesByVotes() {
		Collections.sort(mayor_candidates,new MayorCandidatesByVotesReversedComparator());
	}
}
