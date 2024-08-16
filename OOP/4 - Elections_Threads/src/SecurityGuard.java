import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecurityGuard extends Employee implements Runnable {

	private Vector<Integer> ids;
	private Queue<Voter> guardQueue;
	private Queue<Voter> vsQueue;
	private BoundedQueue<Voter> managerQueue;
	private int salary;
	private int experience;
	private String first_name;
	private String last_name;

	public SecurityGuard(Vector<Integer> ids, Queue<Voter> guardQueue, BoundedQueue<Voter> managerQueue, Queue<Voter> vsQueue) {
		this.ids=ids;
		this.guardQueue = guardQueue;
		this.managerQueue = managerQueue;
		this.vsQueue = vsQueue;
	}

	public void run() {
		while(canVote.get()==true) {
			Voter v = guardQueue.extract();
			if(canVote.get()==true) {
				if (! (isIdOnList(v.getID())==true && v.getAge()>=17))
					managerQueue.insert(v);
				else
					vsQueue.insert(v);
			}
		}
		while(cleanUpVoters.get()==true) {
			if(guardQueue.isEmpty()==false) {
				Voter v = guardQueue.extract();
				if(v!=null)
					if (! (isIdOnList(v.getID())==true && v.getAge()>=17))
						v.goHome();
			}else {
				VotingSystem.setBye(false);
				this.goHome();
			}
		}
	}
	public boolean isIdOnList(int id) {
		try {
			long l = ((long) Math.random()*3000) + 2000;
			Thread.sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0;i<this.ids.size();i++) {
			if(id==this.ids.get(i))
				return true;
		}
		return false;
	}
	public void goHome() {
		cleanUpVoters.set(false);
	}
	public void setCanVoteFalse() {
		canVote.set(false);
	}


}
