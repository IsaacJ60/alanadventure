import java.awt.event.KeyEvent;

//TODO: RESTART GAME FUNCTIONALITY

public class GameManager {

    private static final int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();

    private static MapList maplist;
    public static MapList getMaplist() {return maplist;}

    private static Gems gemManager;
    public static Gems getGemManager() {return gemManager;}

    public static Block[][] introblocks;
    public static Map intromap;

    public static void loadGems() {
        gemManager = new Gems();
        //TODO: get total gems from file
    }

    public static void loadLevels() {
        maplist = new MapList();

        createIntro();

        // making default levels
        for (int i = 0; i < Util.LEVELS; i++) { // loading all levels
            maplist.addMap(new Map(500));
        }
    }

    public static void createIntro() {
        // CREATING INTRO LEVEL
        introblocks = new Block[50][9];
        intromap = new Map(introblocks);
        intromap.fillBlocks(Block.AIR);

        for (int i = 0; i < 3; i++) {
            intromap.placeBlock(20,i,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(20,i).setTile(MapList.wallTop);
        }
        for (int i = 20; i < 49; i++) {
            intromap.placeBlock(i,2,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(i,2).setTile(MapList.wallSideRight);
        }
        for (int i = 6; i < 9; i++) {
            intromap.placeBlock(20,i,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(20,i).setTile(MapList.wallTop);
        }
        for (int i = 20; i < 49; i++) {
            intromap.placeBlock(i,6,Block.WALL,Util.INDEX,Util.NEUTRAL);
            intromap.getBlock(i,6).setTile(MapList.wallSideLeft);
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
            Intro.setAlan(new Alan(20, HEIGHT/2+50, GamePanel.getAlan().getWeapon(), 4, GamePanel.getAlan().getMaxHealth(), GamePanel.getAlan().getHealthProgress())); // resetting alan
            GamePanel.setAlan(new Alan(150, HEIGHT/2-50, GamePanel.getAlan().getWeapon(), 4, GamePanel.getAlan().getMaxHealth(), GamePanel.getAlan().getHealthProgress())); // resetting alan
            Intro.setAlpha(0);
            GamePanel.setAlpha(255);
        } else {
            if (l != 1) {
                AAdventure.setCurrPanel("LEVELCLEAR"); // changing panel to level clear panel
            } else {
                AAdventure.setCurrPanel("GAME"); // set to game if on first intro part
            }
            Util.setLevel(l); // setting level to l
            GamePanel.getEnemyManager().clearEnemies();
            GamePanel.getEnemyManager().generateSnakes(MapList.getBlocksWithoutWallImages(), GamePanel.getAlan());
            //TODO: perhaps make a reset() function in alan to avoid bugs from recreating an instance each level
            GamePanel.setAlan(new Alan(150, HEIGHT/2-50, GamePanel.getAlan().getWeapon(), GamePanel.getAlan().getHealth(), GamePanel.getAlan().getMaxHealth(), GamePanel.getAlan().getHealthProgress())); // resetting alan
        }
    }

    public static void gameOver(){
        AAdventure.setCurrPanel("GAMEOVER");
    }

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
