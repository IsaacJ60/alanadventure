import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
Blaster.java
Isaac Jiang & Jayden Zhao
Contains methods that initialize basic weapons, create bullets, draw bullets on screen,
and check for bullet collision. Also contains support for gunpowder powerup.

Bullet.java
Isaac Jiang
stores positional and movement information about bullets fired from weapons
 */

public class Blaster {
    String name;

    // blaster properties
    private int damage;
    private int capacity;
    private final int originalCapacity;
    private int speed;
    private final int originalSpeed;
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
        this.originalCapacity = this.capacity;
        bullets = new ArrayList<>();
        blastPlaces = new ArrayList<>();
        blastBlocks = new ArrayList<>();
        explosion = new ArrayList<>();
        this.name = name;
        this.damage = damage;
        this.speed = speed;
        this.originalSpeed = speed;
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

    // getters and setters
    public boolean isAlanShoot() {return alanShoot;}
    public int getAmmo() {return ammo;}
    public void setAmmo(int a) {this.ammo = a;}
    public void setAlanShoot(boolean alanShoot) {this.alanShoot = alanShoot;}
    public void setEquippedBullet(String s) {equippedBullet = new ImageIcon("src/assets/alan/shoot/bullets/"+s+".png").getImage().getScaledInstance(16,28,Image.SCALE_DEFAULT);}
    public void setBullets(ArrayList<Bullet> b) {bullets = b;}
    public static ArrayList<Blaster> getBlasters() {return blasters;}
    public int getOriginalCapacity() {return originalCapacity;}
    public int getOriginalSpeed() {return originalSpeed;}

    // loading base guns
    public static void loadGuns() {
        blasters = new ArrayList<>();
        machinegun = new Blaster("Machine Gun", 10,8,13, 2,0.12,1,"bulletMachine GunB");
        blasters.add(machinegun);
        shotgun = new Blaster("Shotgun", 5,12,13, 5,0.24,3,"bulletShotgunB");
        blasters.add(shotgun);
        riflegun = new Blaster("Rifle", 12, 10, 10, 1, 0.16, 1, "bulletRifleB");
        blasters.add(riflegun);
    }

