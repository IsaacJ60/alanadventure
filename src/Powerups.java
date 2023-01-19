import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Powerups{

    //POWERUP CONSTANTS
    //HINT: add powerups here, SHOW POWERUP IF PASSIVE OR ACTIVE, USE IF ACTIVE
    public static int NONACTIVE = 0, ACTIVE = 1, PASSIVE = 2, ONCE = 3;
    public static int RAPIDFIRE = 0, GUNPOWDER = 1, LASERSIGHT = 2, YOUTH = 3, EXTENDMAG = 4, FIREBULLET = 5;
    public static int[] powers = new int[]{RAPIDFIRE, GUNPOWDER, LASERSIGHT, YOUTH, EXTENDMAG, FIREBULLET};
    private final List<usePowers> powerups;

    private int[] activepowers;

    private final String[] powerupIconFiles = {"rapidFire","gunpowder","laserSight","youth","extended Mag", "fireBullet"};

    private Image[] powerupIcons, smallPowerupIcons;
    private int selected;

    public static Util.CustomTimer selectionTimer = new Util.CustomTimer();

    public Powerups() {
        powerups = createPowerups();
        this.activepowers = new int[powers.length];
        powerupIcons = new Image[powers.length];
        smallPowerupIcons = new Image[powers.length];
        selected = 0;
        //HINT: add powerup icon images here!
        for (int i = 0; i < powers.length; i++) {
            powerupIcons[i] = new ImageIcon("src/assets/powerups/"+powerupIconFiles[i]+".png").getImage();
            smallPowerupIcons[i] = new ImageIcon("src/assets/powerups/s"+powerupIconFiles[i]+".png").getImage();
        }
    }

    public List<usePowers> createPowerups() {
        List<usePowers> powerups = new ArrayList<>();
        //HINT: add powerups here
        powerups.add(new RapidFire());
        powerups.add(new Gunpowder());
        powerups.add(new LaserSight());
        powerups.add(new Youth());
        powerups.add(new ExtendedMag());
        powerups.add(new FireBullet());
        return powerups;
    }

    public Image[] getSmallPowerupIcons() {
        return smallPowerupIcons;
    }

    public void setSmallPowerupIcons(Image[] smallPowerupIcons) {
        this.smallPowerupIcons = smallPowerupIcons;
    }

    public Image[] getPowerupIcons() {
        return powerupIcons;
    }

    public void setPowerupIcons(Image[] powerupIcons) {
        this.powerupIcons = powerupIcons;
    }

    public int[] getActivepowers() {
        return activepowers;
    }

    public void setActivepowers(int[] activepowers) {
        this.activepowers = activepowers;
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
        if (selectionTimer.getElapsedTime() > 0.3) {
            if (keys[Util.space]) {
                // HINT: activate powerup
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

class ExtendedMag implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // runs once
        alan.getWeapon().setCapacity(alan.getWeapon().getCapacity()+Util.MAGINCREASE);
        alan.getWeapon().setAmmo(alan.getWeapon().getCapacity());
    }
}

class FireBullet implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        alan.getWeapon().setEquippedBullet("bulletFire");
        alan.getWeapon().setDamage(alan.getWeapon().getDamage()*2);
    }
}

class Gunpowder implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        ;
    }
}

class LaserSight implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
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
        alan.setWeaponSpeed(20);
    }
}

class Youth implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        if (alan.getHealth() < alan.getMaxHealth()) {
            alan.setHealth(alan.getHealth()+1);
        } else {
            if (alan.getHealthProgress() < 3) { // TODO: magic number
                alan.setHealthProgress(alan.getHealthProgress()+1);
            } else {
                alan.setMaxHealth(alan.getMaxHealth()+1);
                alan.setHealth(alan.getHealth()+1);
                alan.setHealthProgress(0);
            }
        }
    }
}
