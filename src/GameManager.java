import java.awt.event.KeyEvent;

public class GameManager {

    private static final int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();

    private static MapList maplist;
    public static MapList getMaplist() {return maplist;}

    public static Block[][] introblocks;

    public static Map intromap;

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
    public static void toLevel(int l) {
        if (l != 1) {
            AAdventure.setCurrPanel("LEVELCLEAR"); // changing panel to level clear panel
        } else {
            AAdventure.setCurrPanel("GAME"); // set to game if on first intro part
        }
        Util.setLevel(l); // setting level to l
        //TODO: perhaps make a reset() function in alan to avoid bugs from recreating an instance each level
        GamePanel.setAlan(new Alan(150, HEIGHT/2-50, GamePanel.getAlan().getWeapon())); // resetting alan
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
