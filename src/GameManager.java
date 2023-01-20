import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
GameManager.java
Isaac Jiang
Utility class that contains methods that update game states, such as restarting or entering a new level
Also contains all maps and the gem manager, which tracks gems globally.
 */
public class GameManager {

    // width and height of panel
    private static final int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();

    // maplist for main mode
    private static MapList maplist;
    public static MapList getMaplist() {return maplist;}

    // gem manager for full game
    private static Gems gemManager;
    public static Gems getGemManager() {return gemManager;}

    public static Block[][] introblocks;
    public static Map intromap;

    private static int rows = 200;
    private final static int levelCount = 100;

    public static void loadGems() {
        int prevGems = 0;
        try {
            Scanner f = new Scanner(new BufferedReader(new FileReader("src/assets/gems/gems.txt")));
            if (f.hasNext()) {
                prevGems = f.nextInt();
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        gemManager = new Gems(prevGems);
    }

    public static void saveGems() {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/assets/gems/gems.txt")));
            out.print(gemManager.getTotalGems());
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void loadBlasters() {
        Blaster.loadGuns();
    }

    public static void loadLevels() {
        maplist = new MapList();

        createIntro();

        loadNextMap();
    }

    public static void createIntro() {
        // CREATING INTRO LEVEL
        introblocks = new Block[50][11];
        intromap = new Map(introblocks);
        intromap.fillBlocks(Block.AIR);

        for (int i = 1; i < 4; i++) {
            intromap.placeBlock(20,i,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(20,i).setTile(MapList.wallTop);
        }
        for (int i = 20; i < 49; i++) {
            intromap.placeBlock(i,3,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(i,3).setTile(MapList.wallSideRight);
        }
        for (int i = 7; i < 10; i++) {
            intromap.placeBlock(20,i,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(20,i).setTile(MapList.wallTop);
        }
        for (int i = 20; i < 49; i++) {
            intromap.placeBlock(i,7,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(i,7).setTile(MapList.wallSideLeft);
        }

        intromap.getBlock(20,3).setTile(MapList.wallTopRight);
        intromap.getBlock(20,7).setTile(MapList.wallTopLeft);

        for (int i = 0; i < 50; i++) {
            intromap.placeBlock(i,0,Block.WALL,Util.INDEX,Util.LEFT);
        }
        for (int i = 0; i < 50; i++) {
            intromap.placeBlock(i,10,Block.WALL,Util.INDEX,Util.RIGHT);
        }

        intromap.getMapWithWallImages();
        maplist.addMap(intromap);
    }

    // changing level
    public static void toLevel(int l, boolean restart) {
        if (restart) {
            AAdventure.setCurrPanel("INTRO");
            // only add gems when dead, not after restart
            if (AAdventure.getGame().getAlan().getHealth() == 0) {
                gemManager.setTotalGems(gemManager.getTotalGems()+gemManager.getGems());
                gemManager.setGems(0);
                gemManager.setActiveGems(new ArrayList<>());
            }
            ArrayList<Map> tmp = new ArrayList<>();
            tmp.add(intromap);
            maplist.setMaps(tmp);
            Util.setLevel(l);
            AAdventure.getIntro().resetMovementKeys();
            AAdventure.getIntro().getEnemyManager().clearEnemies();
            AAdventure.getIntro().getAlan().getWeapon().setBullets(new ArrayList<>());
            Blaster weapon = AAdventure.getGame().getAlan().getWeapon();
            if (AAdventure.getGame().getPowerups().getPower(Powerups.FIREBULLET) == Powerups.PASSIVE) {
                weapon.setDamage(weapon.getDamage()/2);
                weapon.setEquippedBullet("bullet" + AAdventure.getGame().getAlan().getWeapon().getName() + "B");
            }
            weapon.setCapacity(weapon.getOriginalCapacity());
            weapon.setSpeed(weapon.getOriginalSpeed());
            AAdventure.getIntro().setAlan(new Alan(40, HEIGHT/2+50, weapon, 4, 4, 0, AAdventure.getGame().getAlan().getKeyLeft(), AAdventure.getGame().getAlan().getKeyRight(), AAdventure.getGame().getAlan().getKeyJump())); // resetting alan
            AAdventure.getIntro().setAlpha(0);
            AAdventure.getGame().setAlan(new Alan(180, HEIGHT/2-50, weapon, 4, 4, 0, AAdventure.getGame().getAlan().getKeyLeft(), AAdventure.getGame().getAlan().getKeyRight(), AAdventure.getGame().getAlan().getKeyJump())); // resetting alan
            AAdventure.getGame().setAlpha(255);
            AAdventure.getGame().setPowerups(new Powerups());
        } else {
            if (l != 1) {
                AAdventure.setCurrPanel("LEVELCLEAR"); // changing panel to level clear panel
                int a = Util.rand.nextInt(1,Powerups.powers.length-1);
                int b = Util.rand.nextInt(a+1, Powerups.powers.length);
                int c = Util.rand.nextInt(0,a);
                AAdventure.getLevelClear().setRandomPowerups(a,b,c);
                LevelClear.resetSpace();
                Powerups.selectionTimer.start();
            } else {
                AAdventure.setCurrPanel("GAME"); // set to game if on first intro part
            }

            Util.setLevel(l); // setting level to l

            rows += rows < 400 ? 50 : 0; // increment number of rows based on level

            loadNextMap();

            AAdventure.getGame().getAlan().getWeapon().setAmmo(AAdventure.getGame().getAlan().getWeapon().getCapacity());
            gemManager.setActiveGems(new ArrayList<>());
            gemManager.setTotalGems(gemManager.getTotalGems()+gemManager.getGems());
            AAdventure.getGame().getEnemyManager().clearEnemies();
            AAdventure.getGame().getEnemyManager().generateFloorEnemies(MapList.getBlocksWithoutWallImages(), AAdventure.getGame().getAlan());
            AAdventure.getGame().getEnemyManager().generateWallEnemies(MapList.getBlocksWithoutWallImages(), AAdventure.getGame().getAlan());
            AAdventure.getGame().getEnemyManager().generateFlyers(MapList.getBlocksWithoutWallImages(), AAdventure.getGame().getAlan());
            //TODO: perhaps make a reset() function in alan to avoid bugs from recreating an instance each level
            AAdventure.getGame().setAlan(new Alan(180, HEIGHT/2-50, AAdventure.getGame().getAlan().getWeapon(), AAdventure.getGame().getAlan().getHealth(), AAdventure.getGame().getAlan().getMaxHealth(), AAdventure.getGame().getAlan().getHealthProgress(), AAdventure.getGame().getAlan().getKeyLeft(), AAdventure.getGame().getAlan().getKeyRight(), AAdventure.getGame().getAlan().getKeyJump()));
        }

        saveGems();

        AAdventure.getGame().resetMovementKeys();
        AAdventure.getIntro().resetMovementKeys();
    }

    public static void loadNextMap() {
        // lazy load next map
        Map tmp = new Map(rows);
        // starting platform
        for (int j = 4; j < 7; j++) {
            tmp.placeBlock(15,j,Block.PLAT,Util.INDEX,Util.NEUTRAL);
        }
        for (int j = 1; j < 10; j++) {
            tmp.placeBlock(rows-3,j,Block.BOX,Util.INDEX,Util.NEUTRAL);
            tmp.placeBlock(rows-2,j,Block.WALL,Util.INDEX,Util.NEUTRAL);
        }

        // ending structure
        for (int i = 1; i < 4; i++) {
            tmp.placeBlock(rows-30,i,Block.WALL,Util.INDEX,Util.NEUTRAL);
            tmp.getBlock(rows-30,i).setTile(MapList.wallTop);
        }
        for (int i = rows-30; i < rows-1; i++) {
            tmp.placeBlock(i,3,Block.WALL,Util.INDEX,Util.NEUTRAL);
            tmp.getBlock(i,3).setTile(MapList.wallSideRight);
        }
        for (int i = 7; i < 10; i++) {
            tmp.placeBlock(rows-30,i,Block.WALL,Util.INDEX,Util.NEUTRAL);
            tmp.getBlock(rows-30,i).setTile(MapList.wallTop);
        }
        for (int i = rows-30; i < rows-1; i++) {
            tmp.placeBlock(i,7,Block.WALL,Util.INDEX,Util.NEUTRAL);
            tmp.getBlock(i,7).setTile(MapList.wallSideLeft);
        }

        tmp.getBlock(rows-30,3).setTile(MapList.wallTopRight);
        tmp.getBlock(rows-30,7).setTile(MapList.wallTopLeft);

        maplist.addMap(tmp);
    }

    public static void gameOver(){AAdventure.setCurrPanel("GAMEOVER");}

    public static void requestSettings(boolean[] keys) {
        if (keys[KeyEvent.VK_ESCAPE]) {
            if (AAdventure.getCurrPanel().equals("SETTINGS")) {
                AAdventure.setCurrPanel(AAdventure.getLastPanel());
                AAdventure.setLastPanel("SETTINGS");
                AAdventure.getSettingsPanel().getSettings().setChangeReady(false);
                AAdventure.getSettingsPanel().getSettings().saveSettings();
            } else {
                AAdventure.setLastPanel(AAdventure.getCurrPanel());
                AAdventure.setCurrPanel("SETTINGS");
            }
        }
    }
}
