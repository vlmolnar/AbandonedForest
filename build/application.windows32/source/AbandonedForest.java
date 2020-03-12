import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.util.NavigableMap; 
import java.util.Random; 
import java.util.Set; 
import java.util.TreeMap; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class AbandonedForest extends PApplet {



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
int fill = color(213, 213, 213);

public void setup() {
  
  textureMode(NORMAL);
  textureWrap(REPEAT);
  
  gameState = GameState.TITLE;
  
  newGameSetup();

}

public void newGameSetup() {
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

public PVector checkPointCoord() {
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

public int checkPointToInt() {
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

public CheckPoint intToCheckPoint(int num) {
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

public void draw() {
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

public void drawGame() {
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
  
  // Uncomment for grid lattice
  //for (int i = -1 * (width/2); i < width/2; i+=50) {
  //  line(i, -1 * (height/2), i, height/2);
  //}
  //for (int i = -1 * (height/2); i < height/2; i+=50) {
  //  line(-1 * (width/2), i, width/2, i);
  //}
    
}

public void cutSceneFind() {
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

public void checkPointFind() {
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

public void saveToFile() {
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

  saveJSONObject(json, "data/save.json");
}

public void loadFromFile() {
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
 public PImage flipImgHorizontal(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int x = 0 ; x < flipped.width; x++) {                            //loop through each column
      flipped.set(flipped.width-x-1, 0, img.get(x, 0, 1, img.height));  //copy a column in reverse x order
    }
    return flipped;
 }
 
 public PImage flipImgVertical(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int y = 0 ; y < flipped.height; y++) {
      flipped.set(0, flipped.height-y-1, img.get(0, y, img.width, 1));
    }
    return flipped;
 }

public void keyPressed() {
  pressed[keyCode] = true;
}

public void keyReleased() {
  pressed[keyCode] = false;
}

public void mouseReleased() {
  //dungeon.generate(width, height);
}
class Bullet {
  PVector position;
  PVector velocity;
  boolean hasExploded = false;
  //boolean lockOn;
  
  Bullet(PVector position, PVector velocity) {
     this.position = position;
     this.velocity = velocity;
  }
  
  public void move() {
    collisionCheck();
    if (this.hasExploded) return;
    
    this.position.add(velocity);
    
  }
  
  public boolean collisionCheck() {
    Room room = dungeon.getRoom(position);
    int gridX, gridY;
    gridX = (int)(position.x + room.halfScale.x - room.loc.x)/ GRID_SQUARE;
    gridY = 19 - (int)(position.y + room.halfScale.y - room.loc.y)/ GRID_SQUARE;    
    
    if (gridX > 35) gridX = 35;  // Prevents array out of bounds
    if (gridY > 19) gridY = 19;
    if (gridX < 0) gridX = 0;
    if (gridY < 0) gridY = 0;
   
    if (playerCollision() || room.grid[gridY][gridX] == 1 || room.grid[gridY][gridX] == 2 || room.grid[gridY][gridX] == 3) {
      hasExploded = true;
     return true;
    }
    return false;
  }
  
  public boolean playerCollision() {
    if (player.position.x < position.x && player.position.x + player.foxImg.width > position.x 
        && player.position.y < position.y && player.position.y + player.foxImg.height > position.y) {
          player.takeDamage();
          return true;
        }
    return false;
  }
  
  public void draw() {
    move();
    fill(0);
    ellipse(position.x, position.y, BULLET_DIAMETER, BULLET_DIAMETER);
  }
  
}
static class Camera2D {
  final PGraphics2D renderer;
  final PVector loc = new PVector(0.0f, 0.0f);
  final PVector i = new PVector(1.0f, 0.0f); /* Right Axis */
  final PVector j = new PVector(0.0f, 1.0f); /* Up Axis */
  
  float rot = 0.0f;
  float smoothing = 0.04f;
  final PVector scl = new PVector(1.0f, 1.0f, 1.0f);

  // setMatrix feeds into PGraphicsOpenGL's applyMatrixImpl,
  // which uses 3D matrices. These variables are shorthand
  // to the memory addresses of the needed renderer matrices.
  final PMatrix3D pmv; /* Project-Model-View. */
  final PMatrix3D c; /* Camera. */
  final PMatrix3D cInv; /* Camera inverse. */

  Camera2D(PGraphics2D renderer) {
    this.renderer = renderer;
    c = renderer.modelview;
    cInv = renderer.modelviewInv;
    pmv = renderer.projmodelview;
  }

  public Camera2D lookAt(float tx, float ty, float rot, float zoomW, float zoomH, boolean snap) {
      
    
    if (snap) { // Snap to target immediately, used during setup
    loc.set(tx, ty, 0.0f);
    scl.set(zoomW, zoomH, 0.0f);
    this.rot = rot;
    } else { // Ease toward target, used when moving across rooms
    loc.lerp(tx, ty, 0.0f, smoothing);
    scl.lerp(zoomW, zoomH, 0.0f, smoothing);
    this.rot = lerpAngle(this.rot, rot, smoothing);
    PVector.fromAngle(this.rot, i);
    j.set(-i.y, i.x);
    }

    c.set(
      1.0f, 0.0f, 0.0f, renderer.width * 0.5f, 
      0.0f, 1.0f, 0.0f, renderer.height * 0.5f, 
      0.0f, 0.0f, 1.0f, 0.0f, 
      0.0f, 0.0f, 0.0f, 1.0f);
    c.scale(scl.x, -scl.y); /* Flip y axis. */
    c.rotate(-this.rot);  
    c.translate(-loc.x, -loc.y);

    // Instead of calling setMatrix, which resets and then applies
    // the target matrices, set them directly.
    cInv.set(c);
    cInv.invert();
    pmv.set(c);
    return this;
  }
}
final static int GRID_SQUARE = 50;  //50 pixels, height/36, width/20
final float BULLET_DIAMETER = 10;
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
// Key to retrieve room in dungeon
//  Rooms will be sorted first by their vertical position, then by their horizontal position
static class Coord implements Comparable < Coord > {
  final int x; final int y;

  Coord(int x, int y) {
    this.x = x; this.y = y;
  }

  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    Coord other = (Coord)obj;
    if (x != other.x) { return false; }
    if (y != other.y) { return false; }
    return true;
  }

  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  public String toString() {
    return String.format("(%03d, %03d)", x, y);
  }

  // Mandated by comparable interface.
  public int compareTo(Coord c) {
    return y > c.y ? 1 : y < c.y ? -1 :
      x > c.x ? 1 : x < c.x ? -1 : 0;
  }

   public static Coord add(Coord a, Coord b) {
    return new Coord(a.x + b.x, a.y + b.y);
  }

}
class ScriptSpeak {
   Speaker speaker;
   String text;
   ScriptSpeak(Speaker speaker, String text) {
     this.speaker = speaker;
     this.text = text;
   }
}

class Dialogue {
  boolean crowMoving = false;
  PImage dialogueBox = loadImage("dialogue_box.png");
  PImage choiceBox = loadImage("choice_box.png");
  PImage foxProfile, crowProfile;
  ArrayList<ScriptSpeak> script = new ArrayList<ScriptSpeak>();
  long lastSpaceTime = 0;
  PVector roomLoc = new PVector(0, 0);
  int maxStrLen = 72;
  boolean cutSceneOn, cutScene1Complete, cutScene2Complete, cutScene3Complete, cutScene4Complete;
  boolean spaceLock = false;
  
  Dialogue() {
    cutSceneOn = false;
    cutScene1Complete = false;
    cutScene2Complete = false;
    cutScene3Complete = false;
    cutScene4Complete = false;
    foxProfile = flipImgVertical(loadImage("fox_profile.png"));
    crowProfile = flipImgVertical(loadImage("crow_profile.png"));
  }

  
  public void showText(String text, float x, float y) {
    // Text correct display
   fill(0);
   textSize(30);
   pushMatrix();
   scale(1, -1);  // Reversed y axis
   text(text, x, y);
   popMatrix();
  }
  
  public void showDialogue(Speaker speaker, String text) {
    String name = "";
    if (speaker == Speaker.FOX) {
      name = "Fox";
      fill(252, 154, 8);
    }
    else if (speaker == Speaker.CROW) {
      name = "Crow";
      fill(252, 61, 8);
    }
    image(dialogueBox, roomLoc.x - 690, roomLoc.y + height/2 - dialogueBox.height - 10);
    pushMatrix();
    textSize(40);
    scale(1, -1);  // Reversed y axis
    text(name, roomLoc.x - 640, -1 * roomLoc.y + -1 * height/2 + 60);
    fill(225);
    textSize(30);
    text(breakString(text), roomLoc.x - 640, -1 * roomLoc.y +  -1 * height/2 + 110);
    popMatrix();
    
    //Portrait
    if (speaker == Speaker.FOX) {
      image(foxProfile, roomLoc.x - width/2, roomLoc.y + 190);
    } else if (speaker == Speaker.CROW) {
      image(crowProfile, roomLoc.x - width/2, roomLoc.y + 190);
    }
  }
  
  public void showChoice(boolean leftArrow, boolean rightArrow) {
    if (leftArrow) {
     fillEndingSacrifice();
     spaceLock = false;
     script.remove(0);
    } else if (rightArrow) {
      fillEndingAbandon();
      spaceLock = false;
      script.remove(0);
    }
    image(choiceBox, roomLoc.x - 690, roomLoc.y + 50);
    image(choiceBox, roomLoc.x + choiceBox.width - 680, roomLoc.y + 50);
    //fill(225);
    fill(252, 61, 8);
    pushMatrix();
    textSize(40);
    scale(1, -1);  // Reversed y axis
    text("Make Ultimate Sacrifice    <-", roomLoc.x - 640, -1 * roomLoc.y - 100);
    text("->   Abandon Forest", roomLoc.x + 70, -1 * roomLoc.y - 100);
    popMatrix();
  }
  
  public String breakString(String s) {
    if (s.length() <= maxStrLen) return s;
    else {
      char[] newString = s.toCharArray();
      int newLineStart = 0;
      
      while (s.length() > newLineStart + maxStrLen) {
        for (int i = newLineStart + maxStrLen; i >= 0; i--) {
          if(newString[i] == ' ') {
            newString[i] = '\n';
            newLineStart = i+1;
            break;
          } else if (i == 0) return String.valueOf(newString);  //No space in line
        }
      }
      return String.valueOf(newString);
      
    }
  }
  
  public void cutScene(boolean spacePressed, boolean enterPressed, boolean returnPressed, boolean leftArrow, boolean rightArrow) {
    roomLoc = dungeon.getRoom(player.position).loc;
    if (player.gravity > 0) player.reverseGravity();
    
    if (!cutScene1Complete && script.isEmpty()) fillDialogue1();
    else if (!cutScene2Complete && script.isEmpty()) fillDialogue2();
    else if (!cutScene3Complete && script.isEmpty()) fillDialogue3();
    else if (!cutScene4Complete && script.isEmpty()) fillDialogue4();
    
    if (!spaceLock && (spacePressed || enterPressed || returnPressed) && millis() - lastSpaceTime > 500) {
      lastSpaceTime = millis();
      script.remove(0);
      
      if (!script.isEmpty() && script.get(0).speaker.equals(Speaker.NARRATOR) && script.get(0).text.equals("sacrifice")) {
        player.maxLife -= 1;
        player.lives = player.maxLife;
        script.remove(0);
        if (!cutScene2Complete) fill = color(167, 167, 167);
        else if (!cutScene3Complete) fill = color(148, 148, 148);
        else fill = color(75, 75, 75);
      }
      if (script.isEmpty()) {
        if (!cutScene1Complete) cutScene1Complete = true;
        else if (!cutScene2Complete) {
          cutScene2Complete = true;
          player.gravitySacrificed = true;
        }
        else if (!cutScene3Complete) {
          cutScene3Complete = true;
          player.dashSacrificed = true;
        }
        else if (!cutScene4Complete) {
          cutSceneOn = false;
          cutScene4Complete = true;
          saveToFile();
          gameState = GameState.TITLE;
          newGameSetup();
          return;
        }
        cutSceneOn = false;
        saveToFile();
        return;
      }
    }
    if (!script.isEmpty()) {
      showDialogue(script.get(0).speaker, script.get(0).text);
      if (script.get(0).text.equals("Run while you have your sanity, Little One!")) {
        spaceLock = true;
        showChoice(leftArrow, rightArrow);
      }
    }
    
  }
  
  public void fillDialogue1 () {
    script = new ArrayList<ScriptSpeak>();
    script.add(new ScriptSpeak(Speaker.CROW, "So my eyes did not betray me! It is not every day that this wretched place has visitors."));
    script.add(new ScriptSpeak(Speaker.FOX, "Greetings to you too!"));
    script.add(new ScriptSpeak(Speaker.CROW, "What brings you to the Abandoned Forest, Little One?"));   
    script.add(new ScriptSpeak(Speaker.FOX, "My ancestors used to roam these lands before the Forest became too toxic to live in."));
    script.add(new ScriptSpeak(Speaker.FOX, "I’m here to purify the Forest and restore our lost home."));
    script.add(new ScriptSpeak(Speaker.CROW, "A noble errand, but a foolish one."));
    script.add(new ScriptSpeak(Speaker.CROW, "Many have tried before you, and now they lie dead as mere stains upon this land. Turn back while you can!"));
    script.add(new ScriptSpeak(Speaker.FOX, "You can’t scare me away! I have a duty to fulfil and my tribe put their faith in me."));
    script.add(new ScriptSpeak(Speaker.CROW, "Do you even know what is needed to save this land?"));
    script.add(new ScriptSpeak(Speaker.FOX, "I need to find the three sacrificial trees and present a sacrifice on each of them. My ancestors’ spirits said I’ll know what to do when I get there."));
    script.add(new ScriptSpeak(Speaker.CROW, "Heh, you place your trust in them so blindly."));
    script.add(new ScriptSpeak(Speaker.CROW, "But you’ve piqued my interest… allow me to see your journey through to the end. Bitter though it may be."));
    
}

public void fillDialogue2() {
  script = new ArrayList<ScriptSpeak>();
  script.add(new ScriptSpeak(Speaker.CROW, "Ah, so you found your way here! Impressive!"));
  script.add(new ScriptSpeak(Speaker.FOX, "It was a long journey… but I’ve reached the sacrifical tree at last."));
  script.add(new ScriptSpeak(Speaker.FOX, "Tell me, how did this place become so hostile? Why are those odd animals attacking me?"));
  script.add(new ScriptSpeak(Speaker.CROW, "Those are not animals. They are remnants of the Old World."));
  script.add(new ScriptSpeak(Speaker.CROW, "Long ago, a single species ruled the Forest. They tore it down and built it up according to their will."));
  script.add(new ScriptSpeak(Speaker.CROW, "They were not strong but they surpassed all others in intelligence. They created machinery that moves on its own. But it has no will of its own, it just attacks all it sees mindlessly."));
  script.add(new ScriptSpeak(Speaker.FOX, "No will of its own… no mind."));
  script.add(new ScriptSpeak(Speaker.CROW, "That’s right. And if you pursue this task of yours, you are sure to face more danger."));
  script.add(new ScriptSpeak(Speaker.FOX, "I see… but still I must press on. "));
  script.add(new ScriptSpeak(Speaker.FOX, "I just need to make the sacrifice now. But I don’t know how."));
  script.add(new ScriptSpeak(Speaker.CROW, "Heh! If you don’t even know how, maybe you aren’t up for the task."));
  script.add(new ScriptSpeak(Speaker.CROW, "Go home while you still remember how, Little One!"));
  script.add(new ScriptSpeak(Speaker.FOX, "No! I will make the sacrifice!"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "sacrifice"));
  //Sacrifice
  script.add(new ScriptSpeak(Speaker.FOX, "What was that?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The Forest has accepted your sacrifice. You gave your mind to save the land."));
  script.add(new ScriptSpeak(Speaker.FOX, "I gave… my mind? But how could that be when my thoughts are still clear?"));
  script.add(new ScriptSpeak(Speaker.CROW, "Think back… where did you come from and what is your purpose here, Little One?"));
  script.add(new ScriptSpeak(Speaker.FOX, "I came from my tribe’s land to save the Forest."));
  script.add(new ScriptSpeak(Speaker.CROW, "And where is that land?"));
  script.add(new ScriptSpeak(Speaker.FOX, "It’s out there… somewhere. I’m sure of it."));
  script.add(new ScriptSpeak(Speaker.CROW, "You just arrived to this Forest but already forgot about your home?"));
  script.add(new ScriptSpeak(Speaker.FOX, "That can’t be… and yet I don’t remember."));
  script.add(new ScriptSpeak(Speaker.CROW, "Do you have any friends, any family waiting for you?"));
  script.add(new ScriptSpeak(Speaker.FOX, "I don’t know. I wonder if I do…"));
  script.add(new ScriptSpeak(Speaker.CROW, "Maybe if you did, they wouldn’t have sent you here."));
  script.add(new ScriptSpeak(Speaker.CROW, "Get away while you still can. The Abandoned Forest has swallowed your mind, but it has yet to swallow the rest of you."));
  script.add(new ScriptSpeak(Speaker.FOX, "No! Even with my memories lost, I know that my task was important."));
  script.add(new ScriptSpeak(Speaker.CROW, "You’re just a wondering body now, with no mind of your own. Fulfilling a purpose, but not understanding it. Show me if you’re any different from that machinery of old."));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou sacrificed your mind to the Forest"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou lost the ability to reverse gravity"));

}

public void fillDialogue3() {
  script = new ArrayList<ScriptSpeak>();
  script.add(new ScriptSpeak(Speaker.FOX, "At last, the tree!"));
  script.add(new ScriptSpeak(Speaker.CROW, "Took you long enough. I was just wondering if you dropped dead on the way!"));
  script.add(new ScriptSpeak(Speaker.CROW, "So, how does it feel to be without a mind?"));
  script.add(new ScriptSpeak(Speaker.FOX, "With each step I take, I feel more and more lost."));
  script.add(new ScriptSpeak(Speaker.FOX, "On my way here, rain poured from the sky. But it wasn’t water falling down… it burnt, like fire."));
  script.add(new ScriptSpeak(Speaker.CROW, "It was water falling, but it was also something else… ash rain."));
  script.add(new ScriptSpeak(Speaker.CROW, "It’s from the fires of Old World. The smoke corrupted even the skies that once held pure water."));
  script.add(new ScriptSpeak(Speaker.CROW, "The water creatures are all gone. When the sky weeps, the ground absorbs its colour."));
  script.add(new ScriptSpeak(Speaker.FOX, "Tell me, why did I come to this clearing? What… was I looking for?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The corruption got to your brain sooner than I thought. But the corruption gets everyone, it’s only a matter of time."));
  script.add(new ScriptSpeak(Speaker.CROW, "Go home while you still have legs to take you, Little One!"));
  script.add(new ScriptSpeak(Speaker.FOX, "No… I can’t go home, can I?"));
  script.add(new ScriptSpeak(Speaker.FOX, "I will not leave until I fulfil my task. I will stay here until I remember what it is!"));
  //Sacrifice
  script.add(new ScriptSpeak(Speaker.NARRATOR, "sacrifice"));
  script.add(new ScriptSpeak(Speaker.FOX, "What was that?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The forest has accepted your sacrifice. You gave your body to save the land."));
  script.add(new ScriptSpeak(Speaker.FOX, "I gave… my body? But how can that be if I can still see it?"));
  script.add(new ScriptSpeak(Speaker.CROW, "You can still see it, but can you feel the ground beneath your feet?"));
  script.add(new ScriptSpeak(Speaker.FOX, "Ah, it hurts when I look down… My body feels numb yet painful."));
  script.add(new ScriptSpeak(Speaker.CROW, "If it's so painful maybe you shouldn’t go on. Or maybe you should move slowly, where the paths are easier to take. If you flow where life takes you, you can be just like the ash water."));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou sacrificed your body to the Forest"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou lost the ability to dash"));
}

public void fillDialogue4() {
  script = new ArrayList<ScriptSpeak>();
  script.add(new ScriptSpeak(Speaker.FOX, "Here it is, where my journey comes to an end."));
  script.add(new ScriptSpeak(Speaker.FOX, "Are you there, Crow? I can’t see too well in this darkness…"));
  script.add(new ScriptSpeak(Speaker.CROW, "I am here. How was your journey, Little One who lacks a mind or body?"));
  script.add(new ScriptSpeak(Speaker.FOX, "It was agonising. I am in so much pain, there’s not a moment I spend without it."));
  script.add(new ScriptSpeak(Speaker.FOX, "I don’t remember my purpose. My legs carried me here, but I have no memories of who I am, or why I’m here."));
  script.add(new ScriptSpeak(Speaker.FOX, "No… my legs did not carry me. It was the Forest that guided me!"));
  script.add(new ScriptSpeak(Speaker.FOX, "I’m not the only one in pain. The Forest is the one screaming. The voices of all creatures, crying out in unison."));
  script.add(new ScriptSpeak(Speaker.FOX, "Is that what you wanted to show me, cruel Gods of the Forest?"));
  script.add(new ScriptSpeak(Speaker.CROW, "Welcome to the final sacrifice, Little One. Many have been on the journey you took, but few made it this far."));
  script.add(new ScriptSpeak(Speaker.FOX, "What is this place? It’s dark and cold… hard to imagine anyone surviving here."));
  script.add(new ScriptSpeak(Speaker.CROW, "When the fires and machinery of the Old World made the surface uninhabitable, we didn’t have much choice but to move underground."));
  script.add(new ScriptSpeak(Speaker.FOX, "But there’s barely anything here… how do you survive without food and water?"));
  script.add(new ScriptSpeak(Speaker.CROW, "It’s true, there isn’t much here to help sustain a life. But that itself is our salvation. Countless have fallen to this curse, but we few remain standing. Why do you think that is?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The answer is cannibalism. We eat those who fall along the way. Friends, enemies, family, those ties no longer matter in a world such as this."));
  script.add(new ScriptSpeak(Speaker.CROW, "Proud animals like your kind abandoned this place long ago. We’re all that remain, the wretched and the twisted. This is the world your tribe tossed you aside for."));
  script.add(new ScriptSpeak(Speaker.CROW, "That long journey, the sacrifices, it was all to tire you out. You don’t have the energy to fight back anymore, do you? "));
  script.add(new ScriptSpeak(Speaker.CROW, "Tell me Little One, is this the world you sought to save? Is this the truth you want to protect so?"));
  script.add(new ScriptSpeak(Speaker.CROW, "Run while you have your sanity, Little One!"));
  //Choice
}

public void fillEndingSacrifice() {
  script.add(new ScriptSpeak(Speaker.FOX, "Thank you for seeing my journey through the end. I may not remember it, but I hope you will."));
  script.add(new ScriptSpeak(Speaker.FOX, "With the last remnant of my sanity, I sacrifice my soul to the Forest!"));
  //Sacrifice
  script.add(new ScriptSpeak(Speaker.NARRATOR, "sacrifice"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou sacrificed your soul to the Forest"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou lost the ability to move"));
  script.add(new ScriptSpeak(Speaker.CROW, "Unbelievable! You passed all the trials of the Forest."));  
  script.add(new ScriptSpeak(Speaker.CROW, "Even after looking into the abyss and confronted by your futility, you made the sacrifice. Heh, maybe you’re the insane one here, and not this Forest!"));
  script.add(new ScriptSpeak(Speaker.FOX, "…"));
  script.add(new ScriptSpeak(Speaker.CROW, "Forgotten how to speak, have you? Losing your soul does take a toll on you. But do not fret, you won’t need one where you’re going."));
  script.add(new ScriptSpeak(Speaker.CROW, "You have honoured the promise your ancestors made to us, so we, Forest Gods shall honour your sacrifice. Let the Forest prosper, let it be your cradle. Have a peaceful eternal rest, Little One! "));
  lastEnding = 1;
}

public void fillEndingAbandon() {
  script.add(new ScriptSpeak(Speaker.FOX, "No…  my tribe betrayed me?"));
  script.add(new ScriptSpeak(Speaker.FOX, "This Forest is beyond salvation, isn’t it Crow?"));
  script.add(new ScriptSpeak(Speaker.FOX, "The corruption runs deep, all the way to the heart of its residents. Even I can feel its weight after such a short time spent here."));
  script.add(new ScriptSpeak(Speaker.FOX, "I was sent a treat to sate your hunger. There never was a greater cause."));
  script.add(new ScriptSpeak(Speaker.FOX, "Thrown aside one for the sake of the many so the tribe can live on, unbothered by your hunger."));
  script.add(new ScriptSpeak(Speaker.CROW, "So that’s the answer you found. But what can you do with a broken body and mind?"));
  script.add(new ScriptSpeak(Speaker.FOX, "I’m more than some old machinery or mass of water. I won’t let you have my soul."));
  script.add(new ScriptSpeak(Speaker.FOX, "Even a step away from here is further than I could get by giving up here."));
  script.add(new ScriptSpeak(Speaker.CROW, "You’re a curious creature, Little One. So close to reaching your goal only to throw it all away."));
  script.add(new ScriptSpeak(Speaker.CROW, "I hope you don’t regret this. Now go, leave the Forest before the corruption gets to you."));
  script.add(new ScriptSpeak(Speaker.FOX, "You’re not going to eat me?"));
  script.add(new ScriptSpeak(Speaker.CROW, "We are the Gods of the Forest. We have no business with the living and you’re clearly not ready to die."));
  script.add(new ScriptSpeak(Speaker.CROW, "Have a long life filled with struggles, Little One. And don’t forget what this Forest taught you."));
  lastEnding = 2;
}
  
}
// Import Java collections libraries.





class Dungeon {
  long seed;
  final PVector invDim = new PVector();
  final PVector max = new PVector();
  final PVector min = new PVector();
  final Random rng = new Random();
  Room start = new Room(map1);
  final NavigableMap < Coord, Room > rooms = new TreeMap < Coord, Room > ();

  Dungeon(float w, float h) {
    generate(w, h);
  }

  public String toString() {
    return Long.toString(seed);
  }

  public void draw(PGraphicsOpenGL r) {
    r.pushStyle();
    r.noStroke();

    // Draw extents of dungeon.
    r.rectMode(CORNERS);
    r.fill(0x7f373737);
    r.rect(min.x, min.y,
      max.x, max.y);

    // Run through values of map, draw room.
    //for (Room room : rooms.values()) {
    //  room.drawRoom(r);
    //}
    
    // Own code, optimisation, draw only current room and direct neighbours
    for (int i = -1; i <= 1; i++) {
       for (int j = -1; j <= 1; j++) {
         Room room = getRoom(new PVector(player.position.x + i * width, player.position.y + j * height));
         if (room != null) room.drawRoom(r);
       }
    }
    
    r.popStyle();
  }

  public NavigableMap < Coord, Room > generate(float w, float h) {

    // Create a default room located at (0, 0).
    // Reset rooms map and add origin.
    rooms.clear();
    Coord curr = new Coord(0, 0);
    Coord checkpoint = new Coord(0, 0);
    rooms.put(curr, start);


    // To store dungeon minimums and maximums.
    int minx = 0; int miny = 0;
    int maxx = 0; int maxy = 0;
    Room r;
     
    // Own code start
    checkpoint = Coord.add(checkpoint, new Coord(1, 0));
    rooms.put(checkpoint, new Room(map2));
    
    //Rooms for sacrifice 1
    r = new Room(map3);
    curr = Coord.add(checkpoint, new Coord(0, 1));
    rooms.put(curr, r);
    r.addMachine(new Machinery(new PVector(2000, 875)));
    r.addMachine(new Machinery(new PVector(1000, 1200)));
    r.addMachine(new Machinery(new PVector(2250, 1400)));
    
    r = new Room(map5);
    curr = Coord.add(curr, new Coord(1, 1));
    rooms.put(curr, new Room(map5));
    
    r = new Room(map6);
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, r);
    r.addMachine(new Machinery(new PVector(2900, 1400)));
    r.addMachine(new Machinery(new PVector(4400, 1400)));

    // Rooms for sacrifice 2
    checkpoint = Coord.add(checkpoint, new Coord(1, 0));
    rooms.put(checkpoint, new Room(map7));
    
    r = new Room(map8);
    curr = Coord.add(checkpoint, new Coord(1, 0));
    rooms.put(curr, r);
    for (int i = 0; i < 10; i++) {
       r.rain.add(new RainDrop(4900, 5900)); 
    }
    
    r = new Room(map9);
    curr = Coord.add(curr, new Coord(1, 0));
    rooms.put(curr, r);
    
    for (int i = 0; i < 10; i++) {
       r.rain.add(new RainDrop(6700, 7700)); 
    }
    
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, new Room(map10));
    
    //Rooms for sacrifice 3
    curr = Coord.add(checkpoint, new Coord(0, -1));
    rooms.put(curr, new Room(map11));
    
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, new Room(map12));
    
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, new Room(map13));
    
    // Own code end

    // Update min and max coordinates.
    minx = min(minx, curr.x);
    miny = min(miny, curr.y);
    maxx = max(maxx, curr.x);
    maxy = max(maxy, curr.y);
    //}

    // Set room dimensions. invDim will be used to find
    // appropriate room by coordinate given a vector.
    float halfw = w * 0.5f; float halfh = h * 0.5f;
    invDim.set(1.0f / w, 1.0f / h, 0.0f);

    // Find dungeon extents.
    min.set(minx * w, miny * h);
    min.sub(halfw, halfh);
    max.set(maxx * w, maxy * h);
    max.add(halfw, halfh);

    // Each entry in a map is a key-value pair. To loop over
    // all entries of a map, we acquire a set of entries.
    Set < NavigableMap.Entry < Coord, Room > > entries = rooms.entrySet();
    for (NavigableMap.Entry < Coord, Room > entry : entries) {

      // Get key and value from entry.
      Coord coord = entry.getKey();
      Room room = entry.getValue();

      room.loc.set(coord.x * w, coord.y * h);
      room.halfScale.set(halfw, halfh);
    }
    
    start = getRoom(player.position);
    
    return rooms;
  }

  public Room getRoom(PVector in) {    
    Coord coord = new Coord(
      round(in.x * invDim.x),
      round(in.y * invDim.y));
      
      // Own code
      return rooms.containsKey(coord) ? rooms.get(coord) : null;
  }
}
static class DungeonCam extends Camera2D {
  float minZoom = 0.125f;
  float maxZoom = 5.0f;
  float defZoom = 1.0f;
  float zoomIncr = 0.01f;
  float zoom = defZoom;

