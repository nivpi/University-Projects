import Turtle.*;

public class DrunkTurtle extends Turtle {

	public DrunkTurtle() {
		super();
	}

	public DrunkTurtle clone() {
		return new DrunkTurtle();
	}

	public void turnRight(int y) {
		super.turnRight((int) (2 * y * Math.random()));
	}

	public void turnLeft(int y) {
		super.turnLeft((int) (2 * y * Math.random()));
	}

	public void turnLeftNormally(int y) {
		super.turnLeft(y);
	}

	public void moveForward(double x) {
		super.moveForward(x * Math.random());
		if (Math.random() < 0.3)
			turnLeft((int) (x));
		super.moveForward(x * Math.random());
	}

	public void moveNormally(double x) {
		super.moveForward(x);
	}

//			TEST
//	public static void main(String[] args) {
//		DrunkTurtle joey = new DrunkTurtle();
//		joey.show();
//		joey.turnLeft(50);
//		joey.moveForward(100);
//	}
}
