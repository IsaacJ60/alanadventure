import java.awt.*;
import java.util.ArrayList;

//TODO: ADD SUBCLASSES TO ENEMY FOR SPECIFIC ENEMIES
// PRIORITY - LOW
// create subclasses to inhereit from main enemy class
// uniqueness:
// - animations
// - damage method and method of movement

public class Enemy {
    private int x, y, health, speed, animFrame;

    ArrayList<Image> idle = new ArrayList<>();

    public Enemy(int x, int y, int health, int speed) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        animFrame = 0;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void draw(Graphics g, boolean[] keys) {
        if (animFrame == idle.size() - 1) {
            animFrame = 0;
        } else {
            animFrame++;
        }
        g.drawImage(idle.get(animFrame), x, y, null);
    }
}
