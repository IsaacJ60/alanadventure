import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
EnemyManager.java
Isaac Jiang & Jayden Zhao
Generates, draws, and holds all the arraylists of enemy objects.
 */

public class EnemyManager{
	// arraylist of all enemy types
	private ArrayList<Snake> snakes = new ArrayList<>();
	private ArrayList<Crawler> crawlers = new ArrayList<>();
	private ArrayList<Snail> snails = new ArrayList<>();
	private ArrayList<Turtle> turtles = new ArrayList<>();
	private ArrayList<Jelly> jellies = new ArrayList<>();
	private ArrayList<Bat> bats = new ArrayList<>();
	private ArrayList<Skull> skulls = new ArrayList<>();

	// methods to get arraylists of enemies for collisions
	public ArrayList<Snake> getSnakes() {return snakes;}
	public ArrayList<Crawler> getCrawlers() {return crawlers;}
	public ArrayList<Turtle> getTurtles() {return turtles;}
	public ArrayList<Snail> getSnails() {return snails;}
	public ArrayList<Jelly> getJellies() {return jellies;}
	public ArrayList<Bat> getBats() {return bats;}
	public ArrayList<Skull> getSkulls() {return skulls;}

	// methods to add enemies to their arraylists
	public void addSnake(int x, int y) {snakes.add(new Snake(x,y));}
	public void addSnail(int x, int y, int horiDir, int vertDir) {snails.add(new Snail(x,y,horiDir,vertDir));}
	public void addCrawler(int x, int y) {crawlers.add(new Crawler(x,y));}
	public void addTurtle(int x, int y) {turtles.add(new Turtle(x,y));}
	public void addJelly(int x, int y) {jellies.add(new Jelly(x,y));}
	public void addBat(int x, int y) {bats.add(new Bat(x,y));}
	public void addSkull(int x, int y) {skulls.add(new Skull(x,y));}

