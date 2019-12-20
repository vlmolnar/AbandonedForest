class Room {
  final PVector loc = new PVector(0.0, 0.0);
  PVector halfScale = new PVector(0.5, 0.5);
  int[][] grid;
  PImage spikeUp,spikeDown, grass, tree, vine, crow;
  ArrayList<Machinery> machines = new ArrayList();
  ArrayList<Bullet> bullets = new ArrayList();
  ArrayList<RainDrop> rain = new ArrayList();
  
  Room(int[][] map) {
    grid = map;
    spikeUp = flipImgVertical(loadImage("spike.png"));
    spikeDown = loadImage("spike.png");
    grass = flipImgVertical(loadImage("grass.png"));
    tree = flipImgVertical(loadImage("tree.png"));
    vine = flipImgVertical(loadImage("vine.png"));
    crow = flipImgVertical(loadImage("crow.png"));
  }
  
  void addMachine(Machinery machine) {
      machines.add(machine);
  }
  
  
  void drawRoom(PGraphicsOpenGL r) {
    r.beginShape(QUADS);
    r.fill(fill);
    r.vertex(loc.x - halfScale.x, loc.y - halfScale.y);
    r.vertex(loc.x + halfScale.x, loc.y - halfScale.y);
    r.vertex(loc.x + halfScale.x, loc.y + halfScale.y);
    r.vertex(loc.x - halfScale.x, loc.y + halfScale.y);
    r.endShape(CLOSE);
    
    r.fill(0);
    
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == 0) {
          continue;
        } else if (grid[i][j] == 1) {
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
        } else if (grid[i][j] == 7) { //Crow onlooker, facing left
          image(crow, loc.x - halfScale.x + j * GRID_SQUARE, loc.y + halfScale.y - i * GRID_SQUARE - GRID_SQUARE);
        }
      }
    }
    
    for (Machinery m : machines) {
      m.draw(bullets);
    }
    
    Iterator<Bullet> itr = bullets.iterator();
      while (itr.hasNext()) {
        Bullet b = itr.next();
        if (b.hasExploded) itr.remove();
        else b.draw(); 
      }
      
    for (RainDrop drop : rain) {
      drop.draw(r);
    }
  }
}
