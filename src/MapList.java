import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MapList {
    ArrayList<Map> maps;
    public MapList() {
        maps = new ArrayList<>();
    }

    public void addMap(Map map) {
        maps.add(map);
    }

    public ArrayList<Map> getMaps() {
        return maps;
    }

    public void drawBlocks(Graphics g, int level) {
        Map m = maps.get(level);
        Block[][] blocks = m.getMap();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j].getType() == Block.BOX) {
                    g.setColor(Color.RED);
                    g.fillRect(blocks[i][j].getX()+Background.getWallLeftPos()+Background.getWallWidth(), blocks[i][j].getY(), Map.BLOCKLENGTH, Map.BLOCKLENGTH);
                } else if (blocks[i][j].getType() == Block.PLAT) {
                    g.setColor(Color.WHITE);
                    g.fillRect(blocks[i][j].getX()+Background.getWallLeftPos()+Background.getWallWidth(), blocks[i][j].getY(), Map.BLOCKLENGTH, Map.BLOCKLENGTH/2);
                }
            }
        }
    }
}

class Map {
    Block[][] map;
    int columns = 9, rows; // AMOUNT OF COLUMNS AND ROWS IN THE GAME WINDOW
    public static int MAXCHUNKSIZE = 3;
    public static int BLOCKLENGTH = 35; // LENGTH OF ONE BLOCK

    Map(int r) {
        this.rows = r;
        map = new Block[rows][columns];
        generateBlocks();
    }

    Map(Block[][] map) {
        this.map = map;
        this.rows = map.length;
        this.columns = map[0].length;
    }

    public Block[][] getMap() {
        return map;
    }

    public void generateBlocks() {
        Random rand = new Random();
        int randBlockType;
        int r = 0, c;
        for (int y = 0; y < rows*BLOCKLENGTH; y+=BLOCKLENGTH) {
            c = 0;
            for (int x = 0; x < columns*BLOCKLENGTH; x+=BLOCKLENGTH) {
                randBlockType = rand.nextInt(0,15);
                placeBlock(x,y,r,c, randBlockType);
                c++;
            }
            r++;
        }
    }

    public void placeBlock(int x, int y, int r, int c, int blocktype) {
        map[r][c] = new Block(x, y, blocktype);
    }
}

