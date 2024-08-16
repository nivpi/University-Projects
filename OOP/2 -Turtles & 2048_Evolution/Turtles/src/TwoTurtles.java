import Turtle.*;

public class TwoTurtles {

	public static void main(String[] args) {
		Turtle artem = new Turtle();
		Turtle linoy = new Turtle();
		artem.show();
		artem.tailUp();
		linoy.show();
		linoy.tailUp();

		artem.turnLeft(90);
		artem.moveForward(40);
		artem.turnRight(90);
		drawA(artem);
		artem.moveForward(100);

		linoy.turnRight(90);
		linoy.moveForward(40);
		linoy.turnLeft(90);
		drawL(linoy);
		linoy.turnLeft(18);
		linoy.moveForward(85);

		wiggle(artem, linoy, 12);

		artem.hide();
		linoy.hide();
	}

	public static void drawA(Turtle tur) {
		tur.tailDown();
		tur.turnRight(90 - 63);
		tur.moveForward(45);
		tur.turnLeft(180 + 54);
		tur.moveForward(45);
		tur.moveBackward(21);
		tur.turnRight(180 - 63);
		tur.moveForward(21);
		tur.tailUp();
	}

	public static void drawL(Turtle tur) {
		tur.tailDown();
		tur.moveForward(40);
		tur.moveBackward(40);
		tur.turnRight(90);
		tur.moveForward(27);
		tur.tailUp();
	}

	public static void wiggle(Turtle tur1, Turtle tur2, int spins) {
		int count = 0;
		while (count <= spins) {
			tur1.turnLeft(360);
			tur2.turnRight(360);
			count++;
		}
	}

}
