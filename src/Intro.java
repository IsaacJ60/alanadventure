import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
Intro.java
Isaac Jiang
Shown when player has not yet entered the well, has access to
in game shop.
 */

public class Intro extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private static boolean[] keys;

    private static int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private int tarX, tarY, alpha = 0;

    private Alan alan;
    private Background bg;
    private Powerups powers;
    private EnemyManager enemyManager;

    private Image shopLogo;

    public Intro(AAdventure a) {
        mainFrame = a;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

        shopLogo = new ImageIcon("src/assets/shop/logo.png").getImage().getScaledInstance(48,36,Image.SCALE_DEFAULT);

        bg = new Background();

        powers = new Powerups();

        alan = new Alan(40, HEIGHT/2+50, Blaster.getBlasters().get(Blaster.MACHINEGUN), 4, 4, 0, Util.a, Util.d, Util.space);

        enemyManager = new EnemyManager();

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    // getter and setter for mouse pos, lives, and level
    public EnemyManager getEnemyManager(){return enemyManager;}
    public Alan getAlan() {return alan;}
    public void setAlan(Alan a) {alan = a;}
    public void setAlpha(int a) {alpha = a;}
    public void resetMovementKeys() {keys[Util.space] = false; keys[Util.a] = false; keys[Util.d] = false;}

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
        keys[KeyEvent.VK_ESCAPE] = false;
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
        // BLOCKS
        bg.draw(g, 0, alan, true, true, true);

        // GAMEPLAY ELEMENTS
        g.setFont(Util.fontTitle6);
        g.setColor(new Color(245,248,247));
        g.drawString("ALAN'S", 372,420-alan.getOffset()+alan.getScreenOffset());
        g.drawString("ADVENTURE", 322,475-alan.getOffset()+alan.getScreenOffset());

        g.drawImage(shopLogo, Background.getWallRightPos()-50, 635-alan.getOffset()+alan.getScreenOffset(), null);

        if (alan.draw(g, keys, GameManager.intromap, powers, enemyManager) == Util.RIGHT) {
            AAdventure.setLastPanel(AAdventure.getCurrPanel());
            AAdventure.setCurrPanel("SHOP");
        }

        // UI ELEMENTS
        GameManager.getGemManager().displayGemUI(g,true,false, alan, 65, 26);
        UI.displayAll(g, alan, powers);

        // opacity decrease
        if (alan.getY(false) > 25*Util.BLOCKLENGTH) {
            alpha = Util.increaseOpacity(alpha, true);
            Util.overlay(g,0,0,0,alpha);
        }
    }
}

