import java.util.*;
//import ddf.minim.*;
//Minim minim;
//AudioPlayer music, jumpSFX, damageSFX; 

//import processing.sound.*;
//SoundFile file;

int roomCount = 40;
boolean[] pressed = new boolean[256];
boolean tutorialOver = false;
Player player;
Dungeon dungeon;
DungeonCam cam;
PGraphics2D renderer;
CheckPoint checkPoint;
Dialogue dialogue;
GameState gameState;
PImage titleImg0, titleImg1, titleImg2;
JSONObject json;
int lastEnding = 0;  //0 indicates no ending, 1 Sacrifice and 2 Abandon
color fill = color(213, 213, 213);

void setup() {
  //fullScreen(P2D);
  //size(512, 256, P2D);
  size(1800, 1000, P2D);
  textureMode(NORMAL);
  textureWrap(REPEAT);
  
  gameState = GameState.TITLE;
  
  newGameSetup();
  
  //file = new SoundFile(this, "The Midnight Hour.mp3");
  //file.play();
  //file.loop();
  
  //minim = new Minim(this);
  //music = minim.loadFile("The Midnight Hour.mp3");
  //music.play();
  //music.loop();
  //int buffersize = 256;
  //jumpSFX = minim.loadFile("swish.wav");
  //jumpSFX.play();
  //damageSFX = minim.loadFile("hurt.wav");
  //damageSFX.play();
}

void newGameSetup() {
  surface.setTitle("Abandoned Forest");
  
  dialogue = new Dialogue();
  
  loadFromFile();
  fill = color(213, 213, 213);
  
  //checkPoint = CheckPoint.START;
  PVector startPos = checkPointCoord();
  //player = new Player(3000, -3000, 0, 0);  //Cheat to get to ending room
  player = new Player(startPos.x, startPos.y, 0, 0);
  
  titleImg0 = loadImage("title0.jpg");
  titleImg1 = loadImage("title1.jpg");
  titleImg2 = loadImage("title2.jpg");
  
  if (dialogue.cutScene2Complete) {
    player.maxLife -= 1;
    player.lives = player.maxLife;
    player.gravitySacrificed = true;
    fill = color(167, 167, 167);
  }
  if (dialogue.cutScene3Complete) {
    player.maxLife -= 1;
    player.lives = player.maxLife;
    player.dashSacrificed = true;
    fill = color(148, 148, 148);
  }
  
  renderer = (PGraphics2D)g;
  cam = new DungeonCam(renderer);
  dungeon = new Dungeon(width, height);  
  
  cam.lookAt(dungeon, player.position, pressed, UP, DOWN, true);
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
  if (gameState == GameState.GAME) {
    drawGame();
  } else {
    //Background changes based on last ending achieved
    if (lastEnding == 0) background(titleImg0);
    else if (lastEnding == 1) background(titleImg1);
    else if(lastEnding == 2) background(titleImg2);
    fill(252, 154, 8);
    textSize(40);
    text("Press ENTER to start game", width/2 + 200, height/2);
    fill(0);
    if (pressed[ENTER] || pressed[RETURN]) {
      gameState = GameState.GAME;
    }
  }
}

void drawGame() {
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
    dialogue.cutScene(pressed[32], pressed[ENTER], pressed[RETURN], pressed[LEFT], pressed[RIGHT]);
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
   } else if (!dialogue.cutScene2Complete && room.loc.equals(new PVector(3600,2000)) && player.position.x > room.loc.x + 200 && player.position.y > room.loc.y) {
     dialogue.cutSceneOn = true;
   } else if (!dialogue.cutScene3Complete && room.loc.equals(new PVector(7200,-1000)) && player.position.x > room.loc.x + 100 && player.position.y < room.loc.y) {
     dialogue.cutSceneOn = true;
   } else if (room.loc.equals(new PVector(3600,-3000))
         && player.position.x > room.loc.x - 300) {
     dialogue.cutSceneOn = true;
   }
}

void checkPointFind() {
  Room room = dungeon.getRoom(player.position);
  //System.out.println(room.loc.x + ", " + room.loc.y);
   if (room.loc.equals(new PVector(1800,0))
         && player.position.x > room.loc.x - 500 && player.position.x < room.loc.x + 100 && player.position.y < room.loc.y) {
     player.lives = player.maxLife;
     if (checkPoint != CheckPoint.POINT1) {
       checkPoint = CheckPoint.POINT1;
       saveToFile();
     }
   } else if (room.loc.equals(new PVector(3600,2000))
         && player.position.x > room.loc.x + 200 && player.position.y > room.loc.y) {
     player.lives = player.maxLife;
     if (checkPoint != CheckPoint.POINT2) {
       checkPoint = CheckPoint.POINT2;
       saveToFile();
     }
   } else if (room.loc.equals(new PVector(7200,-1000))
         && player.position.x > room.loc.x + 100 && player.position.y < room.loc.y) {
     player.lives = player.maxLife;
     if (checkPoint != CheckPoint.POINT3) {
       checkPoint = CheckPoint.POINT3;
       saveToFile();
     }
   }
  
}

void saveToFile() {
  json = new JSONObject();
  if (!dialogue.cutScene4Complete) {
    json.setInt("checkPoint", checkPointToInt());
    json.setInt("lastEnding", lastEnding);
    json.setBoolean("cutScene1Complete", dialogue.cutScene1Complete);
    json.setBoolean("cutScene2Complete", dialogue.cutScene2Complete);
    json.setBoolean("cutScene3Complete", dialogue.cutScene3Complete);
  } else {
    json.setInt("checkPoint", 0);
    json.setInt("lastEnding", lastEnding);
    json.setBoolean("cutScene1Complete", false);
    json.setBoolean("cutScene2Complete", false);
    json.setBoolean("cutScene3Complete", false);
  }
  
  //json.setBoolean("cutScene4Complete", dialogue.cutScene4Complete);
  saveJSONObject(json, "data/save.json");
}

void loadFromFile() {
    File f = new File(dataPath("save.json"));
    if (f.exists()) {
      json = loadJSONObject(dataPath("save.json"));
      checkPoint = intToCheckPoint(json.getInt("checkPoint"));
      lastEnding = json.getInt("lastEnding");
      dialogue.cutScene1Complete = (json.getBoolean("cutScene1Complete"));
      dialogue.cutScene2Complete = (json.getBoolean("cutScene2Complete"));
      dialogue.cutScene3Complete = (json.getBoolean("cutScene3Complete"));
      //dialogue.cutScene4Complete = (json.getBoolean("cutScene4Complete"));
    } else {
      checkPoint = CheckPoint.START;
 
    }
  }
  
   // Slighty modified from https://stackoverflow.com/questions/29334348/processing-mirror-image-over-x-axis
 PImage flipImgHorizontal(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int x = 0 ; x < flipped.width; x++) {                            //loop through each column
      flipped.set(flipped.width-x-1, 0, img.get(x, 0, 1, img.height));  //copy a column in reverse x order
    }
    return flipped;
 }
 
 PImage flipImgVertical(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int y = 0 ; y < flipped.height; y++) {
      flipped.set(0, flipped.height-y-1, img.get(0, y, img.width, 1));
    }
    return flipped;
 }

void keyPressed() {
  pressed[keyCode] = true;
}

void keyReleased() {
  pressed[keyCode] = false;
}

void mouseReleased() {
  //dungeon.generate(width, height);
}
