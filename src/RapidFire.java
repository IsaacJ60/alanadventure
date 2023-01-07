import java.awt.Graphics;

public class RapidFire implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        alan.setWeaponSpeed(20);
    }
}
