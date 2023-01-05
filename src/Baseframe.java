/* ----------------------------------------------------------------------------------
 * BaseFrame.java
 * Mr. McKenzie
 * Nov. 13 2009
 * --------------
 * Revised Dec 14 2022
 * -----------------------------------------------------------------------------------
 * BaseFrame is designed to take a lot of the complexity out of doing graphics in Java.
 * The idea is, that rather inhetiting from JFrame directly we place a lot of common 
 * code in BaseFrame then inherit from BaseFrame. BaseFrame includes:
 * 
 *  - A Timer, with a 20 ms delay that calls move() and repaint()
 * 	- MouseMotionListener, MouseListener, KeyListener
 *	mx,my,mb,keys- these fields are protected so you have direct access to them from the
 *				   inherited class.
 *  - move() - this is a hook that you can use to update the state of your game
 *  - draw() - repaint() calls paint(), this calls the JPanel paint(), which calls draw()
 *			 - The JPanel is used because it automaticly double-buffered. If we draw 
 *			   directly to the Frame it will flicker. We call the draw method so that
 *			   when we extend the BaseFrame we can override this method to handle 
 *  	   	   our drawing.
 -------------------------------------------------------------------------------------*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class BaseFrame extends JFrame implements MouseMotionListener, MouseListener, KeyListener, ActionListener{
    protected int mx,my,mb;
    protected boolean []keys;

    protected Image back,dbImage;
    protected Graphics dbg;
    protected String col="";
    protected GamePane pane;
    protected Timer timer;

    final protected int LEFT = 37;
    final protected int UP = 38;
    final protected int RIGHT = 39;
    final protected int DOWN = 40;
    final protected int SPACE = 32;
    final protected int ESC = 27;

    public BaseFrame(String t, int w, int h) {
        super(t);
        pane = new GamePane(this);
        pane.setPreferredSize(new Dimension(w, h));

        addKeyListener(this);
        addMouseListener (this);
        addMouseMotionListener(this);

        keys = new boolean[2000];
        add(pane);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        timer = new Timer(20, this);
        timer.start();
    }

    public BaseFrame() {
        ;
    }

    private void updateMouse(MouseEvent e){
        mx = e.getX();
        my = e.getY();
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        updateMouse(e);
        mb = 0;
    }
    public void mouseClicked(MouseEvent e){
        updateMouse(e);
        mb = 0;
    }
    public void mouseDragged(MouseEvent e){
        updateMouse(e);
    }
    public void mouseMoved(MouseEvent e){
        updateMouse(e);
    }
    public void mousePressed(MouseEvent e){
        updateMouse(e);
        mb = e.getButton();
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        System.out.println(e);
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    // should be overloaded
    public void move(){

    }

    public void draw(Graphics g){

    }


    @Override
    public void actionPerformed(ActionEvent e){
        move(); 	// never draw in move
        repaint(); 	// only draw
    }

    class GamePane extends JPanel{
        BaseFrame main;

        public GamePane(BaseFrame m){
            main = m;
        }
        public void paint(Graphics g){
            main.draw(g);
        }
    }
}