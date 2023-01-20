import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/*
MapList.java
Isaac Jiang
Contains full list of maps and tiles that make up the maps.
Contains methods that draw a map.

Map.java
Isaac Jiang
Stores a 2d Array of Blocks, represeting a map.
Contains methods to placeblocks, getblock, and to randomly generate a full level.
Contains methods that randomly generate cliffs, breakable box blocks, platforms, and pillars
 */

public class MapList {
    // ALL MAPS
    private static ArrayList<Map> maps;

    // MAP BLOCKS
    public static Tile boxTile,wallTopLeft,wallTopRight,wallBottomLeft,wallBottomRight,
            wallFullLeft,wallFullRight,wallSideLeft,wallSideRight,wallTopBottom,
            wallTop, wallBottom, platTile, wallImgLeft, wallImgRight, wallFull;

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
        wallTopBottom = new Tile("BOX", "src/tiles/topbottom.png");
        wallTop = new Tile("BOX", "src/tiles/walltop.png");
        wallBottom = new Tile("BOX", "src/tiles/wallbottom.png");
        platTile = new Tile("PLAT", "src/tiles/plat.png");
        wallImgLeft = new Tile("WALL LEFT", "src/tiles/sideleft.png");
        wallImgRight = new Tile("WALL RIGHT", "src/tiles/sideright.png");
        wallFull = new Tile("WALL FULL", "src/tiles/wallfull.png");
    }

    // add map to maps arraylist
    public void addMap(Map map) {maps.add(map);}
    public void setMaps(ArrayList<Map> m) {maps = m;}
    // get all maps
    public static ArrayList<Map> getAllMaps() {return maps;}
    // get blocks for level
    public static Block[][] getBlocksWithWallImages() {return maps.get(Util.getLevel()).getMapWithWallImages();}
    public static Block[][] getBlocksWithoutWallImages() {return maps.get(Util.getLevel()).getMap();}
    // get blocks when given level
    public static Block[][] getBlockswithWallImages(int level) {return maps.get(level).getMapWithWallImages();}

    // draw all blocks
    public void drawBlocks(Graphics g, Map m, Alan alan, boolean includeWalls, boolean includeBlocks) {
        Block[][] blocks = m.getMap(); // get blocks that contain wall images
        int x, y; // x and y for block location
        int alanY = alan.getY(false)/Util.BLOCKLENGTH; // alan's y location used for calculation visible rows
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

            if (includeBlocks) {
                // going through each block row
                for (int j = 0; j < blocks[i].length; j++) {

                    // getting x and y values of block
                    x = blocks[i][j].getX(true);
                    y = blocks[i][j].getY(true, alan);

                    // switch for each type of block
                    switch (blocks[i][j].getType()) {
                        // drawing wall blocks
                        case (Block.WALL) -> {
                            if (i>0 && i<m.getRows()-1 && blocks[i][j].getTile() != null) { // making sure checks are in bounds
                                g.drawImage(blocks[i][j].getTile().getImg(), x, y, null);
                            }
                        }
                        // drawing box blocks
                        case (Block.BOX) -> g.drawImage(boxTile.getImg(), x, y, null);

                        // drawing platform blocks
                        case (Block.PLAT) -> g.drawImage(platTile.getImg(), x, y, null);
                    }
                }
            }
        }
    }

    public void drawBlocks(Graphics g, Map m, boolean includeWalls, boolean includeBlocks) {
        Block[][] blocks = m.getMap(); // get blocks that contain wall images
        int x, y; // x and y for block location

        // only iterate through rows that are visible
        for (int i = 0; i < 30; i++) {

            if (includeWalls) {
                drawWalls(g, blocks, i);
            }

            if (includeBlocks) {
                // going through each block row
                for (int j = 0; j < blocks[i].length; j++) {

                    // getting x and y values of block
                    x = blocks[i][j].getX(true);
                    y = blocks[i][j].getY();

                    // switch for each type of block
                    switch (blocks[i][j].getType()) {
                        // drawing wall blocks
                        case (Block.WALL) -> {
                            if (i>0 && i<m.getRows()-1 && blocks[i][j].getTile() != null) { // making sure checks are in bounds
                                g.drawImage(blocks[i][j].getTile().getImg(), x, y, null);
                            }
                        }
                        // drawing box blocks
                        case (Block.BOX) -> g.drawImage(boxTile.getImg(), x, y, null);

                        // drawing platform blocks
                        case (Block.PLAT) -> g.drawImage(platTile.getImg(), x, y, null);
                    }
                }
            }
        }
    }

    public void drawWalls(Graphics g, Block[][] blocks, int i, Alan alan) {
        g.drawImage(wallImgLeft.getImg(), Background.getWallLeftPos()-15, blocks[i][0].getY(true, alan),null);
        g.drawImage(wallImgRight.getImg(), Background.getWallRightPos(), blocks[i][0].getY(true, alan),null);
    }

    public void drawWalls(Graphics g, Block[][] blocks, int i) {
        g.drawImage(wallImgLeft.getImg(), Background.getWallLeftPos(), blocks[i][0].getY(),null);
        g.drawImage(wallImgRight.getImg(), Background.getWallRightPos(), blocks[i][0].getY(),null);
    }
}

