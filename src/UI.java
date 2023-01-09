import java.awt.*;

public class UI {

    public static void displayAll(Graphics g, Alan alan, Powerups powerups) {
        healthUI(g, alan);
        blasterUI(g, alan);
        powerupUI(g, powerups);
    }

    public static void powerupUI(Graphics g, Powerups powerups) {
        ;
    }

    public static void healthUI(Graphics g, Alan alan) {
        int healthOffsetX = 54 + Background.getWallRightPos();
        int healthOffsetY = 0 + 26;
        healthBar(g, alan, healthOffsetX, healthOffsetY);
        g.setFont(Util.fontText);
        g.setColor(Color.BLACK);
        g.drawString(alan.getHealth() + "/" + alan.getMaxHealth(), 66+healthOffsetX, healthOffsetY+33);
        g.drawString("/", 89+healthOffsetX, 33+healthOffsetY);
        g.setColor(Color.WHITE);
        g.drawString(alan.getHealth() + "/" + alan.getMaxHealth(), 63+healthOffsetX, healthOffsetY+31);
    }

    public static void healthBar(Graphics g, Alan alan, int healthOffsetX, int healthOffsetY) {
        g.setColor(new Color(0, 58, 109));
        for (int i = 0; i < alan.getHealth(); i++) {
            g.fillRect(i*200/alan.getMaxHealth()+4+healthOffsetX, healthOffsetY+9, 200/alan.getMaxHealth(), 19);
        }

        g.setColor(new Color(0, 90, 168));
        for (int i = 0; i < alan.getHealthProgress(); i++) {
            g.fillRect(i*200/4+4+healthOffsetX, healthOffsetY+9+26, 200/4, 6);
        }

        g.setColor(Color.BLACK);
        g.fillRect(4+healthOffsetX,healthOffsetY+4,2,27);
        g.fillRect(202+healthOffsetX,healthOffsetY+4,2,27);

        g.setColor(new Color(245,248,247));
        g.fillRect(healthOffsetX,healthOffsetY+3,3,27);
        g.fillRect(204+healthOffsetX,healthOffsetY+3,3,27);
        g.fillRect(4+healthOffsetX,healthOffsetY,200,3);
        g.fillRect(4+healthOffsetX,healthOffsetY+30,200,3);

        g.fillRect(healthOffsetX,healthOffsetY+3+26+4,3,6);
        g.fillRect(204+healthOffsetX,healthOffsetY+3+26+4,3,6);
        g.fillRect(4+healthOffsetX,healthOffsetY+30+9,200,2);
    }

    public static void blasterUI(Graphics g, Alan alan) {
        ammoUI(g, alan);
    }

    public static void ammoUI(Graphics g, Alan alan) {
        g.setFont(Util.fontTextSmall);
        if (alan.getWeapon().getCapacity() == 2023) {
            g.drawString("AMMO: ∞ / ∞", 25, 90+0);
        } else {
            g.drawString("AMMO: " + alan.getWeapon().getAmmo() + " / " + alan.getWeapon().getCapacity(), 25, 90+0);
        }
    }
}
