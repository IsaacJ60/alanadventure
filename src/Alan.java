import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

//TODO: MOVEMENT AND OFFSET
// PRIORITY - LOW-MEDIUM
//   DONE - PLATFORM COLLISION
//   DONE - acceleration
//   DONE - JUMP
//   DONE - calculate background position based on player offset
// > GRAPHICS:
//   DONE - finish other animations
//   DONE - flip animation for opposite direction
//   DONE - jump animation, 1 block gap, changing to idle

public class Alan {
    // CONSTANTS
    public static final int LEFT = 0, RIGHT = 1; // constants for direction
    public static final int IDLE = 0, WALK = 1, JUMP = 2, FALL = 3; // constants for state

    // STATES
    private int state = IDLE; // current state (e.g., idle, walk, fall, shoot) to change what animation is playing
    private int dir = RIGHT; // the direction alan is facing

    // PLAYER INFO AND STATS
    private static int width, height; // dimensions
    private final Rectangle alanRect; // hitbox
    private int x, y;
    private int health, maxHealth, healthProgress; // current health, health capacity, and progress to +1 maximum health

    // MOVEMENT LIMITERS AND VELOCITIES
    private boolean moveLeft = true, moveRight = true;

    private double velX, velY, jerk; // movement speed & screenoffset jerk
    private final double maxVelX, maxVelY, accelX, accelY; // velocity & acceleration
    private int nearestY;

    // SCREEN OFFSETS
    private int offset, screenOffset; // how far down alan has "travelled", subtracted from other game elements to give the effect that alan is falling

    // WEAPONS
    private Blaster weapon; // current weapon

    // ANIMATIONS
    private double animFrame; // the frame of the current animation being played
    ArrayList<Image> walk = new ArrayList<>();
    ArrayList<Image> rwalk = new ArrayList<>();
    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> ridle = new ArrayList<>();
    ArrayList<Image> fall = new ArrayList<>();
    ArrayList<Image> rfall = new ArrayList<>();
    ArrayList<Image> jump = new ArrayList<>();
    ArrayList<Image> shoot = new ArrayList<>();
    ArrayList<Image> rshoot = new ArrayList<>();
    // all animations
    ArrayList<ArrayList<Image>> allAnims = new ArrayList<>();

    // TIMERS
    Util.CustomTimer jumpTimer = new Util.CustomTimer();

