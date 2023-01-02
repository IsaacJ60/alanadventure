import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Intro extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private final boolean[] keys;

    private static int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private static int tarX, tarY, alpha = 0;

    private static Alan alan;
    Background bg;
    Powerups powers;
    private static EnemyManager enemyManager;

    public Intro(AAdventure a) {
        mainFrame = a;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

        bg = new Background();

        powers = new Powerups(2);

        alan = new Alan(20, HEIGHT/2+50, new Blaster("Machine Gun", 10,32,13));

        enemyManager = new EnemyManager();

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    // getter and setter for mouse pos, lives, and level
    public static int getTarX() {return tarX;}
    public static int getTarY() {return tarY;}
    public static int getWIDTH() {return WIDTH;}
    public static void setWIDTH(int w) {WIDTH = w;}
    public static int getHEIGHT() {return HEIGHT;}
    public static void setHEIGHT(int h) {HEIGHT = h;}
    public static EnemyManager getEnemyManager(){return enemyManager;}
    public static Alan getAlan() {return alan;}
    public static void setAlan(Alan a) {alan = a;}
    public static void setAlpha(int a) {alpha = a;}

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
    }
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
        bg.draw(g, 0, alan);
        alan.draw(g, keys, GameManager.intromap, powers, enemyManager);
        g.setFont(Util.fontTitle6);
        g.drawString("ALAN'S", 372,400-alan.getOffset()+alan.getScreenOffset());
        g.drawString("ADVENTURE", 322,455-alan.getOffset()+alan.getScreenOffset());
        GameManager.getGemManager().displayGems(g,true,false);

        // opacity decrease
        if (alan.getY(false) > 25*Util.BLOCKLENGTH) {
            alpha = Util.increaseOpacity(alpha, true);
            Util.overlay(g,0,0,0,alpha);
        }
    }
}


