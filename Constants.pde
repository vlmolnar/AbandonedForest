final static int GRID_SQUARE = 50;  //50 pixels, height/36, width/20
final PVector checkPointPos0 = new PVector(-825, -450); //Room: (0,0)
final PVector checkPointPos1 = new PVector(1650, -450); //Room: (1800, 0)
final PVector checkPointPos2 = new PVector(3800, 2100); //Room: (3600,2000)
final PVector checkPointPos3 = new PVector(7300, -1450); //Room: (7200, -1000)

enum CheckPoint {
  START, POINT1, POINT2, POINT3
};

enum Speaker {
  FOX, CROW, NARRATOR
}

enum GameState {
  TITLE, GAME
}
