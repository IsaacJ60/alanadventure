import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private final boolean[] keys;

    private static int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private static int tarX, tarY, alpha = 255;
    private static boolean spaced = false, prevSpaced = false;

    Background bg;

    private static Alan alan;
    private static EnemyManager enemyManager;
    private static Blaster mgun;
    private static Powerups powers;

    public GamePanel(AAdventure a) {
        mainFrame = a;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

        bg = new Background(); // background exclusive to game class

        //TODO: create bullet manager class
        mgun = new Blaster("Machine Gun", 10,8,13);

        alan = new Alan(150, HEIGHT/2-50, mgun, 4, 4, 0);

        powers = new Powerups();

        enemyManager = new EnemyManager();

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    // getter and setter for mouse pos, lives, and level
    public static boolean getSpaced() {return spaced;}
    public static boolean getPrevSpaced() {return prevSpaced;}
    public static int getTarX() {return tarX;}
    public static int getTarY() {return tarY;}
    public static int getWIDTH() {return WIDTH;}
    public static void setWIDTH(int w) {WIDTH = w;}
    public static int getHEIGHT() {return HEIGHT;}
    public static void setHEIGHT(int h) {HEIGHT = h;}
    public static Alan getAlan() {return alan;}
    public static void setAlan(Alan a) {alan = a;}
    public static void setAlanCoords(int x, int y) {alan.setX(x); alan.setY(y);}
    public static EnemyManager getEnemyManager(){return enemyManager;}
    public static void setAlpha(int a) {alpha = a;}
    public static Powerups getPowerups() {return powers;}

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {;}
    @Override
    public void mouseExited(MouseEvent e) {;}
    @Override
    public void mousePressed(MouseEvent e) {;}
    @Override
    public void mouseReleased(MouseEvent e) {;}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        keys[key] = true;
        if (key == Util.space) {
            prevSpaced = spaced;
            spaced = true;
        }
        GameManager.requestSettings(keys);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        keys[key] = false;
        if (key == Util.space) {
            prevSpaced = spaced;
            spaced = false;
        }
    }

    // ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
//        Point mouse = MouseInfo.getPointerInfo().getLocation(); // loc of mouse on screen
//        Point offset = getLocationOnScreen(); // loc of panel
//        // getting mouse pos
//        tarX = mouse.x - offset.x;
//        tarY = mouse.y - offset.y;
        // move ball & paddle
        requestFocus();
        mainFrame.start();
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        // BLOCKS
        bg.draw(g, Util.getLevel(), alan, true, true, true);

        // GAMEPLAY ELEMENTS
        enemyManager.drawEnemies(g, MapList.getBlocksWithoutWallImages());
        GameManager.getGemManager().drawGems(g, alan, MapList.getAllMaps().get(Util.getLevel()));
        powers.usePowers(alan, g);
        alan.draw(g, keys, MapList.getAllMaps().get(Util.getLevel()), powers, enemyManager);

        // UI ELEMENTS
        GameManager.getGemManager().displayGems(g,false,true, alan);
        UI.displayAll(g, alan, powers);

        alpha = Util.increaseOpacity(alpha, false);
        Util.overlay(g,0,0,0,alpha);
    }
}
