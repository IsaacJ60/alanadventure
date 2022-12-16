import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Alan {
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D, space = KeyEvent.VK_SPACE;

    int x, y;
    int width, height;
    int health, speed;
    Blaster weapon;

    double animFrame;

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
        this.width = 20;
        this.height = 30;
        // TODO: resolve flickering and distortion from scaling
        for (int i = 0; i < 7; i++) {
            idle.add(new ImageIcon("src/assets/alan/idle/idle" + i + ".png").getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT));
//            idle.add(new ImageIcon("src/assets/alan/idle/idle" + i + ".png").getImage());
        }
        for (int i = 1; i < 8; i++) {
            walk.add(new ImageIcon("src/assets/alan/walk/walk" + i + ".png").getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT));
//            walk.add(new ImageIcon("src/assets/alan/walk/walk" + i + ".png").getImage());
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
        if (keys[a] || keys[d]) {
            if (keys[a]) {
                x -= speed;
                x = Math.max(x, Background.getWallLeftPos()+Background.getWallWidth());
            }
            if (keys[d]) {
                x += speed;
                x = Math.min(x, Background.getWallRightPos()-width);
            }
            changeState(WALK);
        } else {
            changeState(IDLE);
        }
    }

    public void changeState(int MODE) {
        if (state != MODE) {
            animFrame = 0;
        }
        state = MODE;
    }

    public void draw(Graphics g, boolean[] keys) {
        move(keys);
        if (state == IDLE) {
            if ((int)animFrame == idle.size()-1) {
                animFrame = 0;
            } else {
                animFrame+=0.2;
            }
            g.drawImage(idle.get((int)animFrame),x,y,null);
        } else if (state == WALK) {
            if ((int)animFrame == walk.size()-1) {
                animFrame = 0;
            } else {
                animFrame+=0.4;
            }
            g.drawImage(walk.get((int)animFrame),x,y,null);
        }
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