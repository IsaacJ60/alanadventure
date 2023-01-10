import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: ADD SUBCLASSES TO ENEMY FOR SPECIFIC ENEMIES
// PRIORITY - LOW
// create subclasses to inhereit from main enemy class
// uniqueness:
// - animations
// - damage method and method of movement
// - ENSURE ENEMIES BOUNCE OFF EACH OTHER

public class EnemyManager{
    private final ArrayList<Snake> snakes = new ArrayList<>();
    private final ArrayList<Bat> bats = new ArrayList<>();

    public ArrayList<Snake> getSnakes() {return snakes;}
    public ArrayList<Bat> getBats() {return bats;}

    // HINT: modify enemy health here
    private void addSnake(int x, int y) {snakes.add(new Snake(x,y, 20));}
    private void addBat(int x, int y) {bats.add(new Bat(x,y, 30));}

    public void generateSnakes(Block[][] blocks, Alan alan) {
        Random rand = new Random();
        for(int i=Util.GENERATIONSTART; i< blocks.length; i++){
            for(int j=1; j<blocks[i].length-1; j++) {
                if (blocks[i-1][j].getType() == Block.AIR && blocks[i][j].getType() != Block.AIR && blocks[i][j].getType() != Block.SPIKE) {
                    if(rand.nextInt(100)<=20) {
                        addSnake(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
                    }
                }
            }
        }
    }

    public void clearEnemies(){
        snakes.clear();
        bats.clear();
    }

    public void drawEnemies(Graphics g, Block[][] blocks){
        for(Bat b : bats){
            b.draw(g);
        }
        for(Snake s : snakes){
            s.draw(g, blocks);
        }
    }
}

class Snake {
    private final int width, height;
    private int health;
    private double x, y;
    private int dir;
    private double speed, velX, velY, maxVelY, accelY; // the speed and acceleration the enemy has
    private double animFrame;

    private final ArrayList<Image> idleL = new ArrayList<>();
    private final ArrayList<Image> idleR = new ArrayList<>();

    public Snake(int x, int y, int health) {
        this.width = 32;
        this.height = 18;
        this.x = x;
        this.y = y-height+1;
        dir = Util.RIGHT;
        this.health = health;
        this.velX = 2;
        this.accelY = 1;
        this.maxVelY = 13;
        animFrame = 0;

        for (int i = 0; i < 4; i++) {
            idleL.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleL" + i + ".png").getImage());
            idleL.set(i, idleL.get(i).getScaledInstance((idleL.get(i).getWidth(null)*2), (idleL.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 4; i++) {
            idleR.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleR" + i + ".png").getImage());
            idleR.set(i, idleR.get(i).getScaledInstance((idleR.get(i).getWidth(null)*2), (idleR.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
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
    public double getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos() + Background.getWallWidth();
        } else {
            return x;
        }
    }
    public double getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-GamePanel.getAlan().getOffset()+GamePanel.getAlan().getScreenOffset();
        } else {
            return y;
        }
    }
    public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

    public void move(Block[][] blocks){
        int currRow = (int)y/Util.BLOCKLENGTH;
        int grndRow = currRow+1; // add one to get the row of blocks the snake is standing on
        int currColL = (int)x/Util.BLOCKLENGTH;
        int currColC = (int)(x+width/2)/Util.BLOCKLENGTH;
        int currColR = (int)(x+width)/Util.BLOCKLENGTH;

        if(blocks[grndRow][currColC].getType() != Block.AIR) {
            y = grndRow * Util.BLOCKLENGTH - height;
            velY = 0;
            if (dir == Util.LEFT) {
                if (x <= 0) {
                    dir = Util.RIGHT;
                    x += velX;
                } else {
                    if (blocks[grndRow][currColL].getType() != Block.AIR && blocks[grndRow - 1][currColL].getType() == Block.AIR) {
                        x -= velX;
                    } else {
                        dir = Util.RIGHT;
                        x += velX;
                    }
                }
            } else {
                if (x + width >= 9 * Util.BLOCKLENGTH) {
                    dir = Util.LEFT;
                    x -= velX;
                } else {
                    if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
                        x += velX;
                    } else {
                        dir = Util.LEFT;
                        x -= velX;
                    }
                }
            }
        }
        else{
            y+=velY;
            if(velY < maxVelY){
                velY += accelY;
            }
        }
    }

    public void draw(Graphics g, Block[][] blocks) {
        move(blocks);

        if(dir == Util.LEFT) {
            if ((int) animFrame == idleL.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.2;
            }
            g.drawImage(idleL.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }
        else{
            if ((int) animFrame == idleR.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.2;
            }
            g.drawImage(idleR.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
    }
}

class Bat{
    public static final int IDLE = 0, FLY = 1;
    private final int state;
    private final int width, height;
    private int health;
    private double x, y;
    private double speed, velX, velY, maxVelX, maxVelY, accelX, accelY, accelFactor; // the speed and acceleration the enemy has
    private double animFrame;

    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> fly = new ArrayList<>();

    public Bat(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.width = 36;
        this.height = 26;
        this.health = health;
        this.speed = 2;
        this.maxVelX = 4.5;
        this.maxVelY = 4.5;
        this.accelX = 0;
        this.accelY = 0;
        this.accelFactor = .2;
        state = FLY;
        animFrame = 0;
        for (int i = 0; i < 6; i++) {
            idle.add(new ImageIcon("src/assets/enemies/fly/fly" + i + ".png").getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
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