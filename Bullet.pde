class Bullet {
  PVector position;
  PVector velocity;
  boolean hasExploded = false;
  //boolean lockOn;
  
  Bullet(PVector position) {
     this.position = position;
     //this.lockOn = lockOn;
  }
  
  void move() {
    collisionCheck();
    if (this.hasExploded) return;
    
    this.position.add(velocity);
    
  }
  
  boolean collisionCheck() {
    Room room = dungeon.getRoom(position);
    int gridX, gridY;
    gridX = (int)(position.x + room.halfScale.x - room.loc.x)/ GRID_SQUARE;
    gridY = 19 - (int)(position.y + room.halfScale.y - room.loc.y)/ GRID_SQUARE;    
    
    if (gridX > 35) gridX = 35;  // Prevents array out of bounds
    if (gridY > 19) gridY = 19;
    if (gridX < 0) gridX = 0;
    if (gridY < 0) gridY = 0;
   
    if (room.grid[gridY][gridX] == 1 || room.grid[gridY][gridX] == 2 || room.grid[gridY][gridX] == 3) {
      hasExploded = true;
     return true;
    }
    return false;
  }
  
  void draw() {
    fill(0);
    ellipse(position.x, position.y, BULLET_DIAMETER, BULLET_DIAMETER);
  }
  
}
