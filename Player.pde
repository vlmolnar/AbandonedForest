class Player{
  PVector position;
  PVector velocity;
  int sizeX;
  int sizeY;
  boolean hasDied = false;
  PImage foxImg = loadImage("fox.png");
  boolean faceRight = true;
  boolean imgRight = true;
  
 Player(float x, float y, float velX, float velY) {
   this.position = new PVector(x, y);
   this.velocity = new PVector(velX, velY);
   foxImg = flipImgVertical(foxImg);
   sizeX = foxImg.width;
   sizeY = foxImg.height;
 }
 
 // Slighty modified from https://stackoverflow.com/questions/29334348/processing-mirror-image-over-x-axis
 PImage flipImgHorizontal(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int x = 0 ; x < flipped.width; x++) {                            //loop through each column
      flipped.set(flipped.width-x-1, 0, img.get(x, 0, 1, img.height));  //copy a column in reverse x order
    }
    return flipped;
 }
 
 PImage flipImgVertical(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int y = 0 ; y < flipped.height; y++) {
      flipped.set(0, flipped.height-y-1, img.get(0, y, img.width, 1));
    }
    return flipped;
 }
 
 boolean collisionCheck(PVector pos) {
   // Other 3 corners of player sprite
   PVector pos2 = new PVector(pos.x + foxImg.width, pos.y);
   PVector pos3 = new PVector(pos.x + foxImg.width, pos.y + foxImg.height);
   PVector pos4 = new PVector(pos.x, pos.y + foxImg.height);
   
   // Mid-point in player sprite across x axis, because sprite is wider than a block
   PVector pos5 = new PVector(pos.x + foxImg.width/2, pos.y);;
   PVector pos6 = new PVector(pos.x + foxImg.width/2, pos.y + foxImg.height);
   
   // Collision
   return pointCollision(pos, dungeon.getRoom(pos)) || pointCollision(pos2, dungeon.getRoom(pos2))
       || pointCollision(pos3, dungeon.getRoom(pos3)) || pointCollision(pos4, dungeon.getRoom(pos4))
       || pointCollision(pos5, dungeon.getRoom(pos5)) || pointCollision(pos6, dungeon.getRoom(pos6));
 }
 
 boolean pointCollision(PVector pos, Room room) {
   int gridX, gridY;
   gridX = (int)(pos.x + room.halfScale.x - room.loc.x)/ GRID_SQUARE;
   gridY = 19 - (int)(pos.y + room.halfScale.y - room.loc.y)/ GRID_SQUARE; // Compensates for flipped y axis
   if (gridX > 35) gridX = 35;  // Prevents array out of bounds but allows 0 value
   if (gridY > 19) gridY = 19;
   if (gridX < 0) gridX = 0;  // Prevents array out of bounds but allows 0 value
   if (gridY < 0) gridY = 0;
   //System.out.println("pos: " + pos.x + "  " + pos.y);
   //System.out.println("room: " + room.loc.x + "  " + room.loc.y);
   //System.out.println("grid: " + gridX + "  " + gridY);
   if (room.grid[gridY][gridX] == 1) return true;
   return false;
 }
 
 
 void move(boolean[] pressed, int leftKey, int rightKey, int upKey, int downKey) {
   if (pressed[leftKey]) {
     faceRight = false;
     if (imgRight) {
       foxImg = flipImgHorizontal(foxImg);
       imgRight = false;
     }
     moveInput(-1, 0);
   }
   else if (pressed[upKey]) {
     moveInput(0, 1);
   }
   else if (pressed[downKey]) {
     moveInput(0, -1);
   }
   else if (pressed[rightKey]) {
     faceRight = true;
     if (!imgRight) {
       foxImg = flipImgHorizontal(foxImg);
       imgRight = true;
     }
     moveInput(1, 0);
   }
   PVector futurePos = new PVector(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
   if (!collisionCheck(futurePos)) {
     this.position = futurePos;
     this.velocity.mult(0.85);
   } else {
     this.velocity = new PVector(0,0);
   }
   
   //gravity
   //if (!collisionCheck(futurePos, dungeon.getRoom(position))) {
   //  this.velocity.add(0, -0.5);
   //}
 }
 
 void moveInput(float moveX, float moveY) {
   if (velocity.mag() < 6) {
    this.velocity.add(new PVector(moveX, moveY));
   }
 }

 void drawPlayer() {
   fill(255);
   
   //Uncomment to see collision box
   //rect(this.position.x, this.position.y, this.sizeX, this.sizeY);
   
   image(foxImg, this.position.x, this.position.y);
   
 }
}
