import javax.swing.*;

public class AAdventure extends JFrame { // frame
    GamePanel game;
    public AAdventure() {
        super("Alan's Adventure");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // set X to exit
        game = new GamePanel();
        add(game);
        pack();
        setResizable(false);
        setVisible(true); // make panel visible
    }

    public static void main(String[] args) {
        new AAdventure();
    }
}

