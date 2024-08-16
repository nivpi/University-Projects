
public abstract class Tile {
	protected int age;
	protected int factor;
	protected static int maxAge;
	
	public Tile() {
		age = 1;
	}
	public void setMaxAge(int maxAge) {
		Tile.maxAge = maxAge;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getFactor() {
		return factor;
	}
	
	public boolean compatible(Tile other) { // compatible tiles have the same age, and same type
		return age == other.age && factor == other.factor; // same type tiles have the same factor.
	}
	
	public abstract Tile merge(Tile other);
	
	public abstract Tile clone();
	
	public abstract String toString();
	
	public abstract boolean winningTile();
}
