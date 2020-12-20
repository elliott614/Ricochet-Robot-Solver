import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RicochetRobotSolver {
	public static final String BOARD_PATH = "./gameboard.txt";
	public static final String OUTPUT = "./output.txt";
	public static final String SOLUTION = "./solution.txt";
	public static final int RED_X = 6;
	public static final int RED_Y = 7;
	public static final int BLUE_X = 7;
	public static final int BLUE_Y = 0;
	public static final int GREEN_X = 3;
	public static final int GREEN_Y = 3;
	public static final int YELLOW_X = 10;
	public static final int YELLOW_Y = 0;

	public static void main(String[] args) throws Exception {
		// initialize robot starts spaces
		Board board = new Board(BOARD_PATH);
		Robot red = new Robot(RED_Y, RED_X, "red");
		Robot blue = new Robot(BLUE_Y, BLUE_X, "blue");
		Robot green = new Robot(GREEN_Y, GREEN_X, "green");
		Robot yellow = new Robot(YELLOW_Y, YELLOW_X, "yellow");

		Goal[] goals = new Goal[8];

		goals[0] = new Goal(9, 1, "red");
		goals[1] = new Goal(9, 1, "blue");
		goals[2] = new Goal(9, 1, "green");
		goals[3] = new Goal(9, 1, "yellow");

		goals[4] = new Goal(12, 10, "red");
		goals[5] = new Goal(12, 10, "blue");
		goals[6] = new Goal(12, 10, "green");
		goals[7] = new Goal(12, 10, "yellow");

		BufferedWriter bwOutput = new BufferedWriter(new FileWriter(OUTPUT));
		BufferedWriter bwSolution = new BufferedWriter(new FileWriter(SOLUTION));

		for (int i = 0; i < 8; i++) { // only write first solution path
			State initial1 = new State(board, red, blue, green, yellow, goals[i]); // initial state for naive method
			State initial2 = new State(board, red, blue, green, yellow, goals[i]); // initial state for smarter method

			System.out.println("Starting naive IDS on configuration " + i);
			State final1 = initial1.ids();
			System.out.println("finished in " + final1.stepsTracker[0] + " vertex expansions and " + final1.g());

			System.out.println("Starting smarter IDS on configuration " + i);
			State final2 = initial2.smarterIds();
			System.out.println("finished in " + final2.stepsTracker[0] + " vertex expansions and " + final2.g());

			bwOutput.write("" + RED_Y + "," + RED_X + "," + BLUE_Y + "," + BLUE_X + "," + GREEN_Y + "," + GREEN_X + ","
					+ YELLOW_Y + "," + YELLOW_X + "," + goals[i].y() + "," + goals[i].x() + "," + goals[i].color());
			bwOutput.newLine();
			bwOutput.write(
					"" + final1.stepsTracker[0] + "," + final1.g() + "," + final2.stepsTracker[0] + "," + final2.g());
			bwOutput.newLine();

			if (i == 0) { //only write one solution to file
				ArrayList<State> states = new ArrayList<State>();
				State curr = final2;

				while (curr != null) {
					states.add(curr);
					curr = curr.previous();
				}
				for (int j = states.size() - 1; j >= 0; j--) {
					bwSolution.newLine();
					bwSolution.write("Moves Taken: " + (states.size() - 1 - j));
					bwSolution.newLine();
					bwSolution.write(states.get(j).toString());
				}
			}

		}
		
		bwOutput.close();
		bwSolution.close();

	}

}
