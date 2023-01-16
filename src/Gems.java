import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Gems {

    // gem counts
    private int totalGems;
    private int gems;

    // gem images
    private final static ArrayList<Image> gemS = new ArrayList<>();
    private final static ArrayList<Image> gemM = new ArrayList<>();
    private final static ArrayList<Image> gemL = new ArrayList<>();
    private static Image gemL0 = null;
    private ArrayList<Gem> activeGems = new ArrayList<>();

    // initialize gem manager with set amount of total gems
    public Gems(int g) {
        gems = 0; totalGems = g;

        for (int i = 0; i < 4; i++) {
            gemS.add(new ImageIcon("src/assets/gems/gemS"+i+"B.png").getImage());
            gemS.set(i, gemS.get(i).getScaledInstance((gemS.get(i).getWidth(null)*2), (gemS.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 4; i++) {
            gemM.add(new ImageIcon("src/assets/gems/gemM"+i+"B.png").getImage());
            gemM.set(i, gemM.get(i).getScaledInstance((gemM.get(i).getWidth(null)*2), (gemM.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
        for (int i = 0; i < 4; i++) {
            gemL.add(new ImageIcon("src/assets/gems/gemL"+i+"B.png").getImage());
            gemL.set(i, gemL.get(i).getScaledInstance((gemL.get(i).getWidth(null)*2), (gemL.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
        }
        gemL0 = new ImageIcon("src/assets/gems/gemL0BB.png").getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT);
    }

    // getters and setters
    public void setGems(int g) {gems = g;}
    public int getGems() {return gems;}
    public void addGems(int g) {gems+=g;}
    public void setTotalGems(int g) {totalGems = g;}
    public int getTotalGems() {return totalGems;}
    public void addTotalGems(int g) {totalGems+=g;}
    public static ArrayList<Image> getGemS() {return gemS;}
    public static ArrayList<Image> getGemM() {return gemM;}
    public static ArrayList<Image> getGemL() {return gemL;}
    public static Image getGemL0() {return gemL0;}
    public ArrayList<Gem> getActiveGems() {return activeGems;}
    public void setActiveGems(ArrayList<Gem> activeGems) {this.activeGems = activeGems;}

    // drawing gem UI
    public void displayGemUI(Graphics g, boolean total, boolean current, Alan alan) {
        int xPos = 65;
        int gemsOffset = 0;
        g.setFont(Util.fontTextSmall);
        g.setColor(new Color(0, 58, 109));
        g.drawImage(gemL0, 22,gemsOffset+26,null);
        g.fillRect(45,gemsOffset+50,140,2);
        g.drawLine(185, gemsOffset+50, 192, gemsOffset+45);
        g.drawLine(185, gemsOffset+51, 193, gemsOffset+45);
        g.setColor(Color.WHITE);
        if (total && !current) { // display total gems
            g.drawString(totalGems + " (T)", xPos, gemsOffset+50);
        } else if (current && !total) { // display current run gems
            g.drawString(gems + " (C)", xPos, gemsOffset+50);
        }
    }

    // spawn certain amount of gems
    public void spawnGems(int x, int y, int amount) {
        for (int i = 0; i < amount; i++) {
            activeGems.add(new Gem(x,y-20,Util.rand.nextInt(-5,5),Util.rand.nextInt(-4,-1)));
        }
    }

    // displaying gems on screen
    public void drawGems(Graphics g, Alan alan, Map map) {
        // iterating through all active gems
        for (int i = activeGems.size() - 1; i >= 0; i--) {
            Gem activeGem = activeGems.get(i);
            if (activeGem.isClaimed()) { // checking if they have been claimed, remove if so and increment gem counter
                gems += activeGem.getSize();
                activeGems.remove(i);
            }
            //TODO: "500*Util.BLOCKLENGTH" -> ASCAP
            if (activeGem.getGemTimer().getElapsedTime() > 10 || activeGem.getY(false, alan) > map.getRows()*Util.BLOCKLENGTH-200) {
                activeGems.remove(i);
            }
            // drawer gem
            activeGem.draw(g, alan, map);
        }
    }
}

// GEM CLASS
class Gem {
    private int x, y, width, height, size;
    private double velX, velY, animFrame;
    private final double accelX, accelY;
    private final double maxY;

    private boolean claimed;
    private final Rectangle rect;
    private ArrayList<Image> anim;

    private Util.CustomTimer gemTimer = new Util.CustomTimer();

    // creating gem with initial x and y coords and velocities
    Gem(int x, int y, int initialX, int initialY) {
        // random gem size
        switch (Util.rand.nextInt(0,2)) {
            case 0 -> {
                anim = Gems.getGemS();
                size = 1;
                width = 16; height = 16; // hmm TODO: ASCAP...
            }
            case 1 -> {
                anim = Gems.getGemM();
                size = 2;
                width = 24; height = 24;
            }
            case 2 -> {
                anim = Gems.getGemL();
                size = 3;
                width = 40; height = 40;
            }
        }
        this.claimed = false;
        this.x = x;
        this.y = y;
        this.velX = initialX;
        this.velY = initialY;
        this.maxY = 10;
        this.accelX = 0.1;
        this.accelY = 0.5;
        this.rect = new Rectangle(x,y,width,height);
        gemTimer.start();
    }

    public Util.CustomTimer getGemTimer() {return gemTimer;}
    public boolean isClaimed() {return claimed;}
    public int getSize() {return size;}
    public int getX(boolean adjusted) {return (adjusted ? x + Background.getWallLeftPos() : x);}
    public int getY(boolean adjusted, Alan alan) {return (adjusted ? y-alan.getOffset()+alan.getScreenOffset() : y);}

    // check if collision occurs
    public boolean alanCollision(Alan alan) {
        if (alan.getRect().intersects(rect)) {
            GameManager.getGemManager().addGems(size);
            return true;
        }
        return false;
    }

    // moving gems
    public void move(Alan alan, Map map, Graphics g) {
        getCollision(alan, map, g);
        x += (int)velX;
        y += (int)velY;
        if (velY < maxY) {
            velY+=accelY;
        }
        if (velX > 0) {
            velX-=accelX;
        } else if (velX < 0) {
            velX+=accelX;
        }
        rect.setLocation(x,y);
    }

    // getting collision with blocks
    public void getCollision(Alan alan, Map map, Graphics g) {
        Block[][] blocks = map.getMap();
        int nextRow = getY(false,alan)/Util.BLOCKLENGTH+1;
        int nearestRightX = 100, nearestLeftX = 100, nearestBlockY = 100, snapX = 0, snapY = 0;

        // PLAYER COLLISION
        if (rect.intersects(alan.getRect())) {
            claimed = true; // claim gem!
        }

        // top down collision
        for (int i = 0; i < map.getColumns(); i++) {
            int blockType = blocks[nextRow][i].getType();
            if (blockType != Block.AIR) {
                // only check top when velocity is positive (going down)
                if ((blockType == Block.WALL || blockType == Block.BOX || blockType == Block.PLAT) && velY >= 0) {
                    // checkig player has a chance of colliding with block (x value within range of block x values)
                    if (x+width > blocks[nextRow][i].getX(false) && x < blocks[nextRow][i].getX(false) + Util.BLOCKLENGTH) {
                        if (blocks[nextRow][i].getY(false, alan)-y < nearestBlockY) {
                            // updating nearest distance
                            nearestBlockY = Math.abs(blocks[nextRow][i].getY(false, alan)-(y+height));
                            snapY = blocks[nextRow][i].getY(false,alan)-height-1;
                        }
                    }
                }
            }
        }
        if (nearestBlockY <= velY+5) {
            velY = -1 * (velY/2);
            y = snapY;
        }

        // right to left collision checking
        // similar logic to top-down collisions
        // differences:
        // - checks multiple rows, player able to collide with multiple rows of block
        // - checks on y-axis instead of x
        // - snapping to x-axis not bug prone vs snapping to y because no offset
        for (int r = nextRow-2; r < nextRow+1; r++) {
            for (int i = map.getColumns()-1; i >= 0; i--) {
                Block block = blocks[r][i];
                if (block.getX(false) < x) {
                    if (block.getType() != Block.AIR) {
                        if (block.getType() == Block.BOX || block.getType() == Block.WALL) {
                            if (y+height > block.getY(false,alan) && y < block.getY(false,alan) + Util.BLOCKLENGTH) {
                                if (x-(block.getX(false)+Util.BLOCKLENGTH) < nearestLeftX) {
                                    nearestLeftX = Math.abs(x-(block.getX(false)+Util.BLOCKLENGTH));
                                    snapX = block.getX(false)+Util.BLOCKLENGTH+5;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (nearestLeftX <= velX+10) {
            velX = -1 * velX;
            x = snapX;
        }

        // left to right collision checking - SAME AS RIGHT TO LEFT BUT X VALUES TO CHECK ARE DIFFERENT
        for (int r = nextRow-2; r < nextRow+1; r++) {
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
        if (nearestRightX <= velX+10) {
            velX = -1 * velX;
            x = snapX;
        }
    }

    // drawing gem
    public void draw(Graphics g, Alan alan, Map map) {
        move(alan, map, g);
        g.drawImage(anim.get((int)animFrame), getX(true), getY(true, alan), null);
        if (animFrame >= anim.size()-1) {
            animFrame = 0;
        } else {
            animFrame+=0.3;
        }
    }
}