  DungeonCam(PGraphics2D renderer) {
    super(renderer);
  }

  public DungeonCam lookAt(Dungeon dungeon, PVector loc, boolean[] pressed, int zoomIn, int zoomOut, boolean snap) {
    boolean inPress = pressed[zoomIn];
    boolean outPress = pressed[zoomOut];
    if (inPress || outPress) {
      if (inPress) {
        zoom = constrain(zoom + zoomIncr,
          minZoom, maxZoom);
      }
      if (outPress) {
        zoom = constrain(zoom - zoomIncr,
          minZoom, maxZoom);
      }
    } else {
      zoom = defZoom;
    }
    Room room = dungeon.getRoom(loc);
    lookAt(room.loc.x, room.loc.y, 0.0f, zoom, zoom, snap);
    
    return this;
  }
}
public static float lerpAngle(float a, float b, float step) {
  
  // Prefer shortest distance,
  float delta = b - a;
  if (delta == 0.0f) {
  
    // Problem case: where angles are 180 degrees
    // apart and neither clockwise nor counter-clockwise
    // are shorter than the other.
    return lerpAngle(a + EPSILON, b, step);
  } else if (delta < -PI) {
    b += TWO_PI;
  } else if (delta > PI) {
    a += TWO_PI;
  }
  return (1.0f - step) * a + step * b;
}
class Machinery {
  PVector position;
  int lastShotTime = 0;
  
