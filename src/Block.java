//TODO: COLLISION DETECTION AND TILE LINKING SO THAT RENDERING A BLOCK RENDERS THE TILE IT IS PAIRED WITH
// PRIORITY - MEDIUM
// - detect collision based on type of block (they have different dimensions)
// - pair a tile object with it or figure out alternative (dumb-brained way also works, not too much of hassle)

import java.awt.*;

public class Block {
    private int x;
    private int y;
    private int width;
    private int height;
    public static final int AIR = 0, BOX = 1, PLAT = 2, SPIKE = 3, WALL = 4;
    private int type;
    private int side;
    private Tile tile;
    private Rectangle rect;

    public Block(int x, int y, int type, int side) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.side = side;
        rect = new Rectangle(x,y,Util.BLOCKLENGTH,Util.BLOCKLENGTH);
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public boolean collide(Rectangle otherRect) {
        return rect.intersects(otherRect);
    }

    public boolean collide(int x, int y, int width, int height) {
        return rect.intersects(new Rectangle(x,y,width,height));
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

