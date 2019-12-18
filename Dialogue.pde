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
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou sacrificed your mind to the Forest"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou lost the ability to reverse gravity"));

}

void fillDialogue3() {
  //script.add(new ScriptSpeak(Speaker.FOX, ""));
  //script.add(new ScriptSpeak(Speaker.CROW, ""));
  script.add(new ScriptSpeak(Speaker.FOX, "At last, the pedestal!"));
  script.add(new ScriptSpeak(Speaker.CROW, "Took you long enough. I was just wondering if you dropped dead on the way!"));
  script.add(new ScriptSpeak(Speaker.CROW, "So, how does it feel to be without a mind?"));
  script.add(new ScriptSpeak(Speaker.FOX, "With each step I take, I feel more and more lost."));
  script.add(new ScriptSpeak(Speaker.FOX, "On my way here, rain poured from the sky. But it wasn’t water falling down… it burnt, like fire."));
  script.add(new ScriptSpeak(Speaker.CROW, "It was water falling, but it was also something else… poisoned rain."));
  script.add(new ScriptSpeak(Speaker.CROW, "It’s from the fires of Old World. The smoke corrupted even the skies that once held pure water."));
  script.add(new ScriptSpeak(Speaker.CROW, "The water creatures are all gone. When the sky weeps, all of us on the ground weep with it now."));
  script.add(new ScriptSpeak(Speaker.FOX, "Tell me, why did I come to this clearing? What… was I looking for?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The corruption got to your brain sooner than I thought. But the corruption gets everyone, it’s only a matter of time."));
  script.add(new ScriptSpeak(Speaker.CROW, "Go home while you still have legs to take you, Little One!"));
  script.add(new ScriptSpeak(Speaker.FOX, "No… I can’t go home, can I?"));
  script.add(new ScriptSpeak(Speaker.FOX, "I will not leave until I fulfil my task. I will stay here until I remember what it is!"));
  //Sacrifice
  script.add(new ScriptSpeak(Speaker.FOX, "What was that?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The forest has accepted your sacrifice. You gave your body to save the land."));
  script.add(new ScriptSpeak(Speaker.FOX, "I gave… my body? But how can that be if I can still see it?"));
  script.add(new ScriptSpeak(Speaker.CROW, "You can still see it, but can you feel the ground beneath your feet?"));
  script.add(new ScriptSpeak(Speaker.FOX, "Ah, it hurts when I look down… but how can I be in pain when my body has gone numb? What do I do now, how do I go on?"));
  script.add(new ScriptSpeak(Speaker.CROW, "Maybe you shouldn’t go on. Or maybe you should move slowly, where the paths are easier to take. If you flow where life takes you, you can be just like the toxic water."));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou sacrificed your body to the Forest"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou lost the ability to dash"));
}

