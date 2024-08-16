
public class Flintstone extends Tile {

	public Flintstone() {
		super();
		factor = 3;
	}

	public Flintstone(int age) {
		this();
		this.age = age;
	}

	// this method executes when both Flintstones are compatible
	public Tile merge(Tile other) {
		if (age == maxAge) // We reached evolution requirements, returns a new Human at this location
			return new Human();
		else { // otherwise, returns an older Flintstone at this location
			age++;
			return this;
		}
	}

	public Tile clone() {
		return new Flintstone(age);
	}

	public String toString() {
		return "F" + age;
	}
	
	public boolean winningTile() { // Flintstone Tile cannot be a winning Tile.
		return false;
	}
}
