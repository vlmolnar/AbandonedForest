class Machinery {
  PVector position;
  int lastShotTime = 0;
  
  Machinery(PVector position) {
    this.position = position;
  }
  
  void shoot() {
    int currentTime = millis();
    if (currentTime > lastShotTime + 1500) {
      
    }
     
    
  }
  
  void draw() {
    
  }
}
