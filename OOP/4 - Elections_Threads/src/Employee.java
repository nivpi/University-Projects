import java.util.concurrent.atomic.AtomicBoolean;

public class Employee {
	
	protected AtomicBoolean canVote=new AtomicBoolean(true);
	protected AtomicBoolean cleanUpVoters=new AtomicBoolean(true);
	protected long endOfElections;
	
	public Employee() {
		
	}
	
	protected void setCanVoteFalse() {
		this.canVote.set(false);
	}

	
}
