import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//TODO: GIVE REWARD EVERY X LEVELS

public class LevelClear extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private final boolean[] keys;

    private static int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private static int tarX, tarY;

    public LevelClear(AAdventure a) {
        mainFrame = a;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

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

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        requestFocus();
        AAdventure.setCurrPanel("GAME");
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
    public void keyTyped(KeyEvent e) {;}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        keys[key] = true;
        GameManager.requestSettings(keys);
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
        requestFocus();
        mainFrame.start();
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,900,700);
    }
}
