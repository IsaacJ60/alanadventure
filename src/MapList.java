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

            // TMP CODE TO DISPLAY ROW NUMBERS
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i), 250, blocks[i][0].getY()+20);

            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j].getType() == Block.WALL) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(blocks[i][j].getX()+Background.getWallLeftPos()+Background.getWallWidth(), blocks[i][j].getY(), Map.BLOCKLENGTH, Map.BLOCKLENGTH);
                } else if (blocks[i][j].getType() == Block.BOX) {
                    g.setColor(Color.RED);
                    g.fillRect(blocks[i][j].getX()+Background.getWallLeftPos()+Background.getWallWidth(), blocks[i][j].getY(), Map.BLOCKLENGTH, Map.BLOCKLENGTH);
                } else if (blocks[i][j].getType() == Block.PLAT) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(blocks[i][j].getX()+Background.getWallLeftPos()+Background.getWallWidth(), blocks[i][j].getY(), Map.BLOCKLENGTH, Map.BLOCKLENGTH/4);
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
    public static int LEFT = 5, RIGHT = 6, TOP = 7, BOTTOM = 8;

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

    public Block getBlock(int a, int b) {
        return map[a][b];
    }

    public void placeBlock(int a, int b, int blockType, int given) {
        if (a < rows && b < columns) {
            if (given == COORDS) {
                map[a/BLOCKLENGTH][b/BLOCKLENGTH] = new Block(a, b, blockType);
            } else {
                map[a][b] = new Block(BLOCKLENGTH*b, BLOCKLENGTH*a, blockType);
            }
        }
    }

    public void generateBlocks() {
        generateWallBlocks(); // BOX BLOCKS GENERATED WITHIN WALL BLOCKS
    }

    public void generateBoxBlocks(int r, int base, int side, int otherside) {
        int row = r;
        int length = rand.nextInt(1,base);
        if (side == TOP) {
            if (otherside == LEFT) {
                for (int j = 0; j < rand.nextInt(1,4); j++) {
                    for (int i = 0; i < length; i++) {
                        if (getBlock(row, i).getType() == Block.AIR) {
                            placeBlock(row, i, Block.BOX, INDEX);
                        }
                    }
                    row--;
                }
            } else if (otherside == RIGHT) {
                for (int j = 0; j < rand.nextInt(1,4); j++) {
                    for (int i = columns-length; i < columns; i++) {
                        if (getBlock(row, i).getType() == Block.AIR && getBlock(row-1, i).getType() == Block.AIR) {
                            placeBlock(row, i, Block.BOX, INDEX);
                        }
                    }
                    row--;
                }
            }
        }
    }

    public void generateWallBlocks() {
        //TODO: arbitrarily starting generating wall blocks at row 3, change to something that makes sense
        for (int i = 3; i < rows-5; i+=MAXCHUNKSIZE) {
            int wallChance = rand.nextInt(0,7); // approx one wall group every 9 blocks
            //NOTE - 3 PATTERNS ARE:
            // - PILLAR (3x3 to 1x3 to 1x1)
            // - FUNNEL (2 sides funnel into centre)
            // - CLIFF (1 side forms cliff structure)
            switch (wallChance) {
                case 0 -> {
                    System.out.println("ROW: " + i + ", PILLAR");
                    generatePillar(i, rand.nextInt(LEFT, RIGHT + 1), Block.WALL);
                }
                case 1 -> {
                    System.out.println("ROW: " + i + ", FUNNEL");
                    generateFunnel(i);
                }
                case 2 -> {
                    System.out.println("ROW: " + i + ", CLIFF");
                    // create cliff at row i on random side with wall blocks
                    generateCliff(i, rand.nextInt(LEFT, RIGHT + 1), Block.WALL);
                }
            }
        }
    }

    public void generateFunnel(int i) {
        generateCliff(i, LEFT, Block.WALL);
        generateCliff(i, RIGHT, Block.WALL);
    }

    public void generateCliff(int r, int side, int type) {
        int row = r;
        int longest = rand.nextInt(2,5);
        if (rand.nextInt(0,1) == 0) {
            System.out.println("ROW: " + (r-1) + ", BOX BLOCKS");
            generateBoxBlocks(r-1, longest, TOP, side); // GENERATE BOXES ON FUNNEL!
        }
        if (side == LEFT) {
            for (int j = longest; j >= 0; j-=rand.nextInt(0,2)) {
                for (int i = 0; i < j; i++) {
                    placeBlock(row,i,type,INDEX);
                }
                row++;
            }
        } else if (side == RIGHT) {
            for (int j = longest; j >= 0; j-=rand.nextInt(0,2)) {
                for (int i = columns-j; i < columns; i++) {
                    placeBlock(row,i,type,INDEX);
                }
                row++;
            }
        }
    }

    public void generatePillar(int r, int side, int type) {
        int x = rand.nextInt(0,3), y = rand.nextInt(1,4);
        if (side == LEFT) {
            for (int i = r; i < r+y; i++) {
                for (int j = 0; j < x; j++) {
                    placeBlock(i,j,type,INDEX);
                }
            }
        } else if (side == RIGHT) {
            for (int i = r; i < r+y; i++) {
                for (int j = columns-x; j < columns; j++) {
                    placeBlock(i,j,type,INDEX);
                }
            }
        }
    }

//    public void generateSmallRowGroup(int r, int type) {
//        if (type == Block.BOX) {
//            int columnStart = rand.nextInt(0,columns);
//            int columnEnd = rand.nextInt(columnStart, columns+1);
//            for (int i = columnStart; i < columnEnd; i++) {
//                placeBlock(r,i,Block.BOX,INDEX);
//            }
//        }
//    }

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