void fillDialogue4() {
  //script.add(new ScriptSpeak(Speaker.FOX, ""));
  //script.add(new ScriptSpeak(Speaker.CROW, ""));
  script.add(new ScriptSpeak(Speaker.FOX, "Here it is, where my journey comes to an end."));
  script.add(new ScriptSpeak(Speaker.FOX, "Are you there, Crow? I can’t see too well in this darkness…"));
  script.add(new ScriptSpeak(Speaker.CROW, "I am here. How was your journey, Little One who lacks a mind or body?"));
  script.add(new ScriptSpeak(Speaker.FOX, "It was agonising. I am in so much pain, there’s not a moment I spend without it."));
  script.add(new ScriptSpeak(Speaker.FOX, "I don’t remember my purpose. My legs carried me here, but I have no memories of who I am, or why I’m here."));
  script.add(new ScriptSpeak(Speaker.FOX, "No… my legs did not carry me. It was the Forest that guided me!"));
  script.add(new ScriptSpeak(Speaker.FOX, "I’m not the only one in pain. The Forest is the one screaming. The voices of all creatures, crying out in unison.");
  script.add(new ScriptSpeak(Speaker.FOX, "Is that what you wanted to show me, cruel Gods of the Forest?"));
  //Room lights up
  script.add(new ScriptSpeak(Speaker.CROW, "Welcome to the final sacrifice, Little One. Many have been on the journey you took, but few made it this far."));
  script.add(new ScriptSpeak(Speaker.FOX, "What is this place? It’s dark and cold… hard to imagine anyone surviving here."));
  script.add(new ScriptSpeak(Speaker.CROW, "When the fires and machinery of the Old World made the surface uninhabitable, we didn’t have much choice but to move underground."));
  script.add(new ScriptSpeak(Speaker.FOX, "But there’s barely anything here… how do you survive without food and water?"));
  script.add(new ScriptSpeak(Speaker.CROW, "It’s true, there isn’t much here to help sustain a life. But that itself is our salvation. Countless have fallen to this curse, but we few remain standing. Why do you think that is?"));
  script.add(new ScriptSpeak(Speaker.CROW, "The answer is cannibalism. We eat those who fall along the way. Friends, enemies, family, those ties no longer matter in a world such as this."));
  script.add(new ScriptSpeak(Speaker.CROW, "Proud animals like your kind abandoned this place long ago. So did the species of Old. We’re all that remain, the wretched and the twisted. This is the world your tribe tossed you aside for."));
  script.add(new ScriptSpeak(Speaker.CROW, "That long journey, the sacrifices, it was all to tire you out. You don’t have the energy to fight back anymore, do you? "));
  script.add(new ScriptSpeak(Speaker.CROW, "Tell me Little One, is this the world you sought to save? Is this the truth you want to protect so?"));
  script.add(new ScriptSpeak(Speaker.CROW, "Run while you have your sanity, Little One!"));
  //Choice
}

void fillEndingSacrifice() {
  script.add(new ScriptSpeak(Speaker.FOX, "Thank you for seeing my journey through the end. I may not remember it, but I hope you will."));
  script.add(new ScriptSpeak(Speaker.FOX, "With the last remnant of my sanity, I sacrifice my soul to the Forest!"));
  //Sacrifice
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou sacrificed your soul to the Forest"));
  script.add(new ScriptSpeak(Speaker.NARRATOR, "\nYou lost the ability to move"));
  script.add(new ScriptSpeak(Speaker.CROW, "Unbelievable! You passed all the trials of the Forest. Even after looking into the abyss and being confronted with own foolishness, you made the sacrifice. Heh, maybe you’re the insane one here, and not this Forest!"));
  script.add(new ScriptSpeak(Speaker.FOX, "…"));
  script.add(new ScriptSpeak(Speaker.CROW, "Forgotten how to speak, have you? Losing your soul does take a toll on you. But do not fret, you won’t need a soul where you’re going."));
  script.add(new ScriptSpeak(Speaker.CROW, "You have honoured the promise your ancestors made to us, so we, Forest Gods shall honour your sacrifice. Let the Forest prosper, let it be your cradle. Have a peaceful eternal rest, Little One! "));
  //Crows flock Fox, screen turns black
}

void fillEndingAbandon() {
  script.add(new ScriptSpeak(Speaker.FOX, "No…  my tribe betrayed me?"));
  script.add(new ScriptSpeak(Speaker.FOX, "This Forest is beyond salvation, isn’t it Crow? The corruption runs deep, all the way to the heart of its residents. Even I can feel its weight after such a short time spent here. "));
  script.add(new ScriptSpeak(Speaker.FOX, "I was sent a treat to sate your hunger. There never was a greater cause. Throw aside one for the sake of the many so the tribe can live on, unbothered by your hunger."));
  script.add(new ScriptSpeak(Speaker.CROW, "So that’s the answer you found. But what can you do with a broken body and mind?"));
  script.add(new ScriptSpeak(Speaker.FOX, "I’m more than some old machinery or mass of water. I won’t let you have my soul."));
  script.add(new ScriptSpeak(Speaker.FOX, "Even a step away from here is further than I could get by giving up here."));
  script.add(new ScriptSpeak(Speaker.CROW, "You’re a curious creature, Little One. So close to reaching your goal only to throw it all away."));
  script.add(new ScriptSpeak(Speaker.CROW, "I hope you don’t regret this. Now go, leave the Forest before the corruption gets to you."));
  script.add(new ScriptSpeak(Speaker.FOX, "You’re not going to eat me?"));
  script.add(new ScriptSpeak(Speaker.CROW, "We are the Gods of the Forest. We have no business with the living and you’re clearly not ready to die."));
  script.add(new ScriptSpeak(Speaker.CROW, "Have a long life filled with struggles, Little One. And don’t forget what this Forest taught you."));
  //Screen turns black
}
  
}
