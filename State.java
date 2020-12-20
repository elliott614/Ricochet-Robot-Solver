import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	private Board board; // game board
	private Robot red;
	private Robot blue;
	private Robot green;
	private Robot yellow;
	private Goal goal;
	private Move previousMove; // robot moved in previous state
	private State previousState;
	private List<State> nextStates; // next states
	private boolean nextStatesFound; // has state been expanded already?
	private int g;
	private Map<String, Integer> prevVisitedDepths; // keep track of lowest depth an equivalent state has been
													// reached for smarter search
	public int[] stepsTracker;

	// create initial state using board, initial robots, and goal
	public State(Board board, Robot red, Robot blue, Robot green, Robot yellow, Goal goal) {
		this.prevVisitedDepths = new HashMap<String, Integer>();
		this.board = board;
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.yellow = yellow;
		this.goal = goal;
		this.previousMove = null;
		this.previousState = null;
		this.nextStates = new ArrayList<State>();
		this.nextStatesFound = false;
		this.g = 0;
		stepsTracker = new int[1];
		stepsTracker[0] = 0;

	}

	// create a non-initial state
	private State(Robot changed, Move previousMove, State previousState, int g, Map<String, Integer> prevVisitedDepths,
			int[] stepsTracker) {
		this.g = g;
		this.board = previousState.board;
		this.nextStatesFound = false;
		switch (changed.color()) {
		case "red":
			this.red = changed;
			this.blue = previousState.blue;
			this.green = previousState.green;
			this.yellow = previousState.yellow;
			break;
		case "blue":
			this.red = previousState.red;
			this.blue = changed;
			this.green = previousState.green;
			this.yellow = previousState.yellow;
			break;
		case "green":
			this.red = previousState.red;
			this.blue = previousState.blue;
			this.green = changed;
			this.yellow = previousState.yellow;
			break;
		default:
			this.red = previousState.red;
			this.blue = previousState.blue;
			this.green = previousState.green;
			this.yellow = changed;
			break;
		}
		this.goal = previousState.goal;
		this.previousMove = previousMove;
		this.previousState = previousState;
		this.nextStates = new ArrayList<State>();
		this.prevVisitedDepths = prevVisitedDepths;
		this.stepsTracker = stepsTracker;
	}

	// Iterated deepening search for goal state. Returns null if no solution found,
	// otherwise returns goal state
	public State ids() throws Exception {
		State goalState = null;
		for (int i = 1; goalState == null; i++) {
			System.out.println("Checking depth: " + i);
			goalState = this.depthLimitedDFS(i);
		}
		return goalState;
	}

	// depth limited DFS
	public State depthLimitedDFS(int maxDepth) throws Exception {
		// base cases
		if (this.inGoalState())
			return this;
		if (maxDepth == 0) // not in goal state base case
			return null;
		this.stepsTracker[0]++; // increment steps taken
		State checkedState;
		if (!this.nextStatesFound)
			this.findNextStates();
		for (State state : this.nextStates) {
			checkedState = state.depthLimitedDFS(maxDepth - 1);
			if (checkedState != null && checkedState.inGoalState()) // if goal state found
				return checkedState;
		}
		return null; // return null if unsuccessful
	}

	// smarter IDS that prunes equivalent states
	public State smarterIds() throws Exception {
		State goalState = null;
		for (int i = 1; goalState == null; i++) {
			System.out.println("Checking depth: " + i);
			goalState = this.prunedDepthLimitedDFS(i);
		}
		return goalState;
	}

	// smarter depth limited DFS
	public State prunedDepthLimitedDFS(int maxDepth) throws Exception {
		// base cases
		if (this.inGoalState())
			return this;
		if (maxDepth == 0) // not in goal state base case
			return null;
		this.stepsTracker[0]++;
		State checkedState;
		if (!this.nextStatesFound)
			this.pruningFindNextStates();
		for (State state : this.nextStates) {
			checkedState = state.prunedDepthLimitedDFS(maxDepth - 1);
			if (checkedState != null && checkedState.inGoalState()) // if goal state found
				return checkedState;
		}
		return null; // return null if unsuccessful
	}

	// return true if in goal state, otherwise return false
	public boolean inGoalState() {
		switch (this.goal.color()) {
		case "red":
			if (this.goal.x() == this.red.x() && this.goal.y() == this.red.y())
				return true;
			break;
		case "blue":
			if (this.goal.x() == this.blue.x() && this.goal.y() == this.blue.y())
				return true;
			break;
		case "green":
			if (this.goal.x() == this.green.x() && this.goal.y() == this.green.y())
				return true;
			break;
		case "yellow":
			if (this.goal.x() == this.yellow.x() && this.goal.y() == this.yellow.y())
				return true;
			break;
		}
		return false;
	}

	// find all next states depth 1 from this
	public void pruningFindNextStates() throws Exception {
		this.nextStates = new ArrayList<State>(); // clear next states before filling
		this.pruningFindNextStates(this.red);
		this.pruningFindNextStates(this.blue);
		this.pruningFindNextStates(this.green);
		this.pruningFindNextStates(this.yellow);
		this.nextStatesFound = true;
	}

	// find all next states depth 1 from this
	public void findNextStates() throws Exception {
		this.nextStates = new ArrayList<State>(); // clear next states before filling
		this.findNextStates(this.red);
		this.findNextStates(this.blue);
		this.findNextStates(this.green);
		this.findNextStates(this.yellow);
		this.nextStatesFound = true;
	}

	// find all next states depth d from this
	public void pruningFindNextStates(int d) throws Exception {
		if (d == 0)
			return; // base case
		if (!this.nextStatesFound)
			this.pruningFindNextStates();
		for (State state : this.nextStates)
			state.pruningFindNextStates(d - 1);
	}

	// find all next states depth d from this
	public void findNextStates(int d) throws Exception {
		if (d == 0)
			return; // base case
		if (!this.nextStatesFound)
			this.findNextStates();
		for (State state : this.nextStates)
			state.findNextStates(d - 1);
	}

	private void pruningFindNextStates(Robot moved) throws Exception {
		String key;
		// check up
		int y = moved.y();
		while (this.canMoveUpFrom(y, moved.x()))
			y--;
		if (moved.y() != y) {
			State newState = new State(new Robot(y, moved.x(), moved.color()), new Move(moved, 'U'), this, this.g + 1,
					this.prevVisitedDepths, this.stepsTracker);
			key = newState.generateKey();
			if (!prevVisitedDepths.containsKey(key)) {
				this.nextStates.add(newState);
				prevVisitedDepths.put(key, g + 1);
			}
		}

		// check down
		y = moved.y();
		while (this.canMoveDownFrom(y, moved.x()))
			y++;
		if (moved.y() != y) {
			State newState = new State(new Robot(y, moved.x(), moved.color()), new Move(moved, 'D'), this, this.g + 1,
					this.prevVisitedDepths, this.stepsTracker);
			key = newState.generateKey();
			if (!prevVisitedDepths.containsKey(key)) {
				this.nextStates.add(newState);
				prevVisitedDepths.put(key, g + 1);
			} else if (prevVisitedDepths.get(key) > g + 1) {
				this.nextStates.add(newState);
				prevVisitedDepths.put(key, g + 1);
			}
		}

		// check left
		int x = moved.x();
		while (this.canMoveLeftFrom(moved.y(), x))
			x--;
		if (moved.x() != x) {
			State newState = new State(new Robot(moved.y(), x, moved.color()), new Move(moved, 'L'), this, this.g + 1,
					this.prevVisitedDepths, this.stepsTracker);
			key = newState.generateKey();
			if (!prevVisitedDepths.containsKey(key)) {
				this.nextStates.add(newState);
				prevVisitedDepths.put(key, g + 1);
			}
		}

		// check right
		x = moved.x();
		while (this.canMoveRightFrom(moved.y(), x))
			x++;
		if (moved.x() != x) {
			State newState = new State(new Robot(moved.y(), x, moved.color()), new Move(moved, 'R'), this, this.g + 1,
					this.prevVisitedDepths, this.stepsTracker);
			key = newState.generateKey();
			if (!prevVisitedDepths.containsKey(key)) {
				this.nextStates.add(newState);
				prevVisitedDepths.put(key, g + 1);
			}
		}
	}

	// find next states from moving specified robot, but naive because it doens't
	// check if state has been visited previously
	private void findNextStates(Robot moved) throws Exception {
		// check up
		int y = moved.y();
		while (this.canMoveUpFrom(y, moved.x()))
			y--;
		if (moved.y() != y)
			this.nextStates.add(new State(new Robot(y, moved.x(), moved.color()), new Move(moved, 'U'), this,
					this.g + 1, this.prevVisitedDepths, this.stepsTracker));

		// check down
		y = moved.y();
		while (this.canMoveDownFrom(y, moved.x()))
			y++;
		if (moved.y() != y)
			this.nextStates.add(new State(new Robot(y, moved.x(), moved.color()), new Move(moved, 'D'), this,
					this.g + 1, this.prevVisitedDepths, this.stepsTracker));

		// check left
		int x = moved.x();
		while (this.canMoveLeftFrom(moved.y(), x))
			x--;
		if (moved.x() != x)
			this.nextStates.add(new State(new Robot(moved.y(), x, moved.color()), new Move(moved, 'L'), this,
					this.g + 1, this.prevVisitedDepths, this.stepsTracker));

		// check right
		x = moved.x();
		while (this.canMoveRightFrom(moved.y(), x))
			x++;
		if (moved.x() != x)
			this.nextStates.add(new State(new Robot(moved.y(), x, moved.color()), new Move(moved, 'R'), this,
					this.g + 1, this.prevVisitedDepths, this.stepsTracker));
	}

	// generate key for this state
	private String generateKey() {
		Robot other1;
		Robot other2;
		Robot other3;
		Robot player;

		switch (this.goal.color()) {
		case "red":
			player = this.red;
			other1 = this.blue;
			other2 = this.green;
			other3 = this.yellow;
			break;
		case "blue":
			other1 = this.red;
			player = this.blue;
			other2 = this.green;
			other3 = this.yellow;
			break;
		case "green":
			other1 = this.red;
			other2 = this.blue;
			player = this.green;
			other3 = this.yellow;
			break;
		default:
			other1 = this.red;
			other2 = this.blue;
			other3 = this.green;
			player = this.yellow;
			break;
		}

		Integer[] nonPlayers = new Integer[3];
		nonPlayers[0] = other1.y() * Board.CELLS_X + other1.x(); // convert position to single integer rather than pair
		nonPlayers[1] = other2.y() * Board.CELLS_X + other2.x();
		nonPlayers[2] = other3.y() * Board.CELLS_X + other3.x();

		Arrays.sort(nonPlayers); // sort array of nonplayers

		String key = "" + (player.y() * Board.CELLS_X + player.x()) + "," + nonPlayers[0] + "," + nonPlayers[1] + ","
				+ nonPlayers[2];

		return key;
	}

	// check if can move up from coordinate
	private Boolean canMoveUpFrom(int y, int x) throws Exception {

		// look for wall
		if (this.board.getCharacterArray()[this.board.yCenterInChars(y) - Board.DY_CENTER_TO_WALL][this.board
				.xCenterInChars(x)] == '═')
			return false;

		// look for robot
		if (this.red.x() == x && this.red.y() == y - 1 || this.blue.x() == x && this.blue.y() == y - 1
				|| this.green.x() == x && this.green.y() == y - 1 || this.yellow.x() == x && this.yellow.y() == y - 1)
			return false;

		return true;
	}

	// check if can move down from coordinate
	private Boolean canMoveDownFrom(int y, int x) throws Exception {

		// look for wall
		if (this.board.getCharacterArray()[this.board.yCenterInChars(y) + Board.DY_CENTER_TO_WALL][this.board
				.xCenterInChars(x)] == '═')
			return false;

		// look for robot
		if (this.red.x() == x && this.red.y() == y + 1 || this.blue.x() == x && this.blue.y() == y + 1
				|| this.green.x() == x && this.green.y() == y + 1 || this.yellow.x() == x && this.yellow.y() == y + 1)
			return false;

		return true;
	}

	// check if can move left from coordinate
	private Boolean canMoveLeftFrom(int y, int x) throws Exception {

		// look for wall
		if (this.board.getCharacterArray()[this.board.yCenterInChars(y)][this.board.xCenterInChars(x)
				- Board.DX_CENTER_TO_WALL] == '║')
			return false;

		// look for robot
		if (this.red.x() == x - 1 && this.red.y() == y || this.blue.x() == x - 1 && this.blue.y() == y
				|| this.green.x() == x - 1 && this.green.y() == y || this.yellow.x() == x - 1 && this.yellow.y() == y)
			return false;

		return true;
	}

	// check if can move right from coordinate
	private Boolean canMoveRightFrom(int y, int x) throws Exception {

		// look for wall
		if (this.board.getCharacterArray()[this.board.yCenterInChars(y)][this.board.xCenterInChars(x)
				+ Board.DX_CENTER_TO_WALL] == '║')
			return false;

		// look for robot
		if (this.red.x() == x + 1 && this.red.y() == y || this.blue.x() == x + 1 && this.blue.y() == y
				|| this.green.x() == x + 1 && this.green.y() == y || this.yellow.x() == x + 1 && this.yellow.y() == y)
			return false;

		return true;
	}

	// return string representation of game board state
	public static void printGoalSequence(State goal) {
		ArrayList<State> states = new ArrayList<State>();
		State curr = goal;

		while (curr != null) {
			states.add(curr);
			curr = curr.previousState;
		}
		for (int i = states.size() - 1; i >= 0; i--) {
			System.out.println();
			System.out.println("Moves Taken: " + (states.size() - 1 - i));
			System.out.println();
			System.out.println(states.get(i).toString());
		}

	}

	@Override
	public String toString() {
		// copy chars in board
		int height = this.board.getCharacterArray().length;
		int width = this.board.getCharacterArray()[0].length;
		char[][] gameBoard = new char[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				gameBoard[i][j] = this.board.getCharacterArray()[i][j];

		// place previous move
		if (this.previousMove != null) {
			int prevMoveY = this.board.yCenterInChars(this.previousMove.robot.y());
			int prevMoveX = this.board.xCenterInChars(this.previousMove.robot.x());
			char[] moveChars = this.previousMove.draw().toCharArray();
			Robot movedRobot = this.red;
			switch (this.previousMove.robot.color()) {
			case "blue":
				movedRobot = this.blue;
				break;
			case "green":
				movedRobot = this.green;
				break;
			case "yellow":
				movedRobot = this.yellow;
				break;
			}
			int movedToY = this.board.yCenterInChars(movedRobot.y());
			int movedToX = this.board.xCenterInChars(movedRobot.x());
			int leftX = prevMoveX < movedToX ? prevMoveX : movedToX;
			int topY = prevMoveY < movedToY ? prevMoveY : movedToY;
			int rightX = prevMoveX > movedToX ? prevMoveX : movedToX;
			int bottomY = prevMoveY > movedToY ? prevMoveY : movedToY;
			for (int i = topY; i <= bottomY; i += Board.CELL_HEIGHT)
				for (int j = leftX; j <= rightX; j += Board.CELL_WIDTH) {
					gameBoard[i][j - 1] = moveChars[0];
					gameBoard[i][j] = moveChars[1];
					gameBoard[i][j + 1] = moveChars[2];
				}

		}

		// place red robot
		char[] redRobot = Robot.PRINT_RED.toCharArray();
		int redY = this.board.yCenterInChars(red.y());
		int redX = this.board.xCenterInChars(red.x());
		gameBoard[redY][redX - 1] = redRobot[0];
		gameBoard[redY][redX] = redRobot[1];
		gameBoard[redY][redX + 1] = redRobot[2];

		// place blue robot
		char[] blueRobot = Robot.PRINT_BLUE.toCharArray();
		int blueY = this.board.yCenterInChars(blue.y());
		int blueX = this.board.xCenterInChars(blue.x());
		gameBoard[blueY][blueX - 1] = blueRobot[0];
		gameBoard[blueY][blueX] = blueRobot[1];
		gameBoard[blueY][blueX + 1] = blueRobot[2];

		// place green robot
		char[] geenRobot = Robot.PRINT_GREEN.toCharArray();
		int greenY = this.board.yCenterInChars(green.y());
		int greenX = this.board.xCenterInChars(green.x());
		gameBoard[greenY][greenX - 1] = geenRobot[0];
		gameBoard[greenY][greenX] = geenRobot[1];
		gameBoard[greenY][greenX + 1] = geenRobot[2];

		// place yellow robot
		char[] yellowRobot = Robot.PRINT_YELLOW.toCharArray();
		int yellowY = this.board.yCenterInChars(yellow.y());
		int yellowX = this.board.xCenterInChars(yellow.x());
		gameBoard[yellowY][yellowX - 1] = yellowRobot[0];
		gameBoard[yellowY][yellowX] = yellowRobot[1];
		gameBoard[yellowY][yellowX + 1] = yellowRobot[2];

		// place goal
		char[] goalChars = this.goal.PRINT.toCharArray();
		int goalY = this.board.yCenterInChars(this.goal.y());
		int goalX = this.board.xCenterInChars(this.goal.x());
		gameBoard[goalY][goalX - 1] = goalChars[0];
		gameBoard[goalY][goalX + 1] = goalChars[2];
		if (gameBoard[goalY][goalX] == ' ')
			gameBoard[goalY][goalX] = goalChars[1];

		// place into string
		String string = "";
		for (int i = 0; i < gameBoard.length - 1; i++)
			string += new String(gameBoard[i]) + "\n";
		string += new String(gameBoard[gameBoard.length - 1]);

		return string;
	}

	public int g() {
		return this.g;
	}
	
	public State previous() {
		return this.previousState;
	}
}

class Move {
	Robot robot;
	char direction;

	public Move(Robot robot, char direction) throws Exception {
		if (direction == 'U' || direction == 'D' || direction == 'L' || direction == 'R')
			this.direction = direction;
		else
			throw new Exception("invalid direction");
		this.robot = robot;
	}

	// draw the previous move for game board (i.e. return a string to represent
	// previous move on game board)
	public String draw() {
		String symbol = "♥"; // initialize to red's symbol
		switch (this.robot.color()) {
		case "blue":
			symbol = "♦";
			break;
		case "green":
			symbol = "♣";
			break;
		case "yellow":
			symbol = "♠";
			break;
		}
		char arrow = '→';
		switch (this.direction) {
		case 'U':
			arrow = '↑';
			break;
		case 'D':
			arrow = '↓';
			break;
		case 'L':
			arrow = '←';
			break;
		}
		return arrow + symbol + arrow;
	}
}