import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Powerups{

    //POWERUP CONSTANTS
    //HINT: add powerups here, SHOW POWERUP IF PASSIVE OR ACTIVE, USE IF ACTIVE
    public static int NONACTIVE = 0, ACTIVE = 1, PASSIVE = 2, ONCE = 3;
    public static int RAPIDFIRE = 0, GUNPOWDER = 1, LASERSIGHT = 2, YOUTH = 3;
    public static int[] powers = new int[]{RAPIDFIRE, GUNPOWDER, LASERSIGHT, YOUTH};
    private final List<usePowers> powerups;

    private int[] activepowers;

    private final String[] powerupIconFiles = {"rapidfire","gunpowder","lasersight","youth"};
    private Image[] powerupIcons;
    private int selected;

    public static Util.CustomTimer selectionTimer = new Util.CustomTimer();

    public Powerups() {
        powerups = createPowerups();
        this.activepowers = new int[powers.length];
        powerupIcons = new Image[powers.length];
        selected = 0;
        //HINT: add powerup icon images here!
        for (int i = 0; i < powers.length; i++) {
            powerupIcons[i] = new ImageIcon("src/assets/powerups/"+powerupIconFiles[i]+".png").getImage().getScaledInstance(50,50,Image.SCALE_DEFAULT);
        }
    }

    public List<usePowers> createPowerups() {
        List<usePowers> powerups = new ArrayList<>();
        //HINT: add powerups here
        powerups.add(new RapidFire());
        powerups.add(new Gunpowder());
        powerups.add(new LaserSight());
        powerups.add(new Youth());
        return powerups;
    }

    public int getPower(int power) {
        return activepowers[power];
    }

    public void activatePower(int power) {activepowers[power] = ACTIVE;}

    public void activatePowerOnce(int power) {activepowers[power] = ONCE;}

    public void deactivatePower(int power) {
        activepowers[power] = NONACTIVE;
    }

    public void deactivateAll() {
        activepowers = new int[powers.length];
    }

    public void usePowers(Alan alan, Graphics g) {
        for (int i = 0; i < powers.length; i++) {
            if (activepowers[powers[i]] == ACTIVE) {
                powerups.get(i).usePower(alan, g);
            }
            if (activepowers[powers[i]] == ONCE) {
                powerups.get(i).usePower(alan, g);
                activepowers[powers[i]] = PASSIVE;
            }
        }
    }

    public void choosePower(Graphics g, int[] chosen, boolean[] keys) {
        if (selectionTimer.getElapsedTime() > 0.2) {
            if (keys[Util.space]) {
                // HINT: activate powerup
                if (chosen[selected] == YOUTH) {
                    activatePowerOnce(YOUTH);
                } else {
                    activatePower(chosen[selected]);
                }
                AAdventure.setCurrPanel("GAME");
            } else if (keys[Util.a] && selected > 0) {
                selected--;
                selectionTimer.restart();
            } else if (keys[Util.d] && selected < 2) {
                selected++;
                selectionTimer.restart();
            }
        }

        int xPos = Background.getWallLeftPos()+Background.getWallWidth()+40;
        for (int i = 0; i < chosen.length; i++) {
            if (i == selected) {
                // selection circle
                int diameter = 36 * 2;
                g.setColor(Color.WHITE);
                int x = xPos + (i * 90) - ((diameter - 50) / 2);
                g.fillOval(x, 300 - ((diameter-50)/2), diameter, diameter);
                g.setColor(Color.BLACK);
                g.fillOval(x + 3, 300 - ((diameter-50)/2) + 3, diameter - 6, diameter - 6);

                // text
                g.setFont(Util.fontText);
                g.setColor(Color.WHITE);
                g.drawString(powerupIconFiles[chosen[i]].toUpperCase(), 900/2 - (powerupIconFiles[chosen[i]].length()*13), 200);
            }
            g.drawImage(powerupIcons[chosen[i]], xPos + (i*90), 300, null);
        }
    }
}

