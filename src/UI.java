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
        healthBar(g, alan);
        g.setFont(Util.fontText);
        g.setColor(Color.BLACK);
        g.drawString(alan.getHealth() + "/" + alan.getMaxHealth(), 90, 59);
        g.drawString("/", 113, 59);
        g.setColor(Color.WHITE);
        g.drawString(alan.getHealth() + "/" + alan.getMaxHealth(), 87, 57);
    }

    public static void healthBar(Graphics g, Alan alan) {
        g.setColor(new Color(231,9,3));
        for (int i = 0; i < alan.getHealth(); i++) {
            g.fillRect(i*200/alan.getMaxHealth() + 28, 35, 200/alan.getMaxHealth(), 19);
        }

        g.setColor(Color.BLACK);
        g.fillRect(28,30,2,27);
        g.fillRect(226,30,2,27);

        g.setColor(new Color(245,248,247));
        g.fillRect(24,29,3,27);
        g.fillRect(228,29,3,27);
        g.fillRect(28,26,200,3);
        g.fillRect(28,56,200,3);
    }

    public static void blasterUI(Graphics g, Alan alan) {
        ammoUI(g, alan);
    }

    public static void ammoUI(Graphics g, Alan alan) {
        g.setFont(Util.fontTextSmall);
        if (alan.getWeapon().getCapacity() == 2023) {
            g.drawString("AMMO: ∞ / ∞", 25, 120);
        } else {
            g.drawString("AMMO: " + alan.getWeapon().getAmmo() + " / " + alan.getWeapon().getCapacity(), 25, 120);
        }
    }
}
