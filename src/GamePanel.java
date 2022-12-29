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
    EnemyManager enemyManager;
    Blaster mgun;
    Powerups powers;

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
        mgun = new Blaster("Machine Gun", 10,32,13);

        alan = new Alan(150, HEIGHT/2-50, mgun);

        powers = new Powerups(2);

        enemyManager = new EnemyManager();
        enemyManager.addEnemy(400,600);

        timer = new Timer(20, this); // manages frames
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

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        powers.activatePower(Powerups.GUNPOWDER);
    }
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
        mainFrame.start();
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        requestFocus();
        bg.draw(g, Util.getLevel(), alan);
        alan.draw(g, keys, MapList.getAllMaps().get(Util.getLevel()), powers);
        enemyManager.drawEnemies(g, alan);
        powers.usePowers(alan);

        alpha = Util.increaseOpacity(alpha, false);
        Util.overlay(g,0,0,0,alpha);
    }
}
