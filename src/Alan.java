import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

//TODO: MOVEMENT AND OFFSET
// PRIORITY - LOW-MEDIUM
//   IN-PROGRESS - acceleration
//   INCOMPLETE - JUMP
//   INCOMPLETE - PLATFORM COLLISION
//   DONE - calculate background position based on player offset
// > GRAPHICS:
//   INCOMPLETE - finish other animations
//   DONE - flip animation for opposite direction

public class Alan {
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D, space = KeyEvent.VK_SPACE; // constants for keyboard input
    private static final int LEFT = 0, RIGHT = 1; // constants for direction
    public static final int IDLE = 0, WALK = 1, JUMP = 2, FALL = 3; // constants for state
    private static int x, y; // position of alan relative to game window
    private int width, height; // dimensions
    private int health, maxHealth, healthProgress; // current health, health capacity, and progress to +1 maximum health
    private boolean moveLeft = true, moveRight = true; // whether alan can move left or right
    private double velX, velY, maxVelX; // movement speed
    private double accelX, accelY, jerk; // acceleration for gravity
    private static int offset, screenOffset; // how far down alan has "travelled", subtracted from other game elements to give the effect that alan is falling
    private Blaster weapon; // current weapon
    private double animFrame; // the frame of the current animation being played
    private int state = IDLE; // current state (e.g., idle, walk, fall, shoot) to change what animation is playing
    private int dir = LEFT; // the direction alan is facing

    // arraylists for frames of every animation
    ArrayList<Image> walk = new ArrayList<>();
    ArrayList<Image> rwalk = new ArrayList<>();
    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> ridle = new ArrayList<>();
    ArrayList<Image> fall = new ArrayList<>();
    ArrayList<Image> jump = new ArrayList<>();
    ArrayList<ArrayList<Image>> allAnims = new ArrayList<>();

    private Rectangle alanRect; // hitbox

