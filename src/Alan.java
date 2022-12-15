import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Alan {
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D;

    int x, y;
    int health, speed;
    Blaster weapon;

    int animFrame;

    int state = 0;
    public static final int IDLE = 0, WALK = 1, JUMP = 2, FALL = 3;

    ArrayList<Image> walk = new ArrayList<>();
    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> fall = new ArrayList<>();

    public Alan(int x, int y, int health, int speed, Blaster weapon) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        this.weapon = weapon;
        animFrame = 0;
        for (int i = 0; i < 1; i++) {
            idle.add(new ImageIcon("src/assets/alan/idle/idle" + i + ".png").getImage().getScaledInstance(33, 50,Image.SCALE_SMOOTH));
        }
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

    public Blaster getWeapon() {
        return weapon;
    }

    public void setWeapon(Blaster weapon) {
        this.weapon = weapon;
    }

    public void move(boolean[] keys) {
        if (!hitWall()) {
            if (keys[a] || keys[d]) {
                if (keys[a]) {
                    x -= speed;
                }
                if (keys[d]) {
                    x += speed;
                }
                state = WALK;
            } else {
                state = IDLE;
            }
        }
    }

    public boolean hitWall() {
        if (x > Background.getWallRightPos()) {
            return true;
        } else return x < Background.getWallLeftPos();
    }

    public void draw(Graphics g, boolean[] keys) {
        move(keys);
        if (animFrame == idle.size()-1) {
            animFrame = 0;
        } else {
            animFrame++;
        }
        g.drawImage(idle.get(animFrame),x,y,null);
    }
}

class Blaster {
    String name;
    int damage, capacity, speed;
    ArrayList<Image> shootAnim = new ArrayList<>();

    Blaster(String name, int damage, int capacity, int speed, ArrayList<Image> shootAnim) {
        this.name = name;
        this.damage = damage;
        this.capacity = capacity;
        this.speed = speed;
        this.shootAnim = shootAnim;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public ArrayList<Image> getShootAnim() {
        return shootAnim;
    }

    public void setShootAnim(ArrayList<Image> shootAnim) {
        this.shootAnim = shootAnim;
    }
}