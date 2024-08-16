import java.util.Scanner;
import Turtle.*;

public class Army {

	static Scanner sc = new Scanner(System.in);
	static Turtle[] army;
	static final int n = 5;		// army size

	public static void main(String[] args) {
		System.out.println("Choose the type of a turtle:");
		System.out.println("1. Simple");
		System.out.println("2. Smart");
		System.out.println("3. Drunk");
		System.out.println("4. Jumpy");
		System.out.println("5. Eight");

		initArmy();

		lineUp(); 			// Step 1
		tailDown(); 		// Step 2
		marchForward(65); 	// Step 3
		turnLeft(40); 		// Step 4
		marchForward(75); 	// Step 5
		drawPolygon(6, 40); // Step 6
		hide();				// Step 7
	}

	public static void initArmy() {
		army = new Turtle[n];
		for (int i = 0; i < army.length; i++) {
			int input = sc.nextInt();
			switch (input) {
			case 1:
				army[i] = new Turtle();
				break;
			case 2:
				army[i] = new SmartTurtle();
				break;
			case 3:
				army[i] = new DrunkTurtle();
				break;
			case 4:
				army[i] = new JumpyTurtle();
				break;
			case 5:
				army[i] = new EightTurtle();
				break;
			}
		}
	}

	public static void lineUp() {
		for (int i = 0; i < army.length; i++) {
			army[i].tailUp();
			if (army[i] instanceof DrunkTurtle) {
				((DrunkTurtle) army[i]).turnLeftNormally(90);
				((DrunkTurtle) army[i]).moveNormally(-i * 120);
			} else {
				army[i].turnLeft(90);
				army[i].moveForward(-i * 120);
			}
		}
	}

	public static void tailDown() {
		for (int i = 0; i < army.length; i++)
			army[i].tailDown();
	}

	public static void marchForward(int distance) {
		for (int i = 0; i < army.length; i++)
			army[i].moveForward(distance);
	}

	public static void turnLeft(int deg) {
		for (int i = 0; i < army.length; i++)
			army[i].turnLeft(deg);
	}

	public static void drawPolygon(int sides, double size) {
		for (int i = 0; i < army.length; i++)
			// since "Eight" and "Jumpy" extends Smart, they know how to draw a polygon
			if (army[i] instanceof SmartTurtle)
				((SmartTurtle) army[i]).draw(sides, size);
	}

	public static void hide() {
		for (int i = 0; i < army.length; i++)
			army[i].hide();
	}
}
