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
   this.position = new PVector(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
   this.velocity.mult(0.85);
 }
 
 void moveInput(float moveX, float moveY) {
   if (velocity.mag() < 6) {
    this.velocity.add(new PVector(moveX, moveY));
   }
 }
 
 void moveInput(int moveX, int moveY) {
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
