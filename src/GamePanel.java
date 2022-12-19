import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    private final boolean[] keys;

    private static int WIDTH = 900, HEIGHT = 700;
    private static int tarX, tarY;
    private static int level = 0;

    Background bg;
    Alan alan;
    EnemyManager enemyManager;
    Enemy enemy;
    Blaster mgun;
    ArrayList<Image> mgunAnim;

    public GamePanel() {
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

        bg = new Background();


        //TODO: create bullet manager class
        mgunAnim = new ArrayList<>();
        mgunAnim.add(new ImageIcon("src/tiles/middle.png").getImage());
        mgun = new Blaster("Machine Gun", 10,32,10, mgunAnim);

        alan = new Alan(150, HEIGHT/2-50, 5, 10, 5, mgun);

        enemyManager = new EnemyManager();
        enemyManager.addEnemy(350,600);
        enemyManager.addEnemy(450,600);

        timer = new Timer(20, this); // manages frames
        timer.start();
    }

    // getter and setter for mouse pos, lives, and level
    public static int getTarX() {return tarX;}
    public static int getTarY() {return tarY;}
    public static int getWIDTH() {return WIDTH;}
    public static void setWIDTH(int w) {WIDTH = w;}
    public static int getHEIGHT() {return HEIGHT;}
    public static void setHEIGHT(int h) {HEIGHT = h;}
    public static int getLevel() {return level;}
    public static void setLevel(int l) {level = l;}

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {;}
    @Override
    public void mouseEntered(MouseEvent e) {
        ;
    }
    @Override
    public void mouseExited(MouseEvent e) {
        ;
    }
    @Override
    public void mousePressed(MouseEvent e) {;}
    @Override
    public void mouseReleased(MouseEvent e) {
        ;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        keys[key] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        keys[key] = false;
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
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        bg.draw(g);
        alan.draw(g, keys);
        enemyManager.drawEnemies(g, alan);
    }
}