    public Alan(int posX, int posY, Blaster weapon) {
        x = posX;
        y = posY;
        velX = 0;
        velY = 0;
        jerk = 0.2;
        width = 20;
        height = 30;
        this.health = 4;
        this.maxVelX = 10;
        this.maxVelY = 15;
        this.accelY = 1.0;
        this.accelX = 1.0;
        this.animFrame = 0;
        this.weapon = weapon;

        offset = 0;
        screenOffset = 0;

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
        for (int i = 0; i < 5; i++) {
            jump.add(new ImageIcon("src/assets/alan/jump/jump" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        fall.add(new ImageIcon("src/assets/alan/jump/jump4.png").getImage().getScaledInstance(30,height,Image.SCALE_DEFAULT));
        rfall.add(new ImageIcon("src/assets/alan/jump/m_jump4.png").getImage().getScaledInstance(30,height,Image.SCALE_DEFAULT));
        for (int i = 0; i < 6; i++) {
            shoot.add(new ImageIcon("src/assets/alan/shoot/shoot" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 6; i++) {
            rshoot.add(new ImageIcon("src/assets/alan/shoot/m_shoot" + i + ".png").getImage().getScaledInstance(30, height,Image.SCALE_DEFAULT));
        }
        allAnims.add(idle);
        allAnims.add(ridle);
        allAnims.add(walk);
        allAnims.add(rwalk);
        allAnims.add(jump);
        allAnims.add(jump);
        allAnims.add(fall);
        allAnims.add(rfall);
        allAnims.add(shoot);
        allAnims.add(rshoot);
    }

    public int getDir() {return dir;}
    public int getNearestY() {return nearestY;}
    public void setX(int p) {x = p;} // sets x
    public void setY(int p) {y = p;} // sets y
    public int getVelY() {return (int)velY;}
    public int getState() {return state;}
    public int getHealth() {return health;} // gets hp
    public void setHealth(int health) {this.health = health;} // sets hp
    public int getOffset() {return offset;} // gets offset
    public int getScreenOffset() {return screenOffset;} // gets screenOffset
    public Blaster getWeapon() {return weapon;} // gets current weapon
    public void setWeapon(Blaster weapon) {this.weapon = weapon;} // sets current weapon
    public void setWeaponSpeed(int s) {weapon.setSpeed(s);}

    public int getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return this.x + Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return x;
        }
    }

    public int getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return this.y-offset+screenOffset;
        } else {
            return this.y;
        }
    }

    public void changeState(int MODE, int d, boolean forceChange) { // changes state
        if (state != MODE || d != dir) {
            animFrame = 0;
        }
        state = MODE;
        if (forceChange) {
            animFrame = 0;
        }
    }

    public void move(boolean[] keys, Graphics g, Map map, Powerups powerups) {
        getCollision(g,this, map); // getting collision between player and blocks
        alanRect.setLocation(x+5,y); // setting rect location

        // allow jump only if not jumping or falling and if space pressed
        if (keys[Util.space] && !GamePanel.getPrevSpaced() && state != JUMP && state != FALL) {
            changeState(JUMP, dir, false);
        }

        // left right movement keys pressed
        if (keys[Util.a] || keys[Util.d]) {
            // checking if max speed not yet reached
            if (velX < maxVelX) {
                velX += accelX;
            }

            // if "A" key pressed and player allowed to move left
            if (keys[Util.a] && moveLeft) {
                // checking if switched direction, reset velocity
                if (dir == RIGHT) {
                    velX = 0;
                }
                // changing direction state to left
                dir = LEFT;
                // changing horizontal position towards left
                x -= velX;
                // ensuring x doesn't go out of bounds
                x = Math.max(x, 0);
            }

            // if "D" key pressed and player allowed to move right
            if (keys[Util.d] && moveRight) {
                // checking if switched direction, reset velocity
                if (dir == LEFT) {
                    velX = 0;
                }
                // changing direction state to right
                dir = RIGHT;
                // changing horizontal position towards right
                x += velX;
                // making sure player doesn't go out of bounds
                x = Math.min(x, Background.getWallRightPos()-Background.getWallLeftPos()-Background.getWallWidth()-width-10);
            }

            // if moving left right but not in air, walking on ground
            if (velY == 0 && state != JUMP && state != FALL) {
                changeState(WALK, dir, false);
            }
        } else if (state != JUMP && state != FALL) {
            // if not pressing "A" or "D", and not in air, idling
            velX = 0;
            changeState(IDLE, dir, false);
        }

        // JUMPING, logic inside function determines whether jump is performed or not
        jump();

        // increasing game-y value of player and offset
//        System.out.println("Y: " + (y-GamePanel.getHEIGHT()/2-50+100) + " | " + "OFFSET: " + offset);
        y+=(int)velY;
        offset+=(int)velY;

        // applying additional offset to make stopping and accelerating smoother
        if (screenOffset < 47.8 && velY > 0) {
            if (jerk < 3) {
                jerk += 0.1;
            }
            screenOffset+=jerk;
        }

        // setting hitbox rectangle new x and y values
        alanRect.setLocation(x+5,y);

        // SHOOTING, logic inside
        if (weapon.shoot(keys, (int) velY, g, this)) {
            changeState(FALL, dir, true); // just to reset animframe
            velY-=velY*1.2;
        }
        weapon.animation(g,this, map, powerups);
    }

    public void jump() {
        if (state == JUMP) {
            if (jumpTimer.getElapsedTime() > 0.5) {
                velY -= 12;
                jumpTimer.restart();
            } else {
                changeState(IDLE, dir, false);
            }
        }
    }

    // get collision on 4 sides of blocks
    // instead of scanning through all blocks or a general area of blocks,
    // getCollision instead runs through specific rows of blocks that narrow
    // down the search field tremendously
    // each check for each side of blocks is performed separately
    public void getCollision(Graphics g, Alan alan, Map map) {
        Block[][] blocks = map.getMap();
        // getting rows with same y values as player
        int prevRow = getY(false)/Util.BLOCKLENGTH;
        int nextRow = getY(false)/Util.BLOCKLENGTH+1;

        //HINT: CHANGING LEVELS HERE
        // USE GAMEMANAGER TO SWITCH TO NEXT LEVEL WHEN ALAN REACHES CERTAIN POINT
        if (nextRow == map.getRows()-15) {
            GameManager.toLevel(Util.getLevel()+1);
        }

        // variables to track the nearest block distances
        int nearestBlockY = 100, nearestAboveY = 100, nearestLeftX = 100, nearestRightX = 100, snapX = 0;

        // top down collision
        for (int i = 0; i < map.getColumns(); i++) {
            int blockType = blocks[nextRow][i].getType();
            if (blockType != Block.AIR) { // if block isn't air, check for distance to player
                // only check top when velocity is positive (going down)
                if ((blockType == Block.WALL || blockType == Block.BOX || blockType == Block.PLAT) && velY >= 0) {
                    // checkig player has a chance of colliding with block (x value within range of block x values)
                    if (x+width > blocks[nextRow][i].getX(false) && x < blocks[nextRow][i].getX(false) + Util.BLOCKLENGTH) {
                        if (blocks[nextRow][i].getY(false, alan)-y < nearestBlockY) {
                            // updating nearest distance
                            nearestBlockY = Math.abs(blocks[nextRow][i].getY(false, alan)-(y+height));
                        }
                    }
                }
            }
        }
        // if nearest distance is less than or equal to next velocity increment, stop player
        if (nearestBlockY <= velY+5) {

            // set velocity in y-dir to be 0
            velY = 0;

            // if snapping is needed (player went too far passed top of block and is now somewhat stuck in block)
            if (y+height > blocks[nextRow][0].getY(false, alan)) {
                y = blocks[nextRow][0].getY(false, alan)-height;
                //TODO: make offset slowly reach the new value instead of instantly adding (avoid stuttering)
                offset -= nearestBlockY;
            }

            // if the screen offset hasn't caught up yet (hasn't reached 0 and "reset")
            if (screenOffset > 0) {
                // change jerk to create smooth motion
                if (jerk > 0) {
                    jerk -= 0.1;
                }
                // decrease screen offset to "reset" camera position
                screenOffset -= jerk;
            }

            // if originally falling, change state to idle now that player is grounded
            if (state == FALL) {
                changeState(IDLE, dir, false);
            }
        } else { // in the case that the player has not reached block/ground
            if (velY == 0) { // starts falling velocity at 3 (if velocity is 0, when player just starts to fall)
                velY = 5;
            }
            else if (velY < maxVelY) { // increase velocity if not reached max y vel
                velY += accelY;
            }
            changeState(FALL, dir, false); // change state to falling
        }
        nearestY = nearestBlockY;

        // bottom up collision checking
        for (int i = 0; i < map.getColumns(); i++) {
            int blockType = blocks[prevRow][i].getType();
            if (blockType != Block.AIR) {
                // only check bottom collision in solid blocks and when going upwards in y-dir
                if ((blockType == Block.WALL || blockType == Block.BOX) && velY < 0) {
                    // checking if block is in collision boundary of Alan
                    if (x+width > blocks[prevRow][i].getX(false) && x < blocks[prevRow][i].getX(false) + Util.BLOCKLENGTH) {
                        if (Math.abs(y-(blocks[prevRow][i].getY(false,alan)+Util.BLOCKLENGTH)) < nearestAboveY) {
                            // setting nearest distance
                            nearestAboveY = Math.abs(y-(blocks[prevRow][i].getY(false,alan)+Util.BLOCKLENGTH));
                        }
                    }
                }
            }
        }
        // flipping y-dir and decreasing by 1/2
        if (nearestAboveY <= 15) {
            if (velY < 0) {
                velY = Math.abs(velY)/2;
//                offset += 6;
            }
        }

        // right to left collision checking
        // similar logic to top-down collisions
        // differences:
        // - checks multiple rows, player able to collide with multiple rows of block
        // - checks on y-axis instead of x
        // - snapping to x-axis isn't as bug prone as snapping to y because no offset
        for (int r = nextRow-2; r < (state != WALK ? nextRow+1 : nextRow); r++) {
            for (int i = map.getColumns()-1; i >= 0; i--) {
                Block block = blocks[r][i];
                if (block.getX(false) < x) {
                    if (block.getType() != Block.AIR) {
                        if (block.getType() == Block.BOX || block.getType() == Block.WALL) {
                            if (y+height > block.getY(false,alan) && y < block.getY(false,alan) + Util.BLOCKLENGTH) {
                                if (x-(block.getX(false)+Util.BLOCKLENGTH) < nearestLeftX) {
                                    nearestLeftX = Math.abs(x-(block.getX(false)+Util.BLOCKLENGTH));
                                    snapX = block.getX(false)+Util.BLOCKLENGTH+1;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (nearestLeftX <= velX) {
            moveLeft = false;
            x = snapX;
        } else {
            moveLeft = true;
        }

        // left to right collision checking - SAME AS RIGHT TO LEFT BUT X VALUES TO CHECK ARE DIFFERENT
        for (int r = nextRow-2; r < (state != WALK ? nextRow+1 : nextRow); r++) {
            for (int i = 0; i < map.getColumns(); i++) {
                Block block = blocks[r][i];
                if (block.getX(false) > x) {
                    if (block.getType() != Block.AIR) {
                        if (block.getType() == Block.BOX || block.getType() == Block.WALL) {
                            if (y + height > block.getY(false,alan) && y < block.getY(false,alan) + Util.BLOCKLENGTH) {
                                if (Math.abs(block.getX(false) - (x + width)) < nearestRightX) {
                                    nearestRightX = Math.abs(block.getX(false) - (x + width));
                                    snapX = block.getX(false) - width - 5;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (nearestRightX <= velX) {
            moveRight = false;
            x = snapX;
        } else {
            moveRight = true;
        }
    }

    // drawing alan in different states and directions
    public void draw(Graphics g, boolean[] keys, Map map, Powerups powerups) { //

        move(keys, g, map, powerups);

        if (weapon.isAlanShoot()) {
            if ((int) animFrame == shoot.size()-1) {
                animFrame = 0;
                weapon.setAlanShoot(false);
            } else {
                animFrame+=0.8; // frame should update every 1/5 ticks
            }
        } else if (state == IDLE) {
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
        else if (state == JUMP) {
            if ((int) animFrame == jump.size()-1) {
                animFrame = 0;
            } else {
                animFrame += 0.6; // frame should update every 2/5 ticks
            }
        }

        // drawing animation based on direction
        if (dir == LEFT) {
            if (weapon.isAlanShoot()) {
                g.drawImage(rshoot.get((int)animFrame),getX(true)-5,getY(true)+3,null);
            } else {
                g.drawImage(allAnims.get(state*2+1).get((int)animFrame),getX(true)-5,getY(true)+3,null);
            }
        } else {
            if (weapon.isAlanShoot()) {
                g.drawImage(shoot.get((int)animFrame),getX(true)-5,getY(true)+3,null);
            } else {
                g.drawImage(allAnims.get(state*2).get((int)animFrame),getX(true)-5,getY(true)+3,null);
            }
        }

        /*
        code to draw hitboxes
        g.setColor(Color.RED);
        g.drawLine(0,getY(true),GamePanel.getWIDTH(),getY(true));
        g.drawLine(0,getY(true)+height,GamePanel.getWIDTH(),getY(true)+height);
        g.drawRect((int) alanRect.getX()+Background.getWallLeftPos()+Background.getWallWidth(), (int) alanRect.getY()-offset+screenOffset, alanRect.width, height);
        g.setColor(Color.YELLOW);
        g.drawLine(0,GamePanel.getHEIGHT()/2-50+height,900,GamePanel.getHEIGHT()/2-50+height);
        */
    }
}

