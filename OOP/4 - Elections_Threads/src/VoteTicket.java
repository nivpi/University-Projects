
public class VoteTicket implements Comparable<VoteTicket> {

	private int index;
	private Voter v;
	private String mayor;
	private String list;

	public VoteTicket (Voter v,String mayor,String list) throws VoteTicketUnvalidException {
		double p = Math.random();
		if (p > 0.2) {
			this.index=VotingSystem.serial;
			VotingSystem.serial++;
			this.v=v;
			this.mayor=mayor;
			this.list=list;
		}
		else 
			throw new VoteTicketUnvalidException();
	}
	public int getVoterAge() {
		return this.v.getAge();
	}
	public int compareTo(VoteTicket vote) {
		return this.index-vote.index;
	}
	public String getMayor() {
		return this.mayor;
	}
	public String getList() {
		return this.list;
	}
	public int getIndex() {
		return index;
	}
	public int getVoterID() {
		return this.v.getID();
	}
}
