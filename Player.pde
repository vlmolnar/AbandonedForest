class Player{
  PImage foxImg, life, nolife;
  PVector position;
  PVector velocity;
  int sizeX;
  int sizeY;
  int maxLife = 3;
  int lives = 3;
  long lastJumpTime = 0;
  long lastDashTime = 0;
  long lastDamageTime = 0;
  float gravity = -1.2;
  boolean hasDied = false;
  boolean faceRight = true;
  boolean imgRight = true;
  boolean isOnGround = true;
  boolean lockJump = false;
  boolean isImmune = false;
  
 Player(float x, float y, float velX, float velY) {
   this.position = new PVector(x, y);
   this.velocity = new PVector(velX, velY);
   foxImg = flipImgVertical(loadImage("fox.png"));
   life = flipImgVertical(loadImage("life.png"));
   nolife = flipImgVertical(loadImage("nolife.png"));
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
 
  void move(boolean[] pressed, int leftKey, int rightKey, int upKey, int leftDash, int rightDash, int spaceKey) {
    if (!dialogue.cutSceneOn && millis() - dialogue.lastSpaceTime > 500) {
      
      
        if (pressed[upKey] &&  !lockJump && (isOnGround || (!isOnGround && millis() - lastJumpTime < 500)) && millis() -  lastDashTime > 500) {
          if (isOnGround) lastJumpTime = millis();
         velocity = gravity < 0 ? new PVector(velocity.x, 8) : new PVector(velocity.x, -8); 
       } else {
         lockJump = isOnGround ? false : true;
       }
         
       if (pressed[rightKey] && !pressed[leftKey]) {
         faceRight = true;
         if (!imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = true;
         }
         if (isOnGround) {
           moveInput(1.6, 0);
         } else {
           moveInput(1, 0);
         }
       }
       
       if (pressed[leftKey] && !pressed[rightKey]) {
         faceRight = false;
         if (imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = false;
         }
         if (isOnGround) {
         moveInput(-1.6, 0);
         } else {
           moveInput(-1, 0);
         }
       }  
       
       if (pressed[leftDash] && millis() - lastDashTime > 500 ) {
         lastDashTime = millis();
         faceRight = false;
         if (imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = false;
         }
         velocity = new PVector(-10, 0);
       }
       
       if (pressed[rightDash] && millis() - lastDashTime > 500) {
         lastDashTime = millis();
         faceRight = true;
         if (!imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = true;
         }
         velocity = new PVector(10, 0);
       }
       
       if (pressed[spaceKey] && isOnGround) {
           reverseGravity();
       }
    }
   
   PVector futurePos = new PVector(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
   if (!collisionCheck(futurePos)) {
     this.position = futurePos;
     if (millis() - lastDashTime > 200) {
       this.velocity.mult(0.80);
     }
   } else {
     //this.position = new PVector(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
     this.velocity = new PVector(0,0);
   }
   
   //gravity
   if ( !collisionCheck(new PVector(position.x, position.y + gravity)) && millis() - lastDashTime > 200) {
     //System.out.println("gravity yey");
     isOnGround = false;
     this.velocity.add(0, gravity);
     this.position = new PVector(position.x, position.y + gravity);
   } else {
     isOnGround = true;
     //System.out.println("gravity nay");
   }
 }
 
 void reverseGravity() {
   gravity *= -1;
   foxImg = flipImgVertical(foxImg);
 }
 
 boolean collisionCheck(PVector pos) {
   // Other 3 corners of player sprite
   PVector pos2 = new PVector(pos.x + foxImg.width, pos.y);
   PVector pos3 = new PVector(pos.x + foxImg.width, pos.y + foxImg.height);
   PVector pos4 = new PVector(pos.x, pos.y + foxImg.height);
   
   // Mid-point in player sprite across x axis, because sprite is wider than a block
   PVector pos5 = new PVector(pos.x + foxImg.width/2, pos.y);
   PVector pos6 = new PVector(pos.x + foxImg.width/2, pos.y + foxImg.height);
   
   
   //boolean leftCollide = pointCollision(pos3, dungeon.getRoom(pos3)) || pointCollision(pos4, dungeon.getRoom(pos4));
   //boolean rightCollide = pointCollision(pos2, dungeon.getRoom(pos2)) && pointCollision(pos3, dungeon.getRoom(pos3));
   //boolean topCollide = pointCollision(pos3, dungeon.getRoom(pos3)) || pointCollision(pos4, dungeon.getRoom(pos4)) || pointCollision(pos6, dungeon.getRoom(pos6));
   //boolean bottomCollide = pointCollision(pos, dungeon.getRoom(pos)) || pointCollision(pos2, dungeon.getRoom(pos2)) || pointCollision(pos5, dungeon.getRoom(pos5));
   
   //if ((leftCollide && velocity.x < 0) || (rightCollide && velocity.x > 0)) velocity.x = 0;
   //if ((topCollide && velocity. y > 0) || (bottomCollide && velocity.y < 0)) velocity.y = 0;
   
   // Collision
   return (pointCollision(pos, dungeon.getRoom(pos)) | pointCollision(pos2, dungeon.getRoom(pos2))
       | pointCollision(pos3, dungeon.getRoom(pos3)) | pointCollision(pos4, dungeon.getRoom(pos4))
       | pointCollision(pos5, dungeon.getRoom(pos5)) | pointCollision(pos6, dungeon.getRoom(pos6)));
         
         //if() {
         //Collision with ground
       //  if (pointCollision(new PVector(pos3.x, pos3.y - 50), dungeon.getRoom(pos3)) || pointCollision(pos4, dungeon.getRoom(pos4)) || pointCollision(pos6, dungeon.getRoom(pos6))) {
       //    System.out.println("gravity OFF!");
       //    isOnGround = true;
       //  } else {
       //    System.out.println("gravity ON!");
       //    isOnGround = false;
       //}
       
    //   return true;
    //}
    //System.out.println("gravity ON2!");
    //isOnGround = false;
    //return false;
 }
 
 boolean pointCollision(PVector pos, Room room) {
   int gridX, gridY;
   gridX = (int)(pos.x + room.halfScale.x - room.loc.x)/ GRID_SQUARE;
   gridY = 19 - (int)(pos.y + room.halfScale.y - room.loc.y)/ GRID_SQUARE; // Compensates for flipped y axis
   if (gridX > 35) gridX = 35;  // Prevents array out of bounds
   if (gridY > 19) gridY = 19;
   if (gridX < 0) gridX = 0;  // Prevents array out of bounds
   if (gridY < 0) gridY = 0;
   //System.out.println("pos: " + pos.x + "  " + pos.y);
   //System.out.println("room: " + room.loc.x + "  " + room.loc.y);
   //System.out.println("grid: " + gridX + "  " + gridY);
   
   if (isImmune && millis() - lastDamageTime > 2000) {
     isImmune = false;
     foxImg.filter(INVERT);
   }
   if (room.grid[gridY][gridX] == 2 || room.grid[gridY][gridX] == 3) {  // Damage
      if (millis() - lastDamageTime > 2000) {
        lastDamageTime = millis();
        lives -= 1;
        
        // Death
        if (lives == 0) {
          lastDamageTime = 0;
          if (gravity > 0) {
            reverseGravity();
          }
           player.position = checkPointCoord();
           //player.position = checkPointPos0;
           player.lives = player.maxLife;
        } else {
          isImmune = true;
          foxImg.filter(INVERT);
        }
      }
     return true;
   }
   if (room.grid[gridY][gridX] == 1) {  // Wall
     return true;
   }
   return false;
 }
 
 

 
 void moveInput(float moveX, float moveY) {
   //if (abs(velocity.x + moveX) < 8) moveX = moveX/abs(moveX) * 8; 
   //if(abs(velocity.y + moveY) < 10) moveY = moveY/abs(moveY) * 10;
   if(abs(velocity.x + moveX) < 8 && abs(velocity.y + moveY) < 10) {
   this.velocity.add(new PVector(moveX, moveY));
   }
 
 }

 void drawPlayer() {
   fill(255);
   
   //Uncomment to see collision box
   //rect(this.position.x, this.position.y, this.sizeX, this.sizeY);
   
   image(foxImg, this.position.x, this.position.y);
   PVector dungeonCorner = new PVector(dungeon.getRoom(position).loc.x - width/2, dungeon.getRoom(position).loc.y + height/2 - GRID_SQUARE);
   for (int i = 0; i < maxLife; i++) {
     if (i < lives) {
       image(life, dungeonCorner.x + i * GRID_SQUARE, dungeonCorner.y);
     } else {
        image(nolife, dungeonCorner.x + i * GRID_SQUARE, dungeonCorner.y); 
     }
   }
 }
}
