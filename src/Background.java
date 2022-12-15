import javax.swing.*;
import java.awt.*;

public class Background {
    TileGroup walls;
    Tile wallTile;
    Image wallImgLeft;
    Image wallImgRight;

    private static int wallLeftPos, wallRightPos;

    public Background() {
        wallImgLeft = new ImageIcon("src/tiles/sideleft.png").getImage();
        wallImgRight = new ImageIcon("src/tiles/sideright.png").getImage();
        wallTile = new Tile("wall", wallImgLeft, wallImgRight);
        walls = new TileGroup("walls", wallTile);

        wallLeftPos = GamePanel.getWIDTH()/2-GamePanel.getWIDTH()/5;
        wallRightPos = GamePanel.getWIDTH()/2+GamePanel.getWIDTH()/5-walls.tile.getWidth();
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,GamePanel.getWIDTH(),GamePanel.getHEIGHT());
        walls.drawVerticalLine(g, wallLeftPos, 0, GamePanel.getHEIGHT(), true);
        walls.drawVerticalLine(g, wallRightPos, 0, GamePanel.getHEIGHT(), false);
    }

    public static int getWallLeftPos() {
        return wallLeftPos;
    }

    public static void setWallLeftPos(int w) {
        wallLeftPos = w;
    }

    public static int getWallRightPos() {
        return wallRightPos;
    }

    public static void setWallRightPos(int w) {
        wallRightPos = w;
    }
}

class TileGroup {
    String name;
    Tile tile;

    TileGroup(String name, Tile tile) {
        this.name = name;
        this.tile = tile;
    }

    public void drawVerticalLine(Graphics g, int x1, int y1, int y2, boolean flipped) {
        if (flipped) {
            for (int i = y1; i < y2; i+=tile.getWidth()) {
                g.drawImage(tile.getRimg(),x1,i,null);
            }
        } else {
            for (int i = y1; i < y2; i+=tile.getWidth()) {
                g.drawImage(tile.getImg(),x1,i,null);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public Image getRimg() {
        return rimg;
    }

    public void setRimg(Image rimg) {
        this.rimg = rimg;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
