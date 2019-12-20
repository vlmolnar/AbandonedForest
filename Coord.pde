// Key to retrieve room in dungeon
//  Rooms will be sorted first by their vertical position, then by their horizontal position
static class Coord implements Comparable < Coord > {
  final int x; final int y;

  Coord(int x, int y) {
    this.x = x; this.y = y;
  }

  boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    Coord other = (Coord)obj;
    if (x != other.x) { return false; }
    if (y != other.y) { return false; }
    return true;
  }

  int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  String toString() {
    return String.format("(%03d, %03d)", x, y);
  }

  // Mandated by comparable interface.
  int compareTo(Coord c) {
    return y > c.y ? 1 : y < c.y ? -1 :
      x > c.x ? 1 : x < c.x ? -1 : 0;
  }

   static Coord add(Coord a, Coord b) {
    return new Coord(a.x + b.x, a.y + b.y);
  }

}
