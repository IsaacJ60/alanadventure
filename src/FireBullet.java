import java.awt.*;

public class FireBullet implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        alan.getWeapon().setEquippedBullet("bulletFire");
        alan.getWeapon().setDamage(alan.getWeapon().getDamage()*2);
    }
}
