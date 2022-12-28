import java.util.ArrayList;
import java.util.List;

public class Powerups{

    //POWERUP CONSTANTS
    public static int RAPIDFIRE = 0, GUNPOWDER = 1;
    public static int[] powers = new int[]{RAPIDFIRE, GUNPOWDER};
    private final List<usePowers> powerups;

    private int[] activepowers;

    public Powerups(int[] a) {
        powerups = createPowerups();
        this.activepowers = a;
    }

    public Powerups(int a) {
        powerups = createPowerups();
        this.activepowers = new int[a];
    }

    public List<usePowers> createPowerups() {
        List<usePowers> powerups = new ArrayList<>();
        powerups.add(new RapidFire());
        powerups.add(new Gunpowder());
        return powerups;
    }

    public int getPower(int power) {
        return activepowers[power];
    }

    public void activatePower(int power) {
        activepowers[power] = 1;
    }

    public void deactivatePower(int power) {
        activepowers[power] = 0;
    }

    public void deactivateAll() {
        activepowers = new int[powers.length];
    }

    public void usePowers() {
        for (int i = 0; i < powers.length; i++) {
            if (activepowers[powers[i]] == 1) {
                powerups.get(i).usePower();
            }
        }
    }
}

