import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

//TODO: MOVEMENT AND OFFSET
// PRIORITY - LOW-MEDIUM
// - calculate background position based on player offset
// - acceleration
// > GRAPHICS:
//   - flip animation for opposite direction
//   - finish other animations

public class Alan {
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D;

    int x, y;
    int width, height;
    int health, speed, velocity;
    static int offset;
    Blaster weapon;

    double animFrame;

    int state = 0;
    private final int LEFT = 0, RIGHT = 1;
    int dir = LEFT;
    public static final int IDLE = 0, WALK = 1, JUMP = 2, FALL = 3;

    ArrayList<Image> walk = new ArrayList<>();
    ArrayList<Image> rwalk = new ArrayList<>();
    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> ridle = new ArrayList<>();
    ArrayList<Image> fall = new ArrayList<>();
    ArrayList<ArrayList<Image>> allAnims = new ArrayList<>();

    private Rectangle alanRect;

    public Alan(int x, int y, int health, int speed, int velocity, Blaster weapon) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        this.velocity = velocity;
        this.weapon = weapon;
        animFrame = 0;
        this.width = 30;
        this.height = 30;

        offset = 0;

        alanRect = new Rectangle(x,y,width-10,height);

        for (int i = 0; i < 7; i++) {
            idle.add(new ImageIcon("src/assets/alan/idle/idle" + i + ".png").getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 7; i++) {
            ridle.add(new ImageIcon("src/assets/alan/idle/m_idle" + i + ".png").getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 7; i++) {
            walk.add(new ImageIcon("src/assets/alan/walk/walk" + i + ".png").getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 7; i++) {
            rwalk.add(new ImageIcon("src/assets/alan/walk/m_walk" + i + ".png").getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
        }
        allAnims.add(idle);
        allAnims.add(ridle);
        allAnims.add(walk);
        allAnims.add(rwalk);
    }

    public static int getOffset() {
        return offset;
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

    public void move(boolean[] keys, Graphics g) {
        getCollision(MapList.getBlocks(),g);
        if (keys[a] || keys[d]) {
            if (keys[a]) {
                dir = LEFT;
                x -= speed;
                x = Math.max(x, 0);
            }
            if (keys[d]) {
                dir = RIGHT;
                x += speed;
                x = Math.min(x, Background.getWallRightPos()-Background.getWallLeftPos()-Background.getWallWidth());
            }
            changeState(WALK, dir);
        } else {
            changeState(IDLE, dir);
        }
        y+=velocity;
        offset+=velocity;
        alanRect.setLocation(x+5,y);
    }

    public void changeState(int MODE, int d) {
        if (state != MODE || d != dir) {
            animFrame = 0;
        }
        state = MODE;
    }

    public void getCollision(Block[][] blocks, Graphics g) {
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j].collide(alanRect)) {
                    if (blocks[i][j].getType() == Block.AIR) {
                        speed = 10;
                        velocity = 5;
                    } else {
                        //TODO: BRUH COLLISION SO ANNOYING
                        if (alanRect.intersectsLine(blocks[i][j].getX(),blocks[i][j].getY(),blocks[i][j].getX()+Util.BLOCKLENGTH,blocks[i][j].getY())) {
                            velocity = 0;
                            g.setColor(Color.RED);
                            g.drawLine(blocks[i][j].getX()+Background.getWallLeftPos(),blocks[i][j].getY()-offset,blocks[i][j].getX()+Background.getWallLeftPos()+Util.BLOCKLENGTH,blocks[i][j].getY()-offset);
                        } else if (alanRect.intersectsLine(blocks[i][j].getX(),blocks[i][j].getY(),blocks[i][j].getX(),blocks[i][j].getY()+Util.BLOCKLENGTH)) {
                            x = Math.min(x-Util.BLOCKLENGTH,blocks[i][j].getX()-Util.BLOCKLENGTH-1);
                        } else if (alanRect.intersectsLine(blocks[i][j].getX()+Util.BLOCKLENGTH,blocks[i][j].getY(),blocks[i][j].getX()+Util.BLOCKLENGTH,blocks[i][j].getY()+Util.BLOCKLENGTH)) {
                            x = Math.max(x,blocks[i][j].getX()+Util.BLOCKLENGTH+1);
                        }
                    }
                }
            }
        }
    }

    public void draw(Graphics g, boolean[] keys) {
        move(keys, g);
        if (state == IDLE) {
            if ((int) animFrame == idle.size()-1) {
                animFrame = 0;
            } else {
                animFrame+=0.2;
            }
        } else if (state == WALK) {
            if ((int) animFrame == walk.size()-1) {
                animFrame = 0;
            } else {
                animFrame += 0.4;
            }
        }
        if (dir == LEFT) {
            g.drawImage(allAnims.get(state*2+1).get((int)animFrame),x+Background.getWallLeftPos(),y-offset,null);
        } else {
            g.drawImage(allAnims.get(state*2).get((int)animFrame),x+Background.getWallLeftPos(),y-offset,null);
        }
        g.setColor(Color.RED);
        g.drawRect((int) alanRect.getX()+Background.getWallLeftPos(), (int) alanRect.getY()-offset, alanRect.width, alanRect.height);
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

