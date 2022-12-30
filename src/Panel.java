import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.HashMap;

public class Panel extends JPanel implements ActionListener {
    // Panel size
	static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int SCREEN_WIDTH = (int)screenSize.getWidth();
	static final int SCREEN_HEIGHT = (int)screenSize.getHeight();

    // Timer
	Timer timer;

	/* GUI data */

	// Hash map for Square enum and actual Color
	private static HashMap<Game.PixelColor, Color> gridColors = new HashMap<Game.PixelColor, Color>();

	// Size of each cell in the grid
	static int gridSquareSize = 30;
	static int gridXOffsetToMid = SCREEN_WIDTH/2; // The exact position where the grid will appear in the center of the screen

	static boolean bricksGlow = true; // Faint glow will be removed from bricks if set to false
	static int glowStrength = 10; // The distance by which the brick glow will spread
	static int glowIntensity = 50; // The intensity of the brick glow (0 - 255)
	static boolean drawPixelOutline = true; // When true, an outline will be drawn marking individual cells on the grid

	// Constructor
	// Initializes the Panel
	public Panel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		timer = new Timer(0, this);
		timer.start();
		
		// Initialize new sin wave counter
		SineWaveCounterUpdater sineCounter = new SineWaveCounterUpdater(0.1, 40);
		sineCounter.start();

		// Pixel colors are represented with an enum in the main class
		// We use this to assign appropriate color values to each enum variable
		gridColors.put(Game.PixelColor.NONE, new Color(20, 20, 20));
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
		// Score display
		g.setColor(new Color(255, 255, 255, (int)(Math.sin(SineWaveCounterUpdater.counter) * 50 + 185)));
		g.setFont(new Font(null, Font.BOLD, 28));
		g.drawString("SCORE: " + Game.score, 30, 50);

		drawTetrisGrid(gridXOffsetToMid - (gridSquareSize * Game.gridWidth / 2), 50, gridSquareSize, g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	/* Utility methods */

	// Draws the tetris grid
	public static void drawTetrisGrid(int xOffset, int yOffset, int squareSize, Graphics g) {
		try {
			// Draw empty grid cells
			g.setColor(gridColors.get(Game.PixelColor.NONE));
			g.fillRect(xOffset, yOffset, squareSize * Game.gridWidth, squareSize * Game.gridHeight);

			// Draw colored pixels
			for(Game.Pixel pixel : Game.pixelList) {
				// Square
				g.setColor(gridColors.get(pixel.pixelColor));
				g.fillRect(pixel.getX() * squareSize + xOffset, pixel.getY() * squareSize + yOffset, squareSize, squareSize);

				// Glow and behold
				if(bricksGlow) {
					g.setColor(new Color(gridColors.get(pixel.pixelColor).getRed(),
										gridColors.get(pixel.pixelColor).getGreen(),
										gridColors.get(pixel.pixelColor).getBlue(),
										glowIntensity));
					g.fillRect(pixel.getX() * squareSize + xOffset - glowStrength/2, pixel.getY() * squareSize + yOffset + 1 - glowStrength/2, squareSize + glowStrength, squareSize + glowStrength);
				}
			}

			// Draw outline
			if(drawPixelOutline) {
				g.setColor(Color.BLACK);
				for(int i = 0; i < Game.gridHeight; i++)
					for(int j = 0; j < Game.gridWidth; j++)
						g.drawRect(j * squareSize + xOffset, i * squareSize + yOffset, squareSize, squareSize);
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/*
	Updates the counter variable which is used
	for sine waves
	*/
	public class SineWaveCounterUpdater extends Thread {
		public static double counter;
		private static final double MAX_RADIAN = Math.PI * 2; // The maximum number for a radian is 2 * PI
		private static double increment = 0;
		private static long waitTime = 0;

		public SineWaveCounterUpdater(double i, long delay) {
			increment = i;
			waitTime = delay;
		}

		public void run() {
			while(Game.running) {
				counter += increment;

				// In order to avoid number overflows, we need to limit counter
				if(counter > MAX_RADIAN) {
					counter = 0;
				}

				try {
					sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

    // Key adapter
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				for(int i = 0; i < Game.bricks.size(); i++) {
					Game.bricks.get(i).fall();
				}
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
			case KeyEvent.VK_A:
				for(int i = 0; i < Game.bricks.size(); i++) {
					Game.bricks.get(i).rotate();
				}
				break;
			case KeyEvent.VK_D:
				for(int i = 0; i < Game.bricks.size(); i++) {
					for(int j = 0; j < 3; j++)
						Game.bricks.get(i).rotate();
				}
				break;
			}
		}
	}
}
