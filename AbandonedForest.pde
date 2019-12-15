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
JSONObject json;

void setup() {
  //fullScreen(P2D);
  //size(512, 256, P2D);
  size(1800, 1000, P2D);
  textureMode(NORMAL);
  textureWrap(REPEAT);
  
  surface.setTitle("Abandoned Forest");
  
  dialogue = new Dialogue();
  
  loadFromFile();
  
  //checkPoint = CheckPoint.START;
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

int checkPointToInt() {
  int num;
  switch(checkPoint) {
   case POINT1: num = 1;
                break;
   case POINT2: num = 2;
                break;
   case POINT3: num = 3;
                break;
   default: num = 0;
                break;
 }
 return num;
}

CheckPoint intToCheckPoint(int num) {
  CheckPoint point;
  switch(num) {
   case 1: point = CheckPoint.POINT1;
                break;
   case 2: point = CheckPoint.POINT2;
                break;
   case 3: point = CheckPoint.POINT3;
                break;
   default: point = CheckPoint.START;
                break;
 }
 return point;
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
  
  checkPointFind();
  
  if (dialogue.cutSceneOn) {
    dialogue.cutScene1(1, pressed[32]);
  } else {
    cutSceneFind();
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

void cutSceneFind() {
   Room room = dungeon.getRoom(player.position);
   if (!dialogue.cutScene1Complete && room.loc.equals(new PVector(1800,0)) && player.position.x > room.loc.x - 500) {
     dialogue.cutSceneOn = true;
   }
}

void checkPointFind() {
  Room room = dungeon.getRoom(player.position);
   if (room.loc.equals(new PVector(1800,0))
         && player.position.x > room.loc.x - 400 && player.position.x < room.loc.x + 100 && player.position.y < room.loc.y) {
     player.lives = player.maxLife;
     if (checkPoint != CheckPoint.POINT1) {
       checkPoint = CheckPoint.POINT1;
       saveToFile();
     }
   }
  
}

void saveToFile() {
  json = new JSONObject();
  json.setInt("checkPoint", checkPointToInt());
  json.setBoolean("cutScene1Complete", dialogue.cutScene1Complete);
  json.setBoolean("cutScene2Complete", dialogue.cutScene2Complete);
  json.setBoolean("cutScene3Complete", dialogue.cutScene3Complete);
  json.setBoolean("cutScene4Complete", dialogue.cutScene4Complete);
  saveJSONObject(json, "data/save.json");
}

void loadFromFile() {
    File f = new File(dataPath("save.json"));
    if (f.exists()) {
      json = loadJSONObject("save.json");
      checkPoint = intToCheckPoint(json.getInt("checkPoint"));
      dialogue.cutScene1Complete = (json.getBoolean("cutScene1Complete"));
      dialogue.cutScene2Complete = (json.getBoolean("cutScene2Complete"));
      dialogue.cutScene3Complete = (json.getBoolean("cutScene3Complete"));
      dialogue.cutScene4Complete = (json.getBoolean("cutScene4Complete"));
    } else {
      checkPoint = CheckPoint.START;
 
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
