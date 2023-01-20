import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EnemyManager{
	private ArrayList<Snake> snakes = new ArrayList<>();
	private ArrayList<Crawler> crawlers = new ArrayList<>();
	private ArrayList<Snail> snails = new ArrayList<>();
	private ArrayList<Turtle> turtles = new ArrayList<>();
	private ArrayList<Jelly> jellies = new ArrayList<>();
	private ArrayList<Bat> bats = new ArrayList<>();

	public ArrayList<Snake> getSnakes() {return snakes;}
	public ArrayList<Crawler> getCrawlers() {return crawlers;}
	public ArrayList<Turtle> getTurtles() {return turtles;}
	public ArrayList<Snail> getSnails() {return snails;}
	public ArrayList<Jelly> getJellies() {return jellies;}
	public ArrayList<Bat> getBats() {return bats;}

	public void addSnake(int x, int y) {snakes.add(new Snake(x,y));}
	public void addSnail(int x, int y, int horiDir, int vertDir) {snails.add(new Snail(x,y,horiDir,vertDir));}
	public void addCrawler(int x, int y) {crawlers.add(new Crawler(x,y));}
	public void addTurtle(int x, int y) {turtles.add(new Turtle(x,y));}
	public void addJelly(int x, int y) {jellies.add(new Jelly(x,y));}
	public void addBat(int x, int y) {bats.add(new Bat(x,y));}

	public void generateFloorEnemies(Block[][] blocks, Alan alan) {
		for(int i=Util.GENERATIONSTART; i<blocks.length-Util.GENERATIONEND; i++){
			for(int j=1; j<blocks[i].length-1; j++) {
				if ((blocks[i-1][j].getType() == Block.AIR && blocks[i][j].getType() != Block.AIR && blocks[i][j].getType() != Block.SPIKE) && (blocks[i-1][j-1].getType() == Block.AIR && blocks[i][j-1].getType() != Block.AIR)) {
					int spawnChance = Util.rand.nextInt(100);
					if(Util.rand.nextInt(100) <= 45) {
						addSnake(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					else if (Util.rand.nextInt(100)<=20){
						addCrawler(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					if (Util.rand.nextInt(100) <= 15){
						addTurtle(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					i += Util.MAXCHUNKSIZE;
                }
			}
		}
	}

	public void generateWallEnemies(Block[][] blocks, Alan alan){
		for(int i=Util.GENERATIONSTART; i< blocks.length-Util.GENERATIONEND; i++){
			for(int j=1; j<blocks[i].length-1; j++) {
				if(blocks[i][j-1].getType() == Block.AIR && blocks[i][j].getType() == Block.WALL && blocks[i+1][j-1].getType() == Block.AIR && blocks[i+1][j].getType() == Block.WALL) {
					if(Util.rand.nextInt(100)<=45) {
						addSnail(blocks[i][j].getX(false), blocks[i][j].getY(false, alan), Snail.RIGHT, Snail.UP);
                        i += 2*Util.MAXCHUNKSIZE;
					}
				}
//				else if(blocks[i][j+1].getType() == Block.AIR && blocks[i][j].getType() == Block.WALL && blocks[i+1][j+1].getType() == Block.AIR && blocks[i+1][j].getType() == Block.WALL) {
//					if(Util.rand.nextInt(100)<=45) {
//						addSnail(blocks[i][j].getX(false), blocks[i][j].getY(false, alan), Snail.LEFT, Snail.UP);
//						i += Util.MAXCHUNKSIZE;
//					}
//				}
			}
		}
	}

	public void generateFlyers(Block[][] blocks, Alan alan){
		for(int i=Util.GENERATIONSTART; i< blocks.length-Util.GENERATIONEND; i++){
			for(int j=1; j<blocks[i].length-1; j++) {
				if(blocks[i][j].getType() == Block.AIR) {
					if(Util.rand.nextInt(100)<=.25) {
						addJelly(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
                        i += Util.MAXCHUNKSIZE;
					}
				}
				// top of block
				if(blocks[i][j].getType() == Block.AIR && blocks[i-1][j].getType() == Block.WALL) {
					if(Util.rand.nextInt(100)<=10) {
						addBat(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
						i += Util.MAXCHUNKSIZE;
					}
				}
			}
		}
	}

	public void clearEnemies(){
		snakes.clear();
		crawlers.clear();
		snails.clear();
		turtles.clear();
		jellies.clear();
		bats.clear();
	}

	public void drawEnemies(Graphics g, Alan alan, Map map){
		Block[][] blocks = map.getMap();
		for(Snake s : snakes){
			s.draw(g, blocks);
		}
		for(Crawler c: crawlers){
			c.draw(g, blocks);
		}
		for(Snail s : snails){
			s.draw(g, blocks);
		}
		for(Turtle t : turtles){
			t.draw(g, blocks);
		}
		for(Jelly j:jellies){
			j.draw(g, alan, map);
		}
		for(Bat b : bats){
			b.draw(g, alan, map);
		}
	}
}

class Snake {
	private final int LEFT = 0, RIGHT = 1;
	private int width, height;
	private int health;
	private double x, y;
	private int dir;
	private double speed;

	public double getVelX() {return velX;}

	private double velX;
	private double velY;
	private double maxVelY;
	private double accelY; // the speed and acceleration the enemy has
	private double animFrame;

	private final ArrayList<Image> idleL = new ArrayList<>();
	private final ArrayList<Image> idleR = new ArrayList<>();

	public Snake(int x, int y) {
		this.width = 32;
		this.height = 18;
		this.x = x;
		this.y = y-height;
		dir = RIGHT;
		this.health = 10;
		this.velX = Util.rand.nextInt(1,3);
		this.accelY = 1;
		this.maxVelY = Util.rand.nextInt(5,13);
		animFrame = 0;

		for (int i = 0; i < 4; i++) {
			idleL.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleL" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			idleR.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleR" + i + ".png").getImage());
		}
	}

	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public double getX(boolean adjusted) { // gets x
		if (adjusted) { // whether you want x relative to the gameplay window
			return x + Background.getWallLeftPos();
		} else {
			return x;
		}
	}
	public double getY(boolean adjusted) { // gets y
		if (adjusted) { // whether you want y relative to the gameplay window
			return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

	public void move(Block[][] blocks){
		int currRow = (int)y/Util.BLOCKLENGTH;
		int grndRow = currRow+1; // add one to get the row of blocks the snake is standing on
		int currColL = (int)x/Util.BLOCKLENGTH;
		int currColC = (int)(x+width/2)/Util.BLOCKLENGTH;
		int currColR = (int)(x+width)/Util.BLOCKLENGTH;

		if(blocks[grndRow][currColC].getType() != Block.AIR) {
			y = grndRow * Util.BLOCKLENGTH - height;
			velY = 0;
			if (dir == LEFT) {
				if (x <= 0) {
					dir = RIGHT;
					x += velX;
				} else {
					if (blocks[grndRow][currColL].getType() != Block.AIR && blocks[grndRow - 1][currColL].getType() == Block.AIR) {
						x -= velX;
					} else {
						dir = Util.RIGHT;
						x += velX;
					}
				}
			} else {
				if (x + width >= (blocks[0].length-1) * Util.BLOCKLENGTH) {
					dir = LEFT;
					x -= velX;
				} else {
					if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
						x += velX;
					} else {
						dir = LEFT;
						x -= velX;
					}
				}
			}
		}
		else{
			y+=velY;
			if(velY < maxVelY){
				velY += accelY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) {
		move(blocks);

		if(dir == LEFT) {
			if ((int) animFrame == idleL.size() - 1) {
				animFrame = 0;
			} else {
				animFrame += 0.2;
			}
			g.drawImage(idleL.get((int) animFrame), (int) getX(true), (int)getY(true), null);
		}
		else{
			if ((int) animFrame == idleR.size() - 1) {
				animFrame = 0;
			} else {
				animFrame += 0.2;
			}
			g.drawImage(idleR.get((int) animFrame), (int) getX(true), (int)getY(true), null);
		}

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
	}
}

class Crawler {
	private final int LEFT = 0, RIGHT = 1;
	private int width, height;
	private int health;
	private double x, y;
	private int dir;
	private double speed;

	public double getVelX() {return velX;}

	private double velX;
	private double velY;
	private double maxVelY;
	private double accelY; // the speed and acceleration the enemy has
	private double animFrame;

	private final ArrayList<Image> idle = new ArrayList<>();

	public Crawler(int x, int y) {
		this.width = 32;
		this.height = 22;
		this.x = x;
		this.y = y-height;
		dir = RIGHT;
		this.health = 10;
		this.velX = Util.rand.nextInt(1,3);
		this.accelY = 1;
		this.maxVelY = Util.rand.nextInt(5,13);
		animFrame = 0;

		for (int i = 0; i < 2; i++) {
			idle.add(new ImageIcon("src/assets/enemies/crawler/idle/crawlerIdle" + i + ".png").getImage());
		}
	}

	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public double getX(boolean adjusted) { // gets x
		if (adjusted) { // whether you want x relative to the gameplay window
			return x + Background.getWallLeftPos();
		} else {
			return x;
		}
	}
	public double getY(boolean adjusted) { // gets y
		if (adjusted) { // whether you want y relative to the gameplay window
			return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

	public void move(Block[][] blocks){
		int currRow = (int)y/Util.BLOCKLENGTH;
		int grndRow = currRow+1; // add one to get the row of blocks the snake is standing on
		int currColL = (int)x/Util.BLOCKLENGTH;
		int currColC = (int)(x+width/2)/Util.BLOCKLENGTH;
		int currColR = (int)(x+width)/Util.BLOCKLENGTH;

		if(blocks[grndRow][currColC].getType() != Block.AIR) {
			y = grndRow * Util.BLOCKLENGTH - height;
			velY = 0;
			if (dir == LEFT) {
				if (x <= 0) {
					dir = RIGHT;
					x += velX;
				} else {
					if (blocks[grndRow][currColL].getType() != Block.AIR && blocks[grndRow - 1][currColL].getType() == Block.AIR) {
						x -= velX;
					} else {
						dir = Util.RIGHT;
						x += velX;
					}
				}
			} else {
				if (x + width >= (blocks[0].length-1) * Util.BLOCKLENGTH) {
					dir = LEFT;
					x -= velX;
				} else {
					if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
						x += velX;
					} else {
						dir = LEFT;
						x -= velX;
					}
				}
			}
		}
		else{
			y+=velY;
			if(velY < maxVelY){
				velY += accelY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) {
		move(blocks);

		if ((int) animFrame == idle.size() - 1) {
			animFrame = 0;
		} else {
			animFrame += 0.05;
		}
		g.drawImage(idle.get((int) animFrame), (int) getX(true), (int)getY(true), null);

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
	}
}

class Turtle {
	private final int IDLE = 0, WALK = 1;
	private final int LEFT = 0, RIGHT = 1;
	private int width, height;
	private int health;
	private double x, y;
	private int dir, state;
	private double speed;
	private int moveChance, moveTime;

	public double getVelX() {return velX;}

	Util.CustomTimer movementTimer = new Util.CustomTimer();

	private double velX;
	private double velY;
	private double maxVelY;
	private double accelY; // the speed and acceleration the enemy has
	private double animFrame;

	private final ArrayList<Image> idleL = new ArrayList<>();
	private final ArrayList<Image> idleR = new ArrayList<>();
	private final ArrayList<Image> walkL = new ArrayList<>();
	private final ArrayList<Image> walkR = new ArrayList<>();

	public Turtle(int x, int y) {
		this.width = 46;
		this.height = 24;
		this.x = x;
		this.y = y-height;
		dir = RIGHT;
		this.health = 10;
		this.velX = Util.rand.nextInt(1,2);
		this.accelY = 1;
		state = WALK;
		animFrame = 0;
		moveChance = Util.rand.nextInt(100);
		moveTime = Util.rand.nextInt(1,3);

		for (int i = 0; i < 4; i++) {
			idleL.add(new ImageIcon("src/assets/enemies/turtle/idle/turtleIdleL" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			idleR.add(new ImageIcon("src/assets/enemies/turtle/idle/turtleIdleR" + i + ".png").getImage());
		}
		for (int i = 0; i < 6; i++) {
			walkL.add(new ImageIcon("src/assets/enemies/turtle/walk/turtleWalkL" + i + ".png").getImage());
		}
		for (int i = 0; i < 6; i++) {
			walkR.add(new ImageIcon("src/assets/enemies/turtle/walk/turtleWalkR" + i + ".png").getImage());
		}
	}

	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public double getX(boolean adjusted) { // gets x
		if (adjusted) { // whether you want x relative to the gameplay window
			return x + Background.getWallLeftPos();
		} else {
			return x;
		}
	}
	public double getY(boolean adjusted) { // gets y
		if (adjusted) { // whether you want y relative to the gameplay window
			return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

	public void move(Block[][] blocks) {
		int currRow = (int) y / Util.BLOCKLENGTH;
		int grndRow = currRow + 1; // add one to get the row of blocks the snake is standing on
		int currColL = (int) x / Util.BLOCKLENGTH;
		int currColC = (int) (x + width / 2) / Util.BLOCKLENGTH;
		int currColR = (int) (x + width) / Util.BLOCKLENGTH;

//		System.out.println(movementTimer.getElapsedTime());
		if (movementTimer.getElapsedTime() >= moveTime) {
			state = IDLE;
			moveChance = Util.rand.nextInt(100);
			moveTime = Util.rand.nextInt(1,3);
			movementTimer.restart();
		}
		else if(moveChance >= 50){
			state = WALK;
		}
		else{
			state = IDLE;
		}
//			animFrame = 0;
		if (blocks[grndRow][currColC].getType() != Block.AIR) {
			y = grndRow * Util.BLOCKLENGTH - height;
			velY = 0;
			if(state == WALK) {
				if (dir == LEFT) {
					if (x <= 0) {
						dir = RIGHT;
						x += velX;
					} else {
						if (blocks[grndRow][currColL].getType() != Block.AIR && blocks[grndRow - 1][currColL].getType() == Block.AIR) {
							x -= velX;
						} else {
							dir = Util.RIGHT;
							x += velX;
						}
					}
				} else {
					if (x + width >= (blocks[0].length - 1) * Util.BLOCKLENGTH) {
						dir = LEFT;
						x -= velX;
					} else {
						if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
							x += velX;
						} else {
							dir = LEFT;
							x -= velX;
						}
					}
				}
			}
		} else {
			y += velY;
			if (velY < maxVelY) {
				velY += accelY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) {
		move(blocks);

		if(state==IDLE) {
			if (dir == LEFT) {
				if ((int) animFrame >= idleL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.1;
				}
				g.drawImage(idleL.get((int) animFrame), (int) getX(true), (int) getY(true), null);
			} else {
				if ((int) animFrame >= idleR.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.1;
				}
				g.drawImage(idleR.get((int) animFrame), (int) getX(true), (int) getY(true), null);
			}
		}
		else if(state==WALK){
			if (dir == LEFT) {
				if ((int) animFrame >= walkL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.1;
				}
				g.drawImage(walkL.get((int) animFrame), (int) getX(true), (int) getY(true), null);
			} else {
				if ((int) animFrame >= walkR.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.1;
				}
				g.drawImage(walkR.get((int) animFrame), (int) getX(true), (int) getY(true), null);
			}
		}

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
	}
}

class Snail {
	public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
	private int width, height, dir;
	private int health;
	private double x, y;

	public double getVelY() {return velY;}

	private double velY; // the speed and acceleration the enemy has
	private double animFrame;

	private final ArrayList<Image> idleU = new ArrayList<>();
	private final ArrayList<Image> idleD = new ArrayList<>();

	public Snail(int x, int y, int horiDir, int vertDir) {
		this.width = 35;
		this.height = 42;
		this.x = x;
		this.y = y;
		this.dir = vertDir;
		this.health = 30;
		this.velY = 1.5;
		animFrame = 0;

		if(horiDir == LEFT){
			this.x += Util.BLOCKLENGTH;
			for (int i = 0; i < 4; i++) {
				idleU.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleLU" + i + ".png").getImage());
			}
			for (int i = 0; i < 4; i++) {
				idleD.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleLD" + i + ".png").getImage());
			}
		}
		else{
			this.x -= Util.BLOCKLENGTH;
			for (int i = 0; i < 4; i++) {
				idleU.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleRU" + i + ".png").getImage());
			}
			for (int i = 0; i < 4; i++) {
				idleD.add(new ImageIcon("src/assets/enemies/snail/idle/snailIdleRD" + i + ".png").getImage());
			}
		}
	}

	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	public int getHealth() {return health;}
	public void setHealth(int health) {this.health = health;}
	public double getX(boolean adjusted) { // gets x
		if (adjusted) { // whether you want x relative to the gameplay window
			return x + Background.getWallLeftPos();
		} else {
			return x;
		}
	}
	public double getY(boolean adjusted) { // gets y
		if (adjusted) { // whether you want y relative to the gameplay window
			return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);}

	public void move(Block[][] blocks){
		int currRow = (int)y/Util.BLOCKLENGTH;
		int topRow = (int)y/Util.BLOCKLENGTH;
		int bottomRow = (int) (y + height) / Util.BLOCKLENGTH;
		int currCol = (int) x / Util.BLOCKLENGTH;
		int wallCol;
		if(dir == LEFT) {
			wallCol = currCol-1;
		}
		else{
			wallCol = currCol+1;
		}

		if(dir == UP) {
			if (blocks[topRow][wallCol].getType() == Block.WALL && blocks[topRow][currCol].getType() == Block.AIR) {
				y -= velY;
			} else {
				dir = DOWN;
				y += velY;
			}
		}
		else{
			if (blocks[bottomRow][wallCol].getType() == Block.WALL && blocks[bottomRow][currCol].getType() == Block.AIR) {
				y += velY;
			} else {
				dir = UP;
				y -= velY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) {
		move(blocks);

		if(dir == UP) {
			if ((int) animFrame == idleU.size() - 1) {
				animFrame = 0;
			} else {
				animFrame += 0.2;
			}
			g.drawImage(idleU.get((int) animFrame), (int) getX(true), (int)getY(true), null);
		}
		else{
			if ((int) animFrame == idleD.size() - 1) {
				animFrame = 0;
			} else {
				animFrame += 0.2;
			}
			g.drawImage(idleD.get((int) animFrame), (int) getX(true), (int)getY(true), null);
		}

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
	}
}

class Jelly {
	private final int width, height, imageWidth;
	private int health;
	private double x, y;
	private double speed;

	public double getVelX() {return velX;}

	public double getVelY() {return velY;}

	private double velX;
	private double velY;
	private double maxVelX;
	private double maxVelY;
	private double accelX;
	private double accelY;
	private double accelFactor; // the speed and acceleration the enemy has
	private double animFrame;
	private boolean moveLeft, moveRight, moveUp, moveDown;
	private boolean hit;

	ArrayList<Image> idle = new ArrayList<>();
	public Jelly (int x, int y) {
		this.x = x;
		this.y = y;
		this.width = 35;
		this.imageWidth = 40;
		this.height = 30;
		this.health = 30;
		this.speed = 1;
		this.maxVelX = 2;
		this.maxVelY = 2;
		this.accelX = 0;
		this.accelY = 0;
		this.accelFactor = .06;
		animFrame = 0;
		hit = false;
		for (int i = 0; i < 4; i++) {
			idle.add(new ImageIcon("src/assets/enemies/jelly/idle/jellyIdle" + i + ".png").getImage().getScaledInstance(imageWidth, height, Image.SCALE_DEFAULT));
//			idle.set(i, idle.get(i).getScaledInstance((idle.get(i).getWidth(null)*2), (idle.get(i).getHeight(null)*2), Image.SCALE_DEFAULT));
		}
	}
	public double getX(boolean adjusted) { // gets x
		if (adjusted) { // whether you want x relative to the gameplay window
			return x + Background.getWallLeftPos();
		} else {
			return x;
		}
	}
	public double getY(boolean adjusted) { // gets y
		if (adjusted) { // whether you want y relative to the gameplay window
			return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public Rectangle getRect(){return new Rectangle((int) x,(int) y,width,height);}
	public void isHit() {hit = true;}
	public void move(Graphics g, Alan alan, Map map) {
		// distance calculations
		double distX = x + width/2 - AAdventure.getGame().getAlan().getX(false) - alan.getWidth()/2;
		// how far away the enemy is compared to alan
		double distY = y + height/2 - AAdventure.getGame().getAlan().getY(false) - alan.getHeight()/2;
		double distance = Math.hypot(distX, distY); // pythag theorem

		if(distance<400) {
			// adding up how many frames movement has been in x direction, capping out at +-20 to limit terminal velocity
			if (distX < 0 && velX < maxVelX) {
				accelX += accelFactor;
			} else if (distX > 0 && velX > -maxVelX) {
				accelX -= accelFactor;
			}
			if (distY < 0 && velY < maxVelY) {
				accelY += accelFactor;
			} else if (distY > 0 && velY > -maxVelY) {
				accelY -= accelFactor;
			}
			// moving the enemy
			velX = ((-1 / distance) * distX) * speed + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
			velY = ((-1 / distance) * distY) * speed + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan

			Block[][] blocks = map.getMap();
			// getting rows with same y values as jelly
			int prevRow = (int) getY(false) / Util.BLOCKLENGTH;
			int nextRow = prevRow + 1;
			int prevCol = (int) getX(false) / Util.BLOCKLENGTH;
			int nextCol = prevCol + 1;

			// right left collision
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						Rectangle leftSide = new Rectangle(block.getX(false), block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
//                        g.setColor(Color.CYAN);
//                        g.drawRect(block.getX(true), block.getY(true, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(leftSide)) {
							velX = -.25;
							accelX = 0;
						}
					}
				}
			}
			// left right collision
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						Rectangle rightSide = new Rectangle(block.getX(false) + Util.BLOCKLENGTH, block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
//                        g.setColor(Color.MAGENTA);
//                        g.drawRect(block.getX(true) + Util.BLOCKLENGTH, block.getY(true, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(rightSide)) {
							velX = .25;
							accelX = 0;
						}
					}
				}
			}
			// top down collision
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[nextRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					Rectangle topSide = new Rectangle(block.getX(false)+1, block.getY(false, alan), Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(topSide)) {
						velY = -.25;
						accelY = 0;
					}
				}
			}

			// bottom up collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[prevRow][i];
				// only check bottom collision in solid blocks and when going upwards in y-dir
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					Rectangle bottomSide = new Rectangle(block.getX(false)+1, block.getY(false, alan) + Util.BLOCKLENGTH, Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(bottomSide)) {
						velY = .25;
						accelY = 0;
					}
				}
			}


			if(hit){
				accelY = 4;
				hit = false;
			}
			x += velX;
			y += velY;
		}
	}

	public void draw(Graphics g, Alan alan, Map map) {
		move(g, alan, map);

		if ((int) animFrame == idle.size() - 1) {
			animFrame = 0;
		} else {
			animFrame += 0.15;
		}
		g.drawImage(idle.get((int) animFrame), (int) getX(true)-3, (int)getY(true), null);

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
//        g.drawLine((int)getX(true),0,(int)getX(true),1000);
//        g.drawLine((int)getX(true)+width,0,(int)getX(true)+width,1000);
//        g.drawLine(0,(int)getY(true),1000,(int)getY(true));
//        g.drawLine(0,(int)getY(true)+height,1000,(int)getY(true)+height);
	}
}

class Bat {
	private final int HANG = 0, FLY = 1;
	private int width, height;
	private int health;
	private double x, y;
	private double speed;
	private int state, dir;

	public double getVelX() {return velX;}

	public double getVelY() {return velY;}

	private double velX;
	private double velY;
	private double maxVelX;
	private double maxVelY;
	private double accelX;
	private double accelY;
	private double accelFactor; // the speed and acceleration the enemy has
	private double animFrame;
	private boolean moveLeft, moveRight, moveUp, moveDown;
	private boolean hit;

	ArrayList<Image> hang = new ArrayList<>();
	ArrayList<Image> flyL = new ArrayList<>();
	ArrayList<Image> flyR = new ArrayList<>();
	public Bat (int x, int y) {
		this.width = 20;
		this.height = 28;
		this.x = x + (Util.BLOCKLENGTH-this.width)/2;
		this.y = y;
		this.health = 10;
		this.maxVelX = 2.5;
		this.maxVelY = 2.5;
		this.accelX = 0;
		this.accelY = 0;
		this.accelFactor = .5;
		animFrame = 0;
		this.dir = Util.RIGHT;
		state = HANG;

		for (int i = 0; i < 4; i++) {
			hang.add(new ImageIcon("src/assets/enemies/bat/hang/batHang" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			flyL.add(new ImageIcon("src/assets/enemies/bat/fly/batFlyL" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			flyR.add(new ImageIcon("src/assets/enemies/bat/fly/batFlyR" + i + ".png").getImage());
		}
	}
	public double getX(boolean adjusted) { // gets x
		if (adjusted) { // whether you want x relative to the gameplay window
			return x + Background.getWallLeftPos();
		} else {
			return x;
		}
	}
	public double getY(boolean adjusted) { // gets y
		if (adjusted) { // whether you want y relative to the gameplay window
			return y-AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}

	int getWidth() {return width;}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public Rectangle getRect(){return new Rectangle((int) x,(int) y,width,height);}
	public void isHit() {hit = true;}
	public void move(Graphics g, Alan alan, Map map) {
		// distance calculations
		double distX = x + width/2 - alan.getX(false) - alan.getWidth()/2;
		// how far away the enemy is compared to alan
		double distY = y + height/2 - alan.getY(false) - alan.getHeight()/2;
		double distance = Math.hypot(distX, distY); // pythag theorem

		if(alan.getX(false) < x){
			dir = Util.LEFT;
		}
		else{
			dir = Util.RIGHT;
		}
		if(distance < 200){
			width = 28;
			height = 20;
			state = FLY;
		}
		if(distance < 400 && state == FLY) {
			// adding up how many frames movement has been in x direction, capping out at +-20 to limit terminal velocity
			if (distX < 0 && velX < maxVelX) {
				accelX += accelFactor;
			} else if (distX > 0 && velX > -maxVelX) {
				accelX -= accelFactor;
			}
			if (distY < 0 && velY < maxVelY) {
				accelY += accelFactor;
			} else if (distY > 0 && velY > -maxVelY) {
				accelY -= accelFactor;
			}
			// moving the enemy
			velX = ((-1 / distance) * distX) * speed + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
			velY = ((-1 / distance) * distY) * speed + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan

			Block[][] blocks = map.getMap();
			// getting rows with same y values as jelly
			int prevRow = (int) getY(false) / Util.BLOCKLENGTH;
			int nextRow = prevRow + 1;
			int prevCol = (int) getX(false) / Util.BLOCKLENGTH;
			int nextCol = prevCol + 1;

			// right left collision
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						Rectangle leftSide = new Rectangle(block.getX(false), block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
//                        g.setColor(Color.CYAN);
//                        g.drawRect(block.getX(true), block.getY(true, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(leftSide)) {
							velX = -.25;
							accelX = 0;
						}
					}
				}
			}
			// left right collision
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						Rectangle rightSide = new Rectangle(block.getX(false) + Util.BLOCKLENGTH, block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
//                        g.setColor(Color.MAGENTA);
//                        g.drawRect(block.getX(true) + Util.BLOCKLENGTH, block.getY(true, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(rightSide)) {
							velX = .25;
							accelX = 0;
						}
					}
				}
			}
			// top down collision
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[nextRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					Rectangle topSide = new Rectangle(block.getX(false)+1, block.getY(false, alan), Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(topSide)) {
						velY = -.25;
						accelY = 0;
					}
				}
			}

			// bottom up collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[prevRow][i];
				// only check bottom collision in solid blocks and when going upwards in y-dir
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					Rectangle bottomSide = new Rectangle(block.getX(false)+1, block.getY(false, alan) + Util.BLOCKLENGTH, Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(bottomSide)) {
						velY = .25;
						accelY = 0;
					}
				}
			}

			x += velX;
			y += velY;
		}
	}

	public void draw(Graphics g, Alan alan, Map map) {
		move(g, alan, map);

		if (state == HANG){
			if ((int) animFrame == hang.size() - 1) {
				animFrame = 0;
			} else {
				animFrame += 0.1;
			}
			g.drawImage(hang.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
		}
		else if (state == FLY) {
			if (dir == Util.LEFT) {
				if ((int) animFrame == flyL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.25;
				}
				g.drawImage(flyL.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
			}
			else{
				if ((int) animFrame == flyL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.25;
				}
				g.drawImage(flyR.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
			}
		}

//        g.setColor(Color.YELLOW);
//        g.drawRect((int)getX(true), (int)getY(true), width, height);
	}
}