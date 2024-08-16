
public class Human extends Tile {
	private int money;
	
	public Human() {
		super();
		factor = 4;
		money = 2;
	}
	
	public Human(int age, int money) {
		this();
		this.age = age;
		this.money = money;
	}
	
	// this method executes when both Humans are compatible
	public Tile merge(Tile other) {
		//  returns an older Human at this location
		money += ((Human)other).money;
		age++;
		return this;
	}
	
	public Tile clone() {
		return new Human(age, money);
	}
	
	public int getMoney() {
		return money;
	}
	
	public String toString() {
		return "H" + age;
	}
	
	public boolean winningTile() {
		if(age == Tile.maxAge) // if this Human Tile reached the max age, it is a winning tile.
			return true;
		return false;
	}
}
