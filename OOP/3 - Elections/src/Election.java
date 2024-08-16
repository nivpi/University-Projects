import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

public class Election {
	
	private Vector<Municipality> municipalities;
	private Vector<Vote> vote_history;
	private String[][] persons_data, votes_data;
	private int idIndex, nameIndex, ageIndex, cityIndex, yearsIndex, listIndex, typeIndex;
	
	// Exact path to persons.txt and vote_to.txt is needed
	public Election(String persons, String vote_to) throws FileNotFoundException, IOException {
		initPersons(persons);
		initVotes(vote_to);
		electionResults();
		votingRatios();
	}
	
	// Initiate all our "Pre-Election Day" data
	// Data arrangement, from the text file into a 2D array is used for the code's author comfort 
	private void initPersons(String persons_file) throws FileNotFoundException, IOException {
		String persons = new String(Files.readAllBytes(Paths.get(persons_file)));
		this.persons_data = textToArray(persons);
		this.municipalities = new Vector<Municipality>();
		
		// Assuming our data is sorted in a table with the required column headers
		this.idIndex = findIndex(persons_data, "Id");
		this.nameIndex = findIndex(persons_data, "Name");
		this.ageIndex = findIndex(persons_data, "Age");
		this.cityIndex = findIndex(persons_data, "City");
		this.yearsIndex = findIndex(persons_data, "Years_In_City");
		this.listIndex = findIndex(persons_data, "Municipality_List");
		this.typeIndex = findIndex(persons_data, "listCandidate");
		
		for (int i = 1; i < persons_data.length; i++) {
			String[] row = persons_data[i];
			Municipality m = initMunicipality(row[cityIndex]);
			MunicipalityList ml = initList(m, row[listIndex]);
			initPerson(m, ml,row);
		}
	}
	
	// Converting our text data into a 2D string, assuming we received a tab-seperated table data file
	private static String[][] textToArray(String text) {
		String[] rows = text.split(System.getProperty("line.separator"));
		String[][] data = new String[rows.length][];
		for(int i = 0; i < rows.length; i++)
			data[i] = rows[i].split("\t");
		return data;
	}
	
	// Run across the first line of our data, looking for a specific table column
	private static int findIndex(String[][] data, String title) {
		String[] firstRow = data[0];
		for(int i = 0; i < firstRow.length; i++)
			if(firstRow[i].equals(title))
				return i;
		return -1;
	}
	
	private Municipality initMunicipality(String city) {
		for(Municipality m : municipalities) {
			if(m.getName().equals(city))	// given municipality was already created
				return m;
		}
		Municipality m = new Municipality(city);	// create a new one and add it to our vector
		municipalities.add(m);
		return m;
	}
	
	private MunicipalityList initList(Municipality m, String list) {
		if(list.equals("")) // we do not want to create a new list if it was empty in the data array.
			return null;
		for(MunicipalityList ml : m.getMuncipalityLists()) {
			if(ml.getName().equals(list))
				return ml;
		}
		MunicipalityList ml = new MunicipalityList(list, m.getName());
		m.addListCandidates(ml);
		return ml;
	}
	
	private void initPerson(Municipality m, MunicipalityList ml, String[] row) {
		try {
			String name = row[nameIndex], city = row[cityIndex];
			int id = Integer.parseInt(row[idIndex]), age = Integer.parseInt(row[ageIndex]);
			if(ml == null)			// if it's not a candidate, create a normal voter.
				m.addVoter(new Voter(id,name,age,city));
			else {					// if it is a candidate, check which type (list or mayor candidate?)
				String type = row[typeIndex];
				int years = Integer.parseInt(row[yearsIndex]);
				if(type.equals("0")) {
					MayorCandidate mc = new MayorCandidate(id,name,age,city,years,ml);
					m.addMayorCandidates(mc);
					ml.addCandidate(mc);
					m.addVoter(mc);
				}
				else if(type.equals("1")) {
					ListCandidate lc = new ListCandidate(id,name,age,city,years,ml);
					ml.addCandidate(lc);
					m.addVoter(lc);
				}
			}
			
		} catch(IllegalArgumentException e) {	// this condition should be removed in case we want to crash the program because of an invalid person
			System.out.println(e.getMessage());
		}
	}
	
