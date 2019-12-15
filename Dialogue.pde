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
    if (sceneNum == 2 && script.isEmpty()) fillDialogue2();
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
    script.add(new ScriptSpeak(Speaker.CROW, "Heh, you place your trust in them so blindly."));
    script.add(new ScriptSpeak(Speaker.CROW, "But you’ve piqued my interest… allow me to see your journey through to the end. Bitter though it may be."));
    
}

void fillDialogue2() {
  //script.add(new ScriptSpeak(Speaker.FOX, ""));
  //script.add(new ScriptSpeak(Speaker.CROW, ""));
  script.add(new ScriptSpeak(Speaker.CROW, "Ah, so you found your way here! Impressive!"));
  script.add(new ScriptSpeak(Speaker.FOX, "It was a long journey… but I’ve reached the pedestal at last."));
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
  //Sacrifice animation?
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
  script.add(new ScriptSpeak(Speaker.CROW, "Get away while you still can. The Abandoned Forest has swallowd your mind, but it has yet to swallow the rest of you."));
  script.add(new ScriptSpeak(Speaker.FOX, "No! Even with my memories lost, I know that my task was important."));
  script.add(new ScriptSpeak(Speaker.CROW, "You’re just a wondering body now, with no mind of your own. Fulfilling a purpose, but not understanding it. Show me if you’re any different from that machinery of old."));
}
  
}
