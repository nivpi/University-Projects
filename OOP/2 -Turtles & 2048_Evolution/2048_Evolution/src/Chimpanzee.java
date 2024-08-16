
public class Chimpanzee extends Tile {

	public Chimpanzee() {
		super();
		factor = 2;
	}

	public Chimpanzee(int age) {
		this();
		this.age = age;
	}

	// this method executes when both Chimpanzees are compatible
	public Tile merge(Tile other) {
		// We reached evolution requirements, returns a new Flintstone at this location
		if (age == maxAge || age == maxAge - 1)
			return new Flintstone();
		else { // otherwise, increments the chimpanzee's age by 2
			age += 2;
			return this;
		}
	}

	public Tile clone() {
		return new Chimpanzee(age);
	}

	public String toString() {
		return "C" + age;
	}
	
	public boolean winningTile() { // Chimpanzee Tile cannot be a winning Tile.
		return false;
	}
}