  Machinery(PVector position) {
    this.position = position;
  }
  
  public void shoot(ArrayList<Bullet> bullets) {
    int currentTime = millis();
    if (currentTime > lastShotTime + 3500 && position.dist(player.position) < 600) {
      PVector vel = new PVector((player.position.x + player.foxImg.width/2 - position.x), (player.position.y + player.foxImg.height/2 - position.y) );
      vel = vel.normalize().setMag(3);
      bullets.add(new Bullet(new PVector(position.x, position.y), vel));
      lastShotTime = millis();
    }
     
    
  }
  
  public void draw(ArrayList<Bullet> bullets) {
    shoot(bullets);
    fill(0);
    ellipse(position.x, position.y, 50, 50);
  }
}
//Game maps represented as 36x20 grids
static int[][] map0 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map1 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 3, 3, 0, 0, 0, 3, 3, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
{1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map2 = {
{1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 3, 3, 3, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1},
{1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map3 = {
{1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 7, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map4 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 3, 3, 3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 3, 0, 0, 1, 0, 0, 2, 2, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 1, 1, 3, 3, 0, 0, 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 1, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1}};

static int[][] map5 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map6 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0, 1, 2, 1, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 7, 0, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};



static int[][] map7 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
{1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map8 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map9 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
{1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 1, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1}};

static int[][] map10 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 1, 0, 0, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map11 = {
{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1},
{1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1},
{1, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 2, 2, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
{1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map12 = {
{1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 3, 3, 3, 3, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1},
{1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
{1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

static int[][] map13 = {
{1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
class Player{
  PImage foxImg, life, nolife;
  PVector position;
  PVector velocity;
  int sizeX;
  int sizeY;
  int maxLife = 3;
  int lives = 3;
  long lastJumpTime = 0;
  long lastDashTime = 0;
  long lastDamageTime = 0;
  float gravity = -1.2f;
  boolean hasDied = false;
  boolean faceRight = true;
  boolean imgRight = true;
  boolean isOnGround = true;
  boolean lockJump = false;
  boolean isImmune = false;
  boolean gravitySacrificed = false;
  boolean dashSacrificed = false;
  
 Player(float x, float y, float velX, float velY) {
   this.position = new PVector(x, y);
   this.velocity = new PVector(velX, velY);
   foxImg = flipImgVertical(loadImage("fox.png"));
   life = flipImgVertical(loadImage("life.png"));
   nolife = flipImgVertical(loadImage("nolife.png"));
   sizeX = foxImg.width;
   sizeY = foxImg.height;
 }
 
 // Slighty modified from https://stackoverflow.com/questions/29334348/processing-mirror-image-over-x-axis
 public PImage flipImgHorizontal(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int x = 0 ; x < flipped.width; x++) {                            //loop through each column
      flipped.set(flipped.width-x-1, 0, img.get(x, 0, 1, img.height));  //copy a column in reverse x order
    }
    return flipped;
 }
 
 public PImage flipImgVertical(PImage img) {
   PImage flipped = createImage(img.width, img.height, ARGB);
   for(int y = 0 ; y < flipped.height; y++) {
      flipped.set(0, flipped.height-y-1, img.get(0, y, img.width, 1));
    }
    return flipped;
 }
 
  public void moveInput(float x, float y) {
    float moveX = x;
    float moveY = y;
   if (abs(velocity.x + moveX) > 8) moveX = moveX/abs(moveX) * 8; 
   if(abs(velocity.y + moveY) > 10) moveY = moveY/abs(moveY) * 10;
   if(abs(velocity.x + moveX) < 8 && abs(velocity.y + moveY) < 10) {
   this.velocity.add(new PVector(moveX, moveY));
   }
 
 }
 
  public void move(boolean[] pressed, int leftKey, int rightKey, int upKey, int leftDash, int rightDash, int spaceKey) {
    if (!dialogue.cutSceneOn && millis() - dialogue.lastSpaceTime > 500) {
      
      
        if (pressed[upKey] &&  !lockJump && (isOnGround || (!isOnGround && millis() - lastJumpTime < 450)) && millis() -  lastDashTime > 500) {
          if (isOnGround) lastJumpTime = millis();
         velocity = gravity < 0 ? new PVector(velocity.x, 8) : new PVector(velocity.x, -8); 
       } else {
         lockJump = isOnGround ? false : true;
       }
         
       if (pressed[rightKey] && !pressed[leftKey]) {
         faceRight = true;
         if (!imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = true;
         }
         if (isOnGround) {
           moveInput(1.6f, 0);
         } else {
           moveInput(4, 0);
         }
       }
       
       if (pressed[leftKey] && !pressed[rightKey]) {
         faceRight = false;
         if (imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = false;
         }
         if (isOnGround) {
         moveInput(-1.6f, 0);
         } else {
           moveInput(-4, 0);
         }
       }  
       
       if (!dashSacrificed && pressed[leftDash] && millis() - lastDashTime > 500 ) {
         lastDashTime = millis();
         faceRight = false;
         if (imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = false;
         }
         velocity = new PVector(-10, 0);
       }
       
       if (!dashSacrificed && pressed[rightDash] && millis() - lastDashTime > 500) {
         lastDashTime = millis();
         faceRight = true;
         if (!imgRight) {
           foxImg = flipImgHorizontal(foxImg);
           imgRight = true;
         }
         velocity = new PVector(10, 0);
       }
       
       if (!gravitySacrificed && pressed[spaceKey] && isOnGround) {
           reverseGravity();
       }
    }
   
   PVector futurePos = new PVector(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
   if (!collisionCheck(futurePos)) {
     this.position = futurePos;
     if (millis() - lastDashTime > 200) {
       this.velocity.mult(0.80f);
     }
   } else {
     //this.position = new PVector(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
     this.velocity = new PVector(0,0);
   }
   
   //gravity
   if ( !collisionCheck(new PVector(position.x, position.y + gravity)) && millis() - lastDashTime > 200) {
     isOnGround = false;
     this.velocity.add(0, gravity);
     this.position = new PVector(position.x, position.y + gravity);
   } else {
     isOnGround = true;
   }
 }
 
 public void reverseGravity() {
   gravity *= -1;
   foxImg = flipImgVertical(foxImg);
 }
 
 public boolean collisionCheck(PVector pos) {
   // Other 3 corners of player sprite
   PVector pos2 = new PVector(pos.x + foxImg.width, pos.y);
   PVector pos3 = new PVector(pos.x + foxImg.width, pos.y + foxImg.height);
   PVector pos4 = new PVector(pos.x, pos.y + foxImg.height);
   
   // Mid-point in player sprite across x axis, because sprite is wider than a block
   PVector pos5 = new PVector(pos.x + foxImg.width/2, pos.y);
   PVector pos6 = new PVector(pos.x + foxImg.width/2, pos.y + foxImg.height);
   
   // Collision
   return (pointCollision(pos, dungeon.getRoom(pos)) | pointCollision(pos2, dungeon.getRoom(pos2))
       | pointCollision(pos3, dungeon.getRoom(pos3)) | pointCollision(pos4, dungeon.getRoom(pos4))
       | pointCollision(pos5, dungeon.getRoom(pos5)) | pointCollision(pos6, dungeon.getRoom(pos6)));

 }
 
 public boolean pointCollision(PVector pos, Room room) {
   int gridX, gridY;
   gridX = (int)(pos.x + room.halfScale.x - room.loc.x)/ GRID_SQUARE;
   gridY = 19 - (int)(pos.y + room.halfScale.y - room.loc.y)/ GRID_SQUARE; // Compensates for flipped y axis
   if (gridX > 35) gridX = 35;  // Prevents array out of bounds
   if (gridY > 19) gridY = 19;
   if (gridX < 0) gridX = 0;  // Prevents array out of bounds
   if (gridY < 0) gridY = 0;
   
   if (isImmune && millis() - lastDamageTime > 2000) {
     isImmune = false;
     foxImg.filter(INVERT);
   }
   if (room.grid[gridY][gridX] == 2 || room.grid[gridY][gridX] == 3) {  // Damage
     takeDamage();
      return true;
   }
   // Wall
   if (room.grid[gridY][gridX] == 1 
     || (!dialogue.cutScene2Complete && room.grid[gridY][gridX] == 5) 
     || (!dialogue.cutScene3Complete && room.grid[gridY][gridX] == 6)) {
     return true;
   }
   return false;
 }
 
 public void takeDamage() {
   if (millis() - lastDamageTime > 2000) {
        lastDamageTime = millis();
        lives -= 1;
        
        // Death
        if (lives == 0 && maxLife != 0) {
          lastDamageTime = 0;
          if (gravity > 0) {
            reverseGravity();
          }
           player.position = checkPointCoord();
           //player.position = checkPointPos0;
           player.lives = player.maxLife;
        } else {
          isImmune = true;
          foxImg.filter(INVERT);
        }
      }
 }


 public void drawPlayer() {
   fill(255);
   
   //Uncomment to see collision box
   //rect(this.position.x, this.position.y, this.sizeX, this.sizeY);
   
   image(foxImg, this.position.x, this.position.y);
   PVector dungeonCorner = new PVector(dungeon.getRoom(position).loc.x - width/2, dungeon.getRoom(position).loc.y + height/2 - GRID_SQUARE);
   if (maxLife == 0) return;
   for (int i = 0; i < maxLife; i++) {
     if (i < lives) {
       image(life, dungeonCorner.x + i * GRID_SQUARE, dungeonCorner.y);
     } else {
        image(nolife, dungeonCorner.x + i * GRID_SQUARE, dungeonCorner.y); 
     }
   }
 }
}
class RainDrop {
  PVector position;
  PVector velocity;
  boolean hasLanded = false;
  float minX, maxX;
  
  RainDrop(float minX, float maxX) {
    this.minX = minX;
    this.maxX = maxX;
    reset();
  }
  
  public void fall() {
    //System.out.println("fall!");
    position.add(velocity);
    if (position.y <= - 500) reset();
    else if (player.position.x < position.x && player.position.x + player.foxImg.width > position.x 
        && player.position.y < position.y && player.position.y + player.foxImg.height > position.y) {
          player.takeDamage();
          reset();
    }
    
  }
  
  public void reset() {
    position = new PVector(random(minX, maxX), random(500, 1200));
    velocity = new PVector(0, random(-3, -6));
}
  
  public void draw(PGraphicsOpenGL r) {
    fall();
    fill(225);
    r.beginShape(QUADS);
    r.vertex(position.x - 1, position.y);
    r.vertex(position.x + 1, position.y);
    r.vertex(position.x + 1, position.y + 20);
    r.vertex(position.x - 1, position.y + 20);
    r.endShape(CLOSE);
  }
  
  
}
class Room {
  final PVector loc = new PVector(0.0f, 0.0f);
  PVector halfScale = new PVector(0.5f, 0.5f);
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
  
  public void addMachine(Machinery machine) {
      machines.add(machine);
  }
  
  public void drawRoom(PGraphicsOpenGL r) {
    
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
  public void settings() {  size(1800, 1000, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "AbandonedForest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