    // checking and using ammo
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
    public boolean shoot(boolean[] keys, int velY, Graphics g, Alan alan, Powerups powerups) {
        blastAnim(g, alan);
        if (keys[alan.getKeyJump()]) {
            if (alan.getState() == Alan.FALL && shootTimer.getElapsedTime() > firerate && velY > 0) {
                if (useAmmo(amount)) {
                    Sound.alanShoot();
                    shootTimer.restart();
                    for (int i = 0; i < amount; i++) {
                        int newBloom;
                        // changes bloom based on powerup
                        if (powerups.getPower(Powerups.LASERSIGHT) == 1) {
                            newBloom = bloom/2;
                        } else {
                            newBloom = bloom;
                        }
                        // creating new bullet and adding to bullet array
                        bullets.add(new Bullet(alan.getX(false) + (alan.getDir() == Util.LEFT ? 2 : 8), alan.getY(false) + alan.getVelY() + 10,
                                alan.getY(false) + alan.getVelY() + 10,
                                equippedBullet, Util.rand.nextDouble(newBloom*-1,newBloom)));
                    }
                    alanShoot = true;
                    return true;
                } else {
                    Sound.noBullets();
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
            if (getEnemyCollision(b, enemies.getSnakes(), enemies.getCrawlers(), enemies.getTurtles(), enemies.getSnails(), enemies.getJellies(), enemies.getBats(), enemies.getSkulls())) {
                Sound.bulletCollide();
                rm.add(b);
            }
            // block collisions
            if (getCollision(b, alan, map, powerups)) {
                Sound.bulletCollide();
                rm.add(b);
            } else if (b.getY(false,alan) > b.getStartY() + (powerups.getPower(Powerups.LASERSIGHT) == 1 ? Util.EXTENDEDBULLETRANGE : Util.BULLETRANGE)) { // powerup for laser
                // eliminate bullet if out of range (travelled too far and now sucks as a bullet cuz too slow lol)
                rm.add(b);
            } else {
                // drawing bullet if not removed
                g.drawImage(b.getImg(), (int)b.getX(true), b.getY(true,alan), null);
                b.setY(b.getY(false,alan) + speed);
                b.setX((int) (b.getX(false)+b.getVelX()));
            }
        }
        for (Bullet b : rm) {
            bullets.remove(b);
        }
    }

    // getting bullet - enemy collisions
    public boolean getEnemyCollision(Bullet bu, ArrayList<Snake> snakes, ArrayList<Crawler> crawlers, ArrayList<Turtle> turtles, ArrayList<Snail> snails, ArrayList<Jelly> jellies, ArrayList<Bat> bats, ArrayList<Skull> skulls) {
        ArrayList<Snake> removalSnake = new ArrayList<>();
        ArrayList<Crawler> removalCrawler = new ArrayList<>();
        ArrayList<Turtle> removalTurtle = new ArrayList<>();
        ArrayList<Snail> removalSnail= new ArrayList<>();
        ArrayList<Jelly> removalJelly = new ArrayList<>();
        ArrayList<Bat> removalBat = new ArrayList<>();
        ArrayList<Skull> removalSkull = new ArrayList<>();

        // check snake bullet collision
        for (Snake s:snakes) {
            if (s.getRect().intersects(bu.getRect())) {
                s.setHealth(s.getHealth()-damage); // take away from health
                if (s.getHealth() <= 0) {
                    AAdventure.getGame().getAlan().addCombo();
                    removalSnake.add(s);
                    // spawn gems
                    GameManager.getGemManager().spawnGems((int) s.getX(false), (int) s.getY(false), 3);
                }

                //remove dead snakes
                for(Snake r:removalSnake){
                    snakes.remove(r);
                }
                return true;
            }
        }

        // crawler bullet collision
        for (Crawler c:crawlers) {
            if (c.getRect().intersects(bu.getRect())) {
                c.setHealth(c.getHealth()-damage);
                if (c.getHealth() <= 0) {
                    AAdventure.getGame().getAlan().addCombo();
                    removalCrawler.add(c);
                    GameManager.getGemManager().spawnGems((int) c.getX(false), (int) c.getY(false), 3);
                }

                for(Crawler r:removalCrawler){
                    crawlers.remove(r);
                }
                return true;
            }
        }

        // turtle bullet collision
        for (Turtle t:turtles) {
            // not hurt by bullets
            if (t.getRect().intersects(bu.getRect())) {
                return true;
            }
        }

        // check snail bullet collision
        for (Snail s:snails) {
            if (s.getRect().intersects(bu.getRect())) {
                s.setHealth(s.getHealth()-damage);
                if (s.getHealth() <= 0) {
                    AAdventure.getGame().getAlan().addCombo();
                    removalSnail.add(s);
                    GameManager.getGemManager().spawnGems((int)s.getX(false),(int)s.getY(false), 3);
                }

                for(Snail r:removalSnail){
                    snails.remove(r);
                }
                return true;
            }
        }

        // check jelly bullet collision
        for (Jelly j:jellies) {
            if (j.getRect().intersects(bu.getRect())) {
                j.isHit();
                j.setHealth(j.getHealth()-damage);
                if (j.getHealth() <= 0) {
                    AAdventure.getGame().getAlan().addCombo();
                    removalJelly.add(j);
                    // adding gems
                    GameManager.getGemManager().spawnGems((int)j.getX(false),(int)j.getY(false), 3);
                }

                for(Jelly r:removalJelly){
                    jellies.remove(r);
                }
                return true;
            }
        }

        // going through bats to check for damage and spawn gems
        for (Bat b: bats) {
            if (b.getRect().intersects(bu.getRect())) {
                b.setHealth(b.getHealth()-damage);
                if (b.getHealth() <= 0) {
                    AAdventure.getGame().getAlan().addCombo();
                    removalBat.add(b);
                    // adding gems
                    GameManager.getGemManager().spawnGems((int)b.getX(false),(int)b.getY(false), 3);
                }

                for(Bat r:removalBat){
                    bats.remove(r);
                }
                return true;
            }
        }

        // checking for skull taking damage
        for (Skull s: skulls) {
            if (s.getRect().intersects(bu.getRect())) {
                s.setHealth(s.getHealth()-damage);
                s.hit();
                if (s.getHealth() <= 0) {
                    AAdventure.getGame().getAlan().addCombo();
                    removalSkull.add(s);
                    // adding gems
                    GameManager.getGemManager().spawnGems((int)s.getX(false),(int)s.getY(false), 3);
                }

                for(Skull r:removalSkull){
                    skulls.remove(r);
                }
                return true;
            }
        }

        return false;
    }

    // getting bullet-block collision, accounts for gunpowder effects
    public boolean getCollision(Bullet b, Alan alan, Map map, Powerups powerups) {
        // blocks
        Block[][] blocks = map.getMap();
        int nextRow = b.getY(false,alan)/Util.BLOCKLENGTH+1;
        for (int r = nextRow-1; r < nextRow+2; r++) {
            for (int i = 0; i < map.getColumns(); i++) {
                int blockType = blocks[r][i].getType();
                // checking if block is shootable
                if ((blockType == Block.WALL || blockType == Block.BOX)) {
                    if (blocks[r][i].collide(b.getRect())) {
                        if (blockType == Block.BOX) {
                            blocks[r][i].setType(Block.AIR);
                            blastPlaces.add(0);
                            blastBlocks.add(blocks[r][i]);
                            if (powerups.getPower(Powerups.GUNPOWDER) == 1) {
                                // recursive method to find all nearest blocks to eliminate
                                gunpowderEffect(blocks, r, i);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // recursively find all blocks that are touching the one block that was shot at
    private void gunpowderEffect(Block[][] blocks, int row, int col) {
        blocks[row][col].setType(Block.AIR);
        blastPlaces.add(0);
        blastBlocks.add(blocks[row][col]);
        if (row > 0 && col < 8 && col > 0) {
            // finding all nearest blocks that are connected
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
                // animation complete
                blastBlocks.remove(i);
                blastPlaces.remove(i);
            }
        }
    }

    // getters and setters
    public String getName() {return name;}
    public int getDamage() {return damage;}
    public void setDamage(int damage) {this.damage = damage;}
    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity;}
    public void setSpeed(int speed) {this.speed = speed;}
}

// BULLET CLASS
class Bullet {
    private int y;
    private final int startY, width, height;
    private double x;
    private final double velX;
    private Image img;
    private final Rectangle rect;

    // contains velocity and positional data
    Bullet(int x, int y, int startY, Image img, double velX) {
        this.velX = velX;
        this.startY = startY;
        this.x = x;
        this.y = y;
        this.img = img;
        this.width = 12;
        this.height = 16;
        this.rect = new Rectangle(x,y,width, height);
    }

    // getters and setters
    public double getX(boolean adjusted) {return (adjusted ? x + Background.getWallLeftPos() : x);}
    public int getY(boolean adjusted, Alan alan) {return (adjusted ? y-alan.getOffset()+alan.getScreenOffset() : y);}
    public Rectangle getRect(){return rect;}
    public void setX(int x) {this.x = x; this.rect.setLocation((int)this.x, this.y);}
    public void setY(int y) {this.y = y; this.rect.setLocation((int)this.x, this.y);}
    public Image getImg() {return img;}
    public int getStartY() {return startY;}
    public double getVelX() {return velX;}

    // draws bullet
    public void draw(Graphics g) {
        g.drawImage(img, (int) x, y, null);
    }
}

