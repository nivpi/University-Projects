import java.util.Comparator;

public class MayorCandidatesByVotesReversedComparator implements Comparator<MayorCandidate>{

	@Override
	public int compare(MayorCandidate mc1, MayorCandidate mc2) {
		return mc2.getVotes() - mc1.getVotes(); 
	}
}
