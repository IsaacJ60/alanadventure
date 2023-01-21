import javax.swing.*;
import java.awt.*;

/*
AAdventure.java
ICS4U-01 FSE
Isaac Jiang & Jayden Zhao
Executable java file that runs the final FSE game.

3 LEVELS BUT COULD POTENTIALLY BE INFINITE
In Alan's Adventure, you control a player that goes down a well. Alan has guns on his shoes, so he can shoot downwards
at nearby enemies. This also allows him to slow his descent. Alan can also purchase items from the in game shop,
and change keybinds and get help in the settings menu.
 */

public class AAdventure extends JFrame { // frame
    CardLayout card;

    // all screens
    private static GamePanel game;
    private static Intro intro;
    private static LevelClear levelClear;
    private static SettingsPanel settings;
    private static GameOver gameOver;
    private static ShopPanel shop;
    private static GameWin gamewin;

    // sound
    private Sound sound;
    private GameMusic introMusic;
    private GameMusic gameMusic;
    private static String currMusic;

    // game dimensions
    private static final int WIDTH = 900, HEIGHT = 700;
    public static int getGameWidth() {return WIDTH;}
    public static int getGameHeight() {return HEIGHT;}

    // panel management
    private static String currPanel = "INTRO";
    public static String getCurrPanel() {return currPanel;}
    public static void setCurrPanel(String curr) {currPanel = curr;}
    private static String lastPanel = "INTRO";
    public static String getLastPanel() {return lastPanel;}
    public static void setLastPanel(String curr) {lastPanel = curr;}
    private static String switchPanel = "";
    public static String getSwitchPanel() {return switchPanel;}
    public static void setSwitchPanel(String curr) {switchPanel = curr;}

    // getting panels
    public static GamePanel getGame() {return game;}
    public static Intro getIntro() {return intro;}
    public static LevelClear getLevelClear() {return levelClear;}
    public static SettingsPanel getSettingsPanel() {return settings;}
    public static GameOver getGameOver() {return gameOver;}
    public static ShopPanel getShop() {return shop;}
    public static String getCurrMusic() {return currMusic;}
    public static void setCurrMusic(String s) {currMusic = s;}

    public AAdventure() {
        super("Alan's Adventure");

        // loading game contents
        GameManager.loadLevels();
        GameManager.loadGems();
        GameManager.loadBlasters();
        Util.loadFonts();
        currMusic = "Alan's Adventure";

        sound = new Sound();

        // adding panels to card layout
        card = new CardLayout();
        setLayout(card);

        game = new GamePanel(this);
        add("GAME", game);

        intro = new Intro(this);
        add("INTRO", intro);

        levelClear = new LevelClear(this);
        add("LEVELCLEAR", levelClear);

        settings = new SettingsPanel(this);
        add("SETTINGS", settings);

        gameOver = new GameOver(this);
        add("GAMEOVER", gameOver);

        gamewin = new GameWin(this);
        add("GAMEWIN", gamewin);

        shop = new ShopPanel(this);
        add("SHOP", shop);

        pack();

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // set X to exit
        setVisible(true); // make panel visible
    }

    public void start() {
        // playing music
        if (!switchPanel.equals(currPanel)) {
            switch (currPanel) {
                case "INTRO" -> {
                    if (!switchPanel.equals("SETTINGS") && !switchPanel.equals("SHOP")) {
                        introMusic = new GameMusic("src/assets/sounds/bg/intro.mid");
                    }
                }
                case "GAME" -> {
                    if (switchPanel.equals("GAMEOVER") || switchPanel.equals("INTRO")) {
                        gameMusic = new GameMusic("src/assets/sounds/bg/" + currMusic + ".mid");
                    }
                }
            }
            switchPanel = currPanel;
        }
        // display current panel
        card.show(getContentPane(), currPanel);
    }

    public static void main(String[] args) {
        new AAdventure();
    }
}


