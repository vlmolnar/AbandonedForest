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
  
  void fall() {
    //System.out.println("fall!");
    position.add(velocity);
    if (position.y <= - 500) reset();
    else if (player.position.x < position.x && player.position.x + player.foxImg.width > position.x 
        && player.position.y < position.y && player.position.y + player.foxImg.height > position.y) {
          player.takeDamage();
          reset();
    }
    
  }
  
  void reset() {
    position = new PVector(random(minX, maxX), random(500, 1200));
    velocity = new PVector(0, random(-3, -6));
}
  
  void draw(PGraphicsOpenGL r) {
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
