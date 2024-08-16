import Turtle.*;

public class EightTurtle extends SmartTurtle {
	private final double p = 0.7;		// Probability of drawing 8-sides polygon with given side length
	private final int weakness = 8;
	
	public EightTurtle() {
		super();
	}
	
	public void draw(int sides, double size) {
		if (Math.random() < p)	// 70% chance
			super.draw(weakness, size);
		else					// 30% chance
			super.draw(sides, 18);
	}
}
