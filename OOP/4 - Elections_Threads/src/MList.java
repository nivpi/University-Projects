import java.util.Vector;

public class MList implements Voteable, Comparable<MList>{
	private String name;
	private int votes;
	private Vector<String> members;
	
	public MList(String name) {
		this.name=name;
		this.members=new Vector<String>();
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
	public int compareTo(MList other) {
		return this.votes-other.votes;
	}
	public String toString() {
		return ">"+this.name+"<";
	}
}
