import javax.swing.*;
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
        g.setFont(Util.fontText);
        g.drawString("hp: " + alan.getHealth() +"/"+ alan.getMaxHealth(), 20, 60);
    }

    public static void blasterUI(Graphics g, Alan alan) {
        ammoUI(g);
    }

    public static void ammoUI(Graphics g) {
        ;
    }
}
