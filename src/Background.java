import javax.swing.*;
import java.awt.*;

//TODO: UI ELEMENTS
// PRIORITY - LOW
// - health, powerups, etc.

public class Background {
    private static int wallWidth, wallLeftPos, wallRightPos;
    private final Image bg;

    // manages basic background elements and ui as well as side walls
    public Background() {
        bg = new ImageIcon("src/tiles/bg.png").getImage().getScaledInstance(GamePanel.getWIDTH(), GamePanel.getHEIGHT(), Image.SCALE_DEFAULT);
        wallLeftPos = GamePanel.getWIDTH()/2-GamePanel.getWIDTH()/5+1;
        wallRightPos = GamePanel.getWIDTH()/2+GamePanel.getWIDTH()/5-22;
        wallWidth = 22;
    }

    public static int getWallWidth() {return wallWidth;}
    public static int getWallLeftPos() {return wallLeftPos;}
    public static void setWallLeftPos(int w) {wallLeftPos = w;}
    public static int getWallRightPos() {return wallRightPos;}
    public static void setWallRightPos(int w) {wallRightPos = w;}

    // main driver code for drawing map and its blocks
    public void draw(Graphics g, Alan alan) {
        g.setColor(Color.BLACK);
        setBackground(g, g.getColor());
        g.drawImage(bg,0,alan.getScreenOffset()/3,null);
        g.fillRect(getWallLeftPos(), 0, getWallRightPos()-getWallLeftPos(), GamePanel.getHEIGHT());
        GameManager.getMaplist().drawBlocks(g, Util.getLevel(), alan);
    }

    public void setBackground(Graphics g, Color col) {
        g.setColor(col);
        g.fillRect(0,0,GamePanel.getWIDTH(),GamePanel.getHEIGHT());
    }
}

class Tile {
    private String name;
    private Image img, rimg;
    private int width, height;

    Tile(String name, Image img, Image rimg) {
        this.name = name;
        this.img = img;
        this.rimg = rimg;
        this.width = img.getWidth(null);
        this.height = img.getHeight(null);
    }

    Tile(String name, Image img) {
        this.name = name;
        this.img = img;
        this.rimg = img;
        this.width = img.getWidth(null);
        this.height = img.getHeight(null);
    }

    Tile(String name, String file) {
        this.name = name;
        this.img = new ImageIcon(file).getImage().getScaledInstance(35,35,Image.SCALE_DEFAULT);
        this.rimg = img;
        this.width = img.getWidth(null);
        this.height = img.getHeight(null);
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public Image getImg() {return img;}
    public void setImg(Image img) {this.img = img;}
    public Image getRimg() {return rimg;}
    public void setRimg(Image rimg) {this.rimg = rimg;}
    public int getWidth() {return width;}
    public void setWidth(int width) {this.width = width;}
    public int getHeight() {return height;}
    public void setHeight(int height) {this.height = height;}
}

