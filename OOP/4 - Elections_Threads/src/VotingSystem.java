import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class VotingSystem extends Employee implements Runnable{
	
	private VoteTicket vote;
	private Vector<VoteTicket> votes;
	private Queue<Voter> vsQueue;
	private Queue <Voter> copQueue;
	public static int serial=0;
	public static AtomicBoolean bye=new AtomicBoolean(true);

	public VotingSystem(Vector<VoteTicket> votes,Queue<Voter> vsQueue, Queue <Voter> copQueue) {
		this.votes=votes;
		this.vsQueue=vsQueue;
		this.copQueue=copQueue;
	}
	
	public void run() {
		while(canVote.get()==true) {
			Voter currVoter=vsQueue.extract();
			if(canVote.get()==true) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					vote= new VoteTicket(currVoter,currVoter.getMayor(),currVoter.getList());
					votes.add(vote);
					currVoter.goHome();
				} catch (VoteTicketUnvalidException e) {
					copQueue.insert(currVoter);}
				}
		}
		while(cleanUpVoters.get()) {
			if ((!vsQueue.isEmpty())&&bye.get()) {
				Voter currVoter=vsQueue.extract();
				if(currVoter!=null) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					try {
						vote= new VoteTicket(currVoter,currVoter.getMayor(),currVoter.getList());
						votes.add(vote);
						currVoter.goHome();
					} catch (VoteTicketUnvalidException e) {
						copQueue.insert(currVoter);
					}
				}
			}
			else {
				this.goHome();
			}
		}
	}
	
	
	protected void goHome() {
		cleanUpVoters.set(false);
	}
	
	protected void setCanVoteFalse() {
		canVote.set(false);
	}
	public static void setBye(boolean flag){
		bye.set(flag);
	}
	


}

