import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Queue<T> {
	
	protected AtomicBoolean canVote=new AtomicBoolean(true);
	protected Vector<T> q;
	
		public Queue() {
		q=new Vector<T> ();
	}
		
	public synchronized void insert (T t) {
		if(t==null) {
			this.notifyAll();
		}
		else {
			q.add(t);
			this.notifyAll();
		}
	}
	
	public synchronized T extract() {
		while (isEmpty()) {
			if(canVote.get()) {
				try {
					this.wait();
				}catch(InterruptedException e) {}
			}
			else {
				return null;
			}
		}
			T t=q.get(0);
			q.remove(t);
			return t;
	}
	
	public void setCanVoteFalse() {
		canVote.set(false);
	}
		
	public T peek() {
		T t=q.get(0);
		return t;
	}
	
	public boolean isEmpty() {
		if(q.size()==0) {
			return true;
		}
		return false;
	}
	
	public int size() {
		return q.size();
	}

	public void removeAlll() {
		q.removeAll(q);
	}
	
	public Vector <T> getQueue(){
		return q;
	}
}
