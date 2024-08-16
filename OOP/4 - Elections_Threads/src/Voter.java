import java.util.concurrent.atomic.AtomicBoolean;

public class Voter extends Thread{
	private AtomicBoolean alive=new AtomicBoolean(true);
	private String first_name;
	private String last_name;
	private String mayor;
	private String list;
	private int id;
	private int age;
	private int arrival;
	public long endOfElections;
	private boolean copFlag;
	private Queue<Voter> guardQueue;

	public Voter(String first_name,String last_name,int id,int age, String mayor, String list, int arrival, Queue<Voter> guardQueue ,long endOfElections) {
		this.first_name=first_name;
		this.last_name=last_name;
		this.mayor=mayor;
		this.list=list;
		this.id=id;
		this.age=age;
		this.arrival=arrival;
		this.endOfElections=endOfElections;
		this.copFlag=false;
		this.guardQueue=guardQueue;
	}
	
	public void run() {
		while (this.alive.get()) {
			if(this.arrival>=this.endOfElections)
				this.goHome();
			else {
				try {
					Thread.sleep(arrival);
				} catch (InterruptedException e) {
				}
				guardQueue.insert(this);
				goHome();
			}
		}	
	}
	public void goHome() {
		alive.set(false);
	}
	public int getID() {
		return this.id;
	}
	public int getAge() {
		return this.age;
	}
	public String getMayor() {
		return this.mayor;
	}
	public String getList() {
		return this.list;
	}
	public String getFirstName() {
		return this.first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	
	public void setCopFlagTrue() {
		this.copFlag=true;
	}
	public boolean getCopFlag() {
		return this.copFlag;
	}
	public String toString() {
		return this.id + "-" +this.first_name;
	}
}
