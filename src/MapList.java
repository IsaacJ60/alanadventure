import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: CREATE MORE RNG GENERATED BLOCKS
// PRIORITY - HIGH
// DONE - create good patterns for platform and boxes
// IN PROGRESS - keep track of index errors and out of bounds

//FIXME:
// - BOTTOM WALL BLOCK NOT SPAWNING

public class MapList {
    private static ArrayList<Map> maps; // all maps
    // tiles for blocks
    public static Tile boxTile,wallTopLeft,wallTopRight,wallBottomLeft,wallBottomRight,
            wallFullLeft,wallFullRight,wallSideLeft,wallSideRight,wallTopBottom,
            wallTop, wallBottom, platTile;

    // constructor gets images for tiles
    public MapList() {
        maps = new ArrayList<>();
        boxTile = new Tile("BOX", "src/tiles/box.png");
        wallTopLeft = new Tile("BOX", "src/tiles/walltopleft.png");
        wallTopRight = new Tile("BOX", "src/tiles/walltopright.png");
        wallBottomLeft = new Tile("BOX", "src/tiles/wallbottomleft.png");
        wallBottomRight = new Tile("BOX", "src/tiles/wallbottomright.png");
        wallFullLeft = new Tile("BOX", "src/tiles/wallfullleft.png");
        wallFullRight = new Tile("BOX", "src/tiles/wallfullright.png");
        wallSideLeft = new Tile("BOX", "src/tiles/wallsideleft.png");
        wallSideRight = new Tile("BOX", "src/tiles/wallsideright.png");
        wallTopBottom = new Tile("BOX", "src/tiles/walltopbottom.png");
        wallTop = new Tile("BOX", "src/tiles/walltop.png");
        wallBottom = new Tile("BOX", "src/tiles/wallbottom.png");
        platTile = new Tile("PLAT", "src/tiles/plat.png");
    }

    // add map to maps arraylist
    public void addMap(Map map) {
        maps.add(map);
    }

    // get all maps
    public ArrayList<Map> getAllMaps() {
        return maps;
    }

    // get blocks for level
    public static Block[][] getBlocks() {
        return maps.get(GamePanel.getLevel()).getMapWithWallImages();
    }

    public static Block[][] getBlocksWithoutWallImages() {
        return maps.get(GamePanel.getLevel()).getMap();
    }

    // get blocks when given level
    public static Block[][] getBlocks(int level) {
        return maps.get(level).getMapWithWallImages();
    }

    // draw all blocks
    public void drawBlocks(Graphics g, int level) {
        Map m = maps.get(level); // getting map for level
        Block[][] blocks = m.getMap(); // get blocks that contain wall images
        int x, y; // x and y for block location
        int alanY = Alan.getY(false)/Util.BLOCKLENGTH; // alan's y location used for calculation visible rows
        // used to measure when visible rows being and end
        int firstVisibleRow = 0, lastVisibleRow = blocks.length;
        // getting row beginning and end
        if (alanY > 15) {
            firstVisibleRow = alanY - 15;
        }
        if (alanY < m.getRows()-15) {
            lastVisibleRow = alanY + 15;
        }

        // only iterate through rows that are visible
        for (int i = firstVisibleRow; i < lastVisibleRow; i++) {

            // TMP CODE TO DISPLAY ROW NUMBERS
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i), 250, blocks[i][0].getY(true)+20);

            // going through each block row
            for (int j = 0; j < blocks[i].length; j++) {

                // getting x and y values of block
                x = blocks[i][j].getX(true);
                y = blocks[i][j].getY(true);

                // switch for each type of block
                switch (blocks[i][j].getType()) {
                    // drawing wall blocks
                    case (Block.WALL) -> {
                        if (i>0 && i<m.getRows()-1 && blocks[i][j].getTile() != null) { // making sure checks are in bounds
                            g.drawImage(blocks[i][j].getTile().getImg(), x, y, null);
                        }
                    }
                    // drawing box blocks
                    case (Block.BOX) -> {
                        g.drawImage(boxTile.getImg(), x, y, null);
                    }
                    // drawing platform blocks
                    case (Block.PLAT) -> {
                        g.drawImage(platTile.getImg(), x, y, null);
                    }
                }
            }
        }
    }
}

