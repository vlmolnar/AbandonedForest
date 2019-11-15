static class Room {
  final PVector loc = new PVector(0.0, 0.0);
  PVector halfScale = new PVector(0.5, 0.5);
  color fill = 0xffffffff;

  void draw(PGraphicsOpenGL r) {
    r.beginShape(QUADS);
    r.fill(fill);
    r.vertex(loc.x - halfScale.x, loc.y - halfScale.y);
    r.vertex(loc.x + halfScale.x, loc.y - halfScale.y);
    r.vertex(loc.x + halfScale.x, loc.y + halfScale.y);
    r.vertex(loc.x - halfScale.x, loc.y + halfScale.y);
    r.endShape(CLOSE);
  }
}
