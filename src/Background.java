import javax.swing.*;
import java.awt.*;

/*
Background.java
Isaac Jiang & Jayden Zhao

Contains methods that display background of a screen, and draws all screen content
that is found inside of map

Tile.java
Isaac Jiang & Jayden Zhao

Provides methods that allow easy access to sprites for blocks
 */

public class Background {
    private static int wallWidth, wallLeftPos, wallRightPos;
    private static Image bg;

    // manages basic background elements and ui as well as side walls
    public Background() {
        bg = new ImageIcon("src/tiles/bgB.png").getImage().getScaledInstance(900, 700, Image.SCALE_DEFAULT);
        wallLeftPos = 257;
        wallRightPos = 642-Util.BLOCKLENGTH;
        wallWidth = Util.BLOCKLENGTH;
    }

    public static int getWallWidth() {return wallWidth;}
    public static int getWallLeftPos() {return wallLeftPos;}
    public static int getWallRightPos() {return wallRightPos;}
    public static void setBg(Image b) {bg = b;}

    // main driver code for drawing map and its blocks
    public void draw(Graphics g, int level, Alan alan, boolean includeWalls, boolean includeBlocks, boolean includeBG) {
        g.setColor(Color.BLACK);
        setBackground(g, g.getColor());
        if (includeBG) {
            g.drawImage(bg,0,0,null);
        }
        g.fillRect(getWallLeftPos()+Util.BLOCKLENGTH, 0, getWallRightPos()-getWallLeftPos()-Util.BLOCKLENGTH, AAdventure.getGame().getHeight());
        GameManager.getMaplist().drawBlocks(g, MapList.getAllMaps().get(level), alan, includeWalls, includeBlocks);
    }

    public void draw(Graphics g, Map map, boolean includeWalls, boolean includeBlocks, boolean includeBG) {
        g.setColor(Color.BLACK);
        setBackground(g, g.getColor());
        if (includeBG) {
            g.drawImage(bg,0,0,null);
        }
        g.fillRect(getWallLeftPos(), 0, getWallRightPos()-getWallLeftPos(), AAdventure.getGame().getHeight());
        GameManager.getMaplist().drawBlocks(g, map, includeWalls, includeBlocks);
    }

    public void setBackground(Graphics g, Color col) {
        g.setColor(col);
        g.fillRect(0,0,AAdventure.getGame().getWidth(),AAdventure.getGame().getHeight());
    }
}

class Tile {
    private String name;
    private Image img, rimg;
    private int width, height;

    Tile(String name, ImageIcon img) {
        this.name = name;
        this.img = img.getImage();
        this.rimg = img.getImage();
        this.width = img.getIconWidth();
        this.height = img.getIconHeight();
    }

    Tile(String name, String file) {
        this.name = name;
        ImageIcon tmp = new ImageIcon(file);
        this.img = new ImageIcon(file).getImage().getScaledInstance(35,35,Image.SCALE_DEFAULT);
        this.rimg = img;
        this.width = tmp.getIconWidth();
        this.height = tmp.getIconHeight();
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

