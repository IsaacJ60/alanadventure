import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
GameWin.java
Isaac Jiang
shows screenw hen win game
 */

public class GameWin extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private static boolean[] keys;

    private int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private int tarX, tarY;
    private int[] randomPowerups = new int[3];

    private Background bg;

    private int alpha;

    public GameWin(AAdventure a) {
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
    public void mouseClicked(MouseEvent e) {
        requestFocus();
        GameManager.toLevel(0,true);
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
        g.setColor(Color.WHITE);
        g.setFont(Util.fontText);
        g.drawString("GAME BEAT", 335, 100);

        g.setFont(Util.fontTextSmall);
        g.drawString("MAX COMBO: " + AAdventure.getGame().getAlan().getMaxCombo(), 357, 195);

        g.drawString("GEMS GAINED:", 357, 260);
        GameManager.getGemManager().displayGemUI(g,false,true, AAdventure.getGame().getAlan(), 398, 270);
        g.drawString("TOTAL GEMS:", 362, 346);
        GameManager.getGemManager().displayGemUI(g,true,false, AAdventure.getGame().getAlan(), 398, 365);

        g.setFont(Util.fontTextSmaller);
        g.drawString("CLICK ANYWHERE TO RESTART", 328, 500);

        alpha = Util.increaseOpacity(alpha, false);
        Util.overlay(g,0,0,0,alpha);
    }
}
