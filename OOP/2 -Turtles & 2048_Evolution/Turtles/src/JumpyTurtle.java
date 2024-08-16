import Turtle.*;

public class JumpyTurtle extends SmartTurtle {
	private boolean tailUp;
	private int step;		// Step size

	public JumpyTurtle() {
		super();
		this.tailUp = false; // since Turtle() initiates with tail down
		this.step = 5;
	}

	public JumpyTurtle(boolean tailUp, int step) {
		super();
		this.tailUp = tailUp;
		this.step = step;
	}

	public JumpyTurtle clone() {
		return new JumpyTurtle(this.tailUp, this.step);
	}

	public void tailUp() {
		super.tailUp();
		tailUp = true;
	}

	public void tailDown() {
		super.tailDown();
		tailUp = false;
	}

	public void moveForward(double dis) {
		if (tailUp)
			super.moveForward(dis); // results with a normal-looking move
		else // tail down
			jumpyMove(dis);
	}

	private void jumpyMove(double dis) {
		double disLeft = dis;
		while (disLeft >= 2 * step) { // 2 or more full steps are left
			drawStep(step);
			jumpStep(step);
			disLeft -= 2 * step;
		}
		if (disLeft < step) // less than 1 step is left
			drawStep(disLeft);
		else { // 1.xx steps are left
			drawStep(step);
			disLeft -= step;
			jumpStep(disLeft);
		}
	}

	private void drawStep(double dis) {
		super.moveForward(dis);
	}

	private void jumpStep(double dis) {
		super.tailUp();
		super.moveForward(dis);
		super.tailDown();
	}

//				TEST
//	public static void main(String[] args) {
//		JumpyTurtle t = new JumpyTurtle();
//		t.moveForward(38);
//		t.turnLeft(19);
//		t.moveForward(42);
//		t.turnRight(50);
//		t.moveForward(52);
//		t.turnRight(35);
//		t.moveForward(24);
//	}
}
