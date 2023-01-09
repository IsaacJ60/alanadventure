import java.awt.Graphics;

public class ExtendedMag implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        // runs once
        alan.getWeapon().setCapacity(alan.getWeapon().getCapacity()+Util.MAGINCREASE);
    }
}

