import java.util.Vector;

public class BoundedQueue <T> extends Queue<T>{

	private int maxSize;

	public BoundedQueue (int maxSize){
		super();
		this.maxSize=maxSize;
	}

	public synchronized void insert(T t) {

		while (this.size()==maxSize) {
			try {
				wait();
			}catch (InterruptedException e) {}

		}
		if(t==null) {
			notifyAll();
		}else {
			q.add(t);
			notifyAll();
		}
	}

	public synchronized T extract() {
		while (isEmpty()) {
			if(canVote.get()) {
				try {
					wait();
				}catch (InterruptedException e) {}
			}
			else
				return null;
		}
		T t=q.get(0);
		q.remove(t);
		notifyAll();
		return t;
	}

	public Vector <T> getBoundedQueue(){
		return q;
	}

}
