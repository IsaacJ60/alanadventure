import java.awt.event.KeyEvent;

public class Util {
    // GAME CONSTANTS
    public static int BLOCKLENGTH = 35;
    public static int MAXCHUNKSIZE = 3;
    public static int INDEX = 3, COORDS = 4;
    public static int LEFT = 5, RIGHT = 6, TOP = 7, BOTTOM = 8, NEUTRAL = 9;

    // KEYBOARD CONSTANTS
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D, space = KeyEvent.VK_SPACE; // constants for keyboard input

    static class CustomTimer {
        private long startTime;
        CustomTimer() {
            // TIMER METHODS AND VARIABLES
            startTime = 0;
        }
        public void start() {startTime = System.currentTimeMillis();}
        public void restart() {startTime = System.currentTimeMillis();}
        public double getElapsedTime() {return ((System.currentTimeMillis() - startTime) / 1000.0);}
    }
}
