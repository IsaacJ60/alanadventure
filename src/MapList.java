import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: CREATE MORE RNG GENERATED BLOCKS
// PRIORITY - HIGH
// - create good patterns for platform and boxes
// - keep track of index errors and out of bounds
// - implement block instance specific collision


public class MapList {
    private static ArrayList<Map> maps;
    Tile boxTile,wallTopLeft,wallTopRight,wallBottomLeft,wallBottomRight,
            wallFullLeft,wallFullRight,wallSideLeft,wallSideRight,wallTopBottom,
            wallTop, wallBottom;
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
    }

    public void addMap(Map map) {
        maps.add(map);
    }

    public ArrayList<Map> getMaps() {
        return maps;
    }

    public static Block[][] getBlocks() {
        return maps.get(GamePanel.getLevel()).getMap();
    }

    public void drawBlocks(Graphics g, int level) {
        Map m = maps.get(level);
        Block[][] blocks = m.getMap();
        int x, y;
        for (int i = 0; i < blocks.length; i++) {

            // TMP CODE TO DISPLAY ROW NUMBERS
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(i), 250, blocks[i][0].getY()+20);

            for (int j = 0; j < blocks[i].length; j++) {

                x = blocks[i][j].getX()+Background.getWallLeftPos();
                y = blocks[i][j].getY()-Alan.getOffset();

                switch (blocks[i][j].getType()) {
                    case (Block.WALL) -> {
//                        g.setColor(Color.DARK_GRAY);
//                        g.fillRect(x, blocks[i][j].getY(), Util.BLOCKLENGTH, Util.BLOCKLENGTH);
                        if (i>0 && i<m.rows-1) { // making sure checks are in bounds
                            //HINT: CHECKING IF BLOCK IS ALONE (NO VERTICAL CONNECTIONS)
                            if (blocks[i-1][j].getType() == Block.AIR && blocks[i+1][j].getType() == Block.AIR) {
                                // CHECKING SIDE
                                if (blocks[i][j].getSide() == Util.LEFT) {
                                    if (blocks[i][j+1].getType() != Block.WALL) { // FULL SIDE
                                        g.drawImage(wallFullRight.getImg(), x, y,null);
                                    } else { // ONLY TOP BOTTOM - STILL MORE WALL SEGMENTS AHEAD
                                        g.drawImage(wallTopBottom.getImg(), x, y,null);
                                    }
                                    // RIGHT SIDE LOGIC SIMILAR
                                } else if (blocks[i][j].getSide() == Util.RIGHT) {
                                    if (blocks[i][j-1].getType() != Block.WALL) {
                                        g.drawImage(wallFullLeft.getImg(), x, y, null);
                                    } else {
                                        g.drawImage(wallTopBottom.getImg(), x, y,null);
                                    }
                                }
                                //HINT: CHECKING IF PART OF STRAIGHT WALL SEQUENCE
                            } else if (blocks[i-1][j].getType() == Block.WALL && blocks[i+1][j].getType() == Block.WALL) {
                                if (blocks[i][j].getSide() == Util.LEFT) {
                                    if (blocks[i][j+1].getType() == Block.AIR) {
                                        g.drawImage(wallSideRight.getImg(), x, y,null);
                                    }
                                } else if (blocks[i][j].getSide() == Util.RIGHT) {
                                    if (blocks[i][j-1].getType() == Block.AIR) {
                                        g.drawImage(wallSideLeft.getImg(), x, y,null);
                                    }
                                }
                                //HINT: CHECKING IF CORNER OR IF TOP/BOTTOM BLOCK
                                // - SAME LOGIC EXCEPT CORNER HAS 2 SIDES OCCUPIED
                                // - TOP/BOTTOM HAS 3 SIDES OCCUPIED AND ONE HORIZONTAL EDGE IS REMAINING
                            } else if (blocks[i][j].getSide() == Util.LEFT) {
                                if (blocks[i+1][j].getType() == Block.AIR && blocks[i][j+1].getType() == Block.AIR) {
                                    g.drawImage(wallBottomRight.getImg(), x, y, null);
                                } else if (blocks[i-1][j].getType() == Block.AIR && blocks[i][j+1].getType() == Block.AIR) {
                                    g.drawImage(wallTopRight.getImg(), x, y, null);
                                } else { // IF TOP RIGHT AND BOTTOM RIGHT HAVE ONE OCCUPIED SPACE
                                    if (blocks[i-1][j].getType() == Block.WALL) {
                                        g.drawImage(wallBottom.getImg(), x, y, null);
                                    } else if (blocks[i+1][j].getType() == Block.WALL) {
                                        g.drawImage(wallTop.getImg(), x, y, null);
                                    }
                                }
                            } else if (blocks[i][j].getSide() == Util.RIGHT) {
                                if (blocks[i+1][j].getType() == Block.AIR && blocks[i][j-1].getType() == Block.AIR) {
                                    g.drawImage(wallBottomLeft.getImg(), x, y, null);
                                } else if (blocks[i-1][j].getType() == Block.AIR && blocks[i][j-1].getType() == Block.AIR) {
                                    g.drawImage(wallTopLeft.getImg(), x, y, null);
                                } else {
                                    if (blocks[i-1][j].getType() == Block.WALL) {
                                        g.drawImage(wallBottom.getImg(), x, y, null);
                                    } else if (blocks[i+1][j].getType() == Block.WALL) {
                                        g.drawImage(wallTop.getImg(), x, y, null);
                                    }
                                }
                            }
                        }
                    }
                    case (Block.BOX) -> {
                        g.drawImage(boxTile.getImg(), x, y, null);
                    }
                    case (Block.PLAT) -> {
                        g.setColor(Color.YELLOW);
                        g.fillRect(x, y, Util.BLOCKLENGTH, Util.BLOCKLENGTH/4);
                    }
                }
            }
        }
    }
}

class Map {
    Block[][] map;
    int columns = 9, rows; // AMOUNT OF COLUMNS AND ROWS IN THE GAME WINDOW

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
        System.out.println("hmm: " + a + " " + b);
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
        map[r][c] = new Block(x+Background.getWallWidth(), y, blockType, side);
    }

    // GENERATE ALL BLOCKS FOR LEVEL
    public void generateBlocks() {
        generateWallBlocks(); // BOX BLOCKS GENERATED WITHIN WALL BLOCKS
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
                    System.out.println("ROW: " + i + ", PILLAR");
                    generatePillar(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL);
                }
                case 1 -> {
                    System.out.println("ROW: " + i + ", FUNNEL");
                    generateFunnel(i);
                }
                case 2 -> {
                    System.out.println("ROW: " + i + ", CLIFF");
                    // create cliff at row i on random side with wall blocks
                    generateCliff(i, rand.nextInt(Util.LEFT, Util.RIGHT + 1), Block.WALL);
                }
            }
        }
    }

    // FUNNEL IS JUST 2 OPPOSITE CLIFFS
    public void generateFunnel(int i) {
        generateCliff(i, Util.LEFT, Block.WALL);
        generateCliff(i, Util.RIGHT, Block.WALL);
    }

    // GENERATING CLIFFS (TRIANGLE SHAPE) - ALONGSIDE GENERATING BOXES ON TOP
    public void generateCliff(int r, int side, int type) {
        int row = r;
        int longest = rand.nextInt(2,5);
        //HINT: CONTROLS CHANCE OF BOXES SPAWNING ON CLIFF
        if (rand.nextInt(0,1) == 0) {
            System.out.println("ROW: " + (r-1) + ", BOX BLOCKS");
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

