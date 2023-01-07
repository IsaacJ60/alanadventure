import java.awt.*;

public class LaserSight implements usePowers {

    @Override
    public void usePower(Alan alan, Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int) (alan.getX(true)+alan.getRect().getWidth()/2),
                (int) (alan.getY(true) + alan.getRect().getHeight()/2+5),
                2,
                alan.getY(true)+500);
/*
        int tilt = 0;
        if (alan.getVelX() != 0) {
            if (alan.getDir() == Util.RIGHT) {
                tilt = (int) (alan.getVelX() * 3 * -1);
            } else if (alan.getDir() == Util.LEFT) {
                tilt = (int) (alan.getVelX() * 3);
            }
        }

        g.drawLine((int) (alan.getX(true)+alan.getRect().getWidth()/2),
                (int) (alan.getY(true) + alan.getRect().getHeight()/2+5),
                (int) (alan.getX(true)+alan.getRect().getWidth()/2+tilt), // + VELX
                (int) (alan.getY(true) + alan.getRect().getHeight()/2+5)+500);
*/
    }
}
