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
    ArrayList<Image> explosion;
    ArrayList<Bullet> bullets;
    ArrayList<Integer> blastPlaces;
    ArrayList<Block> blastBlocks;
    Util.CustomTimer shootTimer;

    public Blaster(String name, int damage, int capacity, int speed) {
        bullets = new ArrayList<>();
        blastPlaces = new ArrayList<>();
        blastBlocks = new ArrayList<>();
        explosion = new ArrayList<>();
        this.name = name;
        this.damage = damage;
        this.capacity = capacity;
        this.speed = speed;
        lastX = 0; lastY = 0;
        alanShoot = false;
        defaultBullet = new ImageIcon("src/assets/alan/shoot/bullets/bullet.png").getImage().getScaledInstance(16,28,Image.SCALE_DEFAULT);
        for (int i = 0; i < 5; i++) {
            explosion.add(new ImageIcon("src/assets/alan/shoot/explosion/explosion" + i + ".png").getImage());
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
    public boolean shoot(boolean[] keys, int velY, Graphics g, Alan alan) {
        blastAnim(g, alan);
        if (alan.getState() == Alan.FALL && keys[Util.space] && shootTimer.getElapsedTime() > 0.18 && velY > 0) {
            shootTimer.restart();
            // add bullet
            bullets.add(new Bullet(alan.getX(false) + (alan.getDir() == Alan.LEFT ? 2 : 8), alan.getY(false) + alan.getVelY() + 10,
                    alan.getY(false) + alan.getVelY() + 10,
                    defaultBullet));
            alanShoot = true;
            return true;
        }
        return false;
    }

    // DRAWS ALL BULLETS AND CHECKS FOR COLLISION BETWEEN BLOCK AND BULLET
    public void animation(Graphics g, Alan alan, Map map, Powerups powerups) {
        Block[][] blocks = map.getMap();
        ArrayList<Bullet> rm = new ArrayList<>(); // removal list for bullets
        for (Bullet b : bullets) { // go through all bullets
            // block collisions
            if (getCollision(b, blocks, alan, map, powerups, GamePanel.getEnemyManager().getSnakes(), GamePanel.getEnemyManager().getBats())) {
                rm.add(b);
            } else if (b.getY(false,alan) > b.getStartY() + 300) {
                rm.add(b);
            } else if (alan.getNearestY() < 10) {
                rm.add(b);
            } else {
                g.drawImage(b.getImg(), b.getX(true), b.getY(true,alan), null);
                b.setY(b.getY(false,alan) + speed);
            }
        }
        for (Bullet b : rm) {
            bullets.remove(b);
        }
    }

    public boolean getCollision(Bullet b, Block[][] blocks, Alan alan, Map map, Powerups powerups, ArrayList<Snake> snakes, ArrayList<Bat> bats) {
        // enemies
        for(Snake s:snakes){
            if(s.getRect().intersects(b.getRect())){
//                System.out.println("bullet X: "+b.getRect().getX());
//                System.out.println("snake X: "+s.getRect().getX());
                snakes.remove(s);
                return true;
            }
        }
        // blocks
        int nextRow = b.getY(false,alan)/Util.BLOCKLENGTH+1;
        for (int r = nextRow-1; r < nextRow+2; r++) {
            for (int i = 0; i < map.getColumns(); i++) {
                int blockType = blocks[r][i].getType();
                if (blockType != Block.AIR) {
                    if ((blockType == Block.WALL || blockType == Block.BOX || blockType == Block.PLAT)) {
                        if (blocks[r][i].collide(b.getRect())) {
                            if (blockType == Block.BOX) {
                                blocks[r][i].setType(Block.AIR);
                                blastPlaces.add(0);
                                blastBlocks.add(blocks[r][i]);
                                if (powerups.getPower(Powerups.GUNPOWDER) == 1) {
                                    gunpowderEffect(blocks, r, i);
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void gunpowderEffect(Block[][] blocks, int row, int col) {
        blocks[row][col].setType(Block.AIR);
        blastPlaces.add(0);
        blastBlocks.add(blocks[row][col]);
        if (row > 0 && col < 8 && col > 0) {
            if (blocks[row+1][col].getType() == Block.BOX) {
                gunpowderEffect(blocks, row+1, col);
            }
            if (blocks[row-1][col].getType() == Block.BOX) {
                gunpowderEffect(blocks, row-1, col);
            }
            if (blocks[row][col+1].getType() == Block.BOX) {
                gunpowderEffect(blocks, row, col+1);
            }
            if (blocks[row][col-1].getType() == Block.BOX) {
                gunpowderEffect(blocks, row, col-1);
            }
        }
    }

    // plays all explosion animations when box destroyed
    public void blastAnim(Graphics g, Alan alan) {
        for (int i = blastPlaces.size()-1; i >= 0; i--) {
            g.drawImage(explosion.get(blastPlaces.get(i)),blastBlocks.get(i).getX(true),blastBlocks.get(i).getY(true, alan), null);
            blastPlaces.set(i, blastPlaces.get(i)+1);
            if (blastPlaces.get(i) == 5) {
                blastBlocks.remove(i);
                blastPlaces.remove(i);
            }
        }
    }

    public String getName() {return name;}
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
    public ArrayList<Bullet> getBullets(){ return bullets;}
}

class Bullet {
    private int x, y, startY, width, height;
    private Image img;
    private final Rectangle rect;

    Bullet(int x, int y, int startY, Image img) {
        this.startY = startY;
        this.x = x;
        this.y = y;
        this.img = img;
        width = 8;
        height = 16;
        this.rect = new Rectangle(x,y,width, height);
    }

    public int getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return x;
        }
    }

    public int getY(boolean adjusted, Alan alan) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y- alan.getOffset() + alan.getScreenOffset();
        } else {
            return y;
        }
    }
    public Rectangle getRect(){return rect;}
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