import java.util.concurrent.atomic.AtomicBoolean;

public class Policeman extends Employee implements Runnable {
	private Queue<Voter> vsQueue;
	private Queue <Voter> copQueue;
	public int rank;
	public int age;
	public String first_name;
	public String last_name;

	public Policeman (Queue<Voter> vsQueue,Queue <Voter> copQueue) {
		this.vsQueue=vsQueue;
		this.copQueue=copQueue;
	}

	public void run() {
		while(canVote.get()==true) {
			Voter v=copQueue.extract();
			if(canVote.get()==true) {
				if(v.getCopFlag()==false) {
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					double p = Math.random();
					if(p<0.5)
						v.goHome();
					else {
						v.setCopFlagTrue();
						vsQueue.insert(v);
					}
				}else
					v.goHome();
			}
		}
		while(cleanUpVoters.get()==true) {
			if(copQueue.isEmpty()==false) {
				Voter v = copQueue.extract();
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
