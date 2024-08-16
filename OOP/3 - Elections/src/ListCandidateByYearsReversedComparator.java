import java.util.Comparator;

public class ListCandidateByYearsReversedComparator implements Comparator<ListCandidate> {

	@Override
	public int compare(ListCandidate lc_1, ListCandidate lc_2) {
		return (score(lc_2) - score(lc_1));
	}

	private int score(ListCandidate lc) {
		return (2 * (lc.getYears()) + lc.getAge());
	}

}
