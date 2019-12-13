import ddf.minim.*;
Minim minim;

AudioPlayer music, jumpSFX, damageSFX; 

int roomCount = 40;
boolean[] pressed = new boolean[256];
Player player;
Dungeon dungeon;
DungeonCam cam;
PGraphics2D renderer;
CheckPoint checkPoint = CheckPoint.START;

void setup() {
  //fullScreen(P2D);
  //size(512, 256, P2D);
  size(1800, 1000, P2D);
  textureMode(NORMAL);
  textureWrap(REPEAT);
    
  PVector startPos = checkPointCoord();
  player = new Player(startPos.x, startPos.y, 0, 0);
  
  renderer = (PGraphics2D)g;
  cam = new DungeonCam(renderer);
  dungeon = new Dungeon(width, height);  
  
  cam.lookAt(dungeon, player.position, pressed, UP, DOWN, true);
  
  minim = new Minim(this);
  //music = minim.loadFile("The Midnight Hour.mp3");
  //music.loop();
  //int buffersize = 256;
  //jumpSFX = minim.loadFile("swish.wav");
  //jumpSFX.play();
  //damageSFX = minim.loadFile("hurt.wav");
  //damageSFX.play();
}

PVector checkPointCoord() {
  PVector returnPos;
  switch(checkPoint) {
   case POINT1: returnPos = checkPointPos1;
                break;
   case POINT2: returnPos = checkPointPos2;
                break;
   case POINT3: returnPos = checkPointPos3;
                break;
   default: returnPos = checkPointPos0;
                break;
 }
 return returnPos;
}

void draw() {
  player.move(pressed, 65, 68, 87, LEFT, RIGHT, 32); //keys a, d, w, <-, ->, SPACE
  cam.lookAt(dungeon, player.position, pressed, UP, DOWN, false);
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
  dungeon.generate(width, height);
}
