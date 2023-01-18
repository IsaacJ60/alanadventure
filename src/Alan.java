import javax.swing.*;
import java.awt.*;
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
    public static final int IDLE = 0, WALK = 1, JUMP = 2, FALL = 3; // constants for state

    // STATES
    private int state = IDLE; // current state (e.g., idle, walk, fall, shoot) to change what animation is playing
    private int dir = Util.RIGHT; // the direction alan is facing
    private boolean invul;

    // PLAYER INFO AND STATS
    private final int width, height; // dimensions
    private final Rectangle alanRect; // hitbox
    private int x, y;
    private int health, maxHealth, healthProgress; // current health, health capacity, and progress to +1 maximum health

    // MOVEMENT UTIL
    private int keyLeft, keyRight, keyJump;

    // MOVEMENT LIMITERS AND VELOCITIES
    private boolean moveLeft = true, moveRight = true, hop;

    private double velX, velY, jerk; // movement speed & screenoffset jerk
    private final double maxVelX, maxVelY, accelX, accelY; // velocity & acceleration
    private int nearestY;

    // SCREEN OFFSETS
    private int offset, screenOffset; // how far down alan has "travelled", subtracted from other game elements to give the effect that alan is falling

    // WEAPONS
    private Blaster weapon; // current weapon

    // ANIMATIONS
    private double animFrame; // the frame of the current animation being played
    private static final int animIdle = 0, animRun = 1, animFall = 2, animAir = 3, animShoot = 4;
    // all animations
    ArrayList<ArrayList<Image>> allAnims;

    // TIMERS
    Util.CustomTimer jumpTimer = new Util.CustomTimer();
    Util.CustomTimer invulTimer = new Util.CustomTimer();

    public Alan(int x, int y, Blaster weapon, int health, int maxHealth, int healthProgress, int keyLeft, int keyRight, int keyJump) {
        // position and movement
        this.x = x;
        this.y = y;
        this.velX = 0;
        this.velY = 0;
        this.jerk = 0.2;
        this.width = 20;
        this.height = 30;
        this.maxVelX = 7;
        this.maxVelY = 14;
        this.accelY = 1;
        this.accelX = 0.7;
        this.animFrame = 0;
        this.hop = true;

        //movement keybinds
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyJump = keyJump;

        //hitbox
        this.alanRect = new Rectangle(x+5,y,width,height); // adding 5 because the frames for alan's animation has transparency on the sides

        //stats
        this.health = health;
        this.maxHealth = maxHealth;
        this.healthProgress = healthProgress;
        this.invul = false;
        this.weapon = weapon;

        // offset; screenlocation
        this.offset = 0;
        this.screenOffset = 0;

        // loading animation images
        String[] animationFileNames = {"idle/alanIdleL", "idle/alanIdleR", "run/alanRunL", "run/alanRunR", "air/alanAirL",
                "air/alanAirR", "air/alanAirL4", "air/alanAirR4", "shoot/alanShootL", "shoot/alanShootR"};
        int[] animationFrameCounts = new int[]{4, 4, 8, 8, 5, 5, 1, 1, 4, 4};
        int[] noframe = new int[]{0,0,0,0,0,0,1,1,0,0};

        allAnims = loadAnimations("src/assets/alan/", animationFileNames, animationFrameCounts, noframe);
    }

    // loading animations function
    public ArrayList<ArrayList<Image>> loadAnimations(String path, String[] animationFileNames, int[] animationFrameCounts, int[] noframe) {
        ArrayList<ArrayList<Image>> animations = new ArrayList<>();
        for (int i = 0; i < animationFileNames.length; i++) {
            ArrayList<Image> tmp = new ArrayList<>();
            for (int frame = 0; frame < animationFrameCounts[i]; frame++) {
                tmp.add(new ImageIcon(path + animationFileNames[i] + (noframe[i] == 0 ? frame : "") + ".png").getImage());
                tmp.set(frame, tmp.get(frame).getScaledInstance((tmp.get(frame).getWidth(null)*2), (tmp.get(frame).getHeight(null)*2), Image.SCALE_DEFAULT));
            }
            animations.add(tmp);
        }
        return animations;
    }

    // getters and setters
    public int getDir() {return dir;}
    public int getNearestY() {return nearestY;}
    public void setX(int p) {x = p;} // sets x
    public void setY(int p) {y = p;} // sets y
    public int getVelY() {return (int)velY;}
    public int getState() {return state;}
    public int getHealth() {return health;} // gets hp
    public int getMaxHealth() {return maxHealth;} // sets hp
    public int getHealthProgress() {return healthProgress;}
    public int getOffset() {return offset;} // gets offset
    public int getScreenOffset() {return screenOffset;} // gets screenOffset
    public Blaster getWeapon() {return weapon;} // gets current weapon
    public void setWeapon(Blaster weapon) {this.weapon = weapon;} // sets current weapon
    public void setWeaponSpeed(int s) {weapon.setSpeed(s);}
    public double getVelX() {return velX;}
    public void setHealth(int h) {health = h;}
    public void setHealthProgress(int h) {healthProgress = h;}
    public void setMaxHealth(int h) {maxHealth = h;}
    public int getKeyLeft() {return keyLeft;}
    public int getKeyRight() {return keyRight;}
    public int getKeyJump() {return keyJump;}
    public void setKeyLeft(int k) {keyLeft = k;}
    public void setKeyRight(int k) {keyRight = k;}
    public void setKeyJump(int k) {keyJump = k;}
    public int getX(boolean adjusted) {return (adjusted ? x + Background.getWallLeftPos() : x);}
    public int getY(boolean adjusted) {return (adjusted ? y-offset+screenOffset : y);}
    public Rectangle getRect(){return new Rectangle(x,y,width,height);}
    public Rectangle getBoots(){return new Rectangle(x,y+height,width,1);}

    public void changeState(int MODE, int d, boolean forceChange) { // changes state
        if (state != MODE || d != dir) {
            animFrame = 0;
        }
        state = MODE;
        if (forceChange) {
            animFrame = 0;
        }
    }

    // return int represents walls that player colliding with
    public int move(boolean[] keys, Graphics g, Map map, Powerups powerups, EnemyManager enemies) {
        getEnemyCollision(enemies.getSnakes(), enemies.getSnails(), enemies.getJellies()); // collision between alan and snakes
        getCollision(g,this, map); // getting collision between player and blocks
        alanRect.setLocation(x+5,y); // setting rect location
        boolean wallCollideLeft = false, wallCollideRight = false;

        // allow jump only if not jumping or falling and if space pressed
        if (AAdventure.getCurrPanel().equals("GAME")) {
            if (keys[keyJump] && !AAdventure.getGame().getPrevSpaced() && state != JUMP && state != FALL) {
                changeState(JUMP, dir, false);
            }
        } else {
            if (keys[keyJump] && state != JUMP && state != FALL) {
                changeState(JUMP, dir, false);
            }
        }

        // left right movement keys pressed
        if (keys[keyLeft] || keys[keyRight]) {

            // accelerate when max speed not reached
            velX = accelerate(velX, maxVelX, accelX);

            // if "A" key pressed and player allowed to move left
            if (keys[keyLeft] && moveLeft) {
                // checking if switched direction, reset velocity
                if (dir == Util.RIGHT) {
                    velX = 0;
                }
                // changing direction state to left
                dir = Util.LEFT;
                // changing horizontal position towards left
                x -= velX;
                // ensuring x doesn't go out of bounds
                if (Math.max(x,0) == 0) {
                    wallCollideLeft = true;
                }
            }

            // if "D" key pressed and player allowed to move right
            if (keys[keyRight] && moveRight) {
                // checking if switched direction, reset velocity
                if (dir == Util.LEFT) {
                    velX = 0;
                }
                // changing direction state to right
                dir = Util.RIGHT;
                // changing horizontal position towards right
                x += velX;
                // making sure player doesn't go out of bounds
                if (Math.min(x,Background.getWallRightPos()-Background.getWallLeftPos()-width-10) != x) {
                    wallCollideRight = true;
                }
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
        y+=(int)velY;
        offset+=(int)velY;

        // applying additional offset to make stopping and accelerating smoother
        if (screenOffset < 47.8 && velY > 0) {
            if (jerk < 3) { // not actually jerk
                jerk += 0.1;
            }
            screenOffset+=jerk;
        }

        // setting hitbox rectangle new x and y values
        alanRect.setLocation(x+5,y);

        shoot(keys, g, map, powerups, enemies);

        // return int of wall side that player is touching
        if (wallCollideLeft) {
            return Util.LEFT;
        } else if (wallCollideRight) {
            return Util.RIGHT;
        } else {
            return -1;
        }
    }

    public double accelerate(double velX, double maxVelX, double accelX) {
        if (velX < maxVelX) {
            return velX + accelX;
        }
        return velX;
    }

    public void jump() {
        if (state == JUMP) {
            if (jumpTimer.getElapsedTime() > 0.5) {
                velY -= 14;
                jumpTimer.restart();
            } else {
                changeState(IDLE, dir, false);
            }
        }
    }

    public void shoot(boolean[] keys, Graphics g, Map map, Powerups powerups, EnemyManager enemies) {
        // SHOOTING, logic inside
        if (weapon.shoot(keys, (int) velY, g, this)) {
            changeState(FALL, dir, true); // just to reset animframe
            velY-=velY*1.2;
        }
        weapon.animation(g,this, map, powerups, enemies);
    }

    public void getEnemyCollision(ArrayList<Snake> snakes, ArrayList<Snail> snails, ArrayList<Jelly> jellies) {
        ArrayList<Snake> removalSnake = new ArrayList<>();
        ArrayList<Jelly> removalJelly = new ArrayList<>();

        for (Snake s : snakes) {
            if (getRect().intersects(s.getRect())) {
                if(getBoots().intersects(s.getRect()) && velY > 0 && y+height > s.getY(false)){
                    weapon.setAmmo(weapon.getCapacity());
                    removalSnake.add(s);
                    velY = -8;
                    GameManager.getGemManager().spawnGems((int)s.getX(false),(int)s.getY(false), 3);
                }
                else {
                    if (invulTimer.getElapsedTime() >= 2) {
                        health--;
                        invulTimer.restart();
                    }
                }
            }
        }

        for (Snail s : snails) {
            if (getRect().intersects(s.getRect())) {
                if (invulTimer.getElapsedTime() >= 2) {
                    health--;
                    invulTimer.restart();
                }
            }
        }

        for (Jelly j:jellies) {
            if (getRect().intersects(j.getRect())) {
                if(getBoots().intersects(j.getRect()) && velY > 0 && y+height > j.getY(false)){
                    weapon.setAmmo(weapon.getCapacity());
                    removalJelly.add(j);
                    velY = -8;
                    GameManager.getGemManager().spawnGems((int)j.getX(false),(int)j.getY(false), 3);
                }
                else{
                    if (invulTimer.getElapsedTime() >= 2) {
//                        System.out.println("-1 hp");
                        health--;
                        invulTimer.restart();
                    } else {
//                        System.out.print(".");
                    }
                }
            }
        }

        if(health == 0){
            GameManager.gameOver();
        }

        for(Snake s : removalSnake){
            snakes.remove(s);
        }
        for(Jelly j:removalJelly){
            jellies.remove(j);
        }
    }

    // get collision on 4 sides of blocks
    // instead of scanning through all blocks or a general area of blocks,
    // getCollision instead runs through specific rows of blocks that narrow
    // down the search field tremendously
    // each check for each side of blocks is performed separately
    public void getCollision(Graphics g, Alan alan, Map map) {
        // BLOCK COLLISION
        Block[][] blocks = map.getMap();
        // getting rows with same y values as player
        int prevRow = getY(false)/Util.BLOCKLENGTH;
        int nextRow = getY(false)/Util.BLOCKLENGTH+1;

        //HINT: CHANGING LEVELS HERE
        // USE GAMEMANAGER TO SWITCH TO NEXT LEVEL WHEN ALAN REACHES CERTAIN POINT
        if (nextRow > map.getRows()-30) {
            AAdventure.getGame().setAlpha(Util.increaseOpacity(AAdventure.getGame().getAlpha(), true));
        }

        if (nextRow == map.getRows()-15) {
            AAdventure.getLevelClear().setAlpha(255);
            Powerups.selectionTimer.restart();
            GameManager.toLevel(Util.getLevel()+1, false);
        } else {
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
                    weapon.setAmmo(weapon.getCapacity());
                    if (hop) {
                        hop = false;
//                        velY = -4;
                    }
                    changeState(IDLE, dir, false);
                }
            } else { // in the case that the player has not reached block/ground
                if (velY == 0) { // starts falling velocity at 3 (if velocity is 0, when player just starts to fall)
                    velY = 3;
                } else if (velY < maxVelY) { // increase velocity if not reached max y vel
                    velY += accelY;
                } else {
                    hop = true;
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
                }
            }

            // right to left collision checking
            // similar logic to top-down collisions
            // differences:
            // - checks multiple rows, player able to collide with multiple rows of block
            // - checks on y-axis instead of x
            // - snapping to x-axis not bug prone vs snapping to y because no offset
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
    }

    // drawing alan in different states and directions
    public int draw(Graphics g, boolean[] keys, Map map, Powerups powerups, EnemyManager enemies) { //

        int collisionWall = move(keys, g, map, powerups, enemies);

        if (weapon.isAlanShoot()) {
            if ((int) animFrame == allAnims.get(animShoot*2).size()-1) {
                animFrame = 0;
                weapon.setAlanShoot(false);
            } else {
                animFrame+=0.8; // frame should update every 1/5 ticks
            }
        } else if (state == IDLE) {
            if ((int) animFrame == allAnims.get(animIdle*2).size()-1) {
                animFrame = 0;
            } else {
                animFrame+=0.15; // frame should update every 1/5 ticks
            }
        } else if (state == WALK) {
            if ((int) animFrame == allAnims.get(animRun*2).size()-1) {
                animFrame = 0;
            } else {
                animFrame += 0.4; // frame should update every 2/5 ticks
            }
        }
        else if (state == JUMP) {
            if ((int) animFrame == allAnims.get(animAir*2).size()-1) {
                animFrame = 0;
            } else {
                animFrame += 0.6; // frame should update every 2/5 ticks
            }
        }

        // drawing animation based on direction
        if (dir == Util.LEFT) {
            if (weapon.isAlanShoot()) {
                g.drawImage(allAnims.get(animShoot * 2).get((int) animFrame), getX(true) - 5, getY(true)+3, null);
            } else {
                g.drawImage(allAnims.get(state * 2).get((int) animFrame), getX(true) - 5, getY(true)+3, null);
            }
        } else {
            if (weapon.isAlanShoot()) {
                g.drawImage(allAnims.get(animShoot * 2 + 1).get((int) animFrame), getX(true) - 5, getY(true)+3, null);
            } else {
                g.drawImage(allAnims.get(state * 2 + 1).get((int) animFrame), getX(true) - 5, getY(true)+3, null);
            }
        }
        return collisionWall;
    }
}

