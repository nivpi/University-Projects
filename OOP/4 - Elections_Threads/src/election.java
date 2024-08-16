import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Election {

	public static String votersData = "voters data.txt";
	public static String idlist = "id list.txt";
	private static Vector<Voter> voters_database;
	private Vector<Integer> ids;
	private Vector<VoteTicket> votes;
	private Queue<Voter> vsQueue;
	private BoundedQueue <Voter> copQueue;
	private BoundedQueue<Voter> managerQueue;
	private Queue<Voter> guardQueue;
	private SecurityGuard guard;
	private int totalGuards;
	private long endOfElections;
	private Vector<Thread> entities;
	private Vector<Employee> employees;

	public Election(String votersData,String idlist,long endOfElections,int totalGuards) {
		this.totalGuards=totalGuards;
		this.endOfElections=endOfElections;
		this. guardQueue=new Queue<Voter>();
		this.ids=new Vector<Integer>();
		this.voters_database = new Vector<Voter>();
		this.readVoters(votersData);
		this.readIds(idlist);
		this.vsQueue=new Queue<Voter>();
		this. copQueue=new BoundedQueue<Voter>(7);
		this. managerQueue=new BoundedQueue<Voter>(7);
		this.votes= new Vector<VoteTicket>();
		this.entities= new Vector<Thread>();
		this. employees=new Vector<Employee>();
		this.createEntities();

	}
	private void createEntities() {
		createVoters();
		this.createGuards();
		this.createManager();
		this.createVotingSystems();
		this.createCops();
		this.createVotesCounter();
	}
	private void createVoters() {
		for(int i=0;i< voters_database.size();i++) {
			Thread v=new Thread(voters_database.get(i));
			entities.add(v);
			v.start();
		}
	}
	private void createGuards() {
		for(int i=0;i< totalGuards;i++) {//
			guard=new SecurityGuard(ids,guardQueue,managerQueue,vsQueue);
			employees.add(guard);
			Thread s=new Thread(guard);
			entities.add(s);
			s.start();
		}
	}
	private void createManager() {
		Manager m=new Manager(this.guardQueue,this.managerQueue,this.ids);
		this.employees.add(m);
		Thread m1=new Thread(m);
		this.entities.addElement(m1);
		m1.start();
	}
	private void createVotingSystems() {
		for(int i=0;i< 2;i++) {
			VotingSystem vs=new VotingSystem(this.votes,this.vsQueue,this.copQueue);
			this.employees.add(vs);
			Thread v=new Thread(vs);
			this.entities.add(v);
			v.start();
		}
	}
	private void createCops() {
		for(int i=0;i<3;i++) {
			Policeman p=new Policeman(vsQueue,copQueue);
			employees.add(p);
			Thread v=new Thread(p);
			entities.add(v);
			v.start();
		}
	}
	private void createVotesCounter() {
		VotesCounter vc=new VotesCounter(this);
		Thread vvc=new Thread(vc);
		vvc.start();
	}	
	public Vector<Thread> getEntities(){
		return this.entities;
	}
	public Vector<Employee> getEmployees(){
		return this.employees;
	}
	public Vector<VoteTicket> getVotes(){
		return votes;
	}
	public long getEndOfElections() {
		return endOfElections;
	}
	public Queue<Voter> getGuardQueue(){
		return guardQueue;
	}
	public BoundedQueue<Voter> getCopQueue(){
		return copQueue;
	}
	public BoundedQueue<Voter> getManagerQueue(){
		return managerQueue;
	}
	public Queue<Voter> getVSQueue(){
		return vsQueue;
	}
	private void readVoters(String votersData) {	
		BufferedReader inFile = null;
		try {
			FileReader fr = new FileReader(votersData);
			inFile = new BufferedReader(fr);
			String st;
			st = inFile.readLine();
			while ((st = inFile.readLine()) != null) {
				String [] arr = st.split("\t");
				String first_name = arr[0];
				String last_name = arr[1];
				int id = Integer.parseInt(arr[2]);
				int age = Integer.parseInt(arr[3]);
				String mayor_selection = arr[4];
				String list_selection = arr[5];
				int arrival_time = Integer.parseInt(arr[6]);
				Voter v=new Voter(first_name,last_name,id,age,mayor_selection,list_selection,arrival_time,guardQueue,endOfElections);
				voters_database.add(v);
			}
		}
		catch (FileNotFoundException exception) {
		} catch (IOException exception) {
			System.out.println(exception);
		} finally {
			try {
				inFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void readIds(String idlist) {	
		BufferedReader inFile = null;
		try {
			FileReader fr = new FileReader(idlist);
			inFile = new BufferedReader(fr);
			String st;
			st = inFile.readLine();
			while ((st = inFile.readLine()) != null) {
				String [] arr = st.split("\t");
				int id_list = Integer.parseInt(arr[0]);
				this.ids.add(id_list);
			}
		}
		catch (FileNotFoundException exception) {
		} catch (IOException exception) {
			System.out.println(exception);
		} finally {
			try {
				inFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
