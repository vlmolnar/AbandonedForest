import ddf.minim.*;
Minim minim;

AudioPlayer music, jumpSFX, damageSFX; 

int roomCount = 40;
boolean[] pressed = new boolean[256];
boolean tutorialOver = false;
Player player;
Dungeon dungeon;
DungeonCam cam;
PGraphics2D renderer;
CheckPoint checkPoint;
Dialogue dialogue;

void setup() {
  //fullScreen(P2D);
  //size(512, 256, P2D);
  size(1800, 1000, P2D);
  textureMode(NORMAL);
  textureWrap(REPEAT);
  
  surface.setTitle("Abandoned Forest");
  
  
  checkPoint = CheckPoint.START;
  PVector startPos = checkPointCoord();
  player = new Player(startPos.x, startPos.y, 0, 0);
  
  dialogue = new Dialogue(false, false, false, false);
  dialogue.cutSceneOn = false;
  
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
  
  if (!tutorialOver) {
    if (player.position.x < 150 && player.position.y < -150) {
      dialogue.showText("Use a, w and d keys to move", -650, 300);
    } else if (player.position.x < -500 && player.position.y > -150) {
      dialogue.showText("Use <- and -> keys to dash", -400, -200);
    } else if (player.position.x > 150 && player.position.y > -150) {
      dialogue.showText("Use SPACE key to reverse gravity", 220, -300);
    }
    
    if (!(dungeon.getRoom(player.position).loc.x == 0 && dungeon.getRoom(player.position).loc.y == 0)) {
      tutorialOver = true;
    }
  }
  
  
  player.drawPlayer();
  if (dialogue.cutSceneOn) {
    dialogue.cutScene1(1, pressed[32]);
  } else {
    cutSceneCheck();
  }
  //surface.setTitle(String.format("%.1f", frameRate));
  
  // Uncomment for grid lattice
  //for (int i = -1 * (width/2); i < width/2; i+=50) {
  //  line(i, -1 * (height/2), i, height/2);
  //}
  //for (int i = -1 * (height/2); i < height/2; i+=50) {
  //  line(-1 * (width/2), i, width/2, i);
  //}
    
}

void cutSceneCheck() {
   Room room = dungeon.getRoom(player.position);
   if (!dialogue.cutScene1Complete && room.loc.equals(new PVector(1800,0)) && player.position.x > room.loc.x - 500) {
     dialogue.cutSceneOn = true;
   }
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
