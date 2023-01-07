import java.awt.Graphics;

public class Youth implements usePowers {

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

