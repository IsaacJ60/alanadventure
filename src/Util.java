public class Util {
    // GAME CONSTANTS
    public static int BLOCKLENGTH = 35;
    public static int MAXCHUNKSIZE = 3;
    public static int INDEX = 3, COORDS = 4;
    public static int LEFT = 5, RIGHT = 6, TOP = 7, BOTTOM = 8, NEUTRAL = 9;

    // TIMER METHODS AND VARIABLES
    private static long startTime = 0;
    public static void start() {startTime = System.currentTimeMillis();}
    public static void restart() {startTime = System.currentTimeMillis();}
    public static double getElapsedTime() {return ((System.currentTimeMillis() - startTime) / 1000.0);}
}
