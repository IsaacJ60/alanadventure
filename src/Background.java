import javax.swing.*;
import java.awt.*;

//TODO: UI ELEMENTS
// PRIORITY - LOW
// - health, powerups, etc.

public class Background {
    private final MapList maplist;
    private final TileGroup walls;
    private static int wallWidth, wallLeftPos, wallRightPos;
    private Image bg;

    public Background() {
        bg = new ImageIcon("src/tiles/bg.png").getImage().getScaledInstance(GamePanel.getWIDTH(), GamePanel.getHEIGHT(), Image.SCALE_DEFAULT);
        Image wallImgLeft = new ImageIcon("src/tiles/sideleft.png").getImage();
        Image wallImgRight = new ImageIcon("src/tiles/sideright.png").getImage();
        Tile wallTile = new Tile("wall", wallImgLeft, wallImgRight);
        walls = new TileGroup("walls", wallTile);

        wallLeftPos = GamePanel.getWIDTH()/2-GamePanel.getWIDTH()/5+1;
        wallRightPos = GamePanel.getWIDTH()/2+GamePanel.getWIDTH()/5-walls.tile.getWidth();
        wallWidth = walls.tile.getWidth();

        maplist = new MapList();

        // making default level
        Map lvl1 = new Map(500);
        maplist.addMap(lvl1);
    }

    public static int getWallWidth() {return wallWidth;}
    public static int getWallLeftPos() {return wallLeftPos;}
    public static void setWallLeftPos(int w) {wallLeftPos = w;}
    public static int getWallRightPos() {return wallRightPos;}
    public static void setWallRightPos(int w) {wallRightPos = w;}

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        setBackground(g, g.getColor());
        g.drawImage(bg,0,Alan.getScreenOffset()/3,null);
        g.fillRect(getWallLeftPos(), 0, getWallRightPos()-getWallLeftPos(), GamePanel.getHEIGHT());
        maplist.drawBlocks(g, GamePanel.getLevel());
        drawWalls(g);
    }

    public void setBackground(Graphics g, Color col) {
        g.setColor(col);
        g.fillRect(0,0,GamePanel.getWIDTH(),GamePanel.getHEIGHT());
    }

    public void drawWalls(Graphics g) {
        walls.drawWall(g, wallLeftPos, 0, GamePanel.getHEIGHT(), true);
        walls.drawWall(g, wallRightPos, 0, GamePanel.getHEIGHT(), false);
    }
}

class TileGroup {
    String name;
    Tile tile;

    TileGroup(String name, Tile tile) {
        this.name = name;
        this.tile = tile;
    }

    public void drawWall(Graphics g, int x1, int y1, int y2, boolean flipped) {
        if (flipped) {
            for (int i = y1; i < 500*Util.BLOCKLENGTH; i+=tile.getWidth()) {
                g.drawImage(tile.getRimg(),x1,i-Alan.getOffset()+Alan.getScreenOffset(),null);
            }
        } else {
            for (int i = y1; i < 500*Util.BLOCKLENGTH; i+=tile.getWidth()) {
                g.drawImage(tile.getImg(),x1,i-Alan.getOffset()+Alan.getScreenOffset(),null);
            }
        }
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
