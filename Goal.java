
public class Goal {
	private int y; // y coordinate
	private int x; // x coordinate
	private String color; //color that must reach goal
	public final String PRINT = "░▒░";

	//construct goal in specific location
	public Goal(int y, int x, String color) throws Exception {
//		if ((y == 7 || y == 8) && (x == 7 || x == 8))
//			throw new Exception("Invalid goal position in constructor");
		if (color != "red" && color != "blue" && color != "green" && color != "yellow")
			throw new Exception("invalid robot color");
		this.y = y;
		this.x = x;
		this.color = color;
	}
	
	public String color() {
		return this.color;
	}
	
	public int y() {
		return this.y;
	}
	
	public int x() {
		return this.x;
	}
}
