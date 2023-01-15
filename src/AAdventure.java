import javax.swing.*;
import java.awt.*;

public class AAdventure extends JFrame { // frame
    CardLayout card;

    private static GamePanel game;
    private static Intro intro;
    private static LevelClear levelClear;
    private static SettingsPanel settings;
    private static GameOver gameOver;
    private static ShopPanel shop;

    Sound sound;

    GameMusic introMusic;
    GameMusic gameMusic;

    private static final int WIDTH = 900, HEIGHT = 700;
    public static int getGameWidth() {return WIDTH;}
    public static int getGameHeight() {return HEIGHT;}

    private static String currPanel = "INTRO";
    public static String getCurrPanel() {return currPanel;}
    public static void setCurrPanel(String curr) {currPanel = curr;}
    private static String lastPanel = "INTRO";
    public static String getLastPanel() {return lastPanel;}
    public static void setLastPanel(String curr) {lastPanel = curr;}
    private static String switchPanel = "";
    public static String getSwitchPanel() {return switchPanel;}
    public static void setSwitchPanel(String curr) {switchPanel = curr;}

    public static GamePanel getGame() {return game;}
    public static Intro getIntro() {return intro;}
    public static LevelClear getLevelClear() {return levelClear;}
    public static SettingsPanel getSettings() {return settings;}
    public static GameOver getGameOver() {return gameOver;}
    public static ShopPanel getShop() {return shop;}

    public AAdventure() {
        super("Alan's Adventure");

        GameManager.loadLevels();
        GameManager.loadGems();
        Util.loadFonts();

        sound = new Sound();

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

        shop = new ShopPanel(this);
        add("SHOP", shop);

        pack();

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // set X to exit
        setVisible(true); // make panel visible
    }

    public void start(){
        //TODO: request focus only when currpanel changes to avoid constantly requesting
        if (!switchPanel.equals(currPanel)) {
            switch (currPanel) {
                case "INTRO" -> introMusic = new GameMusic("src/assets/sounds/bg/intro.mid");
                case "GAME" -> gameMusic = new GameMusic("src/assets/sounds/bg/game.mid");
                case "SETTINGS" -> {
                    if (gameMusic != null) {
                        gameMusic.endMidi();
                    }
                    if (introMusic != null) {
                        introMusic.endMidi();
                    }
                }
            }
            switchPanel = currPanel;
        }
        card.show(getContentPane(), currPanel);
    }

    public static void main(String[] args) {
        new AAdventure();
    }
}


