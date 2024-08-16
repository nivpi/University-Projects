
public class Mayor implements Comparable<Mayor>, Voteable {
	
	private String name;
	private String city;
	private int votes;
	private int years_in_city;

	public Mayor(String name) {
		this.name=name;
		this.city="BGU";
		this.votes=0;
		this.years_in_city=3;
	}

	public int getVotes() {
		return votes;
	}
	
	public String getName() {
		return name;
	}
	
	public void addVote() {
		votes++;
	}
	
	public int compareTo(Mayor other) {
		return this.votes-other.votes;
	}
	
	public String toString() {
		return ">"+this.name+"<";
	}
}



