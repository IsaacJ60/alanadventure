import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//TODO:
// - explosion animation sprites

public class Blaster {
    String name;
    private int damage;
    private int capacity;
    private int speed;
    private int lastX;
    private int lastY;
    private int ammo;
    private int bloom;
    private int amount;
    private double firerate;
    private Image defaultBullet, equippedBullet;

    private boolean alanShoot;
    private ArrayList<Image> explosion;
    private ArrayList<Bullet> bullets;
    private ArrayList<Integer> blastPlaces;
    private ArrayList<Block> blastBlocks;
    private Util.CustomTimer shootTimer;

    // all game blasters
    private static ArrayList<Blaster> blasters;
    private static Blaster machinegun, shotgun, riflegun;
    public static int MACHINEGUN = 0, SHOTGUN = 1, RIFLEGUN = 2;

    public Blaster(String name, int damage, int capacity, int speed, int bloom, double firerate, int amount, String file) {
        if (capacity == -1) {
            this.capacity = Util.UNLIMITED;
        } else {
            this.capacity = capacity;
        }
        bullets = new ArrayList<>();
        blastPlaces = new ArrayList<>();
        blastBlocks = new ArrayList<>();
        explosion = new ArrayList<>();
        this.name = name;
        this.damage = damage;
        this.speed = speed;
        this.ammo = this.capacity;
        this.bloom = bloom;
        this.firerate = firerate;
        this.amount = amount;
        lastX = 0; lastY = 0;
        alanShoot = false;
        defaultBullet = new ImageIcon("src/assets/alan/shoot/bullets/"+file+".png").getImage().getScaledInstance(16,28,Image.SCALE_DEFAULT);
        equippedBullet = defaultBullet;
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
    public int getAmmo() {return ammo;}
    public void setAmmo(int a) {this.ammo = a;}
    public void setAlanShoot(boolean alanShoot) {this.alanShoot = alanShoot;}
    public void setEquippedBullet(String s) {equippedBullet = new ImageIcon("src/assets/alan/shoot/bullets/"+s+".png").getImage().getScaledInstance(16,28,Image.SCALE_DEFAULT);}
    public void setBullets(ArrayList<Bullet> b) {bullets = b;}
    public static ArrayList<Blaster> getBlasters() {return blasters;}

    public static void loadGuns() {
        blasters = new ArrayList<>();
        machinegun = new Blaster("Machine Gun", 10,8,13, 2,0.12,1,"bulletMachineB");
        blasters.add(machinegun);
        shotgun = new Blaster("Shotgun", 10,8,13, 5,0.24,2,"bulletShotgunB");
        blasters.add(shotgun);
        riflegun = new Blaster("Rifle", 10, 12, 10, 1, 0.15, 1, "bulletRifleB");
        blasters.add(riflegun);
    }

    public boolean useAmmo(int a) {
        if (ammo >= a) {
            if (capacity != Util.UNLIMITED) {
                ammo -= a;
            }
            return true;
        } else {
            return false;
        }
    }

    // SHOOT - CHECKS IF PLAYER WANTS TO SHOOT AND IF TIMER ALLOWS, ADDS BULLET IF ALLOWED
    public boolean shoot(boolean[] keys, int velY, Graphics g, Alan alan) {
        blastAnim(g, alan);
        if (keys[alan.getKeyJump()]) {
            if (alan.getState() == Alan.FALL && shootTimer.getElapsedTime() > firerate && velY > 0) {
                if (useAmmo(amount)) {
                    shootTimer.restart();
                    // add bullet
                    for (int i = 0; i < amount; i++) {
                        bullets.add(new Bullet(alan.getX(false) + (alan.getDir() == Util.LEFT ? 2 : 8), alan.getY(false) + alan.getVelY() + 10,
                                alan.getY(false) + alan.getVelY() + 10,
                                equippedBullet, Util.rand.nextDouble(bloom*-1,bloom)));
                    }
                    alanShoot = true;
                    return true;
                }
            }
        }
        return false;
    }

    // DRAWS ALL BULLETS AND CHECKS FOR COLLISION BETWEEN BLOCK AND BULLET
    public void animation(Graphics g, Alan alan, Map map, Powerups powerups, EnemyManager enemies) {
        ArrayList<Bullet> rm = new ArrayList<>(); // removal list for bullets
        for (Bullet b : bullets) { // go through all bullets
            // enemy collisions
            if (getEnemyCollision(b, enemies.getSnakes(), enemies.getSnails(), enemies.getJellies())) {
                rm.add(b);
            }
            // block collisions
            if (getCollision(b, alan, map, powerups)) {
                rm.add(b);
            } else if (b.getY(false,alan) > b.getStartY() + (powerups.getPower(Powerups.LASERSIGHT) == 1 ? Util.EXTENDEDBULLETRANGE : Util.BULLETRANGE)) { // powerup for laser
                rm.add(b);
            } else if (alan.getNearestY() < 10) {
                rm.add(b);
            } else {
                g.drawImage(b.getImg(), (int)b.getX(true), b.getY(true,alan), null);
                b.setY(b.getY(false,alan) + speed);
                b.setX((int) (b.getX(false)+b.getVelX()));
            }
        }
        for (Bullet b : rm) {
            bullets.remove(b);
        }
    }

    public boolean getEnemyCollision(Bullet b, ArrayList<Snake> snakes, ArrayList<Snail> snails, ArrayList<Jelly> jellies) {
        ArrayList<Snake> removalSnake = new ArrayList<>();
        ArrayList<Snail> removalSnail= new ArrayList<>();
        ArrayList<Jelly> removalJelly = new ArrayList<>();

        for (Snake s:snakes) {
            if (s.getRect().intersects(b.getRect())) {
                s.setHealth(s.getHealth()-damage);
                if (s.getHealth() <= 0) {
                    removalSnake.add(s);
                    GameManager.getGemManager().spawnGems((int) s.getX(false), (int) s.getY(false), 3);
                }

                for(Snake r:removalSnake){
                    snakes.remove(r);
                }
                return true;
            }
        }

        for (Snail s:snails) {
            if (s.getRect().intersects(b.getRect())) {
                s.setHealth(s.getHealth()-damage);
                if (s.getHealth() <= 0) {
                    removalSnail.add(s);
                    GameManager.getGemManager().spawnGems((int)s.getX(false),(int)s.getY(false), 3);
                }

                for(Snail r:removalSnail){
                    snails.remove(r);
                }
                return true;
            }
        }
        for (Jelly j:jellies) {
            if (j.getRect().intersects(b.getRect())) {
                j.isHit();
                j.setHealth(j.getHealth()-damage);
                if (j.getHealth() <= 0) {
                    removalJelly.add(j);
                    GameManager.getGemManager().spawnGems((int)j.getX(false),(int)j.getY(false), 3);
                }

                for(Jelly r:removalJelly){
                    jellies.remove(r);
                }
                return true;
            }
        }

        return false;
    }

    public boolean getCollision(Bullet b, Alan alan, Map map, Powerups powerups) {
        // blocks
        Block[][] blocks = map.getMap();
        int nextRow = b.getY(false,alan)/Util.BLOCKLENGTH+1;
        for (int r = nextRow-1; r < nextRow+2; r++) {
            for (int i = 0; i < map.getColumns(); i++) {
                int blockType = blocks[r][i].getType();
                if (blockType != Block.AIR) {
                    if ((blockType == Block.WALL || blockType == Block.BOX)) {
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

    // recursively find all blocks that are touching the one shot
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
            if (blastPlaces.get(i) == explosion.size()) {
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

// BULLET CLASS
class Bullet {
    private int y;
    private final int startY, width, height;
    private double x;
    private final double velX;
    private Image img;
    private final Rectangle rect;

    Bullet(int x, int y, int startY, Image img, double velX) {
        this.velX = velX;
        this.startY = startY;
        this.x = x;
        this.y = y;
        this.img = img;
        this.width = 8;
        this.height = 16;
        this.rect = new Rectangle(x,y,width, height);
    }

    public double getX(boolean adjusted) {return (adjusted ? x + Background.getWallLeftPos() : x);}
    public int getY(boolean adjusted, Alan alan) {return (adjusted ? y-alan.getOffset()+alan.getScreenOffset() : y);}
    public Rectangle getRect(){return rect;}
    public void setX(int x) {this.x = x; this.rect.setLocation((int)this.x, this.y);}
    public void setY(int y) {this.y = y; this.rect.setLocation((int)this.x, this.y);}
    public Image getImg() {return img;}
    public void setImg(Image img) {this.img = img;}
    public int getStartY() {return startY;}
    public double getVelX() {return velX;}

    public void draw(Graphics g) {
        g.drawImage(img, (int) x, y, null);
    }
}

