import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Board {

	private char[][] boardChars; // contains the 2d array of ASCII characters representing the game board
	private int[][][] cellCenterIndex; // center index of cells in boardChars array
	public static final int HEIGHT = 33; // height of board characters
	public static final int WIDTH = 66; // width of board characters array
	public static final int CELLS_X = 16; // board width in number of cells
	public static final int CELLS_Y = 16; // board height in number of cells
	public static final int CELL_WIDTH = 3; // number of characters in a cell
	public static final int WALL_THICKNESS = 1; // walls are one character
	public static final int CELL_HEIGHT = 1; // cells are 1 character tall
	public static final int DX_CENTER_TO_WALL = CELL_WIDTH / 2 + 1;
	public static final int DY_CENTER_TO_WALL = CELL_HEIGHT / 2 + 1;

	public Board(String path) throws IOException {
		this.readBoard(path);
	}

	// read board from file into boardChars array
	private void readBoard(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		char[][] boardChars = new char[HEIGHT][WIDTH];
		String line;
		for (int i = 0; i < HEIGHT && (line = br.readLine()) != null; i++)
			boardChars[i] = line.toCharArray();
		this.boardChars = boardChars;
		br.close();
	}

	public void calcCellIndeces() {
		int[][][] cellCenterIndex = new int[CELLS_Y][CELLS_X][2]; // celCenterIndex[cellY][cellX] = {i,j} where
																	// boardChars[i][j] == center index
		for (int i = 0; i < CELLS_Y; i++)
			for (int j = 0; j < CELLS_X; j++) {
				cellCenterIndex[i][j][0] = yCenterInChars(i);
				cellCenterIndex[i][j][1] = xCenterInChars(j);
			}

		this.cellCenterIndex = cellCenterIndex;
	}
	
	public int xCenterInChars(int cellX) {
		return (WALL_THICKNESS + CELL_WIDTH) * cellX + WALL_THICKNESS + CELL_WIDTH / 2;
	}
	
	public int yCenterInChars(int cellY) {
		return (WALL_THICKNESS + CELL_HEIGHT) * cellY + WALL_THICKNESS + CELL_HEIGHT / 2;
	}
	
	public char[][] getCharacterArray() {
		return this.boardChars;
	}
}
