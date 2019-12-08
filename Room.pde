static class Room {
  final PVector loc = new PVector(0.0, 0.0);
  PVector halfScale = new PVector(0.5, 0.5);
  color fill = 0xffffffff;
  int[][] grid;
  
  Room(int[][] map) {
    grid = map;
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
    
    r.fill(255, 255, 255);
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == 1) {
          //r.square(loc.x - halfScale.x + i * GRID_SQUARE, loc.y - halfScale.y + j * GRID_SQUARE, GRID_SQUARE);
          r.beginShape(QUADS);
          r.vertex(loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE);
          r.vertex(loc.x - halfScale.x + j * GRID_SQUARE + GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE);
          r.vertex(loc.x - halfScale.x + j * GRID_SQUARE + GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
          r.vertex(loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
          r.endShape(CLOSE);
        }
      }
    }

    
    //for (float i = loc.x - halfScale.x; i < loc.x + halfScale.x; i+=50) {
    //  r.line(i,loc.y - halfScale.y, i, loc.y + halfScale.y);
    //}
    //for (float i = loc.y - halfScale.y; i < loc.y + halfScale.y; i+=50) {
    //  r.line(loc.x - halfScale.x, i, loc.x + halfScale.x, i);
    //}
    r.fill(fill);
  }
}
