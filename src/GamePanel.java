import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
GamePanel.java
Isaac Jiang & Jayden Zhao
Panel that is shown when falling down the well of the game.
Draws Alan, the background, UI elements, and level text.
 */

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private static boolean[] keys;

    // width and height of panel
    private final int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private int tarX, tarY, alpha = 255;
    private boolean spaced = false, prevSpaced = false;

    // background
    private final Background bg;

    // entities
    private Alan alan;
    private final EnemyManager enemyManager;

    // powerups
    private Powerups powers;

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

        alan = new Alan(180, HEIGHT/2-50, Blaster.getBlasters().get(Blaster.MACHINEGUN), 4, 4, 0, Util.a, Util.d, Util.space);

        powers = new Powerups();

        enemyManager = new EnemyManager();

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    // getters and setters
    public boolean getPrevSpaced() {return prevSpaced;}
    public int getHEIGHT() {return HEIGHT;}
    public Alan getAlan() {return alan;}
    public void setAlan(Alan a) {alan = a;}
    public EnemyManager getEnemyManager(){return enemyManager;}
    public int getAlpha() {return alpha;}
    public void setAlpha(int a) {alpha = a;}
    public Powerups getPowerups() {return powers;}
    public void setPowerups(Powerups powerups) {powers = powerups;}
    public void resetMovementKeys() {keys[Util.space] = false; keys[Util.a] = false; keys[Util.d] = false;}

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
        // ensure no jump while holding down space, doesn't happen in intro
        if (key == Util.space) {
            prevSpaced = spaced;
            spaced = true;
        }
        GameManager.requestSettings(keys);
        keys[KeyEvent.VK_ESCAPE] = false;
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
        requestFocus();
        mainFrame.start();
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        // BLOCKS
        bg.draw(g, Util.getLevel(), alan, true, true, true);


        enemyManager.drawEnemies(g, alan, MapList.getAllMaps().get(Util.getLevel()));
        GameManager.getGemManager().drawGems(g, alan, MapList.getAllMaps().get(Util.getLevel()));
        powers.usePowers(alan, g);

        alan.draw(g, keys, MapList.getAllMaps().get(Util.getLevel()), powers, enemyManager);

        // UI ELEMENTS
        GameManager.getGemManager().displayGemUI(g,false,true, alan, 65, 26);
        UI.displayAll(g, alan, powers);

        if (alan.getY(false)/Util.BLOCKLENGTH < 30) {
            alpha = Util.increaseOpacity(alpha, false);
        }
        Util.overlay(g,0,0,0,alpha);
    }
}

