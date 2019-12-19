class Room {
  final PVector loc = new PVector(0.0, 0.0);
  PVector halfScale = new PVector(0.5, 0.5);
  color fill = 0xffffffff;
  int[][] grid;
  PImage spikeUp,spikeDown, grass, tree, vine;
  
  Room(int[][] map) {
    grid = map;
    spikeUp = flipImgVertical(loadImage("spike.png"));
    spikeDown = loadImage("spike.png");
    grass = flipImgVertical(loadImage("grass.png"));
    tree = flipImgVertical(loadImage("tree.png"));
    vine = flipImgVertical(loadImage("vine.png"));
  }
  
  PImage flipImgVertical(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int y = 0 ; y < flipped.height; y++) {
      flipped.set(0, flipped.height-y-1, img.get(0, y, img.width, 1));
    }
    return flipped;
 }
  
  void drawRoom(PGraphicsOpenGL r) {
    r.beginShape(QUADS);
    r.fill(fill);
    r.vertex(loc.x - halfScale.x, loc.y - halfScale.y);
    r.vertex(loc.x + halfScale.x, loc.y - halfScale.y);
    r.vertex(loc.x + halfScale.x, loc.y + halfScale.y);
    r.vertex(loc.x - halfScale.x, loc.y + halfScale.y);
    r.endShape(CLOSE);
    
    //r.fill(255,255,255);
    //r.square(loc.x, loc.y , 50);
    
    //r.beginShape(QUADS);
    //r.fill(255, 255, 255);
    //r.vertex(loc.x - halfScale.x, loc.y + halfScale.y);
    //r.vertex(loc.x - halfScale.x + GRID_SQUARE, loc.y + halfScale.y);
    //r.vertex(loc.x - halfScale.x + GRID_SQUARE, loc.y + halfScale.y - GRID_SQUARE);
    //r.vertex(loc.x - halfScale.x, loc.y + halfScale.y - GRID_SQUARE);
    //r.endShape(CLOSE);
    
    r.fill(0);
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == 0) {
          continue;
        } else if (grid[i][j] == 1) {
          //r.square(loc.x - halfScale.x + i * GRID_SQUARE, loc.y - halfScale.y + j * GRID_SQUARE, GRID_SQUARE);
          //r.beginShape(QUADS);
          //r.vertex(loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE);
          //r.vertex(loc.x - halfScale.x + j * GRID_SQUARE + GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE);
          //r.vertex(loc.x - halfScale.x + j * GRID_SQUARE + GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
          //r.vertex(loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
          //r.endShape(CLOSE);
          image(grass, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
        } else if (grid[i][j] == 2) { // Upwards pointing spike
          image(spikeUp, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
        } else if (grid[i][j] == 3) { // Downwards pointing spike
          image(spikeDown, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE - 10);
        } else if (grid[i][j] == 4) { //Checkpoint tree
          image(tree, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
        } else if (grid[i][j] == 5 && !dialogue.cutScene2Complete) { //Progress block until first sacrifice
          image(vine, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
        } else if (grid[i][j] == 6 && !dialogue.cutScene3Complete) { //Progress block until second sacrifice
          image(vine, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
        }
      }
    }

    //r.fill(255, 255, 255);
    for (float i = loc.x - halfScale.x; i < loc.x + halfScale.x; i+=50) {
      r.line(i,loc.y - halfScale.y, i, loc.y + halfScale.y);
    }
    for (float i = loc.y - halfScale.y; i < loc.y + halfScale.y; i+=50) {
      r.line(loc.x - halfScale.x, i, loc.x + halfScale.x, i);
    }
    r.fill(fill);
  }
}
