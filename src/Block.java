//TODO: COLLISION DETECTION AND TILE LINKING SO THAT RENDERING A BLOCK RENDERS THE TILE IT IS PAIRED WITH
// PRIORITY - MEDIUM
// DONE - pair a tile object with it or figure out alternative (dumb-brained way also works, not too much of hassle)

import java.awt.*;

public class Block {
    // PROPERTIES
    private int x, y, width, height, side;
    private final Rectangle rect;
    // image property
    private Tile tile;

    // TYPE CONSTANTS
    public static final int AIR = 0, BOX = 1, PLAT = 2, SPIKE = 3, WALL = 4;
    // store type of block
    private int type;

    public Block(int x, int y, int type, int side) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.side = side;
        rect = new Rectangle(x,y,Util.BLOCKLENGTH,Util.BLOCKLENGTH);
    }

    public Tile getTile() {return tile;}
    public void setTile(Tile tile) {this.tile = tile;}
    public boolean collide(Rectangle otherRect) {return rect.intersects(otherRect);}
    public boolean collide(int x, int y, int width, int height) {return rect.intersects(new Rectangle(x,y,width,height));}
    public int getSide() {return side;}
    public void setSide(int side) {this.side = side;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public int getWidth() {return width;}
    public void setWidth(int width) {this.width = width;}
    public int getHeight() {return height;}
    public void setHeight(int height) {this.height = height;}
    public void setType(int type) {this.type = type;}
    public int getType() {return type;}

    public int getX(boolean adjusted) {
        if (adjusted) {
            return x+Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return x;
        }
    }

    public int getY(boolean adjusted, Alan alan) {
        if (adjusted) {
            return y-alan.getOffset()+alan.getScreenOffset();
        } else {
            return y;
        }
    }

    public int getY() {
        return y;
    }
}
