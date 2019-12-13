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
  ArrayList<ScriptSpeak> script = new ArrayList<ScriptSpeak>();
  long lastSpaceTime = 0;
  PVector roomLoc = new PVector(0, 0);
  int maxStrLen = 72;
  boolean cutSceneOn, cutScene1Complete, cutScene2Complete, cutScene3Complete, cutScene4Complete;
  
  Dialogue() {
    cutSceneOn = false;
    cutScene1Complete = false;
    cutScene2Complete = false;
    cutScene3Complete = false;
    cutScene4Complete = false;
  }

  
  void showText(String text, float x, float y) {
    // Text correct display
   fill(0);
   textSize(30);
   pushMatrix();
   scale(1, -1);  // Reversed y axis
   text(text, x, y);
   popMatrix();
  }
  
  void showDialogue(Speaker speaker, String text) {
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
    text(name, roomLoc.x - 640, roomLoc.y + -1 * height/2 + 60);
    fill(225);
    textSize(30);
    text(breakString(text), roomLoc.x - 640, roomLoc.y +  -1 * height/2 + 110);
    popMatrix();
  }
  
  String breakString(String s) {
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
  
  void cutScene1(int sceneNum, boolean spacePressed) {
    roomLoc = dungeon.getRoom(player.position).loc;
    if (player.gravity > 0) player.reverseGravity();
    if (sceneNum == 1 && script.isEmpty()) fillDialogue1();
    if (spacePressed && millis() - lastSpaceTime > 500) {
      lastSpaceTime = millis();
      script.remove(0);
      if (script.isEmpty()) {
        cutScene1Complete = true;
        cutSceneOn = false;
        return;
      }
    }
    showDialogue(script.get(0).speaker, script.get(0).text);
  }
  
  void fillDialogue1 () {
    //script.add(new ScriptSpeak(Speaker.FOX, ""));
    //script.add(new ScriptSpeak(Speaker.CROW, ""));
    script.add(new ScriptSpeak(Speaker.CROW, "So my eyes did not betray me! It is not every day that this wretched place has visitors."));
    script.add(new ScriptSpeak(Speaker.FOX, "Greetings to you too!"));
    script.add(new ScriptSpeak(Speaker.CROW, "What brings you to the Abandoned Forest, Little One?"));   
    script.add(new ScriptSpeak(Speaker.FOX, "My ancestors used to roam these lands before the Forest became too toxic to live in."));
    script.add(new ScriptSpeak(Speaker.FOX, "I’m here to purify the Forest and restore our lost home."));
    script.add(new ScriptSpeak(Speaker.CROW, "A noble errand, but a foolish one."));
    script.add(new ScriptSpeak(Speaker.CROW, "Many have tried before you, and now they lie dead as mere stains upon this land. Turn back while you can!"));
    script.add(new ScriptSpeak(Speaker.FOX, "You can’t scare me away! I have a duty to fulfil and my tribe put their faith in me."));
    script.add(new ScriptSpeak(Speaker.CROW, "Do you even know what is needed to save this land?"));
    script.add(new ScriptSpeak(Speaker.FOX, "I need to find the three pedestals and present a sacrifice on each of them. My ancestors’ spirits said I’ll know what to do when I get there."));
    script.add(new ScriptSpeak(Speaker.CROW, "Heh, foolish of you to trust them so blindly."));
    script.add(new ScriptSpeak(Speaker.CROW, "But you’ve piqued my interest… allow me to see your journey through to the bitter end!"));
    
}
  
}
