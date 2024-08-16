
class Voter extends Person implements Comparable<Voter> {

	private boolean votedList, votedMayor; // record whether a vote was made, and from each type
	
	// Constructor
	public Voter(int id, String name, int age, String city) {
		super(id, name, age, city);
		if (age < 17) {		// Aggressive reaction for an attempt to add an underage or negative-age voter
			throw new IllegalArgumentException("Error creating voter with id: " + id + ". A voter cannot be under the age of 17");
		}
		this.votedList = false;
		this.votedMayor = false;
	}

	@Override
	public int compareTo(Voter other) {		// Default comparison
		return this.getAge() - other.getAge();
	}

    public boolean getVotedList(){
        return this.votedList;
    }

    public boolean getVotedMayor() {
    	return this.votedMayor;
    }

    public void voteList() {	// for a later indication that a Voter already voted
        this.votedList = true;
    }

    public void voteMayor() {	// for a later indication that a Voter already voted
        this.votedMayor = true;
    }
    
}
