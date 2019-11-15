int roomCount = 40;
boolean[] pressed = new boolean[256];
//Avatar2D hero;
Player player;
Dungeon dungeon;
DungeonCam cam;
PGraphics2D renderer;

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
  player.move(pressed, 65, 68, 87, 83);
  cam.lookAt(dungeon, player.position, pressed, UP, DOWN);
  background(0xff000000);
  dungeon.draw(renderer);
  player.drawPlayer();
  surface.setTitle(String.format("%.1f", frameRate));
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
