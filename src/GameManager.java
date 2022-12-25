public class GameManager {

    private static final int WIDTH = AAdventure.getGameWidth(), HEIGHT = AAdventure.getGameHeight();

    private static MapList maplist;
    public static MapList getMaplist() {return maplist;}

    public static void loadLevels() {
        maplist = new MapList();

        // making default level
        for (int i = 0; i < Util.LEVELS; i++) { // loading all levels
            maplist.addMap(new Map(500));
        }
    }

    // changing level
    public static void toLevel(int l) {
        AAdventure.setCurrPanel("LEVELCLEAR"); // changing panel to level clear panel
        Util.setLevel(l); // setting level to l
        //TODO: perhaps make a reset() function in alan to avoid bugs from recreating an instance each level
        GamePanel.setAlan(new Alan(150, HEIGHT/2-50, GamePanel.getAlan().getWeapon())); // resetting alan
    }
}
