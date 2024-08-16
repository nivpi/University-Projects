import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SingleSelectionModel;

public class Manager extends Employee implements Runnable  {

	private Queue<Voter> guardQueue;
	private BoundedQueue<Voter> managerQueue;
	private Vector <Integer> ids;

	public Manager(Queue<Voter> guardQueue, BoundedQueue<Voter> managerQueue, Vector <Integer> ids) {
		this.guardQueue=guardQueue;
		this.managerQueue=managerQueue;
		this.ids=ids;
	}

	public void run() {
		while(canVote.get()==true) {
			Voter v = managerQueue.extract();
			if(canVote.get()==true) {
				if(v.getAge()>=17) {
					double p = Math.random();
					if (p<0.9) {
						try {
							long l = ((long) Math.random()*3000) + 3000;
							Thread.sleep(l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ids.add(v.getID());
						guardQueue.insert(v);
					}
					else
						v.goHome();
				}
				else
					v.goHome();
			}	
		}
		while(cleanUpVoters.get()) {
			if (managerQueue.isEmpty()==false){
				Voter v = guardQueue.extract();
				if(v!=null)
					v.goHome();
			}
			else
				this.goHome();
		}
	}

	public void goHome() {
		cleanUpVoters.set(false);
	}

	public void setCanVoteFalse() {
		canVote.set(false);
	}



}
