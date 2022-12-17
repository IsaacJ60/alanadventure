import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: CREATE MORE RNG GENERATED BLOCKS
// PRIORITY - HIGH
// - create good patterns for platform and boxes
// - keep track of index errors and out of bounds
// - implement block instance specific collision


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
                if (blocks[i][j].getType() == Block.WALL) {
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
    public static int LASTCOLUMN = 9;
    public static int FIRSTCOLUMN = 0;
    public static int ONE = 0, GROUP = 1, ROW = 2;
    public static int INDEX = 3, COORDS = 4;

    Random rand = new Random();

    Map(int r) {
        this.rows = r;
        map = new Block[rows][columns];
        fillBlocks(Block.AIR);
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

    public void fillBlocks(int type) {
        if (type == Block.AIR) {
            int r = 0, c;
            for (int y = 0; y < rows*BLOCKLENGTH; y+=BLOCKLENGTH) {
                c = 0;
                for (int x = 0; x < columns*BLOCKLENGTH; x+=BLOCKLENGTH) {
                    placeBlock(x,y,r,c,type);
                    c++;
                }
                r++;
            }
        }
    }

    public void placeBlock(int x, int y, int r, int c, int blockType) {
                map[r][c] = new Block(x, y, blockType);
    }

    public void placeBlock(int a, int b, int blockType, int given) {
        if (given == COORDS) {
            map[a/BLOCKLENGTH][b/BLOCKLENGTH] = new Block(a, b, blockType);
        } else {
            map[a][b] = new Block(BLOCKLENGTH*b, BLOCKLENGTH*a, blockType);
        }
    }

    public void generateBlocks() {
        generateWallBlocks();
    }

    public void generateWallBlocks() {
        for (int i = 0; i < rows-3; i+=MAXCHUNKSIZE) {
            int wallChance = rand.nextInt(0,2); // approx one wall group every 9 blocks
            if (wallChance == 0) {
                int wallShape = rand.nextInt(0,2);
                if (wallShape == 0) {
                    generateBigSideGroup(i,Block.WALL);
                    System.out.println("big group wall spawn");
                } else if (wallShape == 1) {
                    generateSmallHorizontalGroup(i+rand.nextInt(0,4),Block.WALL);
                }
            }
        }
    }

    public void generateBigSideGroup(int r, int type) {
        if (type == Block.WALL) {
            int row = r;
            int longest = rand.nextInt(1,5);
            for (int j = longest; j >= 0; j-=rand.nextInt(0,3)) {
                for (int i = 0; i < j; i++) {
                    placeBlock(row,i,Block.WALL,INDEX);
                }
                row++;
            }
        }
    }

    public void generateSmallHorizontalGroup(int r, int type) {
        if (type == Block.WALL) {
            int columnEnd = rand.nextInt(0, 3);
            for (int i = 0; i < columnEnd; i++) {
                placeBlock(r,i,Block.WALL,INDEX);
            }
        }
    }

    public void generateSmallRowGroup(int r, int type) {
        if (type == Block.BOX) {
            int columnStart = rand.nextInt(0,columns);
            int columnEnd = rand.nextInt(columnStart, columns+1);
            for (int i = columnStart; i < columnEnd; i++) {
                placeBlock(r,i,Block.BOX,INDEX);
            }
        }
    }

//    public void generateBlocks() {
//        int randBlockType, randBlockPattern;
//        int r = 0, c;
//        for (int y = 0; y < rows*BLOCKLENGTH; y=r*BLOCKLENGTH) {
//            c = 0;
//            for (int x = 0; x < columns*BLOCKLENGTH; x=c*BLOCKLENGTH) {
//                if (map[r][c].getType() == Block.AIR) {
//                    randBlockType = rand.nextInt(0,6);
//                    randBlockPattern = rand.nextInt(1,2);
//                    placeBlock(x,y,r,c, randBlockType, randBlockPattern);
//                }
//                c++;
//            }
//            r+=MAXCHUNKSIZE;
//        }
//    }
//
//    public void placeBlock(int x, int y, int r, int c, int blockType, int pattern) {
//        map[r][c] = new Block(x, y, blockType);
//        if (blockType == Block.BOX) {
//            if (pattern == GROUP) {
//                int[] nums = {5,0,0,0};
//                for (int i = 1; i < MAXCHUNKSIZE+1; i++) {
//                    if (nums[i-1] == 0) {
//                        break;
//                    }
//                    nums[i] = rand.nextInt(0,nums[i-1]);
//                }
//                if (c == FIRSTCOLUMN) {
//                    for (int rows = 0; rows < MAXCHUNKSIZE; rows++) {
//                        for (int i = 0; i < nums[rows+1]; i++) {
//                            if (map[r+rows][c+i+1].getType() == Block.AIR) {
//                                map[r+rows][c+i+1] = new Block(x+(BLOCKLENGTH*i+1),y+BLOCKLENGTH*rows,Block.BOX);
//                            }
//                        }
//                    }
//                } else if (c == LASTCOLUMN) {
//                    for (int rows = 0; rows < MAXCHUNKSIZE; rows++) {
//                        for (int i = 0; i < nums[rows+1]; i++) {
//                            if (map[r-rows][c-i-1].getType() == Block.AIR) {
//                                map[r-rows][c-i-1] = new Block(x-(BLOCKLENGTH*i+1),y+BLOCKLENGTH*rows,Block.BOX);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

