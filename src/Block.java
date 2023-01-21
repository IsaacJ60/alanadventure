import java.awt.*;

/*
Block.java
Isaac Jiang
Contains the basic building blocks of maps. Contains methods that get block sprite,
position, and collision.
 */

public class Block {
    // PROPERTIES
    private int x, y, width, height, side;
    private final Rectangle rect;
    // image property
    private Tile tile;

    // TYPE CONSTANTS
    public static final int AIR = 0, BOX = 1, PLAT = 2, SPIKE = 3, WALL = 4, PORTAL = 5;
    // store type of block
    private int type;

    public Block(int x, int y, int type, int side) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.side = side;
        rect = new Rectangle(x,y,Util.BLOCKLENGTH,Util.BLOCKLENGTH);
    }

    // getters and setters
    public Tile getTile() {return tile;}
    public void setTile(Tile tile) {this.tile = tile;}
    public boolean collide(Rectangle otherRect) {return rect.intersects(otherRect);}
    public int getSide() {return side;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public int getWidth() {return width;}
    public void setWidth(int width) {this.width = width;}
    public int getHeight() {return height;}
    public void setHeight(int height) {this.height = height;}
    public void setType(int type) {this.type = type;}
    public int getType() {return type;}

    public int getX(boolean adjusted) {return (adjusted ? x + Background.getWallLeftPos() : x);}
    public int getY(boolean adjusted, Alan alan) {return (adjusted ? y-alan.getOffset()+alan.getScreenOffset() : y);}

    public int getY() {
        return y;
    }
}
