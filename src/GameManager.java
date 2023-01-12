import java.awt.event.KeyEvent;
import java.util.ArrayList;

//TODO: RESTART GAME FUNCTIONALITY

public class GameManager {

    private static final int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();

    private static MapList maplist;
    public static MapList getMaplist() {return maplist;}

    private static Gems gemManager;
    public static Gems getGemManager() {return gemManager;}

    public static Block[][] introblocks;
    public static Map intromap;
    private static int rows = 100;
    private static int levelCount = 100;

    public static void loadGems() {
        gemManager = new Gems();
        gemManager.setTotalGems(999);
        //TODO: get total gems from file
    }

    public static void loadLevels() {
        maplist = new MapList();

        createIntro();

        Map tmp = new Map(rows);
        for (int j = 4; j < 7; j++) {
            tmp.placeBlock(15,j,Block.PLAT,Util.INDEX,Util.NEUTRAL);
        }
        for (int j = 1; j < 10; j++) {
            tmp.placeBlock(rows-3,j,Block.BOX,Util.INDEX,Util.NEUTRAL);
            tmp.placeBlock(rows-2,j,Block.WALL,Util.INDEX,Util.NEUTRAL);
        }
            maplist.addMap(tmp);
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
            Util.setLevel(l);
            Intro.getEnemyManager().clearEnemies();
            Intro.getAlan().getWeapon().setBullets(new ArrayList<>());
            Intro.setAlan(new Alan(40, HEIGHT/2+50, GamePanel.getAlan().getWeapon(), 4, GamePanel.getAlan().getMaxHealth(), GamePanel.getAlan().getHealthProgress(), Util.a, Util.d)); // resetting alan
            GamePanel.setAlan(new Alan(180, HEIGHT/2-50, GamePanel.getAlan().getWeapon(), 4, GamePanel.getAlan().getMaxHealth(), GamePanel.getAlan().getHealthProgress(), Util.a, Util.d)); // resetting alan
            Intro.setAlpha(0);
            GamePanel.setAlpha(255);
            GamePanel.setPowerups(new Powerups());
            gemManager.setTotalGems(gemManager.getTotalGems()+gemManager.getGems());
            gemManager.setGems(0);
            //TODO: reset bullets and ammo capacity
        } else {
            if (l != 1) {
                AAdventure.setCurrPanel("LEVELCLEAR"); // changing panel to level clear panel
                int a = Util.rand.nextInt(1,Powerups.powers.length-1);
                int b = Util.rand.nextInt(a+1, Powerups.powers.length);
                int c = Util.rand.nextInt(0,a);
                LevelClear.setRandomPowerups(a,b,c);
                LevelClear.resetSpace();
                GamePanel.resetMovementKeys();
                Powerups.selectionTimer.start();
            } else {
                AAdventure.setCurrPanel("GAME"); // set to game if on first intro part
            }

            Util.setLevel(l); // setting level to l

            // lazy load next map
            if (MapList.getAllMaps().size() == Util.getLevel()+1) {
                Map tmp = new Map(rows);
                for (int j = 4; j < 7; j++) {
                    tmp.placeBlock(15,j,Block.PLAT,Util.INDEX,Util.NEUTRAL);
                }
                for (int j = 1; j < 10; j++) {
                    tmp.placeBlock(rows-3,j,Block.BOX,Util.INDEX,Util.NEUTRAL);
                    tmp.placeBlock(rows-2,j,Block.WALL,Util.INDEX,Util.NEUTRAL);
                }
                maplist.addMap(tmp);
            }

            GamePanel.getEnemyManager().clearEnemies();
//            GamePanel.getEnemyManager().addJelly(300,2000);
            GamePanel.getEnemyManager().generateSnakes(MapList.getBlocksWithoutWallImages(), GamePanel.getAlan());
            GamePanel.getEnemyManager().generateSnails(MapList.getBlocksWithoutWallImages(), GamePanel.getAlan());
//            GamePanel.getEnemyManager().generateJellies(MapList.getBlocksWithoutWallImages(), GamePanel.getAlan());
            //            GamePanel.getEnemyManager().generateBats(MapList.getBlocksWithoutWallImages(), GamePanel.getAlan());
            //TODO: perhaps make a reset() function in alan to avoid bugs from recreating an instance each level
            GamePanel.setAlan(new Alan(180, HEIGHT/2-50, GamePanel.getAlan().getWeapon(), GamePanel.getAlan().getHealth(), GamePanel.getAlan().getMaxHealth(), GamePanel.getAlan().getHealthProgress(), Util.a, Util.d)); // resetting alan
        }
    }

    public static void gameOver(){AAdventure.setCurrPanel("GAMEOVER");}

    public static void requestSettings(boolean[] keys) {
        if (keys[KeyEvent.VK_ESCAPE]) {
            if (AAdventure.getCurrPanel().equals("SETTINGS")) {
                AAdventure.setCurrPanel(AAdventure.getLastPanel());
                AAdventure.setLastPanel("SETTINGS");
            } else {
                AAdventure.setLastPanel(AAdventure.getCurrPanel());
                AAdventure.setCurrPanel("SETTINGS");
            }
        }
    }
}
