public class CustomTimer {
    private static long startTime = 0;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void restart() {
        startTime = System.currentTimeMillis();
    }

    public static double getElapsedTime() {
        return ((System.currentTimeMillis() - startTime) / 1000.0); //returns in seconds
    }
}