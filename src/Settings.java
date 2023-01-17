import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings {
    private final ArrayList<ArrayList<Property>> properties;
    private int settingType, settingItem, offsetY, wantedOffsetY, offsetVel;
    private boolean changeReady;
    private Rectangle resetRect;

    private final Image arrowLeft = new ImageIcon("src/tiles/arrowL.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT),
            arrowRight = new ImageIcon("src/tiles/arrowR.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT),
            arrowLeftB = new ImageIcon("src/tiles/arrowLB.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT),
            arrowRightB = new ImageIcon("src/tiles/arrowRB.png").getImage().getScaledInstance(22,44,Image.SCALE_DEFAULT);


    public static Util.CustomTimer settingsTimer = new Util.CustomTimer();

    public Settings(ArrayList<ArrayList<Property>> properties) {
        this.properties = properties;
        this.offsetY = AAdventure.getGameHeight()/2-30;
        this.wantedOffsetY = this.offsetY;
        this.settingItem = 0;
        this.settingType = 0;
        this.offsetVel = 20;
        this.changeReady = false;
        this.resetRect = new Rectangle(AAdventure.getGameWidth()/2-100, 650, 200, 50);

        try {
            Scanner f = new Scanner(new BufferedReader(new FileReader("src/assets/settings/settings.txt")));
            if (f.hasNext()) {
                for (ArrayList<Property> props : this.properties) {
                    for (Property p : props) {
                        String tmp = f.nextLine();
                        char c;
                        if (tmp.equals("SPACE")) {
                            c = Util.space;
                        } else {
                            c = tmp.charAt(0);
                        }
                        p.setValue(tmp);
                        switch (p.getName()) {
                            case "Move Left" -> {
                                AAdventure.getGame().getAlan().setKeyLeft(c);
                                AAdventure.getIntro().getAlan().setKeyLeft(c);
                            }
                            case "Move Right" -> {
                                AAdventure.getGame().getAlan().setKeyRight(c);
                                AAdventure.getIntro().getAlan().setKeyRight(c);
                            }
                            case "Jump" -> {
                                AAdventure.getGame().getAlan().setKeyJump(c);
                                AAdventure.getIntro().getAlan().setKeyJump(c);
                            }
                        }
                    }
                }
            }
            f.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex + "dummy");
        }
    }

    public void saveSettings() {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/assets/settings/settings.txt")));
            for (ArrayList<Property> props : this.properties) {
                for (Property p : props) {
                    out.println(p.getValue());
                }
            }
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setChangeReady(boolean b) {
        changeReady = b;
    }

    public void readyToChange(boolean clicked) {
        if (clicked) {
            SettingsPanel.setClicked(false);
            changeReady = !changeReady;
        }
    }

    public void changeProperty(String type, Property p, boolean[] keys, Alan a1, Alan a2) {
        if (type.equals("KEYBINDS")) {
            if (changeReady) {
                getNewKeybind(p, keys, a1, a2);
            }
        }
    }

    public void getNewKeybind(Property p, boolean[] keys, Alan a1, Alan a2) {
        for (int i = 0; i < KeyEvent.KEY_LAST; i++) {
            char c;
            if (keys[i]) {
                c = (char) (i);
                if (i == 32) {
                    p.setValue("SPACE");
                } else {
                    p.setValue(String.valueOf(c));
                }
                switch (p.getName()) {
                    case "Move Left" -> {
                        a1.setKeyLeft(c);
                        a2.setKeyLeft(c);
                    }
                    case "Move Right" -> {
                        a1.setKeyRight(c);
                        a2.setKeyRight(c);
                    }
                    case "Jump" -> {
                        a1.setKeyJump(c);
                        a2.setKeyJump(c);
                    }
                }
            }
        }
    }

    public void selectProperty(boolean[] keys, boolean clicked, Alan a1, Alan a2) {

        Property property = properties.get(settingType).get(settingItem);

        // moving the screen offset to centre focused item
        if (wantedOffsetY > offsetY) {
            offsetY += offsetVel;
        }
        if (wantedOffsetY < offsetY) {
            offsetY -= offsetVel;
        }

        // changing selected item
        if (settingsTimer.getElapsedTime() > 0.4 && !changeReady) {
            if (keys[Util.a]) {
                settingsTimer.restart();
                if (settingType > 0) {
                    settingType--;
                }
            } else if (keys[Util.d]) {
                settingsTimer.restart();
                if (settingType < properties.size() - 1) {
                    settingType++;
                }
            } else if (keys[Util.w]) {
                settingsTimer.restart();
                if (settingItem > 0) {
                    offsetVel = 20;
                    settingItem--;
                    wantedOffsetY += property.getHeight()*2;
                }
            } else if (keys[Util.s]) {
                settingsTimer.restart();
                if (settingItem < properties.get(settingType).size() - 1) {
                    offsetVel = 20;
                    settingItem++;
                    wantedOffsetY -= property.getHeight()*2;
                }
            }
        }

        changeProperty(property.getType(), property, keys, a1, a2);
        readyToChange(clicked);
    }

    public void drawArrows(Graphics g) {
        g.drawImage(arrowLeft, 0, AAdventure.getGameHeight()/2-22, null);
        g.drawImage(arrowRight, AAdventure.getGameWidth()-22, AAdventure.getGameHeight()/2-22, null);
    }

    public void resetButton(Graphics g, int mx, int my, Alan a1, Alan a2, boolean clicked) {
        g.setColor(Color.WHITE);
        g.fillRect((int) resetRect.getX(), (int) resetRect.getY(),(int) resetRect.getWidth(),(int) resetRect.getHeight());
        g.setColor(Color.BLACK);
        g.setFont(Util.fontText);
        g.drawString("RESET", (int) resetRect.getX()+35, (int) resetRect.getY()+40);

        checkReset(mx, my, a1, a2, clicked);
    }

    public void checkReset(int mx, int my, Alan a1, Alan a2, boolean clicked) {
        if (resetRect.contains(mx, my) && clicked) {
            a1.setKeyJump(Util.space);
            a1.setKeyLeft(Util.a);
            a1.setKeyRight(Util.d);
            a2.setKeyJump(Util.space);
            a2.setKeyLeft(Util.a);
            a2.setKeyRight(Util.d);
            properties.get(0).get(0).setValue("A");
            properties.get(0).get(1).setValue("D");
            properties.get(0).get(2).setValue("SPACE");
        }
    }

    public void draw(Graphics g, boolean[] keys, boolean clicked, Alan a1, Alan a2, int mx, int my) {
        selectProperty(keys, clicked, a1, a2);

        drawArrows(g);

        resetButton(g, mx, my, a1, a2, clicked);

        for (int i = 0; i < properties.get(settingType).size(); i++) {

            Property property = properties.get(settingType).get(i);

            int x = (AAdventure.getGameWidth()/2)-(property.getWidth()/2), y = (i*property.getHeight()*2)+offsetY;

            drawSetting(g, x, y, properties.get(settingType).get(i));
        }
    }

    public void drawSetting(Graphics g, int x, int y, Property p) {
        g.setColor(Color.WHITE);
        g.drawRect(x,y,p.getWidth(),p.getHeight());
        g.setFont(Util.fontText);
        g.drawString(p.getType(), AAdventure.getGameWidth()/2 - (p.getType().length()*12), (offsetY-100));
        g.setFont(Util.fontTextSmall);
        g.drawString(p.getName(), x+5, y+20);
        g.drawString(p.getValue(), x+300, y+20);
    }
}

class Property {
    private String name;
    private String value;

    private String type;
    private final int width, height;

    Property(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.width = 400;
        this.height = 50;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}