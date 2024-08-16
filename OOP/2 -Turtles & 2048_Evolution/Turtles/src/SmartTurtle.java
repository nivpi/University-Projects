import Turtle.*;

public class SmartTurtle extends Turtle{
	
	public SmartTurtle() {
		super();
	}
	
	public SmartTurtle clone() {
		return new SmartTurtle();
	}
	
	public void draw(int sides, double size) {
		int sumAngles = (sides-2)*180;			// interior-angles' sum in a polygon
		int angle = (int)(sumAngles/sides);		// regular polygons have equal sizes of angles and sides
		tailDown();
		int stackedAng = angle;					// starting angle
			while (stackedAng <= sumAngles) {
				moveForward(size);
				turnRight(180 - angle);
				stackedAng += angle;
			}
		tailUp();
	}

//				TEST
//	public static void main(String[] args) {
//		SmartTurtle joey = new SmartTurtle();
//		joey.show();
//		joey.draw(15, 30);
//		joey.moveForward(100);
//	}
}