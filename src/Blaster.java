import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//TODO:
// - explosion animation sprites

public class Blaster {
    String name;
    int damage, capacity, speed, lastX, lastY;
    private Image defaultBullet;

    private boolean alanShoot;
    ArrayList<Image> shootAnim;
    ArrayList<Image> explosion;
    ArrayList<Bullet> bullets;
    ArrayList<int[]> blastPlaces;
    Util.CustomTimer shootTimer;

    public Blaster(String name, int damage, int capacity, int speed, ArrayList<Image> shootAnim) {
        bullets = new ArrayList<>();
        blastPlaces = new ArrayList<>();
        explosion = new ArrayList<>();
        this.name = name;
        this.damage = damage;
        this.capacity = capacity;
        this.speed = speed;
        this.shootAnim = shootAnim;
        lastX = 0; lastY = 0;
        alanShoot = false;
        defaultBullet = new ImageIcon("src/assets/alan/shoot/bullets/bullet.png").getImage().getScaledInstance(16,32,Image.SCALE_DEFAULT);
        for (int i = 0; i < 5; i++) {
            shootAnim.add(new ImageIcon("src/assets/alan/shoot/bullets/bullet.png").getImage());
        }
        for (int i = 0; i < 5; i++) {
//            explosion.add(new ImageIcon("src/assets/alan/shoot/bullets/explosion" + i + ".png").getImage());
        }
        shootTimer = new Util.CustomTimer();
        shootTimer.start();
    }

    public void setLastX(int x) {
        lastX = x;
    }
    public void setLastY(int y) {
        lastY = y;
    }
    public boolean isAlanShoot() {
        return alanShoot;
    }

    public void setAlanShoot(boolean alanShoot) {
        this.alanShoot = alanShoot;
    }

    // SHOOT - CHECKS IF PLAYER WANTS TO SHOOT AND IF TIMER ALLOWS, ADDS BULLET IF ALLOWED
    public boolean shoot(boolean[] keys, int velY, Graphics g) {
        blastAnim(blastPlaces, g);
        if (Alan.getState() == Alan.FALL && keys[Util.space] && shootTimer.getElapsedTime() > 0.15 && velY > 0) {
            shootTimer.restart();
            // add bullet
            bullets.add(new Bullet(Alan.getX(false) + (Alan.getDir() == Alan.LEFT ? 2 : 8), Alan.getY(false) + Alan.getVelY() + 10,
                    Alan.getY(false) + Alan.getVelY() + 10,
                    defaultBullet));
            alanShoot = true;
            return true;
        }
        return false;
    }

    // DRAWS ALL BULLETS AND CHECKS FOR COLLISION BETWEEN BLOCK AND BULLET
    public void animation(Graphics g, Block[][] blocks) {
        ArrayList<Bullet> rm = new ArrayList<>(); // removal list
        for (Bullet b : bullets) { // go through all bullets
            if (getCollision(b, blocks)) {
                rm.add(b);
            } else if (b.getY(false) > b.getStartY() + 300) {
                rm.add(b);
            } else if (Alan.getNearestY() < 10) {
                rm.add(b);
            } else {
                g.drawImage(b.getImg(), b.getX(true), b.getY(true), null);
                b.setY(b.getY(false) + speed);
            }
        }
        for (Bullet b : rm) {
            bullets.remove(b);
        }
    }

    public boolean getCollision(Bullet b, Block[][] blocks) {
        int nextRow = b.getY(false)/Util.BLOCKLENGTH+1;
        for (int r = nextRow-1; r < nextRow+2; r++) {
            for (int i = 0; i < Map.getColumns(); i++) {
                int blockType = blocks[r][i].getType();
                if (blockType != Block.AIR) {
                    if ((blockType == Block.WALL || blockType == Block.BOX || blockType == Block.PLAT)) {
                        if (blocks[r][i].collide(b.getRect())) {
                            if (blockType == Block.BOX) {
                                blocks[r][i].setType(Block.AIR);
                                blastPlaces.add(new int[]{r*Util.BLOCKLENGTH, i*Util.BLOCKLENGTH, 0});
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // plays all explosion animations when box destroyed
    public void blastAnim(ArrayList<int[]> blastPlaces, Graphics g) {
        for (int[] coords : blastPlaces) {
//            g.drawImage(explosion.get(coords[2]), coords[0], coords[1],null);
        }
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

class Bullet {
    private int x, y, startY;
    private Image img;
    private final Rectangle rect;

    Bullet(int x, int y, int startY, Image img) {
        this.startY = startY;
        this.x = x;
        this.y = y;
        this.img = img;
        this.rect = new Rectangle(x,y,8, 16);
    }

    public int getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return x;
        }
    }

    public int getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y- Alan.getOffset() + Alan.getScreenOffset();
        } else {
            return y;
        }
    }
    public Rectangle getRect() {return rect;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {
        this.y = y;
        this.rect.setLocation(this.x, this.y);
    }
    public Image getImg() {return img;}
    public void setImg(Image img) {this.img = img;}
    public int getStartY() {return startY;}

    public void draw(Graphics g) {
        g.drawImage(img, x, y, null);
    }
}