class Map {
    Block[][] map; // 1 map
    //TODO: move columns to the Util class or not...
    private static int columns = 9; // columns stay the same
    private int rows; // number of rows of map, determined with constructor input

    Random rand = new Random(); // random object to get random ints for map generation

    Map(int r) { // constructor with rows as input
        this.rows = r;
        map = new Block[rows][columns];
        fillBlocks(Block.AIR);
        generateBlocks();
        getMapWithWallImages();
    }

    Map(Block[][] map) { // constructor with map as input
        this.map = map;
        this.rows = map.length;
    }

    // getters and setters for columns and rows
    public static int getColumns() {return columns;}
    public int getRows() {return rows;}

    // adding tile objects to wall blocks so allows us to not go through logic that checks that wall type a wall block is
    // e.g, a block in the corner of a generation needs to have a corner img as its tile image
    public Block[][] getMapWithWallImages() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getType() == Block.WALL) {
                    if (i>0 && i<rows-1) { // making sure checks are in bounds
                        //HINT: CHECKING IF BLOCK IS ALONE (NO VERTICAL CONNECTIONS)
                        if (map[i-1][j].getType() == Block.AIR && map[i + 1][j].getType() == Block.AIR) {
                            // CHECKING SIDE
                            if (map[i][j].getSide() == Util.LEFT) {
                                if (map[i][j+1].getType() != Block.WALL) { // FULL SIDE
                                    map[i][j].setTile(MapList.wallFullRight);
                                } else { // ONLY TOP BOTTOM - STILL MORE WALL SEGMENTS AHEAD
                                    map[i][j].setTile(MapList.wallTopBottom);
                                }
                                // RIGHT SIDE LOGIC SIMILAR
                            } else if (map[i][j].getSide() == Util.RIGHT) {
                                if (map[i][j-1].getType() != Block.WALL) {
                                    map[i][j].setTile(MapList.wallFullLeft);
                                } else {
                                    map[i][j].setTile(MapList.wallTopBottom);
                                }
                            }
                            //HINT: CHECKING IF PART OF STRAIGHT WALL SEQUENCE
                        } else if (map[i - 1][j].getType() == Block.WALL && map[i + 1][j].getType() == Block.WALL) {
                            if (map[i][j].getSide() == Util.LEFT) {
                                if (map[i][j + 1].getType() == Block.AIR) {
                                    map[i][j].setTile(MapList.wallSideRight);
                                }
                            } else if (map[i][j].getSide() == Util.RIGHT) {
                                if (map[i][j - 1].getType() == Block.AIR) {
                                    map[i][j].setTile(MapList.wallSideLeft);
                                }
                            }
                            //HINT: CHECKING IF CORNER OR IF TOP/BOTTOM BLOCK
                            // - SAME LOGIC EXCEPT CORNER HAS 2 SIDES OCCUPIED
                            // - TOP/BOTTOM HAS 3 SIDES OCCUPIED AND ONE HORIZONTAL EDGE IS REMAINING
                        } else if (map[i][j].getSide() == Util.LEFT) {
                            if (map[i + 1][j].getType() == Block.AIR && map[i][j + 1].getType() == Block.AIR) {
                                map[i][j].setTile(MapList.wallBottomRight);
                            } else if (map[i - 1][j].getType() == Block.AIR && map[i][j + 1].getType() == Block.AIR) {
                                map[i][j].setTile(MapList.wallTopRight);
                            } else { // IF TOP RIGHT AND BOTTOM RIGHT HAVE ONE OCCUPIED SPACE
                                if (map[i - 1][j].getType() == Block.WALL) {
                                    map[i][j].setTile(MapList.wallBottom);
                                } else if (map[i + 1][j].getType() == Block.WALL) {
                                    map[i][j].setTile(MapList.wallTop);
                                }
                            }
                        } else if (map[i][j].getSide() == Util.RIGHT) {
                            if (map[i + 1][j].getType() == Block.AIR && map[i][j - 1].getType() == Block.AIR) {
                                map[i][j].setTile(MapList.wallBottomLeft);
                            } else if (map[i - 1][j].getType() == Block.AIR && map[i][j - 1].getType() == Block.AIR) {
                                map[i][j].setTile(MapList.wallTopLeft);
                            } else {
                                if (map[i - 1][j].getType() == Block.WALL) {
                                    map[i][j].setTile(MapList.wallBottom);
                                } else if (map[i + 1][j].getType() == Block.WALL) {
                                    map[i][j].setTile(MapList.wallTop);
                                }
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    public Block[][] getMap() {
        return map;
    }

    // FILLS ENTIRE MAP WITH ONE TYPE OF BLOCK
    public void fillBlocks(int type) {
        if (type == Block.AIR) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    placeBlock(i,j,Block.AIR,Util.INDEX,Util.NEUTRAL);
                }
            }
        }
    }

    // GET BLOCK AT COORD (a,b)
    public Block getBlock(int a, int b) {
        return map[a][b];
    }

    // PLACE BLOCK AT EITHER COORD a,b OR PIXEL LOCATION a,b
    public void placeBlock(int a, int b, int blockType, int given, int side) {
        if (a < rows && b < columns) {
            if (given == Util.COORDS) {
                placeBlock(a,b,a/Util.BLOCKLENGTH,b/Util.BLOCKLENGTH,blockType,side);
            } else {
                placeBlock(Util.BLOCKLENGTH*b,Util.BLOCKLENGTH*a,a,b,blockType,side);
            }
        }
    }

    private void placeBlock(int x, int y, int r, int c, int blockType, int side) {
        map[r][c] = new Block(x, y, blockType, side);
    }

    // GENERATE ALL BLOCKS FOR LEVEL
    public void generateBlocks() {
        generatePlatBlocks();
        generateWallBlocks(); // BOX BLOCKS GENERATED WITHIN WALL BLOCKS
    }

    public void generatePlatBlocks() {
        for (int i = 3; i < rows-5; i+=Util.MAXCHUNKSIZE) {
            //HINT: getting type of wall, if doesn't match any types then don't spawn
            // - increasing bound decreases wall spawns
            int platType = rand.nextInt(0,7);
            //NOTE - 3 PATTERNS ARE:
            // - PILLAR (3x3 to 1x3 to 1x1)
            // - FUNNEL (2 sides funnel into centre)
            // - CLIFF (1 side forms cliff structure)
            switch (platType) {
                case 0 -> {
//                    System.out.println("ROW: " + i + ", 1FLAT");
                    generateFlat(i,1);
                }
                case 1 -> {
//                    System.out.println("ROW: " + i + ", 2FLAT");
                    generateFlat(i,2);
                }
                case 2 -> {
//                    System.out.println("ROW: " + i + ", 3FLAT");
                    generateFlat(i,3);
                }
            }
        }
    }

    public void generateFlat(int row, int length) {
        int start = rand.nextInt(0,columns-length);
        for (int i = start; i < length; i++) {
            placeBlock(row, i, Block.PLAT, Util.INDEX, Util.NEUTRAL);
        }
    }

    // GENERATING BOX BLOCKS
    public void generateBoxBlocks(int r, int base, int side, int otherside) {
        int row = r;
        //HINT: gets length of box blocks, maximum is the length of the blocks below it
        int length = rand.nextInt(1,base);
        if (side == Util.TOP) {
            if (otherside == Util.LEFT) {
                for (int j = 0; j < rand.nextInt(1,4); j++) {
                    for (int i = 0; i < length; i++) {
                        if (getBlock(row, i).getType() == Block.AIR) {
                            placeBlock(row, i, Block.BOX, Util.INDEX, otherside);
                        }
                    }
                    row--;
                }
            } else if (otherside == Util.RIGHT) {
                for (int j = 0; j < rand.nextInt(1,4); j++) {
                    for (int i = columns-length; i < columns; i++) {
                        //FIXME: NULLPOINTEREXCEPTION FOR getBlock()
                        if (getBlock(row, i).getType() == Block.AIR) {
                            placeBlock(row, i, Block.BOX, Util.INDEX, otherside);
                        }
                    }
                    row--;
                }
            }
        }
    }

    // GENERATING WALL BLOCKS
    public void generateWallBlocks() {
        //TODO: arbitrarily starting generating wall blocks at row 3, change to something that makes sense
        for (int i = 3; i < rows-5; i+=Util.MAXCHUNKSIZE) {
            //HINT: getting type of wall, if doesn't match any types then don't spawn
            // - increasing bound decreases wall spawns
            int wallType = rand.nextInt(0,7);
            //NOTE - 3 PATTERNS ARE:
            // - PILLAR (3x3 to 1x3 to 1x1)
            // - FUNNEL (2 sides funnel into centre)
            // - CLIFF (1 side forms cliff structure)
            switch (wallType) {
                case 0 -> {
//                    System.out.println("ROW: " + i + ", PILLAR");
                    generatePillar(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL);
                }
                case 1 -> {
//                    System.out.println("ROW: " + i + ", FUNNEL");
                    generateFunnel(i);
                }
                case 2 -> {
//                    System.out.println("ROW: " + i + ", CLIFF");
                    // create cliff at row i on random side with wall blocks
                    generateCliff(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL, 4);
                }
            }
        }
    }

    // FUNNEL IS JUST 2 OPPOSITE CLIFFS
    public void generateFunnel(int i) {
        generateCliff(i, Util.LEFT, Block.WALL, 4);
        generateCliff(i, Util.RIGHT, Block.WALL, 4);
    }

    // GENERATING CLIFFS (TRIANGLE SHAPE) - ALONGSIDE GENERATING BOXES ON TOP
    public void generateCliff(int r, int side, int type, int maxLen) {
        int row = r;
        int longest = rand.nextInt(2,maxLen);
        //HINT: CONTROLS CHANCE OF BOXES SPAWNING ON CLIFF
        if (rand.nextInt(0,1) == 0) {
//            System.out.println("ROW: " + (r-1) + ", BOX BLOCKS");
            generateBoxBlocks(r-1, longest, Util.TOP, side); // GENERATE BOXES ON FUNNEL!
        }
        // CREATE CLIFF DIFFERENTLY BASED ON SIDE
        if (side == Util.LEFT) {
            for (int j = longest; j >= 0; j-=rand.nextInt(0,2)) {
                for (int i = 0; i < j; i++) {
                    placeBlock(row,i,type,Util.INDEX,side);
                }
                row++;
            }
        } else if (side == Util.RIGHT) {
            for (int j = longest; j >= 0; j-=rand.nextInt(0,2)) {
                for (int i = columns-j; i < columns; i++) {
                    placeBlock(row,i,type,Util.INDEX,side);
                }
                row++;
            }
        }
    }

    // CREATE PILLAR (RECTANGULAR SHAPED)
    public void generatePillar(int r, int side, int type) {
        //HINT: LENGTH AND WIDTH OF PILLAR
        int x = rand.nextInt(0,3), y = rand.nextInt(1,4);
        if (side == Util.LEFT) {
            for (int i = r; i < r+y; i++) {
                for (int j = 0; j < x; j++) {
                    placeBlock(i,j,type,Util.INDEX,side);
                }
            }
        } else if (side == Util.RIGHT) {
            for (int i = r; i < r+y; i++) {
                for (int j = columns-x; j < columns; j++) {
                    placeBlock(i,j,type,Util.INDEX,side);
                }
            }
        }
    }
}

