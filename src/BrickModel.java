public class BrickModel {
    // Hardcoded models
    public static Game.Pixel[] getModel(int x, int y, int modelID) {
        if(modelID > 5 || modelID < 0) {
            throw new IllegalArgumentException("Brick model ID=" + modelID + " is invalid.\nPossible ID values are: 0, 1, 2, 3, 4");
        }
        Game.PixelColor c;
        switch(modelID) {
        case 0: // 4x1 brick model
            c = Game.PixelColor.BLUE;
            return new Game.Pixel[] {
                new Game.Pixel(x, y, c),
                new Game.Pixel(x+1, y, c),
                new Game.Pixel(x+2, y, c),
                new Game.Pixel(x+3, y, c)
            };
        case 1: // 2x2 Square model
            c = Game.PixelColor.YELLOW;
            return new Game.Pixel[] {
                new Game.Pixel(x, y, c),
                new Game.Pixel(x+1, y, c),
                new Game.Pixel(x+1, y+1, c),
                new Game.Pixel(x, y+1, c)
            };
        case 2: // L-shape model
            c = Game.PixelColor.GREEN;
            return new Game.Pixel[] {
                new Game.Pixel(x, y, c),
                new Game.Pixel(x+1, y, c),
                new Game.Pixel(x+2, y, c),
                new Game.Pixel(x+2, y+1, c)
            };
        case 3: // T-shape model
            c = Game.PixelColor.PINK;
            return new Game.Pixel[] {
                new Game.Pixel(x, y, c),
                new Game.Pixel(x+1, y, c),
                new Game.Pixel(x+1, y+1, c),
                new Game.Pixel(x+2, y, c)
            };
        case 4: // Lightning-shaped model
            c = Game.PixelColor.RED;
            return new Game.Pixel[] {
                new Game.Pixel(x, y, c),
                new Game.Pixel(x+1, y, c),
                new Game.Pixel(x+1, y+1, c),
                new Game.Pixel(x+2, y+1, c)
            };
        }
        return null;
    }
}
