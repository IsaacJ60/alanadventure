import javax.management.monitor.GaugeMonitor;
import javax.swing.*;
import java.awt.*;

public class AAdventure extends JFrame { // frame
    CardLayout card;

    private GamePanel game;
    private Intro intro;
    private LevelClear levelClear;

    private static final int WIDTH = 900, HEIGHT = 700;
    public static int getGameWidth() {return WIDTH;}
    public static int getGameHeight() {return HEIGHT;}

    private static String currPanel = "INTRO";
    public static String getCurrPanel() {return currPanel;}
    public static void setCurrPanel(String curr) {currPanel = curr;}

    public AAdventure() {
        super("Alan's Adventure");

        GameManager.loadLevels();
        Util.loadFonts();

        card = new CardLayout();
        setLayout(card);

        game = new GamePanel(this);
        add("GAME", game);

        intro = new Intro(this);
        add("INTRO", intro);

        levelClear = new LevelClear(this);
        add("LEVELCLEAR", levelClear);

        pack();

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // set X to exit
        setVisible(true); // make panel visible
    }

    public void start(){
        card.show(getContentPane(), currPanel);  // I don't know why we need to tell the LayoutManager what it's parent
        // Container is, but when we add to a JFrame, we are adding to the ContentPane.
    }

    public static void main(String[] args) {
        new AAdventure();
    }
}