class Map {
    Block[][] map; // 1 map
    private final int columns = Util.DEFAULTCOLUMNS+2; // columns stay the same
    private final int rows; // number of rows of map, determined with constructor input

    Random rand = new Random(); // random object to get random ints for map generation

    Map(int r) { // constructor with rows as input
        this.rows = r;
        map = new Block[rows][columns];
        fillBlocks(Block.AIR);
        generateBlocks();
        getMapWithWallImages();
    }

    Map(Block[][] b) { // constructor with rows as input
        this.rows = b.length;
        map = new Block[this.rows][columns];
        fillBlocks(Block.AIR);
    }

    // getters and setters for columns and rows
    public int getColumns() {return columns;}
    public int getRows() {return rows;}
    // getting map and blocks
    public Block[][] getMap() {return map;}
    public Block getBlock(int r, int c) {
        if (r < rows && c < columns) {
            return map[r][c];
        }
        return map[0][0];
    }
    // placing blocks
    private void placeBlock(int x, int y, int r, int c, int blockType, int side) {map[r][c] = new Block(x, y, blockType, side);}

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

    // GENERATE ALL BLOCKS FOR LEVEL
    public void generateBlocks() {
        generateWallBlocks(); // BOX BLOCKS GENERATED WITHIN WALL BLOCKS
        GenerateHoveringBoxBlocks();
        generateSideWalls();
        generatePlatBlocks();
    }

    // generates walls on sides of levels
    public void generateSideWalls() {
        for (int i = 0; i < rows; i++) {
            placeBlock(i,0,Block.WALL,Util.INDEX,Util.LEFT);
        }
        for (int i = 0; i < rows; i++) {
            placeBlock(i,columns-1,Block.WALL,Util.INDEX,Util.RIGHT);
        }
    }

    // generates box blocks that spawn in the middle of the level
    public void GenerateHoveringBoxBlocks() {
        for (int i = Util.GENERATIONSTART; i < rows-Util.GENERATIONEND; i+=Util.MAXCHUNKSIZE) {
            int boxType = rand.nextInt(0,20);
            switch (boxType) {
                case 0 -> generateBoxes(i, rand.nextInt(3,5), 1);
                case 1 -> generateBoxes(i,rand.nextInt(4,6), 2);
                case 2 -> generateBoxes(i,rand.nextInt(2,4),2);
            }
        }
    }

    // generate boxes with less rng
    public void generateBoxes(int row, int length, int height) {
        int start = rand.nextInt(1,columns-length-1-1);
        for (int i = row; i < row+height; i++) {
            for (int j = start; j < start+length; j++) {
                if (rand.nextInt(0,3) != 0 && getBlock(i,j).getType() == Block.AIR) {
                    placeBlock(i,j,Block.BOX,Util.INDEX,Util.NEUTRAL);
                }
            }
        }
    }

    // choosing platforms (choosing that to spawn)
    public void generatePlatBlocks() {
        // chance to spawn every 2 chunks
        for (int i = Util.GENERATIONSTART; i < rows-Util.GENERATIONEND; i+=Util.MAXCHUNKSIZE*2) {
            int platType = rand.nextInt(0,7);
            // random length
            switch (platType) {
                case 0 -> generateFlat(i,1);
                case 1 -> generateFlat(i,2);
            }
        }
    }

