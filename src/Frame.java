import javax.swing.JFrame;
import java.awt.Toolkit;

public class Frame extends JFrame {
    // Frame constructor
	public Frame() {
		this.add(new Panel());
		this.setTitle("Tetris");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
        this.setIcon("Images/tetris_logo.png");
	}

    // Attempts to set the icon of the frame to a predefined image
    public void setIcon(String path) {
        try {
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(path)));
        } catch(Exception e) {
            System.err.println("Failed to load window icon because an error occured.\nERROR: " + e);
        }
    }
}
