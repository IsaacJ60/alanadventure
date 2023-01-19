import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//TODO: GIVE REWARD EVERY X LEVELS

public class LevelClear extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private static boolean[] keys;

    private int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private int tarX, tarY;
    private int[] randomPowerups = new int[3];

    private final Background bg;

    private int alpha;

    public LevelClear(AAdventure a) {
        mainFrame = a;
        alpha = 255;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

        bg = new Background();

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    // getter and setter for mouse pos, lives, and level
    public void setAlpha(int a) {alpha = a;}
    public void setRandomPowerups(int a, int b, int c) {randomPowerups = new int[]{a,b,c};}
    public static void resetSpace() {keys[Util.space] = false;}

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {;}
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
//        GameManager.requestSettings(keys);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        keys[key] = false;
    }

    // ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        requestFocus();
        mainFrame.start();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,900,700);
        bg.draw(g, new Map(new Block[100][9]), true, false, false);

        // UI ELEMENTS
        GameManager.getGemManager().displayGemUI(g,false,true, AAdventure.getGame().getAlan());
        UI.displayAll(g, AAdventure.getGame().getAlan(), AAdventure.getGame().getPowerups());

        AAdventure.getGame().getPowerups().choosePower(g, randomPowerups, keys);

        alpha = Util.increaseOpacity(alpha, false);
        Util.overlay(g,0,0,0,alpha);
    }
}

