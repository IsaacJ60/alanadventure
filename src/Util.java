import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Util {
    // GAME FONTS
    static Font fontTitle6, fontText;

    // GAME VARIABLES
    private static int level = 0;
    public static int getLevel() {return level;}
    public static void setLevel(int level) {Util.level = level;}

    // GAME CONSTANTS
    public static final int LEVELS = 10;
    public static final int DEFAULTCOLUMNS = 9;
    public static final int BLOCKLENGTH = 35;
    public static final int MAXCHUNKSIZE = 3;
    public static final int GENERATIONSTART = 30;
    public static final int INDEX = 3, COORDS = 4;
    public static final int LEFT = 5, RIGHT = 6, TOP = 7, BOTTOM = 8, NEUTRAL = 9;
    public static final int FADEVEL = 14;

    // KEYBOARD CONSTANTS
    public static final int a = KeyEvent.VK_A, d = KeyEvent.VK_D, space = KeyEvent.VK_SPACE; // constants for keyboard input

    //
    static Random rand = new Random();

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

    public static int increaseOpacity(int alpha, boolean reverse) {
        if (reverse) {
            if (alpha < 255-Util.FADEVEL) {
                alpha += Util.FADEVEL;
            } else {
                alpha = 255;
            }
        } else {
            // opacity increase
            if (alpha > Util.FADEVEL) {
                alpha -= Util.FADEVEL;
            } else {
                alpha = 0;
            }
        }
        return (alpha);
    }

    public static void overlay(Graphics g, int r, int green,int b,int alpha) {
        g.setColor(new Color(r,green,b,alpha));
        g.fillRect(0,0,AAdventure.getGameWidth(),AAdventure.getGameHeight());
    }

    // loading all fonts
    public static void loadFonts() {
        // normal-sized font
        String fName3 = "Youtube Star.ttf";
        InputStream is3 = GamePanel.class.getResourceAsStream(fName3);
        try {
            assert is3 != null;
            fontText = Font.createFont(Font.TRUETYPE_FONT, is3).deriveFont(24f);
        } catch(IOException ex){
            System.out.println(ex + "title font");
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }

        // HINT: hmm also likie
        String fName6 = "Vlump.ttf";
        InputStream is6 = GamePanel.class.getResourceAsStream(fName6);
        try {
            assert is6 != null;
            fontTitle6 = Font.createFont(Font.TRUETYPE_FONT, is6).deriveFont(24f);
        } catch(IOException ex){
            System.out.println(ex + "title font");
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
