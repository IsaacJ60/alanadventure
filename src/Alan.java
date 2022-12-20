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
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D, space = KeyEvent.VK_SPACE;

    private boolean moveLeft = true, moveRight = true;

    int x, y;
    int width, height;
    int health, speed;
    double velocity, accel = 0.1, jerk = 0.1;
    static int offset, screenoffset;
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

    public Alan(int x, int y, int health, int speed, double velocity, Blaster weapon) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
        this.velocity = velocity;
        this.weapon = weapon;
        animFrame = 0;
        this.width = 20;
        this.height = 30;

        offset = 0;

        alanRect = new Rectangle(x+5,y,width,height);

        for (int i = 0; i < 7; i++) {
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
        allAnims.add(idle);
        allAnims.add(ridle);
        allAnims.add(walk);
        allAnims.add(rwalk);
    }

    public static int getOffset() {
        return offset;
    }

    public int getX(boolean adjusted) {
        if (adjusted) {
            return x + Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return x;
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY(boolean adjusted) {
        if (adjusted) {
            return y-offset+screenoffset;
        } else {
            return y;
        }
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

    //TODO: JUMP
    public void move(boolean[] keys, Graphics g) {
//        System.out.println(jerk);
        getCollision(MapList.getBlocks(),g);
        alanRect.setLocation(x+5,y);
        //HINT: make sure to check states if they are giving bugs
        if (keys[space] && state != JUMP) {
            changeState(JUMP, dir);
        }
        if (keys[a] || keys[d]) {
            if (speed < 10) {
                speed += 1;
            }
            if (keys[a] && moveLeft) {
                if (dir == RIGHT) {
                    speed = 0;
                }
                dir = LEFT;
                x -= speed;
                x = Math.max(x, 0);
            }
            if (keys[d] && moveRight) {
                if (dir == LEFT) {
                    speed = 0;
                }
                dir = RIGHT;
                x += speed;
                x = Math.min(x, Background.getWallRightPos()-Background.getWallLeftPos()-Background.getWallWidth()-width);
            }
            if (state != JUMP) {
                changeState(WALK, dir);
            }
        } else { // TODO: change to else if and put conditions
            changeState(IDLE, dir);
            speed = 0;
        }
        y+=(int)velocity;
        offset+=(int)velocity;
        if (screenoffset < 50 && velocity != 0) {
            if (jerk < 3) {
                jerk += 0.1;
            }
            screenoffset+=(int)jerk;
        }
        alanRect.setLocation(x+5,y);
    }

    public void changeState(int MODE, int d) {
        if (state != MODE || d != dir) {
            animFrame = 0;
        }
        state = MODE;
    }

    public void jump() {
        System.out.println("JUMP!");
    }

    public void getCollision(Block[][] blocks, Graphics g) {
        int nextRow = getY(false)/Util.BLOCKLENGTH+1;
        int mostY = 100, mostLeftX = 100, mostRightX = 100;
        for (int i = 0; i < Map.getColumns(); i++) {
            int blockType = blocks[nextRow][i].getType();
            if (blockType != Block.AIR) {
                if (blockType == Block.PLAT) {
                    //TODO: CUSTOM PLATFORM COLLISION DETECTION
                } else if (blockType == Block.WALL || blockType == Block.BOX) {
                    if (x+width > blocks[nextRow][i].getX(false) && x < blocks[nextRow][i].getX(false) + Util.BLOCKLENGTH) {
                        if (blocks[nextRow][i].getY(false)-y < mostY) {
                            mostY = blocks[nextRow][i].getY(false)-y;
                        }
                    }
                }

            }
        }
        if (mostY <= 10) {
            if (screenoffset > 0) {
                if (jerk > 0) {
                    jerk -= 0.1;
                }
                screenoffset -= (int)jerk;
            }
            velocity = 0;
        } else {
            if (velocity == 0) {
                velocity = 3;
            } else if (velocity < 10) {
                velocity += accel*5;
            }
        }
        for (int r = nextRow-1; r < nextRow; r++) {
            for (int i = Map.getColumns()-1; i >= 0; i--) {
                if (blocks[r][i].getX(false) < x) {
                    if (blocks[r][i].getType() != Block.AIR) {
                        if (y+height > blocks[r][i].getY(false) && y < blocks[r][i].getY(false) + Util.BLOCKLENGTH) {
                            g.setColor(Color.CYAN);
                            g.drawRect(blocks[r][i].getX(true), blocks[r][i].getY(true), Util.BLOCKLENGTH, Util.BLOCKLENGTH);
                            if (x-(blocks[r][i].getX(false)+Util.BLOCKLENGTH) < mostLeftX) {
                                mostLeftX = Math.abs(x-(blocks[r][i].getX(false)+Util.BLOCKLENGTH));
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (mostLeftX <= speed) {
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
                            if (blocks[r][i].getX(false)-x+width < mostRightX) {
                                mostRightX = Math.abs(blocks[r][i].getX(false)-(x+width));
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (mostRightX <= speed) {
            moveRight = false;
        } else {
            moveRight = true;
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
            //FIXME: not yet added jump animation so index out of bounds (DON'T JUMP!)
            g.drawImage(allAnims.get(state*2+1).get((int)animFrame),getX(true),getY(true),null);
        } else {
            g.drawImage(allAnims.get(state*2).get((int)animFrame),getX(true),getY(true),null);
        }
        g.setColor(Color.RED);
        g.drawRect((int) alanRect.getX()+Background.getWallLeftPos()+Background.getWallWidth(), (int) alanRect.getY()-offset+screenoffset, alanRect.width, alanRect.height);
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

