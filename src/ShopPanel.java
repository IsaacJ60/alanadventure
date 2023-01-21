import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
ShopPanel.java
Isaac Jiang
Draws shop
 */

public class ShopPanel extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private static boolean[] keys;

    private int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private static int[] randomPowerups = new int[3];

    private Background bg;
    private Shop shop;

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

        Image clouds = new ImageIcon("src/tiles/bgB.png").getImage();
        Image cloudsbg = new ImageIcon("src/tiles/bgB.png").getImage().getScaledInstance(900, 700, Image.SCALE_DEFAULT);
        ImageIcon previewcloudsbg = new ImageIcon("src/tiles/bgB.png");

        Image snow = new ImageIcon("src/tiles/snowbg.png").getImage();
        Image snowbg = new ImageIcon("src/tiles/snowbg.png").getImage().getScaledInstance(900, 700, Image.SCALE_DEFAULT);
        ImageIcon previewsnowbg = new ImageIcon("src/tiles/snowbg.png");

        Image frog = new ImageIcon("src/tiles/frogbgB.png").getImage();
        Image frogbg = new ImageIcon("src/tiles/frogbgB.png").getImage().getScaledInstance(900, 700, Image.SCALE_DEFAULT);
        ImageIcon previewfrogbg = new ImageIcon("src/tiles/frogbgB.png");

        ArrayList<ArrayList<Cosmetics>> cosmetics = new ArrayList<>();
        cosmetics.add(new ArrayList<>());
        cosmetics.get(0).add(new Cosmetics("BACKGROUNDS", "CLOUDS", previewcloudsbg, cloudsbg, 128, 73, -1));
        cosmetics.get(0).add(new Cosmetics("BACKGROUNDS", "WELL", previewsnowbg, snowbg, 128, 73, 100));
        cosmetics.get(0).add(new Cosmetics("BACKGROUNDS", "FROGS", previewfrogbg, frogbg, 128, 73, 200));

        ImageIcon previewmachine = new ImageIcon("src/assets/alan/shoot/bullets/bulletMachinePreview.png");
        ImageIcon previewshotgun = new ImageIcon("src/assets/alan/shoot/bullets/bulletShotgunPreview.png");
        ImageIcon previewrifle = new ImageIcon("src/assets/alan/shoot/bullets/bulletRiflePreview.png");

        cosmetics.add(new ArrayList<>());
        cosmetics.get(1).add(new Cosmetics("BLASTERS", "MACHINE GUN", previewmachine, Blaster.getBlasters().get(Blaster.MACHINEGUN), 110, 73, -1));
        cosmetics.get(1).add(new Cosmetics("BLASTERS", "SHOTGUN", previewshotgun, Blaster.getBlasters().get(Blaster.SHOTGUN), 110, 73, 200));
        cosmetics.get(1).add(new Cosmetics("BLASTERS", "RIFLE", previewrifle, Blaster.getBlasters().get(Blaster.RIFLEGUN), 110, 73, 200));

        shop = new Shop(cosmetics);

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    // getter and setter for mouse pos, lives, and level
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
            AAdventure.getIntro().getAlan().setX(Background.getWallRightPos()-Background.getWallLeftPos()-50);
            AAdventure.getIntro().resetMovementKeys();
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

        shop.draw(g, keys, GameManager.getGemManager());

        // UI ELEMENTS
        GameManager.getGemManager().displayGemUI(g,true,false, AAdventure.getGame().getAlan(), 65, 26);
        UI.displayAll(g, AAdventure.getGame().getAlan(), AAdventure.getGame().getPowerups());

        alpha = Util.increaseOpacity(alpha, false);
        Util.overlay(g,0,0,0,alpha);
    }
}

