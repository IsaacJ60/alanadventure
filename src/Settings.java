import java.awt.*;
import java.util.ArrayList;

public class Settings {
    private final ArrayList<ArrayList<Property>> properties;
    private int settingType, settingItem, offsetY;

    public Settings(ArrayList<ArrayList<Property>> properties) {
        this.properties = properties;
        this.offsetY = 0;
        this.settingItem = 0;
        this.settingType = 0;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < properties.get(settingType).size(); i++) {
            drawSetting(g, Background.getWallLeftPos(), i*100+offsetY, properties.get(settingType).get(settingItem));
        }
    }

    public void drawSetting(Graphics g, int x, int y, Property p) {
        g.setColor(Color.WHITE);
        g.drawRect(x,y,200,20);
    }
}

class Property {
    private String name;

    Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}