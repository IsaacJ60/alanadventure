import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

//TODO: ADD SUBCLASSES TO ENEMY FOR SPECIFIC ENEMIES
// PRIORITY - LOW
// create subclasses to inhereit from main enemy class
// uniqueness:
// - animations
// - damage method and method of movement
// - ENSURE ENEMIES BOUNCE OFF EACH OTHER

public class EnemyManager{
    private ArrayList<Flyer> flyers = new ArrayList<>();
    private ArrayList<Worm> worms = new ArrayList<>();

    public void addFlyer(int x, int y){
        flyers.add(new Flyer(x,y));
    }
    public void addWorm(int x, int y){
        worms.add(new Worm(x,y));
    }
    public void generateWorms(Block[][] blocks, Alan alan){
        for(int i=1; i<blocks.length; i++){
            for(int j=0; j<blocks[i].length; j++) {
                if (blocks[i-1][j].getType() == Block.AIR && blocks[i][j].getType() != Block.AIR && blocks[i][j].getType() != Block.SPIKE) {
                    addWorm(blocks[i][j].getX(true), blocks[i][j].getY(true, alan));
                }
            }
        }
    }

    public void drawEnemies(Graphics g, Block[][] blocks){
        for(Flyer f : flyers){
            f.draw(g);
        }
        for(Worm w: worms){
            w.draw(g, blocks);
        }
    }
}

class Worm{
    public static final int LEFT = 0, RIGHT = 1;
    private final int width, height;
    private int health;
    private int x, y;
    private int dir;
    private double speed, velX, velY; // the speed and acceleration the enemy has
    private double animFrame;

    ArrayList<Image> idle = new ArrayList<>();

    public Worm(int x, int y) {
        this.width = 25;
        this.height = 12;
        this.x = x;
        this.y = y-height;
        dir = RIGHT;
        this.health = 10;
        this.speed = 2;
        animFrame = 0;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x - Background.getWallLeftPos() - Background.getWallWidth();
        } else {
            return x;
        }
    }
    public int getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-GamePanel.getAlan().getOffset()+GamePanel.getAlan().getScreenOffset();
        } else {
            return y;
        }
    }


    public void move(Block[][] blocks){
        Block[][] oneIndBlocks = new Block[blocks.length][blocks[0].length+2];
        int prevCol = getX(true)/Util.BLOCKLENGTH-1+1;
        int nextCol = getX(true)/Util.BLOCKLENGTH+1+1;
        int currRow = getY(false)/Util.BLOCKLENGTH;
        int grndRow = currRow+1; // add one to get the row of blocks the worm is standing on
        int currCol = getX(true)/Util.BLOCKLENGTH+0+1;

        for(int i=0; i<blocks.length; i++) {
            for(int j=0; j<blocks[i].length; j++){
                oneIndBlocks[i][j+1] = blocks[i][j];
            }
            oneIndBlocks[i][0] = new Block(0,0,Block.AIR,Util.NEUTRAL);
            oneIndBlocks[i][10] = new Block(0,0,Block.AIR,Util.NEUTRAL);
        }

//        System.out.println(Arrays.toString(blocks[30]));
//        System.out.println(Arrays.toString(oneIndBlocks[30]));
//        System.out.println("-------------");

//        System.out.println("prevCol: "+prevCol);
//        System.out.println("nextCol: "+nextCol);
//        System.out.println("grndRow: "+grndRow);
//        System.out.println("currCol: "+currCol);
//        System.out.println("------------------");

        if(dir == LEFT){
            if (oneIndBlocks[grndRow][currCol].getType() != Block.AIR && oneIndBlocks[currRow][currCol].getType() == Block.AIR) {
                x-=speed;
            } else {
                dir = RIGHT;
                x+=2*speed;
            }
        }
        else{
            if (oneIndBlocks[grndRow][currCol].getType() != Block.AIR && oneIndBlocks[currRow][currCol].getType() == Block.AIR) {
                x+=speed;
            } else {
                dir = LEFT;
                x-=2*speed;
            }
        }
    }

    public void draw(Graphics g, Block[][] blocks) {
        move(blocks);
        g.setColor(Color.YELLOW);
        g.drawRect(x, y-GamePanel.getAlan().getOffset()+GamePanel.getAlan().getScreenOffset(), width, height);
    }
}

class Flyer {
    public static final int IDLE = 0, FLY = 1;
    private final int state = FLY;
    private final int width, height;
    private int health;
    private double x, y;
    private double speed, velX, velY, maxVelX, maxVelY, accelX, accelY, accelFactor; // the speed and acceleration the enemy has
    private double animFrame;

    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> fly = new ArrayList<>();

    public Flyer(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 36;
        this.height = 26;
        this.health = 30;
        this.speed = 2;
        this.maxVelX = 4.5;
        this.maxVelY = 4.5;
        this.accelX = 0;
        this.accelY = 0;
        this.accelFactor = .2;
        animFrame = 0;
        for (int i = 0; i < 6; i++) {
            idle.add(new ImageIcon("src/assets/enemy/fly/fly" + i + ".png").getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        }
    }

    public int getX() {
        return (int) x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return (int) y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void move() {
        // distance calculations
        double distX = x - GamePanel.getAlan().getX(false);
        // how far away the enemy is compared to alan
        double distY = y - GamePanel.getAlan().getY(false);
        double distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)); // pythag theorem
        // adding up how many frames movement has been in x direction, capping out at +-20 to limit terminal velocity
        if (distX < 0 && velX < maxVelX) {
            accelX += accelFactor;
        } else if (distX > 0 && velX > -maxVelX) {
            accelX -= accelFactor;
        }
        if (distY < 0 && velY < maxVelY) {
            accelY += accelFactor;
        } else if (distY > 0 && velY > -maxVelY) {
            accelY -= accelFactor;
        }
        // moving the enemy
        velX = ((-1 / distance) * distX) * speed + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
        velY = ((-1 / distance) * distY) * speed + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan
        x += velX;
        y += velY;
    }


    public void draw(Graphics g) {
        move();

        if (state == FLY) {
            if ((int) animFrame == idle.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.33;
            }
            g.drawImage(idle.get((int) animFrame), (int) x+Background.getWallLeftPos(), (int) y-GamePanel.getAlan().getOffset(), null);
        }
    }
}