	// generates all enemies that crawl on the floor
	public void generateFloorEnemies(Block[][] blocks, Alan alan) {
		for(int i=Util.GENERATIONSTART; i<blocks.length-Util.GENERATIONEND; i++){
			for(int j=1; j<blocks[i].length-1; j++) {
				// if there is an open 2 wide platform
				if ((blocks[i-1][j].getType() == Block.AIR && blocks[i][j].getType() != Block.AIR) && (blocks[i-1][j-1].getType() == Block.AIR && blocks[i][j-1].getType() != Block.AIR)) {
					if(Util.rand.nextInt(100) <= 45) { // 45% chance to spawn a snake
						addSnake(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					else if (Util.rand.nextInt(100) <= 20){ // 20% chance to spawn a crawler
						addCrawler(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					else if (Util.rand.nextInt(100) <= 20){ // 20% chance to spawn a turtle
						addTurtle(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					i += Util.MAXCHUNKSIZE;
                }
			}
		}
	}

	// generates all enemies that crawl on walls
	public void generateWallEnemies(Block[][] blocks, Alan alan){
		for(int i=Util.GENERATIONSTART; i< blocks.length-Util.GENERATIONEND; i++){
			for(int j=1; j<blocks[i].length-1; j++) {
				// snails that spawn on the left of a wall
				if(blocks[i][j-1].getType() == Block.AIR && blocks[i][j].getType() == Block.WALL && blocks[i+1][j-1].getType() == Block.AIR && blocks[i+1][j].getType() == Block.WALL) {
					if(Util.rand.nextInt(100)<=50) {
						addSnail(blocks[i][j].getX(false), blocks[i][j].getY(false, alan), Util.RIGHT, Util.TOP);
                        i += 2*Util.MAXCHUNKSIZE;
					}
				}
				// snails that spawn on the right of a wall
				if(blocks[i][j+1].getType() == Block.AIR && blocks[i][j].getType() == Block.WALL && blocks[i+1][j+1].getType() == Block.AIR && blocks[i+1][j].getType() == Block.WALL) {
					if(Util.rand.nextInt(100)<=50) {
						addSnail(blocks[i][j].getX(false), blocks[i][j].getY(false, alan), Util.LEFT, Util.TOP);
						i += 2*Util.MAXCHUNKSIZE;
					}
				}
			}
		}
	}

	// generates all enemies that fly
	public void generateFlyers(Block[][] blocks, Alan alan){
		for(int i=Util.GENERATIONSTART; i< blocks.length-Util.GENERATIONEND; i++){
			for(int j=1; j<blocks[i].length-1; j++) {
				if(blocks[i][j].getType() == Block.AIR) {
					if(Util.rand.nextInt(100)<=2) {
						addJelly(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}

					if(Util.rand.nextInt(100)<=.5){
						addSkull(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					i += Util.MAXCHUNKSIZE;
				}
				// bats that hang from the bottom of a block
				if(blocks[i][j].getType() == Block.AIR && blocks[i-1][j].getType() == Block.WALL) {
					if(Util.rand.nextInt(100)<=10) {
						addBat(blocks[i][j].getX(false), blocks[i][j].getY(false, alan));
					}
					i += Util.MAXCHUNKSIZE;
				}
			}
		}
	}

	// resets all enemies for the next level
	public void clearEnemies(){
		snakes.clear();
		crawlers.clear();
		snails.clear();
		turtles.clear();
		jellies.clear();
		bats.clear();
		skulls.clear();
	}

	// draws all enemies
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
		for(Skull s : skulls){
			s.draw(g, alan, map);
		}
	}
}

/*
Snake.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for snakes
*/
class Snake {
	private double x, y; // position
	private int width, height; // dimensions
	private int health; // hp
	private int dir; // direction the snake is facing

	private double velX, velY; // snake's velocities
	private double accelY, maxVelY; // snake's accelerations/velocities for gravity
	private double animFrame; // current frame of the animation

	// arraylists that hold all frames of animation
	private final ArrayList<Image> idleL = new ArrayList<>();
	private final ArrayList<Image> idleR = new ArrayList<>();

	public Snake(int x, int y) {
		this.width = 32;
		this.height = 18;
		this.x = x;
		this.y = y-height;
		dir = Util.RIGHT;
		this.health = 10;
		this.velX = Util.rand.nextInt(1,3); // each snake moves at a random speed so they don't look uniform
		this.accelY = 1;
		this.maxVelY = Util.rand.nextInt(5,13);
		animFrame = 0;

		// adding all frames of each animation to their arraylists
		for (int i = 0; i < 4; i++) {
			idleL.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleL" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			idleR.add(new ImageIcon("src/assets/enemies/snake/idle/snakeIdleR" + i + ".png").getImage());
		}
	}

	public int getHealth() { // gets the health of the snake
		return health;
	}
	public void setHealth(int health) { // sets the health of the snake
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
			return y - AAdventure.getGame().getAlan().getOffset()+AAdventure.getGame().getAlan().getScreenOffset();
		} else {
			return y;
		}
	}
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);} // gets rect for collisions

	public void move(Block[][] blocks){ // moves the snake back and forth and checks if there is solid ground under it
		int currRow = (int)y/Util.BLOCKLENGTH; // row the snake's hitbox is occupying
		int grndRow = currRow+1; // add one to get the row of blocks the snake is standing on
		int currColL = (int)x/Util.BLOCKLENGTH; // the column the left side of the snake is on
		int currColC = (int)(x+width/2)/Util.BLOCKLENGTH; // the column the centre of the snake is on
		int currColR = (int)(x+width)/Util.BLOCKLENGTH; // the column the right side of the snake is on

		if(blocks[grndRow][currColC].getType() != Block.AIR) {
			y = grndRow * Util.BLOCKLENGTH - height;
			velY = 0;
			if (dir == Util.LEFT) {
				if (x <= 0) {
					dir = Util.RIGHT;
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
					dir = Util.LEFT;
					x -= velX;
				} else {
					if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
						x += velX;
					} else {
						dir = Util.LEFT;
						x -= velX;
					}
				}
			}
		}
		else{ // falls if there is no block below
			y+=velY;
			if(velY < maxVelY){
				velY += accelY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) { // draws and updates animation frame for the snake
		move(blocks);

		if(dir == Util.LEFT) {
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

	}
}

/*
Crawler.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for crawler
*/
class Crawler {
	private double x, y; // position
	private int width, height; // dimensions
	private int health; // hp
	private int dir; // direction the crawler is facing

	private double velX, velY; // crawler's velocities
	private double accelY, maxVelY; // crawler's accerations/velocities for gravity
	private double animFrame; // current frame of the animation

	// arraylist that holds all frames of animation
	private final ArrayList<Image> idle = new ArrayList<>();

	public Crawler(int x, int y) {
		this.width = 32;
		this.height = 22;
		this.x = x;
		this.y = y-height;
		dir = Util.RIGHT;
		this.health = 10;
		this.velX = Util.rand.nextInt(1,3); // each crawler moves at a random speed so they don't look uniform
		this.accelY = 1;
		this.maxVelY = Util.rand.nextInt(5,13);
		animFrame = 0;

		// adding all frames of each animation to their arraylists
		for (int i = 0; i < 2; i++) {
			idle.add(new ImageIcon("src/assets/enemies/crawler/idle/crawlerIdle" + i + ".png").getImage());
		}
	}

	public int getHealth() { // gets the health of the crawler
		return health;
	}
	public void setHealth(int health) { // sets the health of the crawler
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
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);} // gets rect for collisions

	public void move(Block[][] blocks){ // moves the crawler back and forth and checks if there is solid ground under it
		int currRow = (int)y/Util.BLOCKLENGTH; // row the crawler's hitbox is occupying
		int grndRow = currRow+1; // add one to get the row of blocks the crawler is standing on
		int currColL = (int)x/Util.BLOCKLENGTH; // the column the left side of the crawler is on
		int currColC = (int)(x+width/2)/Util.BLOCKLENGTH; // the column the centre of the crawler is on
		int currColR = (int)(x+width)/Util.BLOCKLENGTH; // the column the right side of the crawler is on

		if(blocks[grndRow][currColC].getType() != Block.AIR) {
			y = grndRow * Util.BLOCKLENGTH - height;
			velY = 0;
			if (dir == Util.LEFT) {
				if (x <= 0) {
					dir = Util.RIGHT;
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
					dir = Util.LEFT;
					x -= velX;
				} else {
					if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
						x += velX;
					} else {
						dir = Util.LEFT;
						x -= velX;
					}
				}
			}
		}
		else{ // falls if there is no block below
			y+=velY;
			if(velY < maxVelY){
				velY += accelY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) { // draws and updates animation frame for the crawler
		move(blocks);

		if ((int) animFrame == idle.size() - 1) {
			animFrame = 0;
		} else {
			animFrame += 0.05;
		}
		g.drawImage(idle.get((int) animFrame), (int) getX(true), (int)getY(true), null);
	}
}

/*
Turtle.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for turtles
*/
class Turtle {
	private final int IDLE = 0, WALK = 1; // states
	private double x, y; // position
	private int width, height; // dimensions
	private int health; // hp
	private int dir; // direction the turtle is facing
	private int state; // turtle's state (idle, walk)
	private int moveChance, moveTime; // whether or not the turtle moves, and how long the enemy moves/doesn't move
	Util.CustomTimer movementTimer = new Util.CustomTimer(); // timer to keep track of moveTime

	private double velX, velY; // turtle's velocities
	private double accelY, maxVelY; // turtle's accelerations/velocities for gravity
	private double animFrame; // current frame of the animation

	// arraylists that hold all frames of animation
	private final ArrayList<Image> idleL = new ArrayList<>();
	private final ArrayList<Image> idleR = new ArrayList<>();
	private final ArrayList<Image> walkL = new ArrayList<>();
	private final ArrayList<Image> walkR = new ArrayList<>();

	public Turtle(int x, int y) {
		this.width = 46;
		this.height = 24;
		this.x = x;
		this.y = y-height;
		dir = Util.RIGHT;
		this.health = 10;
		this.velX = Util.rand.nextInt(1,2); // each turtle moves at a random speed so they don't look uniform
		this.accelY = 1;
		this.maxVelY = Util.rand.nextInt(5,13);
		state = WALK;
		animFrame = 0;
		moveChance = Util.rand.nextInt(100);
		moveTime = Util.rand.nextInt(1,3);

		// adding all frames of each animation to their arraylists
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
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);} // gets rect for collisions

	public void move(Block[][] blocks) { // moves the turtle back and forth and checks if there is solid ground under it
		int currRow = (int)y/Util.BLOCKLENGTH; // row the turtle's hitbox is occupying
		int grndRow = currRow+1; // add one to get the row of blocks the turtle is standing on
		int currColL = (int)x/Util.BLOCKLENGTH; // the column the left side of the turtle is on
		int currColC = (int)(x+width/2)/Util.BLOCKLENGTH; // the column the centre of the turtle is on
		int currColR = (int)(x+width)/Util.BLOCKLENGTH; // the column the right side of the turtle is on

		// when the timer runs out, recalculate the change and time
		if (movementTimer.getElapsedTime() >= moveTime) {
			moveChance = Util.rand.nextInt(100);
			moveTime = Util.rand.nextInt(1,3);
			movementTimer.restart();
		}
		else if(moveChance >= 50){ // 50% chance to walk
			state = WALK;
		}
		else{ // 50% chance to idle
			state = IDLE;
		}

		if (blocks[grndRow][currColC].getType() != Block.AIR) {
			y = grndRow * Util.BLOCKLENGTH - height;
			velY = 0;
			if(state == WALK) {
				if (dir == Util.LEFT) {
					if (x <= 0) {
						dir = Util.RIGHT;
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
						dir = Util.LEFT;
						x -= velX;
					} else {
						if (blocks[grndRow][currColR].getType() != Block.AIR && blocks[grndRow - 1][currColR].getType() == Block.AIR) {
							x += velX;
						} else {
							dir = Util.LEFT;
							x -= velX;
						}
					}
				}
			}
		} else { // falls if there is no block below
			y+=velY;
			if(velY < maxVelY){
				velY += accelY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) { // displays and updates animation frame for the turtle
		move(blocks);

		if(state==IDLE) {
			if (dir == Util.LEFT) {
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
			if (dir == Util.LEFT) {
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
	}
}

/*
Snail.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for snails
*/
class Snail {
	private double x, y; // position
	private int width, height; // dimensions
	private int health; // hp
	private int horiDir, vertDir; // direction the snail is facing
	private double velY; // the speed and acceleration the enemy has
	private double animFrame; // current frame of the animation

	// arraylists that hold all frames of animation
	private final ArrayList<Image> idleU = new ArrayList<>();
	private final ArrayList<Image> idleD = new ArrayList<>();

	public Snail(int x, int y, int horiDir, int vertDir) {
		this.width = 35;
		this.height = 42;
		this.x = x;
		this.y = y;
		this.horiDir = horiDir;
		this.vertDir = vertDir;
		this.health = 30;
		this.velY = 1.5;
		animFrame = 0;

		// adding all frames of each animation to their arraylists
		if(horiDir == Util.LEFT){
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

	public int getHealth() { // gets the health of the snail
		return health;
	}
	public void setHealth(int health) { // sets the health of the snail
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
	public Rectangle getRect(){return new Rectangle((int)x,(int)y,width,height);} // gets rect for collisions

	public void move(Block[][] blocks){ // moves the snail up and down the wall
		int topRow = (int)y/Util.BLOCKLENGTH; // row the top the snail is on
		int bottomRow = (int) (y + height) / Util.BLOCKLENGTH; // row the bottom of the snail is on
		int currCol = (int) x / Util.BLOCKLENGTH; // the column the snail's hitbox is on
		int wallCol; // the column the snail is "sticking" to
		if(horiDir == Util.LEFT) {
			wallCol = currCol-1;
		}
		else{
			wallCol = currCol+1;
		}

		if (vertDir == Util.TOP) {
			if (blocks[topRow][wallCol].getType() == Block.WALL && blocks[topRow][currCol].getType() == Block.AIR) {
				y -= velY;
			} else {
				vertDir = Util.BOTTOM;
				y += velY;
			}
		} else {
			if (blocks[bottomRow][wallCol].getType() == Block.WALL && blocks[bottomRow][currCol].getType() == Block.AIR) {
				y += velY;
			} else {
				vertDir = Util.TOP;
				y -= velY;
			}
		}
	}

	public void draw(Graphics g, Block[][] blocks) { // draws and updates animation frame for the snail
		move(blocks);

		if(vertDir == Util.TOP) {
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
	}
}

/*
Jelly.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for jellies
*/
class Jelly {
	private double x, y; // position
	private int width, height; // dimensions
	private int imageWidth; // width of the image
	private int health; // hp

	private double velX, velY; // jelly's velocities
	private double maxVelX, maxVelY; // jelly's maximum velocities
	private double accelX, accelY; // jelly's accelerations
	private double accelFactor; // how fast the jelly can accelerate
	private double animFrame; // current frame of the animation
	private double collisionVel; // velocity of jelly once it collides with a block

	// arraylist that holds all the frames of animation
	ArrayList<Image> idle = new ArrayList<>();
	public Jelly (int x, int y) {
		this.x = x;
		this.y = y;
		this.width = 35;
		this.imageWidth = 40;
		this.height = 30;
		this.health = 30;
		this.maxVelX = 2;
		this.maxVelY = 2;
		this.accelX = 0;
		this.accelY = 0;
		this.accelFactor = .06;
		this.animFrame = 0;
		this.collisionVel = 0.25;

		// adding all frames of each animation to their arraylists
		for (int i = 0; i < 4; i++) {
			idle.add(new ImageIcon("src/assets/enemies/jelly/idle/jellyIdle" + i + ".png").getImage().getScaledInstance(imageWidth, height, Image.SCALE_DEFAULT));
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
	public int getHealth() {
		return health;
	} // gets the health of the jelly
	public void setHealth(int health) {
		this.health = health;
	} // sets the health of the jelly
	public Rectangle getRect(){return new Rectangle((int) x,(int) y,width,height);} // gets rect for collisions
	public void isHit() {accelY = 4;} // moves the jelly back if hit by the bullet
	public void move(Graphics g, Alan alan, Map map) { // calculates the distance between alan and the jelly, moves and updates the animation frame
		// distance calculations
		double distX = x + width/2 - AAdventure.getGame().getAlan().getX(false) - alan.getWidth()/2;
		double distY = y + height/2 - AAdventure.getGame().getAlan().getY(false) - alan.getHeight()/2;
		double distance = Math.hypot(distX, distY);

		// jelly only chases if alan is 400 away
		if(distance<400) {
			// accelerating the jelly towards alan
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

			// calculating the velocities
			velX = ((-1 / distance) * distX) + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
			velY = ((-1 / distance) * distY) + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan

			Block[][] blocks = map.getMap();

			int prevRow = (int) getY(false) / Util.BLOCKLENGTH; // getting the row the top of the jelly is on
			int nextRow = prevRow + 1; // getting the row the bottom of the jelly is on
			int prevCol = (int) getX(false) / Util.BLOCKLENGTH; // getting the column the left of the jelly is on
			int nextCol = prevCol + 1; // getting the column the right of the jelly is on

			// right to left collision checking
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						// left side of the block
						Rectangle leftSide = new Rectangle(block.getX(false), block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(leftSide)) {
							velX = -collisionVel;
							accelX = 0;
						}
					}
				}
			}
			// left to right collision checking
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						// right side of the block
						Rectangle rightSide = new Rectangle(block.getX(false) + Util.BLOCKLENGTH, block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(rightSide)) {
							velX = collisionVel;
							accelX = 0;
						}
					}
				}
			}
			// top to bottom collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[nextRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					// top side of the block
					Rectangle topSide = new Rectangle(block.getX(false)+1, block.getY(false, alan), Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(topSide)) {
						velY = -collisionVel;
						accelY = 0;
					}
				}
			}

			// bottom to top collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[prevRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					// bottom side of the block
					Rectangle bottomSide = new Rectangle(block.getX(false)+1, block.getY(false, alan) + Util.BLOCKLENGTH, Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(bottomSide)) {
						velY = collisionVel;
						accelY = 0;
					}
				}
			}

			x += velX;
			y += velY;
		}
	}

	public void draw(Graphics g, Alan alan, Map map) { // draws and updates animation frame for the jelly
		move(g, alan, map);

		if ((int) animFrame == idle.size() - 1) {
			animFrame = 0;
		} else {
			animFrame += 0.15;
		}
		g.drawImage(idle.get((int) animFrame), (int) getX(true)-3, (int)getY(true), null);
	}
}

/*
Bat.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for bats
*/
class Bat {
	private final int HANG = 0, FLY = 1; // states
	private double x, y; // position
	private int width, height; // dimensions
	private int health; // hp
	private int dir; // direction the bat is facing
	private int state; // bat's state (hang, fly)

	private double velX, velY; // bat's velocities
	private double maxVelX, maxVelY; // bat's maximum velocities
	private double accelX, accelY; // bat's accelerations
	private double accelFactor; // how fast the bat can accelerate
	private double animFrame; // current frame of the animation
	private double collisionVel; // velocity of bat once it collides with a block

	// arraylists that holds all the frames of animation
	ArrayList<Image> hang = new ArrayList<>();
	ArrayList<Image> flyL = new ArrayList<>();
	ArrayList<Image> flyR = new ArrayList<>();
	public Bat (int x, int y) {
		this.width = 20;
		this.height = 28;
		this.x = x + (Util.BLOCKLENGTH-this.width)/2; // centres the bat while it hangs
		this.y = y;
		this.health = 10;
		this.maxVelX = 2.5;
		this.maxVelY = 2.5;
		this.accelX = 0;
		this.accelY = 0;
		this.accelFactor = .5;
		this.animFrame = 0;
		this.dir = Util.RIGHT;
		this.state = HANG;

		// adding all frames of each animation to their arraylists
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

	public int getHealth() {
		return health;
	} // gets the health of the bat
	public void setHealth(int health) {
		this.health = health;
	} // sets the health of the bat
	public Rectangle getRect(){return new Rectangle((int) x,(int) y,width,height);} // gets rect for collisions
	public void move(Graphics g, Alan alan, Map map) { // calculates the distance between alan and the bat, moves and updates the animation frame
		// distance calculations
		double distX = x + width/2 - alan.getX(false) - alan.getWidth()/2;
		double distY = y + height/2 - alan.getY(false) - alan.getHeight()/2;
		double distance = Math.hypot(distX, distY);

		// changing directions
		if(alan.getX(false) < x){
			dir = Util.LEFT;
		}
		else{
			dir = Util.RIGHT;
		}

		// if alan is 200 away then activate
		if(distance < 200 && state == HANG){
			width = 28;
			height = 20;
			state = FLY;
		}

		// bat chases if alan is 400 away
		if(distance < 400 && state == FLY) {
			// accelerating the bat towards alan
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

			// calculating the velocities
			velX = ((-1 / distance) * distX) + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
			velY = ((-1 / distance) * distY) + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan

			Block[][] blocks = map.getMap();

			int prevRow = (int) getY(false) / Util.BLOCKLENGTH; // getting the row the top of the bat is on
			int nextRow = prevRow + 1; // getting the row the bottom of the bat is on
			int prevCol = (int) getX(false) / Util.BLOCKLENGTH; // getting the column the left of the bat is on
			int nextCol = prevCol + 1; // getting the column the right of the bat is on

			// right to left collision checking
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						// left side of the block
						Rectangle leftSide = new Rectangle(block.getX(false), block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(leftSide)) {
							velX = -collisionVel;
							accelX = 0;
						}
					}
				}
			}
			// left to right collision checking
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						// right side of the block
						Rectangle rightSide = new Rectangle(block.getX(false) + Util.BLOCKLENGTH, block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(rightSide)) {
							velX = collisionVel;
							accelX = 0;
						}
					}
				}
			}
			// top to bottom collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[nextRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					// top side of the block
					Rectangle topSide = new Rectangle(block.getX(false)+1, block.getY(false, alan), Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(topSide)) {
						velY = -collisionVel;
						accelY = 0;
					}
				}
			}

			// bottom to top collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[prevRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					// bottom side of the block
					Rectangle bottomSide = new Rectangle(block.getX(false)+1, block.getY(false, alan) + Util.BLOCKLENGTH, Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(bottomSide)) {
						velY = collisionVel;
						accelY = 0;
					}
				}
			}

