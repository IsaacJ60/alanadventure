import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
Shop.java
Isaac Jiang
Contains methods to draw shop items and check for shop purchases and interactions
 */

public class Shop {
    private final ArrayList<ArrayList<Cosmetics>> allItems;
    private int selectedItem;
    private final int[] equippedItems;
    private int selectedType;
    private double offsetY;
    private int wantedOffsetY;
    private double offsetVel;

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
        this.equippedItems = new int[allItems.size()];

        try {
            Scanner f = new Scanner(new BufferedReader(new FileReader("src/assets/shop/items.txt")));
            if (f.hasNext()) {
                ArrayList<ArrayList<Cosmetics>> items = this.allItems;
                for (int i = 0; i < items.size(); i++) {
                    ArrayList<Cosmetics> allItem = items.get(i);
                    for (int j = 0; j < allItem.size(); j++) {
                        Cosmetics cosmetics = allItem.get(j);
                        int status = f.nextInt();
                        if (status == 2) {
                            cosmetics.setOwned(true);
                            selectedType = i;
                            selectedItem = j;
                            switch (cosmetics.getType()) {
                                case "BACKGROUNDS" -> {
                                    equippedItems[selectedType] = selectedItem;
                                    Background.setBg(allItems.get(selectedType).get(selectedItem).getEnlargedImg());
                                }
                                case "BLASTERS" -> {
                                    equippedItems[selectedType] = selectedItem;
                                    AAdventure.getIntro().getAlan().setWeapon(cosmetics.getBlaster());
                                    AAdventure.getGame().getAlan().setWeapon(cosmetics.getBlaster());
                                }
                            }
                        } else if (status == 1) {
                            cosmetics.setOwned(true);
                        }
                    }
                }
            }
            selectedItem = 0;
            selectedType = 0;
            f.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex + "dummy");
        }
    }

    public void addCosmetic(int type, Cosmetics c) {allItems.get(type).add(c);}

    public void saveItems() {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/assets/shop/items.txt")));
            ArrayList<ArrayList<Cosmetics>> items = this.allItems;
            for (int j = 0; j < items.size(); j++) {
                ArrayList<Cosmetics> allItem = items.get(j);
                for (int i = 0; i < allItem.size(); i++) {
                    Cosmetics cosmetics = allItem.get(i);
                    if (equippedItems[j] == i && cosmetics.getOwned()) {
                        out.println(2);
                    } else if (cosmetics.getOwned()) {
                        out.println(1);
                    } else {
                        out.println(0);
                    }
                }
            }
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

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
            if (keys[Util.a]) {
                shopTimer.restart();
                if (selectedType > 0) {
                    selectedType--;
                }
            } else if (keys[Util.d]) {
                shopTimer.restart();
                if (selectedType < allItems.size()-1) {
                    selectedType++;
                }
            } else if (keys[Util.w]) {
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
                if (!item.getOwned()) {
                    if (item.purchase(gems)) {
                        gems.setTotalGems(gems.getTotalGems() - item.getCost());
                        GameManager.saveGems();
                        item.setOwned(true);
                        saveItems();
                    }
                } else {
                    switch (item.getType()) {
                        case "BACKGROUNDS" -> {
                            equippedItems[selectedType] = selectedItem;
                            Background.setBg(allItems.get(selectedType).get(selectedItem).getEnlargedImg());
                        }
                        case "BLASTERS" -> {
                            equippedItems[selectedType] = selectedItem;
                            AAdventure.getIntro().getAlan().setWeapon(item.getBlaster());
                            AAdventure.getGame().getAlan().setWeapon(item.getBlaster());
                        }
                    }
                    saveItems();
                }
            }
        }
    }

    public void drawArrows(Graphics g) {
        g.drawImage(arrowLeft, Background.getWallLeftPos()+Background.getWallWidth(), AAdventure.getGameHeight()/2-11, null);
        g.drawImage(arrowRight, Background.getWallRightPos()-22, AAdventure.getGameHeight()/2-11, null);

        g.drawImage(arrowLeftB, Background.getWallLeftPos()+Background.getWallWidth(), (int) (equippedItems[selectedType]*200+offsetY+17), null);
        g.drawImage(arrowRightB, Background.getWallRightPos()-22, (int) (equippedItems[selectedType]*200+offsetY+17), null);
    }

    public void draw(Graphics g, boolean[] keys, Gems gems) {

        // getting keyinput and new selections
        selectItem(keys, gems);

        drawArrows(g);

        g.setColor(Color.WHITE);
        g.setFont(Util.fontTextSmall);

        String type = allItems.get(selectedType).get(0).getType();

        g.drawString(type, AAdventure.getGameWidth()/2 - (type.length()*7), (int) (offsetY-100));

        for (int i = 0; i < allItems.get(selectedType).size(); i++) {
            g.setFont(Util.fontTextSmall);

            // getting item to blit for easy use
            Cosmetics item = allItems.get(selectedType).get(i);

            int x = (AAdventure.getGameWidth()/2)-(item.getWidth()/2), y = (i*200)+(int)offsetY;

            //HINT: blit frame
            g.fillRect(x,y,item.getWidth()+4,item.getHeight()+4);

            if (item.getOwned()) {
                g.drawString("OWNED", x+(item.getWidth()/2)-("OWNED".length()*7), y+item.getHeight()+40);
            } else {
                g.drawImage(Gems.getGemS().get(0), x+(item.getWidth()/2)-(String.valueOf(item.getCost()).length()*7)-10, y+item.getHeight()+24, null);
                g.drawString(String.valueOf(item.getCost()),x+(item.getWidth()/2)-(String.valueOf(item.getCost()).length()*7)+8,y+item.getHeight()+40);
            }

            g.setFont(Util.fontTextSmaller);
            g.drawString(item.getName(), AAdventure.getGameWidth()/2 - (item.getName().length()*5), (y-10));
            g.drawImage(item.getImg(),x+2,y+2,null);
        }
    }
}

class Cosmetics {
    private String type, name;
    private Image img, enlargedImg;

    private Blaster blaster;

    private int width, height;

    private int cost;

    private boolean owned;

    Cosmetics(String type, String name, ImageIcon img, Blaster blaster, int width, int height, int cost) {
        this.owned = false;
        this.type = type;
        this.name = name;
        this.width = width;
        this.height = height;
        this.cost = cost;
        if (this.cost == -1) {
            this.owned = true;
            this.cost = 0;
        }
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        this.enlargedImg = this.img;
        this.blaster = blaster;
    }

    Cosmetics(String type, String name, ImageIcon img, Image enlargedImg, int width, int height, int cost) {
        this.owned = false;
        this.type = type;
        this.name = name;
        this.width = width;
        this.height = height;
        this.cost = cost;
        if (this.cost == -1) {
            this.owned = true;
            this.cost = 0;
        }
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        this.enlargedImg = enlargedImg;
        this.blaster = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Blaster getBlaster() {
        return blaster;
    }

    public void setBlaster(Blaster blaster) {
        this.blaster = blaster;
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