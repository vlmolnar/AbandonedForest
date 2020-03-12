class Machinery {
  PVector position;
  int lastShotTime = 0;
  
  Machinery(PVector position) {
    this.position = position;
  }
  
  void shoot(ArrayList<Bullet> bullets) {
    int currentTime = millis();
    if (currentTime > lastShotTime + 3500 && position.dist(player.position) < 600) {
      PVector vel = new PVector((player.position.x + player.foxImg.width/2 - position.x), (player.position.y + player.foxImg.height/2 - position.y) );
      vel = vel.normalize().setMag(3);
      bullets.add(new Bullet(new PVector(position.x, position.y), vel));
      lastShotTime = millis();
    }
     
    
  }
  
  void draw(ArrayList<Bullet> bullets) {
    shoot(bullets);
    fill(0);
    ellipse(position.x, position.y, 50, 50);
  }
}
