import java.util.ArrayList;
import java.lang.Thread; 
import java.util.Random;

public class Game {
    public static PixelColor[][] grid; // The game is represented by a grid of a certain size
    public static enum PixelColor {NONE, PINK, BLUE, YELLOW, GREEN, RED} // Pixel color enum
    public static ArrayList<Brick> bricks = new ArrayList<>();
    public static ArrayList<Pixel> pixelList = new ArrayList<>();
    public static boolean running = true;
    public static final long fastFallUpdateDelay = 20;
    public static long fallUpdateDelay = 500;
    private static Random random = new Random();
    public static boolean fastMode = false; // Fast mode

    // Main
    public static void main(String[] args) {
        initGrid(16, 30);
        newBrick(random.nextInt(5), 7, 1);

        BrickFallUpdate t = new BrickFallUpdate();
        t.start();

        new Frame(); // Initialize new JFrame
    }

    // Initializes the grid
    public static void initGrid(int width, int height) {
        grid = new PixelColor[height][width];
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                grid[i][j] = PixelColor.NONE;
            }
        }
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
        Brick b = new Brick(brickStructure, brickID);
        b.dx = x;
        b.dy = y;
        return b;
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
            grid[y][x] = this.pixelColor;
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
            // The grid is only a graphical representation. Updating Pixel objects will not affect the grid
            // Therefore, we need to manually clear out or draw new pixels on the grid
            // This part clears out the pixel at the old position
            grid[this.y][this.x] = PixelColor.NONE;

            this.x = newX;
            this.y = newY;

            grid[this.y][this.x] = this.pixelColor; // Same story as above, but draws a new pixel at the new position
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
        public int angle;
        public int dx, dy;
        public int modelID;

        // Constructor
        public Brick(Pixel[] pixels, int ID) {
            this.pixels = pixels;
            this.falling = true;
            this.modelID = ID;
            bricks.add(this);
        }

        public void deactivate() {
            for(int i = 0; i < this.pixels.length; i++) {
                this.pixels[i].active = false;
            }
        }

        // Checks whether any pixels have collided with the ground or other bricks
        // If so, then this.falling is set to false
        public void checkCollisions() {
            for(int i = 0; i < this.pixels.length; i++) {
                if(this.pixels[i].active) {
                    // Check if a pixel is touching the ground
                    if(this.pixels[i].getY() >= grid.length-1) {
                        this.falling = false;
                        newBrick(random.nextInt(5), 7, 1);
                        this.deactivate();
                    } else {
                        // Check if any pixels are colliding with this pixel
                        // Because of the horrible implementation, we need to check the coordinates AND 2 IDs
                        for(int j = 0; j < pixelList.size(); j++) {
                            // Check if the pixel is under this one
                            if(pixelList.get(j).getY() == this.pixels[i].getY()+1 && pixelList.get(j).getX() == this.pixels[i].getX()) {
                                // Check if the pixel IDs DO NOT match
                                if(!pixelList.get(j).ID.equals(this.pixels[i].ID)) {
                                    // Check if the pixel brick IDs DO NOT match
                                    if(!pixelList.get(j).brickID.equals(this.pixels[i].brickID)) {
                                        this.falling = false;
                                        newBrick(random.nextInt(5), 7, 1);
                                        this.deactivate();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Checks if there is free space to the right of the brick
        public boolean spaceToTheRightFree() {
            for(int i = 0; i < this.pixels.length; i++) {
                if(this.pixels[i].getX() == grid[0].length-1) {
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

        // Rotates the brick 90 degrees to the right
        // Dont ask me how/why it works, I have as much a clue as you
        public void rotate() {
            if(this.falling) {
                boolean[][] brickToGrid = new boolean[4][4];
                for(int i = 0; i < this.pixels.length; i++)
                    for(int j = 0; j < this.pixels.length; j++)
                        brickToGrid[i][j] = false;
                for(int i = 0; i < this.pixels.length; i++) {
                    brickToGrid[this.pixels[i].getY()-this.dy][this.pixels[i].getX()-this.dx] = true;
                }
                boolean[][] brickToGridTurned = new boolean[4][4];
                ArrayList<int[]> positions = new ArrayList<int[]>(); // x, y
                for(int i = 0; i < brickToGrid.length; i++) {
                    for(int j = 0; j < brickToGrid[0].length; j++) {
                        brickToGridTurned[i][j] = brickToGrid[j][brickToGrid.length - 1 - i];
                        if(brickToGridTurned[i][j]) {
                            positions.add(new int[]{j, i});
                        }
                    }
                }
                for(int i = 0; i < this.pixels.length; i++) {
                    this.pixels[i].setPosition(positions.get(i)[0] + this.dx, positions.get(i)[1] + this.dy);
                }
            }
        }

        // Makes the brick fall down by 1 pixel
        public void fall() {
            checkCollisions();
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
                        try {
                            sleep(!fastMode ? fallUpdateDelay : fastFallUpdateDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
