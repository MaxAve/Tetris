import java.util.ArrayList;
import java.lang.Thread; 
import java.util.Random;

public class Game {
    // Grid size limits
    public static final int MAX_GRID_WIDTH = 80;
    public static final int MAX_GRID_HEIGHT = 40;

    // Grid dimensions
    public static int gridWidth;
    public static int gridHeight;

    // Selected grid dimensions in the menu
    public static int selectedGridWidth;
    public static int selectedGridHeight;

    public static enum PixelColor {NONE, PINK, BLUE, YELLOW, GREEN, RED}
    public static enum Difficulty {EASY, MEDIUM, HARD, EXTREME, CUSTOM}

    // Containers for object instances
    public static ArrayList<Brick> bricks = new ArrayList<>();
    public static ArrayList<Pixel> pixelList = new ArrayList<>(); // Pixel objects can be owned by both this ArrayList and by a Brick instance

    public static boolean running = true;
    public static boolean paused = false;

    // "Fall update" refers to the pause time taken by the thread to call the fall() method on falling Brick instances
    public static final long MAX_FALL_UPDATE_DELAY = 700; // Max bound
    public static final long MIN_FALL_UPDATE_DELAY = 100; // Min bound
    public static long fallUpdateDelay;
    public static long selectedFallUpdateDelay;

    public static long score = 0;
    public static Difficulty selectedDifficulty = Difficulty.EASY; // Selected difficulty
    public static int difficulty; // Calculated difficulty (is used to calculate score on CUSTOM difficulty)

    // Misc
    public static boolean gameOverPending = false; // When true, it will tell the program to wait until SPACE is pressed to exit the game
    private static Random random = new Random();

    // Main
    public static void main(String[] args) {
        // Default difficulty
        // TODO remove once menu GUI is finished
        selectedDifficulty = Difficulty.HARD;

        startNewGame(selectedDifficulty);

        new Frame(); // Initialize new JFrame (creates window)
    }

    public static void startNewGame(Difficulty d) {
        Panel.currentScreenPage = Panel.ScreenPage.GAME;

        int newGridWidth = 0;
        int newGridHeight = 0;
        long newFallUpdateDelay = 0;

        // Initialize grid
        if(d == Difficulty.EASY) {
            newGridWidth = 16;
            newGridHeight = 30;
            newFallUpdateDelay = 500;
        } else if(d == Difficulty.MEDIUM) {
            newGridWidth = 16;
            newGridHeight = 30;
            newFallUpdateDelay = 350;
        } else if(d == Difficulty.HARD) {
            newGridWidth = 16;
            newGridHeight = 30;
            newFallUpdateDelay = 220;
        } else if(d == Difficulty.EXTREME) {
            newGridWidth = 16;
            newGridHeight = 30;
            newFallUpdateDelay = 100;
        } else if(d == Difficulty.CUSTOM) {
            newGridWidth = selectedGridWidth;
            newGridHeight = selectedGridHeight;
            newFallUpdateDelay = selectedFallUpdateDelay;
            difficulty = calculateDifficulty(); // Calculate game difficulty
            System.out.println("Starting new custom game with difficulty " + difficulty);
        }

        initGrid(newGridWidth, newGridHeight);
        newBrick(random.nextInt(5), random.nextInt(gridWidth-4), 0);
        fallUpdateDelay = newFallUpdateDelay;
        running = true;
        BrickFallUpdate t = new BrickFallUpdate();
        t.start();
    }

    public static void gameOver() {
        gameOverPending = true;
    }

    public static void endCurrentGame() {
        // Clear out object instance containers
        bricks.clear();
        pixelList.clear();

        running = false;
        gameOverPending = false;
        Panel.currentScreenPage = Panel.ScreenPage.MAIN_MENU; // Go to main menu
    }

    // Calculates difficulty using an unneccesarily complicated formula based on certain game
    // factors such as grid size and brick fall speed.
    public static int calculateDifficulty() {
        return (int)((((MAX_GRID_HEIGHT + 1 - gridHeight) * gridWidth) * ((int)MAX_FALL_UPDATE_DELAY + 1 - (int)fallUpdateDelay) / 100) / 100);
    }

