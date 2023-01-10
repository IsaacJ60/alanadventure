import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

//TODO: GIVE REWARD EVERY X LEVELS

public class ShopPanel extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private static boolean[] keys;

    private static int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private static int tarX, tarY;
    private static int[] randomPowerups = new int[3];

    Background bg;

    Shop shop;

    private static int alpha;

    public ShopPanel(AAdventure a) {
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

        Image snow = new ImageIcon("src/tiles/snowbg.png").getImage();
        Image snowbg = new ImageIcon("src/tiles/snowbg.png").getImage().getScaledInstance(900, 700, Image.SCALE_DEFAULT);
        ImageIcon previewsnowbg = new ImageIcon("src/tiles/snowbg.png");

        Image frog = new ImageIcon("src/tiles/frogbgB.png").getImage();
        Image frogbg = new ImageIcon("src/tiles/frogbgB.png").getImage().getScaledInstance(900, 700, Image.SCALE_DEFAULT);
        ImageIcon previewfrogbg = new ImageIcon("src/tiles/frogbgB.png");

        ArrayList<ArrayList<Cosmetics>> cosmetics = new ArrayList<>();
        cosmetics.add(new ArrayList<>());
        cosmetics.get(0).add(new Cosmetics("BG", previewsnowbg, 128, 73));
        cosmetics.get(0).add(new Cosmetics("BG", previewfrogbg, 128, 73));
        shop = new Shop(cosmetics);

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
    public static void setAlpha(int a) {alpha = a;}
    public static void setRandomPowerups(int a, int b, int c) {randomPowerups = new int[]{a,b,c};}
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
        if (keys[KeyEvent.VK_ESCAPE]) {
            String last = AAdventure.getLastPanel();
            AAdventure.setCurrPanel(AAdventure.getLastPanel());
            AAdventure.setLastPanel(last);
            Intro.getAlan().setX(Background.getWallRightPos()-Background.getWallLeftPos()-Background.getWallWidth()-50);
            Intro.resetMovementKeys();
        }
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
        g.setColor(Color.BLACK);
        g.fillRect(0,0,900,700);
        bg.draw(g, new Map(new Block[100][9]), true, false, false);

        shop.draw(g);

        // UI ELEMENTS
        GameManager.getGemManager().displayGems(g,true,false, GamePanel.getAlan());
        UI.displayAll(g, GamePanel.getAlan(), GamePanel.getPowerups());

        alpha = Util.increaseOpacity(alpha, false);
        Util.overlay(g,0,0,0,alpha);
    }
}

