import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

//TODO: ADD SUBCLASSES TO ENEMY FOR SPECIFIC ENEMIES
// PRIORITY - LOW
// create subclasses to inhereit from main enemy class
// uniqueness:
// - animations
// - damage method and method of movement
// - ENSURE ENEMIES BOUNCE OFF EACH OTHER

public class EnemyManager{
    private ArrayList<Snake> snakes = new ArrayList<>();
    private ArrayList<Snail> snails = new ArrayList<>();
    private ArrayList<Jelly> jellies = new ArrayList<>();
    private ArrayList<Bat> bats = new ArrayList<>();

    public ArrayList<Snake> getSnakes() {return snakes;}
    public ArrayList<Snail> getSnails() {return snails;}
    public ArrayList<Jelly> getJellies() {return jellies;}
    public ArrayList<Bat> getBats() {return bats;}

    // HINT: modify enemy health here
    public void addSnake(int x, int y) {snakes.add(new Snake(x,y));}
    public void addSnail(int x, int y, int horiDir, int vertDir) {snails.add(new Snail(x,y,horiDir,vertDir));}
    public void addJelly(int x, int y) {jellies.add(new Jelly(x,y));}
    public void addBat(int x, int y) {bats.add(new Bat(x,y));}
    public void generateSnakes(Block[][] blocks, Alan alan) {
        Random rand = new Random();
        for(int i=Util.GENERATIONSTART; i<blocks.length-1; i++){
            for(int j=1; j<blocks[i].length-1; j++) {
                if ((blocks[i-1][j].getType() == Block.AIR && blocks[i][j].getType() != Block.AIR && blocks[i][j].getType() != Block.SPIKE) && (blocks[i-1][j-1].getType() == Block.AIR && blocks[i][j-1].getType() != Block.AIR)) {
                    if(rand.nextInt(100)<=35) {
                        addSnake(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
                    }
                }
            }
        }
    }
    public void generateSnails(Block[][] blocks, Alan alan){
        Random rand = new Random();
        for(int i=Util.GENERATIONSTART; i< blocks.length-1; i++){
            for(int j=1; j<blocks[i].length-1; j++) {
                if(blocks[i][j-1].getType() == Block.AIR && blocks[i][j].getType() == Block.WALL && blocks[i+1][j-1].getType() == Block.AIR && blocks[i+1][j].getType() == Block.WALL) {
                    if(rand.nextInt(100)<=20) {
                        addSnail(blocks[i][j].getX(false), blocks[i][j].getY(false, alan), Snail.RIGHT, Snail.UP);
                    }
                }
            }
        }
    }
    public void generateJellies(Block[][] blocks, Alan alan){
        Random rand = new Random();
        for(int i=Util.GENERATIONSTART; i< blocks.length-1; i++){
            for(int j=1; j<blocks[i].length-1; j++) {
                if(blocks[i][j].getType() == Block.AIR && blocks[i][j+1].getType() == Block.AIR) {
                    if(rand.nextInt(100)<=.5) {
                        addJelly(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
//                        return;
                    }
                }
            }
        }
    }
    public void generateBats(Block[][] blocks, Alan alan){
        Random rand = new Random();
        for(int i=Util.GENERATIONSTART; i< blocks.length; i++){
            for(int j=1; j<blocks[i].length-1; j++) {
                if(blocks[i][j-1].getType() == Block.AIR) {
                    if(rand.nextInt(100)<=2) {
                        addBat(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
                    }
                }
            }
        }
    }

    public void clearEnemies(){
        snakes.clear();
        snails.clear();
        jellies.clear();
        bats.clear();
    }

    public void drawEnemies(Graphics g, Alan alan, Map map){
        Block[][] blocks = map.getMap();
        for(Snake s : snakes){
            s.draw(g, blocks);
        }
        for(Snail s : snails){
            s.draw(g, blocks);
        }
        for(Jelly j:jellies){
            j.draw(g, alan, map);
        }
        for(Bat b : bats){
            b.draw(g);
        }
    }
}

class Snake {
    private final int LEFT = 0, RIGHT = 1;
    private int width, height;
    private int health;
    private double x, y;
    private int dir;
    private double speed, velX, velY, maxVelY, accelY; // the speed and acceleration the enemy has
    private double animFrame;

    private final ArrayList<Image> idleL = new ArrayList<>();
    private final ArrayList<Image> idleR = new ArrayList<>();

    public Snake(int x, int y) {
        this.width = 32;
        this.height = 18;
        this.x = x;
        this.y = y-height+1;
        dir = RIGHT;
        this.health = 10;
        this.velX = 2;
        this.accelY = 1;
        this.maxVelY = Util.rand.nextInt(5,13);
        animFrame = 0;

        for (int i = 0; i < 4; i++) {
            idleL.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleL" + i + ".png").getImage());
            idleL.set(i, idleL.get(i).getScaledInstance((idleL.get(i).getWidth(null)*2), (idleL.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 4; i++) {
            idleR.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleR" + i + ".png").getImage());
            idleR.set(i, idleR.get(i).getScaledInstance((idleR.get(i).getWidth(null)*2), (idleR.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
    }

    public void setX(int x) {
        this.x = x;
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
    public double getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos();
        } else {
            return x;
        }
    }
    public double getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
        } else {
            return y;
        }
    }
    public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

    public void move(Block[][] blocks){
        int currRow = (int)y/Util.BLOCKLENGTH;
        int grndRow = currRow+1; // add one to get the row of blocks the snake is standing on
        int currColL = (int)x/Util.BLOCKLENGTH;
        int currColC = (int)(x+width/2)/Util.BLOCKLENGTH;
        int currColR = (int)(x+width)/Util.BLOCKLENGTH;

        if(blocks[grndRow][currColC].getType() != Block.AIR) {
            y = grndRow * Util.BLOCKLENGTH - height;
            velY = 0;
            if (dir == LEFT) {
                if (x <= 0) {
                    dir = RIGHT;
                    x += velX;
                } else {
                    if (blocks[grndRow][currColL].getType() != Block.AIR && blocks[grndRow - 1][currColL].getType() == Block.AIR) {
                        x -= velX;
                    } else {
                        dir = Util.RIGHT;
                        x += velX;
                    }
                }
            } else {
                if (x + width >= (blocks[0].length-1) * Util.BLOCKLENGTH) {
                    dir = LEFT;
                    x -= velX;
                } else {
                    if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
                        x += velX;
                    } else {
                        dir = LEFT;
                        x -= velX;
                    }
                }
            }
        }
        else{
            y+=velY;
            if(velY < maxVelY){
                velY += accelY;
            }
        }
    }

    public void draw(Graphics g, Block[][] blocks) {
        move(blocks);

        if(dir == LEFT) {
            if ((int) animFrame == idleL.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.2;
            }
            g.drawImage(idleL.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }
        else{
            if ((int) animFrame == idleR.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.2;
            }
            g.drawImage(idleR.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
    }
}

class Snail {
    public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
    private int width, height, dir;
    private int health;
    private double x, y;
    private double velY; // the speed and acceleration the enemy has
    private double animFrame;

    private final ArrayList<Image> idleU = new ArrayList<>();
    private final ArrayList<Image> idleD = new ArrayList<>();

    public Snail(int x, int y, int horiDir, int vertDir) {
        this.width = 35;
        this.height = 42;
        this.x = x;
        this.y = y;
        this.dir = vertDir;
        this.health = 30;
        this.velY = 1.5;
        animFrame = 0;

        if(horiDir == LEFT){
            this.x += Util.BLOCKLENGTH;
            for (int i = 0; i < 4; i++) {
                idleU.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleLU" + i + ".png").getImage());
                idleU.set(i, idleU.get(i).getScaledInstance((idleU.get(i).getWidth(null)*2), (idleU.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
            }
            for (int i = 0; i < 4; i++) {
                idleD.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleLD" + i + ".png").getImage());
                idleD.set(i, idleD.get(i).getScaledInstance((idleU.get(i).getWidth(null)*2), (idleD.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
            }
        }
        else{
            this.x -= Util.BLOCKLENGTH;
            for (int i = 0; i < 4; i++) {
                idleU.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleRU" + i + ".png").getImage());
                idleU.set(i, idleU.get(i).getScaledInstance((idleU.get(i).getWidth(null)*2), (idleU.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
            }
            for (int i = 0; i < 4; i++) {
                idleD.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleRD" + i + ".png").getImage());
                idleD.set(i, idleD.get(i).getScaledInstance((idleU.get(i).getWidth(null)*2), (idleD.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
            }
        }
    }

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public int getHealth() {return health;}
    public void setHealth(int health) {this.health = health;}
    public double getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos();
        } else {
            return x;
        }
    }
    public double getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
        } else {
            return y;
        }
    }
    public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

    public void move(Block[][] blocks){
        int currRow = (int)y/Util.BLOCKLENGTH;
        int topRow = (int)y/Util.BLOCKLENGTH;
        int bottomRow = (int)(y+height)/Util.BLOCKLENGTH;
        int currCol = (int) x / Util.BLOCKLENGTH;
        int wallCol;
        if(dir == LEFT) {
            wallCol = currCol-1;
        }
        else{
            wallCol = currCol+1;
        }

        if(dir == UP) {
            if (blocks[topRow][wallCol].getType() == Block.WALL && blocks[topRow][currCol].getType() == Block.AIR) {
                y -= velY;
            } else {
                dir = DOWN;
                y += velY;
            }
        }
        else{
            if (blocks[bottomRow][wallCol].getType() == Block.WALL && blocks[bottomRow][currCol].getType() == Block.AIR) {
                y += velY;
            } else {
                dir = UP;
                y -= velY;
            }
        }
    }

    public void draw(Graphics g, Block[][] blocks) {
        move(blocks);

        if(dir == UP) {
            if ((int) animFrame == idleU.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.2;
            }
            g.drawImage(idleU.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }
        else{
            if ((int) animFrame == idleD.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.2;
            }
            g.drawImage(idleD.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
    }
}

class Jelly{
    private final int width, height;
    private int health;
    private double x, y;
    private double speed, velX, velY, maxVelX, maxVelY, accelX, accelY, accelFactor; // the speed and acceleration the enemy has
    private double animFrame;
    private boolean moveLeft, moveRight, moveUp, moveDown;

    ArrayList<Image> idle = new ArrayList<>();
    public Jelly(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 30;
        this.health = 30;
        this.speed = 1;
        this.maxVelX = 2;
        this.maxVelY = 2;
        this.accelX = 0;
        this.accelY = 0;
        this.accelFactor = .06;
        animFrame = 0;
        moveLeft = moveRight = moveUp = moveDown = false;
        for (int i = 0; i < 4; i++) {
            idle.add(new ImageIcon("src/assets/enemies/jelly/idle/jellyIdle" + i + ".png").getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
            idle.set(i, idle.get(i).getScaledInstance((idle.get(i).getWidth(null)*2), (idle.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
    }
    public double getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos();
        } else {
            return x;
        }
    }
    public double getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
        } else {
            return y;
        }
    }
    public void setX(int x) {
        this.x = x;
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
    public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}
    public void move(Graphics g, Alan alan, Map map) {
        // distance calculations
        double distX = x - AAdventure.getGame().getAlan().getX(false);
        // how far away the enemy is compared to alan
        double distY = y - AAdventure.getGame().getAlan().getY(false);
        double distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)); // pythag theorem

        if(distance<450) {
            // adding up how many frames movement has been in x direction, capping out at +-20 to limit terminal velocity
            if (distX < 0 && velX < maxVelX) {
                accelX += accelFactor;
            } else if (distX > 0 && velX > -maxVelX) {
                accelX -= accelFactor;
            }
            if (distY < 0 && velY < maxVelY) {
                accelY += accelFactor;
            } else if (distY > 0 && velY > -maxVelY) {
                accelY -= accelFactor;
            }
            // moving the enemy
            velX = ((-1 / distance) * distX) * speed + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
            velY = ((-1 / distance) * distY) * speed + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan

            Block[][] blocks = map.getMap();
            // getting rows with same y values as jelly
            int prevRow = (int) getY(false) / Util.BLOCKLENGTH;
            int nextRow = prevRow + 1;
            int prevCol = (int) getX(false) / Util.BLOCKLENGTH;
            int nextCol = prevCol + 1;

            // right left collision
            for(int r=prevRow; r<=nextRow; r++) {
                for (int i = 0; i < map.getColumns(); i++) {
                    Block block = blocks[r][i];
                    if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
                        Rectangle leftSide = new Rectangle(block.getX(false), block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
//                        g.setColor(Color.CYAN);
//                        g.drawRect(block.getX(true), block.getY(true, alan)+1, 1, Util.BLOCKLENGTH-2);
                        if (getRect().intersects(leftSide)) {
                            x += 2*accelFactor;
                            velX = -1;
                            accelX = 0;
                        }
                    }
                }
            }
            // left right collision
            for(int r=prevRow; r<=nextRow; r++) {
                for (int i = 0; i < map.getColumns(); i++) {
                    Block block = blocks[r][i];
                    if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
                        Rectangle rightSide = new Rectangle(block.getX(false) + Util.BLOCKLENGTH, block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
//                        g.setColor(Color.MAGENTA);
//                        g.drawRect(block.getX(true) + Util.BLOCKLENGTH, block.getY(true, alan)+1, 1, Util.BLOCKLENGTH-2);
                        if (getRect().intersects(rightSide)) {
                            x -= 2*accelFactor;
                            velX = 1;
                            accelX = 0;
                        }
                    }
                }
            }
            // top down collision
            for (int i = 0; i < map.getColumns(); i++) {
                Block block = blocks[nextRow][i];
                if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
                    Rectangle topSide = new Rectangle(block.getX(false)+1, block.getY(false, alan), Util.BLOCKLENGTH-2, 1);
                    if (getRect().intersects(topSide)) {
                        y -= 2*accelFactor;
                        velY = -1;
                        accelY = 0;
                    }
                }
            }

            // bottom up collision checking
            for (int i = 0; i < map.getColumns(); i++) {
                Block block = blocks[prevRow][i];
                // only check bottom collision in solid blocks and when going upwards in y-dir
                if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
                    Rectangle bottomSide = new Rectangle(block.getX(false)+1, block.getY(false, alan) + height, Util.BLOCKLENGTH-2, 1);
                    if (getRect().intersects(bottomSide)) {
                        y += 2*accelFactor;
                        velY = 1;
                        accelY = 0;
                    }
                }
            }

//            ArrayList<Jelly> jellies = AAdventure.getGame().getEnemyManager().getJellies();
//            for(Jelly j:jellies){
//                if(getRect().intersects(j.getRect()) && this!=j){
//                    velX *= -0.75;
//                    velY *= -0.75;
//                    accelX *= -1;
//                    accelY *= -1;
//                    j.velX *= -0.75;
//                    j.velY *= -0.75;
//                    j.accelX *= -1;
//                    j.accelY *= -1;
//
//                }
//            }

            x += velX;
            y += velY;
        }
    }

    public void draw(Graphics g, Alan alan, Map map) {
        move(g, alan, map);

        if ((int) animFrame == idle.size() - 1) {
            animFrame = 0;
        } else {
            animFrame += 0.15;
        }
        g.drawImage(idle.get((int) animFrame), (int) getX(true), (int)getY(true), null);

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
//        g.drawLine((int)getX(true),0,(int)getX(true),1000);
//        g.drawLine((int)getX(true)+width,0,(int)getX(true)+width,1000);
//        g.drawLine(0,(int)getY(true),1000,(int)getY(true));
//        g.drawLine(0,(int)getY(true)+height,1000,(int)getY(true)+height);
    }
}

class Bat{
    public static final int IDLE = 0, FLY = 1;
    private final int state;
    private final int width, height;
    private int health;
    private double x, y;
    private double speed, velX, velY, maxVelX, maxVelY, accelX, accelY, accelFactor; // the speed and acceleration the enemy has
    private double animFrame;

    ArrayList<Image> idle = new ArrayList<>();
    ArrayList<Image> fly = new ArrayList<>();

    public Bat(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 36;
        this.height = 26;
        this.health = 30;
        this.speed = 2;
        this.maxVelX = 4.5;
        this.maxVelY = 4.5;
        this.accelX = 0;
        this.accelY = 0;
        this.accelFactor = .2;
        state = FLY;
        animFrame = 0;
        for (int i = 0; i < 6; i++) {
            idle.add(new ImageIcon("src/assets/enemies/fly/fly" + i + ".png").getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        }
    }
    public double getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return x + Background.getWallLeftPos();
        } else {
            return x;
        }
    }
    public double getY(boolean adjusted) { // gets y
        if (adjusted) { // whether you want y relative to the gameplay window
            return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
        } else {
            return y;
        }
    }
    public void setX(int x) {
        this.x = x;
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
    public void move() {
        // distance calculations
        double distX = x - AAdventure.getGame().getAlan().getX(false);
        // how far away the enemy is compared to alan
        double distY = y - AAdventure.getGame().getAlan().getY(false);
        double distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)); // pythag theorem
        // adding up how many frames movement has been in x direction, capping out at +-20 to limit terminal velocity
        if (distX < 0 && velX < maxVelX) {
            accelX += accelFactor;
        } else if (distX > 0 && velX > -maxVelX) {
            accelX -= accelFactor;
        }
        if (distY < 0 && velY < maxVelY) {
            accelY += accelFactor;
        } else if (distY > 0 && velY > -maxVelY) {
            accelY -= accelFactor;
        }
        // moving the enemy
        velX = ((-1 / distance) * distX) * speed + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
        velY = ((-1 / distance) * distY) * speed + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan
        x += velX;
        y += velY;
    }
    public void getCollision(){

    }

    public void draw(Graphics g) {
        move();

        if (state == FLY) {
            if ((int) animFrame == idle.size() - 1) {
                animFrame = 0;
            } else {
                animFrame += 0.33;
            }
            g.drawImage(idle.get((int) animFrame), (int) getX(true), (int)getY(true), null);
        }
    }
}