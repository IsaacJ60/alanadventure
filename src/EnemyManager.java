import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//TODO: ADD SUBCLASSES TO ENEMY FOR SPECIFIC ENEMIES
// PRIORITY - LOW
// create subclasses to inhereit from main enemy class
// uniqueness:
// - animations
// - damage method and method of movement
// - ENSURE ENEMIES BOUNCE OFF EACH OTHER

public class EnemyManager{
    private ArrayList<Enemy> enemies = new ArrayList<>();

    public void addEnemy(int x, int y){
        enemies.add(new Enemy(x,y,30));
    }
    public void drawEnemies(Graphics g, Alan alan){
        for(Enemy e : enemies){
            e.draw(g, alan);
        }
    }
}

class Enemy {
    public static final int IDLE = 0, FLY = 1;
    private int state = FLY;
    private int width, height, health;
    private int xFrames, yFrames; // amount of frames moving in a positive/negative x/y
    private double x, y;
    private double distance, distX, distY; // how far away the enemy is compared to alan
    private double speed, velX, velY, accel; // the speed and acceleration the enemy has
    private double animFrame;

    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> fly = new ArrayList<>();

    public Enemy(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.width = 36;
        this.height = 26;
        this.health = health;
        this.speed = 2;
        this.accel = 0.1;
        this.xFrames = 0;
        this.yFrames = 0;
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

    public void move(Alan alan) {
        // distance calculations
        distX = x - alan.getX();
        distY = y - alan.getY();
        distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)); // pythag theorem
        // adding up how many frames movement has been in x direction, capping out at +-20 to limit terminal velocity
        if (distX > 0 && xFrames > -20) {
            xFrames--;
        } else if (distX < 0 && xFrames < 20) {
            xFrames++;
        }
        if (distY > 0 && yFrames > -20) {
            yFrames--;
        } else if (distY < 0 && yFrames < 20) {
            yFrames++;
        }
        // moving the enemy
        velX = ((-1 / distance) * distX) * speed + xFrames * accel; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
        velY = ((-1 / distance) * distY) * speed + yFrames * accel; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan
        x += velX;
        y += velY;
    }

    public void moveY(int d) {
        y -= d;
    }

    public void draw(Graphics g, Alan alan) {
        move(alan);

        if (state == FLY) {
            if ((int) animFrame == idle.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.33;
            }
            g.drawImage(idle.get((int) animFrame), (int) x+Background.getWallLeftPos(), (int) y-Alan.getOffset(), null);
        }
    }
}