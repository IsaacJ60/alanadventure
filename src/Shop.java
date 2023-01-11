import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Shop {
    private final ArrayList<ArrayList<Cosmetics>> allItems;
    private int selectedItem, equippedItem;
    private int selectedType;
    private double offsetY;
    private int wantedOffsetY;
    private double offsetVel;
    private final double offsetVelMax;

    private final Image arrowLeft = new ImageIcon("src/tiles/arrowL.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT),
            arrowRight = new ImageIcon("src/tiles/arrowR.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT),
            arrowLeftB = new ImageIcon("src/tiles/arrowLB.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT),
            arrowRightB = new ImageIcon("src/tiles/arrowRB.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT);

    public static Util.CustomTimer shopTimer = new Util.CustomTimer();

    public Shop(ArrayList<ArrayList<Cosmetics>> allItems) {
        this.allItems = allItems;
        this.selectedItem = 0;
        this.selectedType = 0;
        this.offsetY = AAdventure.getGameHeight()/2.0-30.0;
        this.wantedOffsetY = (int)offsetY;
        this.offsetVel = 20;
        this.offsetVelMax = 20;
        this.equippedItem = this.selectedItem;
    }

    public void addCosmetic(int type, Cosmetics c) {allItems.get(type).add(c);}

    public void selectItem(boolean[] keys, Gems gems) {

        Cosmetics item = allItems.get(selectedType).get(selectedItem);

        // moving the screen offset to centre focused item
        if (wantedOffsetY > offsetY) {
            offsetY+=offsetVel;
        }
        if (wantedOffsetY < offsetY) {
            offsetY-=offsetVel;
        }

        // changing selected item
        if (shopTimer.getElapsedTime() > 0.4) {
            if (keys[Util.w]) {
                shopTimer.restart();
                if (selectedItem > 0) {
                    offsetVel = 20;
                    selectedItem--;
                    wantedOffsetY += 200;
                }
            } else if (keys[Util.s]) {
                shopTimer.restart();
                if (selectedItem < allItems.get(selectedType).size()-1) {
                    offsetVel = 20;
                    selectedItem++;
                    wantedOffsetY -= 200;
                }
            } else if (keys[Util.space]) {
                shopTimer.restart();
                //HINT: BUY STUFF
                if (item.purchase(gems)) {
                    if (!item.getOwned()) {
                        gems.setTotalGems(gems.getTotalGems() - item.getCost());
                        item.setOwned(true);
                    }
                    if (allItems.get(selectedType).get(selectedItem).getType().equals("BG")) {
                        equippedItem = selectedItem;
                        Background.setBg(allItems.get(selectedType).get(selectedItem).getEnlargedImg());
                    }
                }
            }
        }
    }

    public void drawArrows(Graphics g) {
        g.drawImage(arrowLeft, Background.getWallLeftPos()+Background.getWallWidth(), AAdventure.getGameHeight()/2-11, null);
        g.drawImage(arrowRight, Background.getWallRightPos()-22, AAdventure.getGameHeight()/2-11, null);

        g.drawImage(arrowLeftB, Background.getWallLeftPos()+Background.getWallWidth(), (int) (equippedItem*200+offsetY+17), null);
        g.drawImage(arrowRightB, Background.getWallRightPos()-22, (int) (equippedItem*200+offsetY+17), null);
    }

    public void draw(Graphics g, boolean[] keys, Gems gems) {

        // getting keyinput and new selections
        selectItem(keys, gems);

        drawArrows(g);

        for (int i = 0; i < allItems.get(selectedType).size(); i++) {

            // getting item to blit for easy use
            Cosmetics item = allItems.get(selectedType).get(i);

            int x = (AAdventure.getGameWidth()/2)-(item.getWidth()/2), y = (i*200)+(int)offsetY;

            //HINT: blit frame for bgs
            if (item.getType().equals("BG")) {
                g.setColor(Color.WHITE);
                g.fillRect(x,y,item.getWidth()+4,item.getHeight()+4);
            }

            //HINT: draw preview img
            g.setColor(Color.WHITE);
            g.setFont(Util.fontTextSmall);

            if (item.getOwned()) {
                g.drawString("OWNED", x+(item.getWidth()/2)-("OWNED".length()*7), y+item.getHeight()+40);
            } else {
                g.drawString(String.valueOf(item.getCost()),x+(item.getWidth()/2)-(String.valueOf(item.getCost()).length()*7),y+item.getHeight()+40);
            }

            g.drawImage(item.getImg(),x+2,y+2,null);
        }
    }
}

class Cosmetics {
    private String type;
    private Image img, enlargedImg;

    private int width, height;

    private int cost;

    private boolean owned;

    Cosmetics(String type, ImageIcon img, int width, int height, int cost) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.cost = cost;
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        this.enlargedImg = this.img;
        this.owned = false;
    }

    Cosmetics(String type, ImageIcon img, Image enlargedImg, int width, int height, int cost) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.cost = cost;
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        this.enlargedImg = enlargedImg;
        this.owned = false;
    }

    public boolean purchase(Gems gems) {
        return gems.getTotalGems() > cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Image getEnlargedImg() {
        return enlargedImg;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean getOwned() {
        return owned;
    }

    public void setOwned(boolean b) {
        owned = b;
    }
}