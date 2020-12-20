
public class Robot {
	private int x; // x coordinate (in cells)
	private int y; // y coordinate (in cells)
	private String color; // red, blue, green, or yellow
	public static final String PRINT_RED = "♥R♥";
	public static final String PRINT_BLUE = "♦B♦";
	public static final String PRINT_GREEN = "♣G♣";
	public static final String PRINT_YELLOW = "♠Y♠";

	public Robot(int y, int x, String color) throws Exception {
		if (color != "red" && color != "blue" && color != "green" && color != "yellow")
			throw new Exception("invalid robot color");
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public String color() {
		return this.color;
	}
}