    public Alan(int x, int y, Blaster weapon) {
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 30;
        this.health = 4;
        this.velX = 5;
        this.velY = 10;
        this.accelX = 1.5;
        this.accelY = 0.1;
        this.maxVelX = 10;
        this.jerk = 0.1;
        this.weapon = weapon;
        this.animFrame = 0;

        this.offset = 0;
        this.screenOffset = 0;

        this.alanRect = new Rectangle(x+5,y,width,height); // adding 5 because the frames for alan's animation has transparency on the sides

        for (int i = 0; i < 7; i++) { // adding all frames for each animation and scaling all images to 30x30 for 1:1 aspect ratio
            idle.add(new ImageIcon("src/assets/alan/idle/idle" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 7; i++) {
            ridle.add(new ImageIcon("src/assets/alan/idle/m_idle" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 7; i++) {
            walk.add(new ImageIcon("src/assets/alan/walk/walk" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 7; i++) {
            rwalk.add(new ImageIcon("src/assets/alan/walk/m_walk" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 9; i++) {
            jump.add(new ImageIcon("src/assets/alan/jump/jump" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        allAnims.add(idle);
        allAnims.add(ridle);
        allAnims.add(walk);
        allAnims.add(rwalk);
        allAnims.add(jump);
        allAnims.add(jump);
    }

    public static int getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return x;
        }
    }

    public void setX(int x) {
        this.x = x;
    } // sets x


    public static int getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-offset+screenOffset;
        } else {
            return y;
        }
    }

    public void setY(int p) {y = p;} // sets y
    public int getHealth() {return health;} // gets hp
    public void setHealth(int health) {this.health = health;} // sets hp
    public static int getOffset() {return offset;} // gets offset
    public static int getScreenOffset() {return screenOffset;} // gets screenOffset
    public Blaster getWeapon() {return weapon;} // gets current weapon
    public void setWeapon(Blaster weapon) {this.weapon = weapon;} // sets current weapon

    public void changeState(int MODE, int d) { // changes state
        if (state != MODE || d != dir) {
            animFrame = 0;
        }
        state = MODE;
    }


    //TODO: JUMP
    public void move(boolean[] keys, Graphics g) {
        getCollision(MapList.getBlocks(),g); // getting collision between player and blocks
        alanRect.setLocation(x+5,y); // setting rect location
        //HINT: make sure to check states if they are giving bugs
        if (keys[space] && state != JUMP) {
            changeState(JUMP, dir);
        }
        if (keys[a] || keys[d]) {
            if (velX < maxVelX) {
                velX += accelX;
            }
            if (keys[a] && moveLeft) {
                if (dir == RIGHT) {
                    velX = 0;
                }
                dir = LEFT;
                x -= velX;
                x = Math.max(x, 0);
            }
            if (keys[d] && moveRight) {
                if (dir == LEFT) {
                    velX = 0;
                }
                dir = RIGHT;
                x += velX;
                x = Math.min(x, Background.getWallRightPos()-Background.getWallLeftPos()-Background.getWallWidth()-width);
            }
            if (state != JUMP) {
                changeState(WALK, dir);
            }
        } else if (state != JUMP) { // TODO: change to else if and put conditions
            changeState(IDLE, dir);
            velX = 0;
        }
        if (state == JUMP) {
            jump();
        }
        y+=(int)velY;
        offset+=(int)velY;
        if (screenOffset < 50 && velY != 0) {
            if (jerk < 3) {
                jerk += 0.2;
            }
            screenOffset+=(int)jerk;
        }
        alanRect.setLocation(x+5,y);
    }

    public void jump() {
        if (velY == 0.0) {
            changeState(IDLE, dir); //TODO: make function to not changestate but detectstate
        } else if (velY < 0) {
            System.out.println("GOING UP");
        } else if (velY > 0) {
            System.out.println("GOING DOWN");
        }
    }

    public void getCollision(Block[][] blocks, Graphics g) {
        int nextRow = getY(false)/Util.BLOCKLENGTH+1;
        int nearestBlockY = 100, nearestLeftX = 100, nearestRightX = 100;
        for (int i = 0; i < Map.getColumns(); i++) {
            int blockType = blocks[nextRow][i].getType();
            if (blockType != Block.AIR) {
                if (blockType == Block.PLAT) {
                    //TODO: CUSTOM PLATFORM COLLISION DETECTION
                } else if (blockType == Block.WALL || blockType == Block.BOX) {
                    if (x+width > blocks[nextRow][i].getX(false) && x < blocks[nextRow][i].getX(false) + Util.BLOCKLENGTH) {
                        if (blocks[nextRow][i].getY(false)-y < nearestBlockY) {
                            nearestBlockY = blocks[nextRow][i].getY(false)-y;
                        }
                    }
                }

            }
        }
        if (nearestBlockY <= 10) {
            if (screenOffset > 0) {
                if (jerk > 0) {
                    jerk -= 0.1;
                }
                screenOffset -= (int)jerk;
            }
            velY = 0;
        } else {
            if (velY == 0) {
                velY = 3;
            } else if (velY < 10) {
                velY += accelY*5;
            }
        }
        for (int r = nextRow-1; r < nextRow; r++) {
            for (int i = Map.getColumns()-1; i >= 0; i--) {
                if (blocks[r][i].getX(false) < x) {
                    if (blocks[r][i].getType() != Block.AIR) {
                        if (y+height > blocks[r][i].getY(false) && y < blocks[r][i].getY(false) + Util.BLOCKLENGTH) {
                            g.setColor(Color.CYAN);
                            g.drawRect(blocks[r][i].getX(true), blocks[r][i].getY(true), Util.BLOCKLENGTH, Util.BLOCKLENGTH);
                            if (x-(blocks[r][i].getX(false)+Util.BLOCKLENGTH) < nearestLeftX) {
                                nearestLeftX = Math.abs(x-(blocks[r][i].getX(false)+Util.BLOCKLENGTH));
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (nearestLeftX <= velX) {
            moveLeft = false;
        } else {
            moveLeft = true;
        }

        for (int r = nextRow-1; r < nextRow; r++) {
            for (int i = 0; i < Map.getColumns(); i++) {
                if (blocks[r][i].getX(false) > x) {
                    if (blocks[r][i].getType() != Block.AIR) {
                        if (y+height > blocks[r][i].getY(false) && y < blocks[r][i].getY(false) + Util.BLOCKLENGTH) {
                            g.setColor(Color.CYAN);
                            g.drawRect(blocks[r][i].getX(true), blocks[r][i].getY(true), Util.BLOCKLENGTH, Util.BLOCKLENGTH);
                            if (blocks[r][i].getX(false)-x+width < nearestRightX) {
                                nearestRightX = Math.abs(blocks[r][i].getX(false)-(x+width));
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (nearestRightX <= velX) {
            moveRight = false;
        } else {
            moveRight = true;
        }
    }

    public void draw(Graphics g, boolean[] keys) { //
        move(keys, g);
        if (state == IDLE) {
            if ((int) animFrame == idle.size()-1) {
                animFrame = 0;
            } else {
                animFrame+=0.2; // frame should update every 1/5 ticks
            }
        } else if (state == WALK) {
            if ((int) animFrame == walk.size()-1) {
                animFrame = 0;
            } else {
                animFrame += 0.4; // frame should update every 2/5 ticks
            }
        }
        if (dir == LEFT) {
            g.drawImage(allAnims.get(state*2+1).get((int)animFrame),getX(true),getY(true),null);
        } else {
            g.drawImage(allAnims.get(state*2).get((int)animFrame),getX(true),getY(true),null);
        }
        g.setColor(Color.RED);
        g.drawRect((int) alanRect.getX()+Background.getWallLeftPos()+Background.getWallWidth(), (int) alanRect.getY()-offset+screenOffset, alanRect.width, alanRect.height);
    }
}

class Blaster {
    String name;
    int damage, capacity, speed;
    ArrayList<Image> shootAnim;

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