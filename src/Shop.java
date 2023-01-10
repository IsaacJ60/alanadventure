import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Shop {
    private ArrayList<ArrayList<Cosmetics>> allItems;
    private int selectedItem;
    private int selectedType;

    public Shop(ArrayList<ArrayList<Cosmetics>> allItems) {
        this.allItems = allItems;
        this.selectedItem = 0;
        this.selectedType = 0;
    }

    public void addCosmetic(int type, Cosmetics c) {allItems.get(type).add(c);}

    public void draw(Graphics g) {
        for (int i = 0; i < allItems.get(selectedType).size(); i++) {
            Cosmetics item = allItems.get(selectedType).get(i);
            g.drawImage(item.getImg(), (AAdventure.getGameWidth()/2)-(item.getWidth()/2), 100+(i*200), null);
        }
    }
}

class Cosmetics {
    private String type;
    private Image img;

    private int width, height;

    Cosmetics(String type, ImageIcon img, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.img = img.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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