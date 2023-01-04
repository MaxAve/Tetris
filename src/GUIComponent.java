import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

interface Action {
    void perfmorm();
}

public class GUIComponent {
    // This ArrayList holds on to MessageBox instances until they need to be destroyed
    private static ArrayList<MessageBox> messageBoxes = new ArrayList<>();
    private static Random random = new Random();

    public static void closeAllmessageBoxes() {
        for(int i = 0; i < messageBoxes.size(); i++) {
            messageBoxes.get(i).hide();
        }
    }

    public static class MessageBox {
        private boolean hidden = false;
        public final String ID;
        public int x;
        public int y;
        public int width;
        public int height;
        public String[] messages;
        public String title;
        private static final int xSize = 15;
        private static final int xMargin = 12;

        public MessageBox(int w, int h, String[] messages, String title) {
            this.width = w;
            this.height = h;
            this.messages = messages;
            this.title = title;
            this.ID = generateID();
            messageBoxes.add(this);
        }

        private static String generateID() {
            // TODO may cause bugs, add code to verify ID
            String availableChars = "abcdefghijklmnopqrstuvwxyz0123456789-";
            int idLength = random.nextInt(10) + 10;
            String newID = "";
            for(int i = 0; i < idLength; i++) {
                newID += availableChars.charAt(random.nextInt(availableChars.length()));
            }
            return newID;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Puts the MessageBox at the center of the screen
        public void setPositionCenter() {
            this.x = Panel.SCREEN_WIDTH/2 - width/2;
            this.y = Panel.SCREEN_HEIGHT/2 - height/2;
        }

        // Renders the MessageBox to the selected Graphics
        public void draw(Graphics g) {
            if(!this.hidden) {
                // Box body
                g.setColor(Color.BLACK);
                g.fillRect(this.x, this.y, this.width, this.height);

                // Box outline
                g.setColor(Color.WHITE);
                g.drawRect(this.x, this.y, this.width, this.height);

                // X
                g.drawLine(this.x + this.width - xMargin, this.y + xMargin, this.x + this.width - xMargin - xSize, this.y + xMargin + xSize);
                g.drawLine(this.x + this.width - xMargin - xSize, this.y + xMargin, this.x + this.width - xMargin, this.y + xMargin + xSize);
                
                // Text
                for(int i = 0; i < this.messages.length; i++) {
                    g.setFont(new Font("Arial", Font.PLAIN, 17));
                    g.drawString(this.messages[i], this.x + 20, this.y + 80 + i*20);
                }

                // Title
                g.setFont(new Font("Arial", Font.PLAIN, 17));
                g.drawString(this.title, this.x + 10, this.y + 20);
            }
        }

        // Get X position of the X
        private int getCloseButtonX() {
            return this.x + this.width - xMargin;
        }

        // Get Y position of the X
        private int getCloseButtonY() {
            return this.y + xMargin;
        }

        // Executes a function (parameter) when the X is clicked, and then hides the box
        public void onClickX(Action a) {
            if(Frame.mouseClickX > getCloseButtonX() && Frame.mouseClickX < getCloseButtonX() + xSize && Frame.mouseClickY > getCloseButtonY() && Frame.mouseClickY < (this.y + this.height/2)) {
                a.perfmorm();
                this.hide();
                Frame.resetMouseClick();
            }
        }

        public boolean isHidden() {
            return this.hidden;
        }

        // Destroys the MessageBox
        public void hide() {
            this.hidden = true;
        }

        public void show() {
            this.hidden = false;
        }
    }
}
