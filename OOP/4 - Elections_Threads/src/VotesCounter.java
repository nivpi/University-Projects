import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class VotesCounter implements Runnable {

	private AtomicBoolean alive = new AtomicBoolean(true);
	public Election e;
	public long electionsEndTime;
	private Vector<Mayor> mayors;
	private Vector<MList> lists;

	public VotesCounter(Election e) {
		this.e=e;
		this.mayors=new Vector<Mayor>();
		this.lists=new Vector<MList>();
		this.electionsEndTime=e.getEndOfElections();
	}

	public void run() {
		try {
			Thread.sleep(electionsEndTime*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageEndOfElections();
		wakeUpCall();
		joinedThreads();
		sortVotes();
		printResults();
		printPcgForMayor();
		this.goHome();
	}
	private void joinedThreads() {
		for(int i=0;i<e.getEntities().size();i++) {
			try {
				e.getEntities().get(i).join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	private void wakeUpCall() {
		e.getGuardQueue().insert(null);
		e.getManagerQueue().insert(null);
		e.getVSQueue().insert(null);
		e.getCopQueue().insert(null);
	}
	private void messageEndOfElections() {
		e.getGuardQueue().setCanVoteFalse();
		e.getCopQueue().setCanVoteFalse();
		e.getManagerQueue().setCanVoteFalse();
		e.getVSQueue().setCanVoteFalse();
		for(int i=0; i<e.getEmployees().size();i++) {
			e.getEmployees().get(i).setCanVoteFalse();
		}
	}

	private void sortVotes() {
		for(int i=0;i<e.getVotes().size();i++) {
			Mayor m=new Mayor(e.getVotes().get(i).getMayor());
			mayors.add(m);
			MList l=new MList(e.getVotes().get(i).getList());
			lists.add(l);
		}

		for (int i=0;i<e.getVotes().size();i++) {
			String mayor=e.getVotes().get(i).getMayor();
			for ( int j=0;j<mayors.size();j++) {
				if(mayors.get(j).getName().equals(mayor)) {
					mayors.get(j).addVote();
				}
				else {
					Mayor m=new Mayor(mayor);
					mayors.add(m);
				}
			}

			String list=e.getVotes().get(i).getList();
			for ( int j=0;j<lists.size();j++) {
				if(lists.get(j).getName().equals(list)) {
					lists.get(j).addVote();
				}
				else {
					MList l=new MList(list);
					lists.add(l);
				}
			}
		}
	}

	private Mayor winningMayor() {
		return Collections.max(this.mayors);
	}

	private MList winningList() {
		return Collections.max(this.lists);
	}

	private int numOfVotesFor(String s) {
		for(Mayor m: this.mayors) {
			if(m.getName().equals(s))
				return m.getVotes();
		}
		return 0;
	}

	public void printPcgForMayor() {
		int totalVotes=e.getVotes().size();
		System.out.println("Voting Percentages are below:");
		System.out.println("Roie Zivan: "+((numOfVotesFor("Roie Zivan")*100.0/totalVotes))+"%");
		System.out.println("Ben Rachmut: "+((numOfVotesFor("Ben Rachmut")*100.0/totalVotes))+"%");
		System.out.println("Maya Lavie: "+((numOfVotesFor("Maya Lavie")*100.0/totalVotes))+"%");
		System.out.println("Noam Gaon: "+((numOfVotesFor("Noam Gaon")*100.0/totalVotes))+"%");
	}

	public void printResults() {
		System.out.println("Mayor Chosen is : "+winningMayor());
		System.out.println("List Chosen is : "+winningList());
	}

	public void goHome() {
		alive.set(false);
	}

}