    // generating platforms
    public void generateFlat(int row, int length) {
        int start;
        // choosing if platform should be centred or potentially to the side
        if (rand.nextInt(0, 3) == 0) {
            start = rand.nextInt(4, 6);
        } else {
            start = rand.nextInt(1, columns - length - 1);
        }
        for (int i = start; i < start+length; i++) {
            // making sure there is room for player to pass on sides
            if (i == start) {
                if (getBlock(row, i-1).getType() != Block.AIR) {
                    continue;
                }
            } else if (i == start+length-1) {
                if (getBlock(row, i+1).getType() != Block.AIR) {
                    continue;
                }
            } else if (getBlock(row, i).getType() != Block.AIR) {
                continue;
            }
            // neutral side because flats don't require side checks
            placeBlock(row, i, Block.PLAT, Util.INDEX, Util.NEUTRAL);
        }
    }

    // GENERATING BOX BLOCKS
    public void generateBoxBlocks(int r, int base, int side, int otherside) {
        int row = r;
        int length = rand.nextInt(1,base);
        if (side == Util.TOP) {
            if (otherside == Util.LEFT) {
                for (int j = 0; j < rand.nextInt(1,4); j++) {
                    for (int i = 1; i < length+1; i++) {
                        if (getBlock(row, i).getType() == Block.AIR) {
                            placeBlock(row, i, Block.BOX, Util.INDEX, otherside);
                        }
                    }
                    row--;
                }
            } else if (otherside == Util.RIGHT) {
                for (int j = 0; j < rand.nextInt(1,4); j++) {
                    for (int i = columns-length-1; i < columns-1; i++) {
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
        for (int i = Util.GENERATIONSTART; i < rows-Util.GENERATIONEND; i+=Util.MAXCHUNKSIZE) {
            //HINT: getting type of wall, if doesn't match any types then don't spawn
            // - increasing bound decreases wall spawns
            int wallType = rand.nextInt(0,100);
            //NOTE - 3 PATTERNS ARE:
            // - PILLAR (3x3 to 1x3 to 1x1)
            // - FUNNEL (2 sides funnel into centre)
            // - CLIFF (1 side forms cliff structure)
            int pillarSpawnRate = 14;
            int funnelSpawnRate = pillarSpawnRate+16;
            int cliffSpawnRate = funnelSpawnRate+20;
            int largeCliffSpawnRate = cliffSpawnRate+8;
            // adding to the spawn index to ensure player always has a way down
            if (wallType < pillarSpawnRate) {
                i += generatePillar(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL);
            } else if (wallType < funnelSpawnRate) {
                i += generateFunnel(i);
            } else if (wallType < cliffSpawnRate) {
                i += generateCliff(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL, 5, false);
            } else if (wallType < largeCliffSpawnRate) {
                i += generateCliff(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL, 8, true);
            }
        }
    }

    // FUNNEL IS JUST 2 OPPOSITE CLIFFS
    public int generateFunnel(int i) {
        int d1 = generateCliff(i, Util.LEFT, Block.WALL, 5, false);
        int d2 = generateCliff(i, Util.RIGHT, Block.WALL, 4, false);
        return Math.max(d1, d2);
    }

    // GENERATING CLIFFS (TRIANGLE SHAPE) - ALONGSIDE GENERATING BOXES ON TOP
    public int generateCliff(int r, int side, int type, int maxLen, boolean tryLong) {
        int row = r, longest;
        if (tryLong) {
            longest = rand.nextInt(maxLen-3,maxLen);
        } else {
            longest = rand.nextInt(2,maxLen);
        }
        // Spawning box blocks on cliff
        if (rand.nextInt(0,1) == 0) {
            generateBoxBlocks(r-1, longest, Util.TOP, side);
        }
        // CREATE CLIFF DIFFERENTLY BASED ON SIDE
        if (side == Util.LEFT) {
            for (int j = longest; j >= 0; j-=rand.nextInt(0,2)) {
                for (int i = 1; i < j+1; i++) {
                    placeBlock(row,i,type,Util.INDEX,side);
                }
                row++;
            }
        } else if (side == Util.RIGHT) {
            for (int j = longest; j >= 0; j-=rand.nextInt(0,2)) {
                for (int i = columns-j-1; i < columns-1; i++) {
                    placeBlock(row,i,type,Util.INDEX,side);
                }
                row++;
            }
        }
        return row-(r+2); // return height of cliff
    }

    // CREATE PILLAR (RECTANGULAR SHAPED)
    public int generatePillar(int r, int side, int type) {
        // length and width
        int x = rand.nextInt(0,3), y = rand.nextInt(1,4);
        if (side == Util.LEFT) {
            for (int i = r; i < r+y; i++) {
                for (int j = 1; j < x+1; j++) {
                    placeBlock(i,j,type,Util.INDEX,side);
                }
            }
        } else if (side == Util.RIGHT) {
            for (int i = r; i < r+y; i++) {
                for (int j = columns-x-1; j < columns-1; j++) {
                    placeBlock(i,j,type,Util.INDEX,side);
                }
            }
        }
        return y; // return height
    }

    // adding tile objects to wall blocks so allows us to not go through logic that checks that wall type a wall block is
    // e.g, a block in the corner of a generation needs to have a corner img as its tile image
    public Block[][] getMapWithWallImages() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getType() == Block.WALL) {
                    if (i>0 && i<rows-1) { // making sure checks are in bounds
                        if (j == 0) {
                            map[i][j].setTile(MapList.wallFull);
                        } else if (j == columns - 1) {
                            map[i][j].setTile(MapList.wallFull);
                        } else { //HINT: CHECKING IF BLOCK IS ALONE (NO VERTICAL CONNECTIONS)
                            if (map[i-1][j].getType() != Block.WALL && map[i+1][j].getType() != Block.WALL) {
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
                            } else if (map[i-1][j].getType() == Block.WALL && map[i+1][j].getType() == Block.WALL) {
                                if (map[i][j].getSide() == Util.LEFT) {
                                    if (map[i][j+1].getType() != Block.WALL) {
                                        map[i][j].setTile(MapList.wallSideRight);
                                    }
                                } else if (map[i][j].getSide() == Util.RIGHT) {
                                    if (map[i][j - 1].getType() != Block.WALL) {
                                        map[i][j].setTile(MapList.wallSideLeft);
                                    }
                                }
                                //HINT: CHECKING IF CORNER OR IF TOP/BOTTOM BLOCK
                                // - SAME LOGIC EXCEPT CORNER HAS 2 SIDES OCCUPIED
                                // - TOP/BOTTOM HAS 3 SIDES OCCUPIED AND ONE HORIZONTAL EDGE IS REMAINING
                            } else if (map[i][j].getSide() == Util.LEFT) {
                                if (map[i+1][j].getType() != Block.WALL && map[i][j+1].getType() != Block.WALL) {
                                    map[i][j].setTile(MapList.wallBottomRight);
                                } else if (map[i-1][j].getType() != Block.WALL && map[i][j+1].getType() != Block.WALL) {
                                    map[i][j].setTile(MapList.wallTopRight);
                                } else { // IF TOP RIGHT AND BOTTOM RIGHT HAVE ONE OCCUPIED SPACE
                                    if (map[i-1][j].getType() == Block.WALL) {
                                        map[i][j].setTile(MapList.wallBottom);
                                    } else if (map[i+1][j].getType() == Block.WALL) {
                                        map[i][j].setTile(MapList.wallTop);
                                    }
                                }
                            } else if (map[i][j].getSide() == Util.RIGHT) {
                                if (map[i+1][j].getType() != Block.WALL && map[i][j-1].getType() != Block.WALL) {
                                    map[i][j].setTile(MapList.wallBottomLeft);
                                } else if (map[i-1][j].getType() != Block.WALL && map[i][j-1].getType() != Block.WALL) {
                                    map[i][j].setTile(MapList.wallTopLeft);
                                } else {
                                    if (map[i-1][j].getType() == Block.WALL) {
                                        map[i][j].setTile(MapList.wallBottom);
                                    } else if (map[i+1][j].getType() == Block.WALL) {
                                        map[i][j].setTile(MapList.wallTop);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
}

