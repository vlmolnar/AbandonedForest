int roomCount = 40;
boolean[] pressed = new boolean[256];
//Avatar2D hero;
Player player;
Dungeon dungeon;
DungeonCam cam;
PGraphics2D renderer;
final static int GRID_SQUARE = 50;  //50 pixels, height/36, width/20

void setup() {
  //fullScreen(P2D);
  //size(512, 256, P2D);
  size(1800, 1000, P2D);
  textureMode(NORMAL);
  textureWrap(REPEAT);
  renderer = (PGraphics2D)g;
  cam = new DungeonCam(renderer);
  dungeon = new Dungeon(System.currentTimeMillis(),
    roomCount, width, height);  
  player = new Player(dungeon.start.loc.x, dungeon.start.loc.y, 0, 0);
  
}

void draw() {
  player.move(pressed, 65, 68, 87, 83, 81, 69); //keys a, d, w, s, q, e
  cam.lookAt(dungeon, player.position, pressed, UP, DOWN);
  background(0xff000000);
  dungeon.draw(renderer);
  player.drawPlayer();
  surface.setTitle(String.format("%.1f", frameRate));
  //for (int i = -1 * (width/2); i < width/2; i+=50) {
  //  line(i, -1 * (height/2), i, height/2);
  //}
  //for (int i = -1 * (height/2); i < height/2; i+=50) {
  //  line(-1 * (width/2), i, width/2, i);
  //}
    
}

void keyPressed() {
  pressed[keyCode] = true;
}

void keyReleased() {
  pressed[keyCode] = false;
}

void mouseReleased() {
  dungeon.generate(System.currentTimeMillis(),
    roomCount, width, height);
}
