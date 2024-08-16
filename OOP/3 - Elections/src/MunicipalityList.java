
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;

public class MunicipalityList implements Votable, Comparable<MunicipalityList> {

	private Vector<ListCandidate> list_candidates;
	private String name, city;
	private int votes;

	// Constructor 1, in case we already had a list of some of our candidates
	public MunicipalityList(String name, String city, Vector<ListCandidate> list_candidates) {
		this.name = name;
		this.city = city;
		this.list_candidates = list_candidates;
		this.votes = 0;
	}

	// Constructor 2, in case we don't
	public MunicipalityList(String name, String city) {
		this(name, city, new Vector<ListCandidate>());
	}

	public String getName() {
		return this.name;
	}

	public String getCity() {
		return this.city;
	}

	public int getVotes() {
		return this.votes;
	}

	@Override
	public String toString() {
		return "List: " + name + ", City: " + city;
	}

	public void vote() {
		this.votes++;
	}

	@Override
	// Natural comparison between lists
	public int compareTo(MunicipalityList other) {
		return this.votes - other.votes;
	}

	public int listSize() {
		return list_candidates.size();
	}

	// Adding a candidate, straight into his default-sorted location
	public void addCandidate(ListCandidate candidate) {
		if (!city.equals(candidate.getCity())) { // the given candidate is from a different city
			throw new IllegalArgumentException("Candidates and Lists should be from the same city");
		}
		sortNormally();
		int place = findPlaceInList(candidate);
		list_candidates.add(place, candidate);
	}

	// Sort naturally (where comparison points = age)
	// In a descending/reverse order (higher to lower / older to younger)
	public void sortNormally() {
		sortList(Collections.reverseOrder()); // compareTo is being used, higher to lower (instead of lower to higher)
	}

	// secondary way of sorting, years in city is taken into consideration
	public void sortAbnormally() {
		ListCandidateByYearsReversedComparator c = new ListCandidateByYearsReversedComparator();
		sortList(c);
	}

	// locating the sorted location of a valid candidate we are trying to add
	private int findPlaceInList(ListCandidate candidate) {
		if (listSize() == 0 || candidate instanceof MayorCandidate) // "Extreme cases" taken into consideration first
			return 0;

		int index = 0;
		if (hasMayor())
			index = 1; // skip the mayor, nobody can be in front of him
		while (index < listSize()) {
			if (candidate.compareTo(list_candidates.get(index)) > 0) // found a younger candidate, our candidate should
																		// be in front of him
				return index;
			index++;
		}
		return index; // reached the end of the list and failed every comparison, our candidate is the youngest
	}

	// returns a boolean value whether a mayor candidate is listed already
	// in order to help sort the array
	private boolean hasMayor() {
		if (list_candidates.get(0) instanceof MayorCandidate)
			return true;
		return false;
	}
	
	// sort the list with a given Comparator
	private void sortList(Comparator c) {
		if (listSize() <= 1)
			return;
		else if (hasMayor())
			Collections.sort(list_candidates.subList(1, listSize() - 1), c); // skip the Mayor candidate
		else
			Collections.sort(list_candidates, c);
	}

}