    // Initializes the grid
    public static void initGrid(int width, int height) {
        gridWidth = width <= MAX_GRID_WIDTH ? width : MAX_GRID_WIDTH;
        gridHeight = height <= MAX_GRID_HEIGHT ? height : MAX_GRID_HEIGHT;
    }

    // Returns a new brick instance
    public static Brick newBrick(int brickID, int x, int y) {
        if(brickID > 4 || brickID < 0) {
            throw new IllegalArgumentException("Brick ID=" + brickID + " is invalid.\nPossible ID values are: 0, 1, 2, 3, 4");
        }
        Pixel[] brickStructure = BrickModel.getModel(x, y, brickID);
        String pixelBrickID = Pixel.generateID();
        for(int i = 0; i < brickStructure.length; i++) {
            brickStructure[i].brickID = pixelBrickID;
        }
        Brick b = new Brick(brickStructure, brickID, brickID != 1);
        b.dx = x;
        b.dy = y;
        return b;
    }

    // Checks if there are any lines, if there are, it removes those lines
    public static void removeLines() {
        /*
        Here we convert the 1-dimensional ArrayList of pixels into one 2D Array
        Is this an overcomplication? An oversimplification? Who knows, I just know that it works...
        */
        int[][] pixelListToGrid = new int[gridHeight][gridWidth]; // Indices of the pixels
        for(int i = 0; i < gridHeight; i++) {
            for(int j = 0; j < gridWidth; j++) {
                pixelListToGrid[i][j] = -1;
            }
        }
        for(int i = 0; i < pixelList.size(); i++) {
            pixelListToGrid[pixelList.get(i).getY()][pixelList.get(i).getX()] = i;
        }

        /*
        Check if there are any lines, if so, then the line will be removed
        */
        int linesRemoved = 0;
        int lowestLineRemoved = -2147483647; // We need to know where the line NEAREST TO THE BOTTOM was removed so that we know which pixels to shift and which not to
        for(int i = 0; i < gridHeight; i++) {
            boolean lineFull = true;
            for(int j = 0; j < gridWidth; j++) {
                if(pixelListToGrid[i][j] < 0) {
                    lineFull = false;
                }
            }
            if(lineFull) {
                if(i > lowestLineRemoved) {
                    lowestLineRemoved = i;
                }
                linesRemoved++;
                // Remove the line
                for(int j = 0; j < gridWidth; j++) {
                    if(pixelListToGrid[i][j] >= 0) {
                        try {
                            int indexToRemove = pixelListToGrid[i][j];
                            pixelList.remove(indexToRemove);

                            // When we remove an object from an ArrayList, all indicex AFTER the removed index get shifted by -1
                            // We need to reflect this change on the 2D array in order to avoid IndexOutOfBoundsExceptions
                            for(int a = 0; a < gridHeight; a++) {
                                for(int b = 0; b < gridWidth; b++) {
                                    if(pixelListToGrid[a][b] >= indexToRemove) {
                                        pixelListToGrid[a][b]--;
                                    }
                                }
                            }
                        } catch(Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            }
        }

        // Shift all remaining pixels downwards by 1 unit
        for(int a = 0; a < linesRemoved; a++) {
            // Give points based on difficulty (custom selected difficulty)
            // In CUSTOM difficulty, a fixed number of points is incremented by difficulty^2
            if(selectedDifficulty == Difficulty.CUSTOM)
                score += Math.pow(difficulty, 2);

            for(int b = 0; b < pixelList.size(); b++) {
                if(pixelList.get(b).getY() < lowestLineRemoved) {
                    pixelList.get(b).shiftPosition(0, 1);
                }
            }
        }

        // Give points based on line combo
        // On difficulties EASY to EXTREME, score is incremented by (number of lines removed in the last update)^3,
        // that is, if 1 line is removed, the player gets 1 point, if 2 lines are removed, they get 8 points, if 3 then 27, and so on.
        score += Math.pow(linesRemoved, 3);
    }

    /* Individual pixel */
    public static class Pixel {
        public final String ID; // The pixel's individual ID
        public String brickID; // The brick ID. All pixels within a brick MUST have the same ID

        public boolean active = true;
        public PixelColor pixelColor;
        private int x, y; // Position on grid

        // Constructor
        public Pixel(int x, int y, PixelColor color) {
            this.x = x;
            this.y = y;
            this.pixelColor = color;
            this.ID = generateID();
            //grid[y][x] = this.pixelColor;
            pixelList.add(this);
        }

        public static String generateID() {
            // TODO may cause bugs, add code to verify ID
            String availableChars = "abcdefghijklmnopqrstuvwxyz0123456789-";
            int idLength = random.nextInt(10) + 10;
            String newID = "";
            for(int i = 0; i < idLength; i++) {
                newID += availableChars.charAt(random.nextInt(availableChars.length()));
            }
            return newID;
        }

        /* Getters */

        public int getX() { return this.x; } // Get X
        public int getY() { return this.y; } // Get Y

        /* Setters */

        // Set position
        public void setPosition(int newX, int newY) {
            this.x = newX;
            this.y = newY;
        }

        /* Utility methods */

        // Shifts the position by deltaX and deltaY
        public void shiftPosition(int deltaX, int deltaY) {
            int newX = this.x + deltaX;
            int newY = this.y + deltaY;
            this.setPosition(newX, newY);
        }
    }

    /* Brick (consists of Pixels) */
    public static class Brick {
        public Pixel[] pixels; // Container for all pixels which make up the brick
        public boolean falling;
        public boolean rotatable;
        public int angle = 0;
        public int dx, dy;
        public int modelID;

        // Constructor
        public Brick(Pixel[] pixels, int ID, boolean rotatable) {
            this.pixels = pixels;
            this.falling = true;
            this.modelID = ID;
            this.rotatable = rotatable;
            bricks.add(this);
        }

        public void deactivate() {
            for(int i = 0; i < this.pixels.length; i++) {
                this.pixels[i].active = false;
            }
        }

        // Checks whether any pixels have collided with the ground or other bricks
        // If so, then this.falling is set to false
        public boolean checkCollisions() {
            for(int i = 0; i < this.pixels.length; i++) {
                if(this.pixels[i].active) {
                    // Check if a pixel is touching the ground
                    if(this.pixels[i].getY() >= gridHeight-1) {
                        this.falling = false;
                        newBrick(random.nextInt(5), random.nextInt(gridWidth-4), 0);
                        this.deactivate();
                        return true;
                    } else {
                        // Check if any pixels are colliding with this pixel
                        for(int j = 0; j < pixelList.size(); j++) {
                            if(pixelList.get(j).getY() == this.pixels[i].getY()+1 && pixelList.get(j).getX() == this.pixels[i].getX()) {
                                if(!pixelList.get(j).ID.equals(this.pixels[i].ID)) {
                                    if(!pixelList.get(j).brickID.equals(this.pixels[i].brickID)) {
                                        this.falling = false;
                                        newBrick(random.nextInt(5), random.nextInt(gridWidth-4), 0);
                                        this.deactivate();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        // Checks if there is free space to the right of the brick
        public boolean spaceToTheRightFree() {
            for(int i = 0; i < this.pixels.length; i++) {
                //if(this.pixels[i].getX() == grid[0].length-1) {
                if(this.pixels[i].getX() == gridWidth-1) {
                    return false;
                }
                // Check if there is a pixel to the right of this one
                for(int j = 0; j < pixelList.size(); j++) {
                    // Check if the pixel is under this one
                    if(pixelList.get(j).getY() == this.pixels[i].getY() && pixelList.get(j).getX()-1 == this.pixels[i].getX()) {
                        // Check if the pixel IDs DO NOT match
                        if(!pixelList.get(j).ID.equals(this.pixels[i].ID)) {
                            // Check if the pixel brick IDs DO NOT match
                            if(!pixelList.get(j).brickID.equals(this.pixels[i].brickID)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        // Checks if there is free space to the left of the brick
        public boolean spaceToTheLeftFree() {
            for(int i = 0; i < this.pixels.length; i++) {
                if(this.pixels[i].getX() == 0) {
                    return false;
                }
                // Check if there is a pixel to the left of this one
                for(int j = 0; j < pixelList.size(); j++) {
                    // Check if the pixel is under this one
                    if(pixelList.get(j).getY() == this.pixels[i].getY() && pixelList.get(j).getX()+1 == this.pixels[i].getX()) {
                        // Check if the pixel IDs DO NOT match
                        if(!pixelList.get(j).ID.equals(this.pixels[i].ID)) {
                            // Check if the pixel brick IDs DO NOT match
                            if(!pixelList.get(j).brickID.equals(this.pixels[i].brickID)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        // Rotates the brick 90 degrees clockwise
        // Dont ask me how/why it works, I have as much a clue as you
        public void rotate() {
            if(this.falling && this.rotatable) {
                this.angle++;
                if(this.angle > 3)
                    this.angle = 0;
                int newXOffset = 0;
                int newYOffset = 0;
                if(this.angle == 1) newXOffset = 1;
                else if(this.angle == 2) newYOffset = -1;
                else if(this.angle == 3) newXOffset = -1;
                else if(this.angle == 0) newYOffset = 1;
                boolean[][] brickToGrid = new boolean[4][4];
                for(int i = 0; i < this.pixels.length; i++)
                    for(int j = 0; j < this.pixels.length; j++)
                        brickToGrid[i][j] = false;
                for(int i = 0; i < this.pixels.length; i++) {
                    brickToGrid[this.pixels[i].getY()-this.dy][this.pixels[i].getX()-this.dx] = true;
                }
                boolean[][] brickToGridTurned = new boolean[4][4];
                ArrayList<int[]> positions = new ArrayList<int[]>();
                for(int i = 0; i < brickToGrid.length; i++) {
                    for(int j = 0; j < brickToGrid[0].length; j++) {
                        brickToGridTurned[i][j] = brickToGrid[j][brickToGrid.length - 1 - i];
                        if(brickToGridTurned[i][j]) {
                            positions.add(new int[]{j, i});
                        }
                    }
                }
                for(int i = 0; i < this.pixels.length; i++) {
                    this.pixels[i].setPosition(positions.get(i)[0] + this.dx + newXOffset, positions.get(i)[1] + this.dy + newYOffset);
                }
                this.dx += newXOffset;
                this.dy += newYOffset;
            }
        }

        public void up() {
            if(this.falling) {
                for(int i = 0; i < this.pixels.length; i++) {
                    pixels[i].shiftPosition(0, -1);
                }
                this.dy--;
            }
        }

        // Makes the brick fall down by 1 pixel
        public void fall() {
            boolean brickFallen = checkCollisions();
            if(brickFallen) removeLines();
            if(this.falling) {
                for(int i = pixels.length-1; i >= 0; i--) {
                    pixels[i].shiftPosition(0, 1);
                }
                this.dy++;
            }
        }

        // Shifts the brick to the right by 1 pixel
        public void right() {
            if(this.falling && this.spaceToTheRightFree()) {
                for(int i = pixels.length-1; i >= 0; i--) {
                    pixels[i].shiftPosition(1, 0);
                }
                this.dx++;
            }
        }

        // Shifts the brick to the left by 1 pixel
        public void left() {
            if(this.falling && this.spaceToTheLeftFree()) {
                for(int i = 0; i < pixels.length; i++) {
                    pixels[i].shiftPosition(-1, 0);
                }
                this.dx--;
            }
        }
    }

    /*
    This thread class will make bricks fall while waiting a
    specific amount of time before each update
    */
    public static class BrickFallUpdate extends Thread {
        @Override
        public void run() {
            while(running) {
                for(int i = 0; i < Game.bricks.size(); i++) {
                    if(Game.bricks.get(i).falling) {
                        Game.bricks.get(i).fall();
                        // Cancel fall if game is paused
                        if(paused)
                            Game.bricks.get(i).up();
                        try {
                            sleep(fallUpdateDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
