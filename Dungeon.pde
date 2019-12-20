// Import Java collections libraries.
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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

  String toString() {
    return Long.toString(seed);
  }

  void draw(PGraphicsOpenGL r) {
    r.pushStyle();
    r.noStroke();

    // Draw extents of dungeon.
    r.rectMode(CORNERS);
    r.fill(0x7f373737);
    r.rect(min.x, min.y,
      max.x, max.y);

    // Run through values of map, draw room.
    for (Room room : rooms.values()) {
      room.drawRoom(r);
    }
    
    // Optimisation, draw only current room and direct neighbours
    //for (int i = -1; i <= 1; i++) {
    //   for (int j = -1; j <= 1; j++) {
    //     Room room = getRoom(new PVector(player.position.x + i * width, player.position.y + j * height));
    //     if (room != null) room.drawRoom(r);
    //   }
    //}
    
    r.popStyle();
  }

  NavigableMap < Coord, Room > generate(float w, float h) {

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
      
    checkpoint = Coord.add(checkpoint, new Coord(1, 0));
    rooms.put(checkpoint, new Room(map2));
    
    //Rooms for sacrifice 1
    r = new Room(map3);
    curr = Coord.add(checkpoint, new Coord(0, 1));
    rooms.put(curr, r);
    r.addMachine(new Machinery(new PVector(2000, 875)));
    r.addMachine(new Machinery(new PVector(1000, 1200)));
    r.addMachine(new Machinery(new PVector(2250, 1400)));
    
    //curr = Coord.add(curr, new Coord(0, 1));
    //rooms.put(curr, new Room(map4));
    
    r = new Room(map5);
    curr = Coord.add(curr, new Coord(1, 1));
    rooms.put(curr, new Room(map5));
    //r.addMachine(new Machinery(new PVector(2800, 1500)));
    //r.addMachine(new Machinery(new PVector(2700, 1000)));
    //r.addMachine(new Machinery(new PVector(3600, 2200)));
    //r.addMachine(new Machinery(new PVector(2250, 1200)));
    
    r = new Room(map6);
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, r);
    r.addMachine(new Machinery(new PVector(2900, 1400)));
    r.addMachine(new Machinery(new PVector(4400, 1400)));
    //r.addMachine(new Machinery(new PVector(2700, 1000)));
    
    //curr = Coord.add(curr, new Coord(1, 0));
    //rooms.put(curr, new Room(map0));
    
    //curr = Coord.add(curr, new Coord(0, 1));
    //rooms.put(curr, new Room(map0));
    
    //curr = Coord.add(curr, new Coord(1, 1));
    //rooms.put(curr, new Room(map7));
    
    
    // Rooms for sacrifice 2
    checkpoint = Coord.add(checkpoint, new Coord(1, 0));
    rooms.put(checkpoint, new Room(map7));
    
    curr = Coord.add(checkpoint, new Coord(1, 0));
    rooms.put(curr, new Room(map8));
    
    curr = Coord.add(curr, new Coord(1, 0));
    rooms.put(curr, new Room(map9));
    
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, new Room(map10));
    
    //curr = Coord.add(curr, new Coord(1, 0));
    //rooms.put(curr, new Room(map0));
    
    //Rooms for sacrifice 3
    curr = Coord.add(checkpoint, new Coord(0, -1));
    rooms.put(curr, new Room(map11));
    
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, new Room(map12));
    
    curr = Coord.add(curr, new Coord(0, -1));
    rooms.put(curr, new Room(map13));

    // Update min and max coordinates.
    minx = min(minx, curr.x);
    miny = min(miny, curr.y);
    maxx = max(maxx, curr.x);
    maxy = max(maxy, curr.y);
    //}

    // Set room dimensions. invDim will be used to find
    // appropriate room by coordinate given a vector.
    float halfw = w * 0.5; float halfh = h * 0.5;
    invDim.set(1.0 / w, 1.0 / h, 0.0);

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

      // Set fill color, location and half scale.
      room.fill = color(213, 213, 213);
    //  room.fill = lerpColor(
    //    0xffef3f3f, 0xff3f3fef,
    //    rng.nextFloat(), HSB);
      room.loc.set(coord.x * w, coord.y * h);
      room.halfScale.set(halfw, halfh);
    }
    
    start = getRoom(player.position);
    
    return rooms;
  }

  Room getRoom(PVector in) {    
    Coord coord = new Coord(
      round(in.x * invDim.x),
      round(in.y * invDim.y));
      
      return rooms.containsKey(coord) ? rooms.get(coord) : null;

    // To simplify the example, the dungeon
    // returns its starting room when the
    // coordinate supplied is not in the map.
    //return rooms.getOrDefault(coord, start);
  }
}