	private void initVotes(String vote_to_file) throws FileNotFoundException, IOException {
		String vote_to = new String(Files.readAllBytes(Paths.get(vote_to_file)));
		this.votes_data = textToArray(vote_to);
		this.vote_history = new Vector<Vote>();
		
		for(int i = 1; i < votes_data.length; i++){
			String[] row = votes_data[i];
			int id = Integer.parseInt(row[1]);
			String voteTo = row[0];
			try {
				if(isMayorVote(voteTo)) {
					MayorCandidate mc = findMayor(Integer.parseInt(voteTo));
					Municipality m = findMunicipality(mc.getCity());
					mayorVoting(id, mc, m);
				}
				else {
					MunicipalityList ml = findMunicipalityList(voteTo);
					Municipality m = findMunicipality(ml.getCity());
					listVoting(id, ml, m);
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private boolean isMayorVote(String voteTo) {
		try {
			int mayorId = Integer.parseInt(voteTo);
			return true; // we received a valid mayor ID.
		}
		catch(NumberFormatException e) {
			return false; // we received a list, it is not a mayor vote.
		}
	}
	
	// an attempt to vote for a mayor
	public void mayorVoting(int voter_ID, MayorCandidate mc, Municipality m) throws Exception {
		Voter v = findVoter(voter_ID);
		if(!m.getName().equals(v.getCity()))
			throw new Exception("Mayor Candidate must be in your city.");
		if(v.getVotedMayor())
			throw new Exception("You already voted for a mayor!");
		mc.vote();
		v.voteMayor();
		vote_history.add(new Vote(v,mc));
	}
	
	// an attempt to vote for a list
	public void listVoting(int voter_ID, MunicipalityList mc, Municipality m) throws Exception {
		Voter v = findVoter(voter_ID);
		if(!m.getName().equals(v.getCity()))
			throw new Exception("Municipality List must be in your city.");
		if(v.getVotedList())
			throw new Exception("You already voted for a list!");
		mc.vote();
		v.voteList();
		vote_history.add(new Vote(v,mc));
	}
	
	private MayorCandidate findMayor(int mayorID) throws Exception{
		for(Municipality m : municipalities)
			for(MayorCandidate mc : m.getMayorCandidates())
				if(mayorID == mc.getId())
					return mc;
		throw new Exception("Mayor not found.");
	}
	
	private MunicipalityList findMunicipalityList(String voteTo) throws Exception {
		for(Municipality m : municipalities)
			for(MunicipalityList ml : m.getMuncipalityLists())
				if(voteTo.equals(ml.getName()))
					return ml;
		throw new Exception("Municipality List not found.");
	}
	
	private Municipality findMunicipality(String name) throws Exception {
    	for(Municipality m : municipalities)
    		if(name.equals(m.getName()))
    				return m;
    	throw new Exception("Municipality not found.");
    }
	
	private Voter findVoter(int id) throws Exception {
		for(Municipality m : municipalities)
			for(Voter v : m.getVoters())
				if(id == v.getId())
					return v;
		throw new Exception("Voter not found.");
	}
	
	public void electionResults() {
		winningMayors();
		evaluateLists();
	}
	
	// find who got the most votes in a given city, for all cities
	private void winningMayors() {
		for(Municipality m : municipalities) {
			m.sortMayorCandidatesByVotes();
			MayorCandidate winner = m.getMayorCandidates().get(0);
			int winnerVotes = winner.getVotes();
			double totalVotes = 0;
			for(Voter v : m.getVoters())
				if(v.getVotedMayor())
					totalVotes++;
			double percentage = (int)(winnerVotes/totalVotes * 1000)/10.0; // ensures having 1 decimal point
			System.out.println("Mayor elected: " + winner + " with " + percentage + "%.");
		}
	}
	
	// evaluating the seat allocation at the city, for all cities
	private void evaluateLists() {
		for(Municipality m : municipalities) {
			System.out.println("Evaluating seats of lists in " + m.getName() + ":");
			int totalSeats = totalSeats(m);
			int listVotes = listVotes(m);
			for(MunicipalityList ml : m.getMuncipalityLists()) {
				int votes = ml.getVotes();
				long seats = Math.round((double)votes/listVotes * totalSeats);
				System.out.println(ml.getName() + " with " + seats + " seats.");
			}
			System.out.println();
		}
	}
	
	// calculating how many seats in total a given city should get
	// based on population and min\max values
	private int totalSeats(Municipality m) {
		int potentialVoters = m.getVoters().size();
		if(potentialVoters > 19)
			return 19;
		if(potentialVoters < 5)
			return 5;
		return potentialVoters;
	}
	
	// pulling list-votes in a given city
	private int listVotes(Municipality m) {
		int listVotes = 0;
		for(Voter v : m.getVoters())
			if(v.getVotedList())
				listVotes++;
		return listVotes;
	}
	
	private void votingRatios() {
		System.out.println("Total voting ratio: " + votingRatio());
    	for(Municipality m : municipalities)
    		System.out.println("Voting ratio in " + m.getName() + ": " + votingRatio(m));
	}
	
	public double votingRatio() {
    	int potentialVoters = 0;
    	int voters = 0;
    	for(Municipality m : municipalities) {
    		potentialVoters += m.getVoters().size();
    		voters += m.actualVoters();
    	}
    	return (double)voters/potentialVoters;
    }
    
    public double votingRatio(Municipality m) {
    	return (double) m.actualVoters() / m.getVoters().size();
    }
    
    // Max value element based on natural comparison
    public static Comparable getMax(Vector<Comparable> list) {
    	if(list == null || list.size() == 0)
    		return null;
    	Comparable max = list.get(0);
    	for(Comparable c : list)
    		if(c.compareTo(max) > 0)
    			max = c;
    	return max;
    }
    
    public Vector<Votable> getVoteable(Voter v) throws Exception {
    	Vector<Votable> list = new Vector<Votable>();
    	Municipality m = findMunicipality(v.getCity());
    	for(MayorCandidate mc : m.getMayorCandidates())
    		list.add(mc);
    	for(MunicipalityList ml : m.getMuncipalityLists())
    		list.add(ml);
    	return list;
    }
}
