class Dialogue {
  
  void showText(String text, float x, float y) {
    // Text correct display
   fill(0);
   textSize(30);
   pushMatrix();
   scale(1, -1);  // Reversed y axis
   text(text, x, y);
   popMatrix();
  }
  
  void showDialogue() {
    
  }
  
}
