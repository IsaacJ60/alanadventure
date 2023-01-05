import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Gems {
    public static Random rand = new Random();
    private int totalGems;
    private int gems;

    private final static ArrayList<Image> gemS = new ArrayList<>();
    private final static ArrayList<Image> gemM = new ArrayList<>();
    private final static ArrayList<Image> gemL = new ArrayList<>();
    private static Image gemL0 = null;

    private final ArrayList<Gem> activeGems = new ArrayList<>();

    public Gems() {
        gems = 0; totalGems = 0;

        for (int i = 0; i < 4; i++) {
            gemS.add(new ImageIcon("src/assets/gems/gemS"+i+".png").getImage());
        }
        for (int i = 0; i < 4; i++) {
            gemM.add(new ImageIcon("src/assets/gems/gemM"+i+".png").getImage());
        }
        for (int i = 0; i < 4; i++) {
            gemL.add(new ImageIcon("src/assets/gems/gemL"+i+".png").getImage());
        }
        gemL0 = gemL.get(0).getScaledInstance(30,30,Image.SCALE_DEFAULT);
    }

    public Gems(int g) {
        gems = 0; totalGems = g;

        for (int i = 0; i < 4; i++) {
            gemS.add(new ImageIcon("src/assets/gems/gemS"+i+".png").getImage());
        }
        for (int i = 0; i < 4; i++) {
            gemM.add(new ImageIcon("src/assets/gems/gemM"+i+".png").getImage());
        }
        for (int i = 0; i < 4; i++) {
            gemL.add(new ImageIcon("src/assets/gems/gemL"+i+".png").getImage());
        }
    }

    public void setGems(int g) {gems = g;}
    public int getGems() {return gems;}
    public void addGems(int g) {gems+=g;}
    public void setTotalGems(int g) {totalGems = g;}
    public int getTotalGems() {return totalGems;}
    public void addTotalGems(int g) {totalGems+=g;}
    public static ArrayList<Image> getGemS() {return gemS;}
    public static ArrayList<Image> getGemM() {return gemM;}
    public static ArrayList<Image> getGemL() {return gemL;}

    public void displayGems(Graphics g, boolean total, boolean current, Alan alan) {
        int xPos = 65;
        int gemsOffset = alan.getScreenOffset()/5;
        g.setFont(Util.fontTextSmall);
        g.setColor(new Color(204, 0, 1));
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

    public void spawnGems(int x, int y, int amount) {
        for (int i = 0; i < amount; i++) {
            activeGems.add(new Gem(x,y-20,rand.nextInt(-5,5),rand.nextInt(-4,-1)));
        }
    }

    public void drawGems(Graphics g, Alan alan, Map map) {
        for (int i = activeGems.size() - 1; i >= 0; i--) {
            Gem activeGem = activeGems.get(i);
            if (activeGem.isClaimed()) {
                gems += activeGem.getSize();
                activeGems.remove(i);
            }
            //TODO: "500*Util.BLOCKLENGTH" -> ASCAP
            if (activeGem.getGemTimer().getElapsedTime() > 10 || activeGem.getY(false, alan) > 500*Util.BLOCKLENGTH) {
                activeGems.remove(i);
            }
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

    Gem(int x, int y, int initialX, int initialY) {
        // random gem size
        switch (Gems.rand.nextInt(0,3)) {
            case 0 -> {
                anim = Gems.getGemS();
                size = 1;
                width = 8; height = 8; // hmm TODO: ASCAP...
            }
            case 1 -> {
                anim = Gems.getGemM();
                size = 2;
                width = 12; height = 12;
            }
            case 2 -> {
                anim = Gems.getGemL();
                size = 3;
                width = 20; height = 20;
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
    public int getX(boolean adjusted) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return this.x + Background.getWallLeftPos()+Background.getWallWidth();
        } else {
            return this.x;
        }
    }
    public int getY(boolean adjusted, Alan alan) { // gets x
        if (adjusted) { // whether you want x relative to the gameplay window
            return this.y-alan.getOffset()+alan.getScreenOffset();
        } else {
            return this.y;
        }
    }

    public boolean retrieved(Alan alan) {
        if (alan.getRect().intersects(rect)) {
            GameManager.getGemManager().addGems(size);
            return true;
        }
        return false;
    }

    public void move(Alan alan, Map map, Graphics g) {
        getCollision(alan, map, g);
        x += (int)velX;
        x = Math.min(Background.getWallRightPos()-(Background.getWallLeftPos()+Background.getWallWidth())-width, x);
        x = Math.max(0, x);
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

    public void getCollision(Alan alan, Map map, Graphics g) {
        // PLAYER COLLISION
        if (rect.intersects(alan.getRect())) {
            claimed = true; // claim gem!
        }

        // BLOCK COLLISION
        Block[][] blocks = map.getMap();
        int nextRow = getY(false,alan)/Util.BLOCKLENGTH+1;
        for (int r = nextRow-1; r < nextRow+2; r++) {
            for (int i = 0; i < map.getColumns(); i++) {
                Block b = blocks[r][i];
                int blockType = b.getType();
                if (blockType != Block.AIR) {
                    if ((blockType == Block.WALL || blockType == Block.BOX || blockType == Block.PLAT)) {
                        if (rect.intersectsLine(b.getX(false), b.getY(false, alan), b.getX(false)+Util.BLOCKLENGTH, b.getY(false, alan))) {
                            velY = -1 * (velY/2);
                            y = b.getY(false,alan)-height-1;
                            break;
                        }
                    }
                }
            }
        }

        for (int r = nextRow-2; r < nextRow; r++) {
            for (int i = map.getColumns()-1; i >= 0; i--) {
                Block b = blocks[r][i];
                int blockType = b.getType();
                if (b.getX(false) < x) {
                    if (blockType != Block.AIR) {
                        if (blockType == Block.BOX || blockType == Block.WALL) {
                            if (rect.intersectsLine(b.getX(false), b.getY(false, alan), b.getX(false), b.getY(false, alan)+Util.BLOCKLENGTH)) {
                                x = b.getX(false)-width-1;
                                velX = -1 * velX;
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int r = nextRow-2; r < nextRow; r++) {
            for (int i = 0; i < map.getColumns(); i++) {
                Block b = blocks[r][i];
                int blockType = b.getType();
                if (b.getX(false) < x) {
                    if (blockType != Block.AIR) {
                        if (blockType == Block.BOX || blockType == Block.WALL) {
                            if (rect.intersectsLine(b.getX(false)+Util.BLOCKLENGTH, b.getY(false, alan), b.getX(false)+Util.BLOCKLENGTH, b.getY(false, alan)+Util.BLOCKLENGTH)) {
                                x = b.getX(false)+Util.BLOCKLENGTH+1;
                                velX = -1 * velX;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

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