			x += velX;
			y += velY;
		}
	}

	public void draw(Graphics g, Alan alan, Map map) { // draws and updates animation frame for the bat
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
	}
}

/*
Skull.java
Isaac Jiang & Jayden Zhao
Contains methods that move, draw and check collisions for skulls
*/
class Skull {
	public static final int CALM = 0, ANGRY = 1; // states
	private double x, y; // position
	private int width, height; // dimensions
	private int health; // hp
	private int dir; // direction the skull is facing
	private int state; // skull's state (calm, angry)

	private double velX, velY; // skull's velocities
	private double maxVelX, maxVelY; // skull's maximum velocities
	private double accelX, accelY; // skull's accelerations
	private double accelFactor; // how fast the skull can accelerate
	private double animFrame; // current frame of the animation
	private double collisionVel; // velocity of skull once it collides with a block

	// arraylists that holds all the frames of animation
	ArrayList<Image> calmL = new ArrayList<>();
	ArrayList<Image> calmR = new ArrayList<>();
	ArrayList<Image> angryL = new ArrayList<>();
	ArrayList<Image> angryR = new ArrayList<>();
	public Skull (int x, int y) {
		this.width = 34;
		this.height = 34;
		this.x = x;
		this.y = y;
		this.health = 35;
		this.maxVelX = 2;
		this.maxVelY = 2;
		this.accelX = 0;
		this.accelY = 0;
		this.accelFactor = .1;
		animFrame = 0;
		this.dir = Util.RIGHT;
		state = CALM;

		// adding all frames of each animation to their arraylists
		for (int i = 0; i < 4; i++) {
			calmL.add(new ImageIcon("src/assets/enemies/skull/calm/skullCalmL" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			calmR.add(new ImageIcon("src/assets/enemies/skull/calm/skullCalmR" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			angryL.add(new ImageIcon("src/assets/enemies/skull/angry/skullAngryL" + i + ".png").getImage());
		}
		for (int i = 0; i < 4; i++) {
			angryR.add(new ImageIcon("src/assets/enemies/skull/angry/skullAngryR" + i + ".png").getImage());
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

	public int getHealth() {
		return health;
	} // gets the health of the skull
	public void setHealth(int health) {
		this.health = health;
	} // sets the health of the skull
	public Rectangle getRect(){return new Rectangle((int) x,(int) y,width,height);} // gets rect for collisions
	public void hit() { // sets the skull to be angry when hit by a bullet
		state = ANGRY;
		maxVelX = 2.4;
		maxVelY = 2.4;
		accelFactor = 0.6;
		animFrame = 0;
	}
	public int getState(){ return state;} // gets if the skull is calm or angry to see if alan can stomp on it or not
	public void move(Graphics g, Alan alan, Map map) {
		// distance calculations
		double distX = x + width/2 - alan.getX(false) - alan.getWidth()/2;
		// how far away the enemy is compared to alan
		double distY = y + height/2 - alan.getY(false) - alan.getHeight()/2;
		double distance = Math.hypot(distX, distY); // pythag theorem

		// changing directions
		if (alan.getX(false) < x){
			dir = Util.LEFT;
		}
		else{
			dir = Util.RIGHT;
		}

		if(distance < 400) {
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
			velX = ((-1 / distance) * distX) + accelX; // -1 so the enemy moves TOWARDS alan, just 1 would make the enemy run away from alan
			velY = ((-1 / distance) * distY) + accelY; // frames*accel so the enemy speeds up/down for a more "natural" look, instead of perfectly tracking alan

			Block[][] blocks = map.getMap();

			int prevRow = (int) getY(false) / Util.BLOCKLENGTH; // getting the row the top of the skull is on
			int nextRow = prevRow + 1; // getting the row the bottom of the skull is on
			int prevCol = (int) getX(false) / Util.BLOCKLENGTH; // getting the column the left of the skull is on
			int nextCol = prevCol + 1; // getting the column the right of the skull is on

			// right to left collision checking
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						// left side of the block
						Rectangle leftSide = new Rectangle(block.getX(false), block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(leftSide)) {
							velX = -collisionVel;
							accelX = 0;
						}
					}
				}
			}
			// left to right collision checking
			for(int r=prevRow; r<=nextRow; r++) {
				for (int i = 0; i < map.getColumns(); i++) {
					Block block = blocks[r][i];
					if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
						// right side of the block
						Rectangle rightSide = new Rectangle(block.getX(false) + Util.BLOCKLENGTH, block.getY(false, alan)+1, 1, Util.BLOCKLENGTH-2);
						if (getRect().intersects(rightSide)) {
							velX = collisionVel;
							accelX = 0;
						}
					}
				}
			}
			// top to bottom collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[nextRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					// top side of the block
					Rectangle topSide = new Rectangle(block.getX(false)+1, block.getY(false, alan), Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(topSide)) {
						velY = -collisionVel;
						accelY = 0;
					}
				}
			}

			// bottom to top collision checking
			for (int i = 0; i < map.getColumns(); i++) {
				Block block = blocks[prevRow][i];
				if (block.getType() == Block.WALL || block.getType() == Block.BOX) {
					// bottom side of the block
					Rectangle bottomSide = new Rectangle(block.getX(false)+1, block.getY(false, alan) + Util.BLOCKLENGTH, Util.BLOCKLENGTH-2, 1);
					if (getRect().intersects(bottomSide)) {
						velY = collisionVel;
						accelY = 0;
					}
				}
			}

			x += velX;
			y += velY;
		}
	}

	public void draw(Graphics g, Alan alan, Map map) { // draws and updates animation frame for the skull
		move(g, alan, map);

		if (state == CALM){
			if (dir == Util.LEFT) {
				if ((int) animFrame == calmL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.15;
				}
				g.drawImage(calmL.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
			}
			else{
				if ((int) animFrame == calmL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.15;
				}
				g.drawImage(calmR.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
			}
		}
		else if (state == ANGRY) {
			if (dir == Util.LEFT) {
				if ((int) animFrame == angryL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.15;
				}
				g.drawImage(angryL.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
			}
			else{
				if ((int) animFrame == angryL.size() - 1) {
					animFrame = 0;
				} else {
					animFrame += 0.15;
				}
				g.drawImage(angryR.get((int) animFrame), (int) getX(true) - 3, (int) getY(true), null);
			}
		}
	}
}