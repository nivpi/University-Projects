
class MayorCandidate extends ListCandidate implements Votable {
	
	private int votes;
	
	// Constructor
	public MayorCandidate(int id, String name, int age, String city, int years_in_city, MunicipalityList list_name) {
		super(id, name, age, city, years_in_city, list_name);
		this.votes = 0;
	}
	
	// Method to increase votes count by 1, each time a vote is made
	public void vote() {
		this.votes++;
	}

    public int getVotes() {
    	return this.votes;
    }
    
}
