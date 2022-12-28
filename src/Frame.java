import javax.swing.JFrame;
import java.awt.Toolkit;

@SuppressWarnings("serial")
public class Frame extends JFrame {
    // Frame constructor
	public Frame() {
		this.add(new Panel());
		this.setTitle("Tris");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
        //this.setIcon(""); // TODO get a cool icon (save icon at Images folder)
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
