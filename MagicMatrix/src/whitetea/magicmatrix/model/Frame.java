package whitetea.magicmatrix.model;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * 
 * @author WhiteTea
 *
 */
public class Frame implements Cloneable {

	//TODO overal met x y & width heigthwerken ipv rowNr & colNr
	private final int rows, cols;
	private Color[][] colors;

	public Frame(int rows, int cols) {
		if(rows <= 0)
			throw new IllegalArgumentException("The number of rows must be strictly positive.");
		if(cols <= 0)
			throw new IllegalArgumentException("The number of columns must be strictly positive.");
		this.rows = rows;
		this.cols = cols;
		colors = new Color[rows][cols];
		fill(new Color(0, 0, 0));
	}

	public Frame(Frame frame) {
		this.rows = frame.getNbOfRows();
		this.cols = frame.getNbOfColumns();
		colors = new Color[rows][cols];
		for (int y = 0; y < this.rows; y++)
			for (int x = 0; x < this.cols; x++)
				setPixelColor(y, x, frame.getPixelColor(y, x));
	}

	public int getNbOfRows() {
		return rows;
	}

	public int getNbOfColumns() {
		return cols;
	}

	public boolean isValidRowNr(int rowNr) {
		return rowNr >= 0 && rowNr < rows;
	}

	public boolean isValidColNr(int colNr) {
		return colNr >= 0 && colNr < cols;
	}

	public void setPixelColor(int rowNr, int colNr, Color c) {
		if (!isValidRowNr(rowNr) || !isValidColNr(colNr))
			throw new IllegalArgumentException(
					"The given indices are not valid for this matrix size."
							+ "\nMatrix size: " + rows + "x" + cols
							+ "\nGiven indices: " + rowNr + ", " + colNr);
		colors[rowNr][colNr] = c;
	}

	public Color getPixelColor(int rowNr, int colNr) {
		if (!isValidRowNr(rowNr) || !isValidColNr(colNr))
			throw new IllegalArgumentException(
					"The given indices are not valid for this matrix size."
							+ "\nMatrix size: " + rows + "x" + cols
							+ "\nGiven indices: " + rowNr + ", " + colNr);
		return colors[rowNr][colNr];
	}

	public void fill(Color c) {
		for (int i = 0; i < rows; i++) {
			fillRow(i, c);
		}
	}

	public void fillRow(int rowNr, Color c) {
		if (!isValidRowNr(rowNr))
			throw new IllegalArgumentException("Invalid row number: " + rowNr
					+ ". Matrix size is: " + rows + "x" + cols);
		for (int i = 0; i < cols; i++)
			setPixelColor(rowNr, i, c);
	}

	public void fillColumn(int colNr, Color c) {
		if (!isValidColNr(colNr))
			throw new IllegalArgumentException("Invalid column number: "
					+ colNr + ". Matrix size is: " + rows + "x" + cols);
		for (int i = 0; i < rows; i++)
			setPixelColor(i, colNr, c);
	}

	//TODO laatste rij kleuren van eerste rij met boolean
	//TODO strategy met color voor laatste rij
	public void shiftLeft() {
		for (int x = 0; x < this.cols; x++) 
			for (int y = 0; y < this.rows; y++)
				setPixelColor(y, x, 
						(x < this.cols - 1) ? getPixelColor(y, x+1) : new Color(0,0,0));
	}

	public void shiftRight() {
		for (int y = 0; y < this.rows; y++) {
			for (int x = this.cols - 1; x >= 0; x--) {
				setPixelColor(y, x, (x > 0) ? getPixelColor(y,x - 1) : new Color(0,0,0));
			}
		}
	}

	public void shiftUp() {
		for (int y = 0; y < this.rows; y++) {
			for (int x = 0; x < this.cols; x++) {
				setPixelColor(y, x, 
						(y < this.rows - 1) ? getPixelColor(y + 1, x) : new Color(0,0,0));
			}
		}
	}

	public void shiftDown() {
		for (int y = this.rows - 1; y >= 0; y--) {
			for (int x = 0; x < this.cols; x++) {
				setPixelColor(y, x, (y > 0) ? getPixelColor(y - 1, x) : new Color(0,0,0));
			}
		}
	}
	
	public BufferedImage getImage() {
		BufferedImage bufferedImage = new BufferedImage(colors[0].length, colors.length,
		        BufferedImage.TYPE_INT_RGB);

		// Set each pixel of the BufferedImage to the color from the Color[][].
		for (int x = 0; x < getNbOfColumns(); x++)
		    for (int y = 0; y < getNbOfRows(); y++)
		        bufferedImage.setRGB(x, y, colors[y][x].getRGB());
		return bufferedImage;
	}
	
	@Override
	public Frame clone() {
		return new Frame(this);
	}

}
