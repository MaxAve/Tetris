import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.HashMap;

@SuppressWarnings("serial")
public class Panel extends JPanel implements ActionListener {
    // Panel size
	static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int SCREEN_WIDTH = (int)screenSize.getWidth();
	static final int SCREEN_HEIGHT = (int)screenSize.getHeight();

    // Time
	static final int DELAY = 0;
	Timer timer;

	// GUI data

	// Hash map for Square enum and actual Color
	private static HashMap<Game.PixelColor, Color> gridColors = new HashMap<Game.PixelColor, Color>();

	// Size of each cell in the grid
	static int gridSquareSize = 30;
	static int gridXOffsetToMid = SCREEN_WIDTH/2; // The exact position where the grid will appear in the center of the screen

	// Constructor
	// Initializes the Panel
	public Panel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		timer = new Timer(DELAY, this);
		timer.start();

		// Pixel colors are represented with an enum in the main class
		// We use this to assign appropriate color values to each enum variable
		gridColors.put(Game.PixelColor.NONE, new Color(25, 25, 25));
		gridColors.put(Game.PixelColor.PINK, new Color(255, 0, 240));
		gridColors.put(Game.PixelColor.BLUE, new Color(0, 230, 255));
		gridColors.put(Game.PixelColor.YELLOW, new Color(255, 220, 50));
		gridColors.put(Game.PixelColor.GREEN, new Color(0, 255, 50));
		gridColors.put(Game.PixelColor.RED, new Color(255, 70, 130));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
        drawTetrisGrid(gridXOffsetToMid - (gridSquareSize * Game.grid[0].length / 2), 50, gridSquareSize, g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	/* Utility methods */

	// Draws the tetris grid
	public static void drawTetrisGrid(int xOffset, int yOffset, int squareSize, Graphics g) {
		for(int i = 0; i < Game.grid.length; i++) {
			for(int j = 0; j < Game.grid[0].length; j++) {
				// Square
				g.setColor(gridColors.get(Game.grid[i][j]));
				g.fillRect(j * squareSize + xOffset, i * squareSize + yOffset, squareSize, squareSize);

				// Outline
				g.setColor(Color.BLACK);
				g.drawRect(j * squareSize + xOffset, i * squareSize + yOffset, squareSize, squareSize);

				// Glow
				int glowStrength = 10;
				g.setColor(new Color(gridColors.get(Game.grid[i][j]).getRed(),
						   gridColors.get(Game.grid[i][j]).getGreen(),
						   gridColors.get(Game.grid[i][j]).getBlue(),
						   50));
				g.fillRect(j * squareSize + xOffset - glowStrength/2, i * squareSize + yOffset + 1 - glowStrength/2, squareSize + glowStrength, squareSize + glowStrength);
			}
		}
	}

    // Key adapter
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				Game.fastMode = !Game.fastMode;
				break;
			case KeyEvent.VK_RIGHT:
				for(int i = 0; i < Game.bricks.size(); i++) {
					Game.bricks.get(i).right();
				}
				break;
			case KeyEvent.VK_LEFT:
				for(int i = 0; i < Game.bricks.size(); i++) {
					Game.bricks.get(i).left();
				}
				break;
			case KeyEvent.VK_DOWN:
				for(int i = 0; i < Game.bricks.size(); i++) {
					Game.bricks.get(i).rotate();
				}
				break;
			}
		}
	}
}
