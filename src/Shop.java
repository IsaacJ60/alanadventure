import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Shop {
    private final ArrayList<ArrayList<Cosmetics>> allItems;
    private int selectedItem;
    private int selectedType;
    private double offsetY;
    private int wantedOffsetY;
    private double offsetVel;
    private final double offsetVelMax;
    private final double offsetAccel;

    public static Util.CustomTimer shopTimer = new Util.CustomTimer();

    public Shop(ArrayList<ArrayList<Cosmetics>> allItems) {
        this.allItems = allItems;
        this.selectedItem = 0;
        this.selectedType = 0;
        this.offsetY = AAdventure.getGameHeight()/2.0-30.0;
        this.wantedOffsetY = (int)offsetY;
        this.offsetVel = 5;
        this.offsetAccel = 2.0;
        this.offsetVelMax = 15;
    }

    public void addCosmetic(int type, Cosmetics c) {allItems.get(type).add(c);}

    public void selectItem(boolean[] keys, Gems gems) {

        // moving the screen offset to centre focused item
        if (wantedOffsetY > offsetY) {
            offsetY+=offsetVel;
            if (offsetVel < offsetVelMax) {
                offsetVel+=offsetAccel;
            }
        }
        if (wantedOffsetY < offsetY) {
            offsetY-=offsetVel;
            if (offsetVel < offsetVelMax) {
                offsetVel+=offsetAccel;
            }
        }

        // changing selected item
        if (shopTimer.getElapsedTime() > 0.5) {
            if (keys[Util.w]) {
                shopTimer.restart();
                if (selectedItem > 0) {
                    offsetVel = 5;
                    selectedItem--;
                    wantedOffsetY += 200;
                }
            } else if (keys[Util.s]) {
                shopTimer.restart();
                if (selectedItem < allItems.get(selectedType).size()-1) {
                    offsetVel = 5;
                    selectedItem++;
                    wantedOffsetY -= 200;
                }
            } else if (keys[Util.space]) {
                shopTimer.restart();
                //HINT: BUY STUFF
                if (allItems.get(selectedType).get(selectedItem).purchase(gems)) {
                    if (allItems.get(selectedType).get(selectedItem).getType().equals("BG")) {
                        Background.setBg(allItems.get(selectedType).get(selectedItem).getEnlargedImg());
                    }
                }
            }
        }
    }

    public void draw(Graphics g, boolean[] keys, Gems gems) {

        // getting keyinput and new selections
        selectItem(keys, gems);

        for (int i = 0; i < allItems.get(selectedType).size(); i++) {

            // getting item to blit for easy use
            Cosmetics item = allItems.get(selectedType).get(i);

            //HINT: blit frame for bgs
            if (item.getType().equals("BG")) {
                g.setColor(Color.WHITE);
                g.fillRect((AAdventure.getGameWidth()/2)-(item.getWidth()/2)-2, (i*200)+(int)offsetY-2, item.getWidth()+4, item.getHeight()+4);
            }

            //HINT: draw preview img
            g.drawImage(item.getImg(), (AAdventure.getGameWidth()/2)-(item.getWidth()/2), (i*200)+(int)offsetY, null);
        }
    }
}

class Cosmetics {
    private String type;
    private Image img, enlargedImg;

    private int width, height;
    private int cost;

    Cosmetics(String type, ImageIcon img, int width, int height, int cost) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.cost = cost;
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        this.enlargedImg = this.img;
    }

    Cosmetics(String type, ImageIcon img, Image enlargedImg, int width, int height, int cost) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.cost = cost;
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        this.enlargedImg = enlargedImg;
    }

    public boolean purchase(Gems gems) {
        if (gems.getTotalGems() > cost) {
            gems.setTotalGems(gems.getTotalGems() - cost);
            return true;
        }
        return false;
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
}