import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

//TODO: DISPLAY LEVEL STATS

public class SettingsPanel extends JPanel implements KeyListener, ActionListener, MouseListener {
    Timer timer;

    AAdventure mainFrame;

    private final boolean[] keys;

    private static boolean clicked;

    private Settings settings;

    private int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();
    private int alpha = 0, tarX = 0, tarY = 0;

    public SettingsPanel(AAdventure a) {
        mainFrame = a;
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        // adding listener for events
        addMouseListener(this);
        addKeyListener(this);

        ArrayList<ArrayList<Property>> properties = new ArrayList<>();
        properties.add(new ArrayList<>());
        properties.get(0).add(new Property("MOVE LEFT", "A", "KEYBINDS"));
        properties.get(0).add(new Property("MOVE RIGHT", "D", "KEYBINDS"));
        properties.get(0).add(new Property("JUMP", "SPACE", "KEYBINDS"));

        settings = new Settings(properties);

        timer = new Timer(25, this); // manages frames
        timer.start();
    }

    public Settings getSettings() {
        return settings;
    }

    public static boolean isClicked() {
        return clicked;
    }

    public static void setClicked(boolean c) {
        clicked = c;
    }

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
    public void mouseReleased(MouseEvent e) {
        clicked = true;
    }
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

        if (AAdventure.getCurrPanel().equals("SETTINGS")) {
            Point mouse = MouseInfo.getPointerInfo().getLocation(); // loc of mouse on screen
            Point offset = new Point(0,0);
            try {
                offset = getLocationOnScreen(); // loc of panel
            } catch (IllegalComponentStateException ex) {
                System.out.println(ex + " hmm");
            }
            tarX = mouse.x - offset.x;
            tarY = mouse.y - offset.y;
        }

        mainFrame.start();
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        alpha = Util.increaseOpacity(alpha, true);
        Util.overlay(g,0,0,0,alpha);
        //TODO: key binding & control help display
        settings.draw(g, keys, clicked, AAdventure.getGame().getAlan(), AAdventure.getIntro().getAlan(), tarX, tarY);
    }
}

