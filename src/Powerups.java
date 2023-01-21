import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
Powerups.java
Isaac Jiang
Contains methods that activate, run, and check powerups.
also runs the method used to select powerup after beating level
Uses interface to use different powerups
//CITATION
Polymorphism Command Pattern found here - https://stackoverflow.com/a/2172730
 */

public class Powerups{

    //POWERUP CONSTANTS
    public static int NONACTIVE = 0, ACTIVE = 1, PASSIVE = 2, ONCE = 3;
    public static int RAPIDFIRE = 0, GUNPOWDER = 1, LASERSIGHT = 2, YOUTH = 3, EXTENDMAG = 4, FIREBULLET = 5;
    public static int[] powers = new int[]{RAPIDFIRE, GUNPOWDER, LASERSIGHT, YOUTH, EXTENDMAG, FIREBULLET};
    private final List<usePowers> powerups;

    // array of powerup states (nonactive, active, passive, one occurence)
    private int[] activepowers;

    // files and icons
    private final String[] powerupIconFiles = {"rapidFire","gunpowder","laserSight","youth","extended Mag", "fireBullet"};
    private Image[] powerupIcons, smallPowerupIcons;

    // for use in level clear powerup selection screen
    private int selected;

    // powerup flavour text
    private final String[] powerupFlavours = {"Increase Bullet Speed", "Breaks Nearest Blocks",
            "Farther Bullet Range + Laser", "+1 to HP or HP Overflow Bar", "Extends Current Magazine",
            "Increase Bullet Damage"};

    // timer to prevent rapid selection change when selecting powerup
    public static Util.CustomTimer selectionTimer = new Util.CustomTimer();

    // loads images, creates power states array
    public Powerups() {
        powerups = createPowerups();
        this.activepowers = new int[powers.length];
        powerupIcons = new Image[powers.length];
        smallPowerupIcons = new Image[powers.length];
        selected = 0;
        for (int i = 0; i < powers.length; i++) {
            powerupIcons[i] = new ImageIcon("src/assets/powerups/"+powerupIconFiles[i]+".png").getImage();
            smallPowerupIcons[i] = new ImageIcon("src/assets/powerups/s"+powerupIconFiles[i]+".png").getImage();
        }
    }

    // creates all powerups
    public List<usePowers> createPowerups() {
        List<usePowers> powerups = new ArrayList<>();
        powerups.add(new RapidFire());
        powerups.add(new Gunpowder());
        powerups.add(new LaserSight());
        powerups.add(new Youth());
        powerups.add(new ExtendedMag());
        powerups.add(new FireBullet());
        return powerups;
    }

    // getters and setters
    public Image[] getSmallPowerupIcons() {return smallPowerupIcons;}
    public int[] getActivepowers() {return activepowers;}
    public int getPower(int power) {return activepowers[power];}
    public void activatePower(int power) {activepowers[power] = ACTIVE;}
    public void activatePowerOnce(int power) {activepowers[power] = ONCE;}
    public void deactivatePower(int power) {activepowers[power] = NONACTIVE;}

    // goes through states array and if state requires activation, activate the power
    // deactivates if powerup state is to use once
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

    // chose power in selection screen after level beat
    public void choosePower(Graphics g, int[] chosen, boolean[] keys) {
        // only allow change in selection or finalization after 0.3 seconds
        if (selectionTimer.getElapsedTime() > 0.3) {
            // activate powerups, some activate differently than others
            if (keys[Util.space]) {
                if (chosen[selected] == YOUTH) {
                    activatePowerOnce(YOUTH);
                } else if (chosen[selected] == EXTENDMAG) {
                    activatePowerOnce(EXTENDMAG);
                } else if (chosen[selected] == FIREBULLET) {
                    activatePowerOnce(FIREBULLET);
                } else {
                    activatePower(chosen[selected]);
                }
                AAdventure.setCurrPanel("GAME");
            } else if (keys[Util.a] && selected > 0) { // switching selection
                selected--;
                selectionTimer.restart();
            } else if (keys[Util.d] && selected < 2) { // switching selection
                selected++;
                selectionTimer.restart();
            }
        }

        int xPos = Background.getWallLeftPos()+Background.getWallWidth()+40;
        for (int i = 0; i < chosen.length; i++) {
            if (i == selected) {
                // selection circle
                drawCircle(g, xPos, i);

                // text
                g.setFont(Util.fontText);
                g.setColor(Color.WHITE);
                g.drawString(powerupIconFiles[chosen[i]].toUpperCase(), 900/2 - (powerupIconFiles[chosen[i]].length()*13), 200);
                g.setFont(Util.fontTextSmaller);
                g.drawString(powerupFlavours[chosen[i]].toUpperCase(), 900/2 - (powerupFlavours[chosen[i]].length()*5), 400);
            }
            g.drawImage(powerupIcons[chosen[i]], xPos + (i*90), 300, null);
        }
    }

    // draws circle given initial xposition and number of positions moved
    public void drawCircle(Graphics g, int xPos, int i) {
        int diameter = 36 * 2;
        g.setColor(Color.WHITE);
        int x = xPos + (i * 90) - ((diameter - 50) / 2);
        g.fillOval(x, 300 - ((diameter - 50) / 2), diameter, diameter);
        g.setColor(Color.BLACK);
        g.fillOval(x + 3, 300 - ((diameter - 50) / 2) + 3, diameter - 6, diameter - 6);
    }
}

class ExtendedMag implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // runs once, adds additional mag capacity
        alan.getWeapon().setCapacity(alan.getWeapon().getCapacity()+Util.MAGINCREASE);
        alan.getWeapon().setAmmo(alan.getWeapon().getCapacity());
    }
}

class FireBullet implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // runs once, ups damage and sets new bullet image
        alan.getWeapon().setEquippedBullet("bulletFire");
        alan.getWeapon().setDamage(alan.getWeapon().getDamage()*2);
    }
}

class Gunpowder implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {}
}

class LaserSight implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // draws laser sight
        g.setColor(Color.RED);
        g.fillRect((int) (alan.getX(true)+alan.getRect().getWidth()/2),
                (int) (alan.getY(true) + alan.getRect().getHeight()/2+5),
                2,
                alan.getY(true)+500);
    }
}

class RapidFire implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // increase weapon speed
        alan.setWeaponSpeed(20);
    }
}

class Youth implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // add to health if not full, if full add to health progress, if health progress full,
        // add new health capacity
        if (alan.getHealth() < alan.getMaxHealth()) {
            alan.setHealth(alan.getHealth()+1);
        } else {
            if (alan.getHealthProgress() < 3) {
                alan.setHealthProgress(alan.getHealthProgress()+1);
            } else {
                alan.setMaxHealth(alan.getMaxHealth()+1);
                alan.setHealth(alan.getHealth()+1);
                alan.setHealthProgress(0);
            }
        }
    }
}
