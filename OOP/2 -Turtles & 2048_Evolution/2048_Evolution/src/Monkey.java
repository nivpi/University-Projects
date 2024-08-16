
public class Monkey extends Tile {
	public Monkey() {
		super();
		factor = 1;
	}

	public Monkey(int age) {
		this();
		this.age = age;
	}

	// this method executes when both monkeys are compatible
	public Tile merge(Tile other) {
		if (age == maxAge) // We reached evolution requirements, returns a new chimpanzee at this location
			return new Chimpanzee();
		else { // otherwise, returns an older monkey at this location
			age++;
			return this;
		}
	}

	public Tile clone() {
		return new Monkey(age);
	}

	public String toString() {
		return "M" + age;
	}
	
	public boolean winningTile() { // Monkey Tile cannot be a winning Tile.
		return false;
	}
}
