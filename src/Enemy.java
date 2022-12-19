import java.awt.*;
import java.util.ArrayList;

//TODO: ADD SUBCLASSES TO ENEMY FOR SPECIFIC ENEMIES
// PRIORITY - LOW
// create subclasses to inhereit from main enemy class
// uniqueness:
// - animations
// - damage method and method of movement
// - ENSURE ENEMIES BOUNCE OFF EACH OTHER

public class Enemy {
    private int width, height,  health;
    private int xFrames, yFrames; // amount of frames moving in a positive/negative x/y
    private double x, y;
    private double distance, distX, distY; // how far away the enemy is compared to alan
    private double speed, velX, velY, accel; // the speed and acceleration the enemy has
    private double animFrame;

    ArrayList<Image> idle = new ArrayList<>();

    public Enemy(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 30;
        this.health = health;
        this.speed = 2;
        this.accel = 0.1;
        this.xFrames = 0;
        this.yFrames = 0;
        animFrame = 0;
    }

    public int getX() {return (int)x;}

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {return (int)y;}

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
        if(distX>0 && xFrames>-20){xFrames--;}
        else if(distX<0 && xFrames<20){xFrames++;}
        if(distY>0 && yFrames>-20){yFrames--;}
        else if(distY<0 && yFrames<20){yFrames++;}
        // moving the enemy
        velX = ((-1/distance)*distX)*speed + xFrames*accel; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
        velY = ((-1/distance)*distY)*speed + yFrames*accel; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan
        x += velX;
        y += velY;
    }

    public void draw(Graphics g, Alan alan) {
        move(alan);
        g.setColor(Color.MAGENTA);
        g.drawRect((int)x+Background.getWallLeftPos()+Background.getWallWidth(),(int)y-Alan.getOffset(), width,height);
    }
}