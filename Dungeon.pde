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
  final Room start = new Room(map1);
  final NavigableMap < Coord, Room > rooms = new TreeMap < Coord, Room > ();

  Dungeon(long seed, int count, float w, float h) {
    generate(seed, count, w, h);
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
    r.popStyle();
  }

  NavigableMap < Coord, Room > generate(long seed, int count,
    float w, float h) {

    // Create a default room located at (0, 0).
    // Reset rooms map and add origin.
    rooms.clear();
    Coord curr = new Coord(0, 0);
    Coord checkpoint = new Coord(0, 0);
    rooms.put(curr, start);

    // Set random number generator to seed.
    //this.seed = seed;
    //rng.setSeed(this.seed);
    //println("Dungeon seed:", this);

    
    // To store dungeon minimums and maximums.
    int minx = 0; int miny = 0;
    int maxx = 0; int maxy = 0;

/*

    // Start room has already been determined, so
    // loop for one less room than count.
    for (int i = 0; i < count - 1; ++i) {

      // Take a random walk. Prevent a room that has
      // already been added at a coordinate from being
      // added again to the map.
      while (rooms.containsKey(curr)) {

        // Get a random coordinate, either (1, 0), (0, 1),
        // (-1, 0) or (0, -1) away.
        curr = Coord.add(curr, Coord.random(rng));
      }
      */
      
      // Once a unique coordinate is set, add new room.
      checkpoint = Coord.add(checkpoint, new Coord(1, 0));
      rooms.put(checkpoint, new Room(map2));
      
      //Rooms for sacrifice 1
      curr = Coord.add(checkpoint, new Coord(0, 1));
      rooms.put(curr, new Room(map3));
      
      curr = Coord.add(curr, new Coord(0, 1));
      rooms.put(curr, new Room(map0));
      
      curr = Coord.add(curr, new Coord(1, 0));
      rooms.put(curr, new Room(map0));
      
      curr = Coord.add(curr, new Coord(0, -1));
      rooms.put(curr, new Room(map0));
      
      curr = Coord.add(curr, new Coord(1, 0));
      rooms.put(curr, new Room(map0));
      
      curr = Coord.add(curr, new Coord(0, 1));
      rooms.put(curr, new Room(map0));
      
      curr = Coord.add(curr, new Coord(1, 0));
      rooms.put(curr, new Room(map0));
      
      
      // Rooms for sacrifice 2
      checkpoint = Coord.add(checkpoint, new Coord(1, 0));
      rooms.put(checkpoint, new Room(map1));
      
      curr = Coord.add(checkpoint, new Coord(1, 0));
      rooms.put(curr, new Room(map1));
      
      curr = Coord.add(curr, new Coord(1, 0));
      rooms.put(curr, new Room(map1));
      
      curr = Coord.add(curr, new Coord(0, -1));
      rooms.put(curr, new Room(map1));
      
      curr = Coord.add(curr, new Coord(1, 0));
      rooms.put(curr, new Room(map1));
      
      //Rooms for sacrifice 3
      //curr = Coord.add(checkpoint1, new Coord(1, -1));
      //rooms.put(curr, new Room(map0));

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
      room.fill = lerpColor(
        0xffef3f3f, 0xff3f3fef,
        rng.nextFloat(), HSB);
      room.loc.set(coord.x * w, coord.y * h);
      room.halfScale.set(halfw, halfh);
    }
    return rooms;
    
    //// Create a default room located at (0, 0).
    //// Reset rooms map and add origin.
    //rooms.clear();
    //Coord curr = new Coord(0, 0);
    //rooms.put(curr, start);

    //// Set random number generator to seed.
    ////this.seed = seed;
    ////rng.setSeed(this.seed);
    ////println("Dungeon seed:", this);

    //// To store dungeon minimums and maximums.
    //int minx = 0; int miny = 0;
    //int maxx = 0; int maxy = 0;

    //// Start room has already been determined, so
    //// loop for one less room than count.
    //for (int i = 0; i < count - 1; ++i) {

    //  // Take a random walk. Prevent a room that has
    //  // already been added at a coordinate from being
    //  // added again to the map.
    //  while (rooms.containsKey(curr)) {

    //    // Get a random coordinate, either (1, 0), (0, 1),
    //    // (-1, 0) or (0, -1) away.
    //    curr = Coord.add(curr, Coord.random(rng));
    //  }

    //  // Once a unique coordinate is set, add new room.
    //  rooms.put(curr, new Room(map1));

    //  // Update min and max coordinates.
    //  minx = min(minx, curr.x);
    //  miny = min(miny, curr.y);
    //  maxx = max(maxx, curr.x);
    //  maxy = max(maxy, curr.y);
    //}

    //// Set room dimensions. invDim will be used to find
    //// appropriate room by coordinate given a vector.
    //float halfw = w * 0.5; float halfh = h * 0.5;
    //invDim.set(1.0 / w, 1.0 / h, 0.0);

    //// Find dungeon extents.
    //min.set(minx * w, miny * h);
    //min.sub(halfw, halfh);
    //max.set(maxx * w, maxy * h);
    //max.add(halfw, halfh);

    //// Each entry in a map is a key-value pair. To loop over
    //// all entries of a map, we acquire a set of entries.
    //Set < NavigableMap.Entry < Coord, Room > > entries = rooms.entrySet();
    //for (NavigableMap.Entry < Coord, Room > entry : entries) {

    //  // Get key and value from entry.
    //  Coord coord = entry.getKey();
    //  Room room = entry.getValue();

    //  // Set fill color, location and half scale.
    //  room.fill = lerpColor(
    //    0xffef3f3f, 0xff3f3fef,
    //    rng.nextFloat(), HSB);
    //  room.loc.set(coord.x * w, coord.y * h);
    //  room.halfScale.set(halfw, halfh);
    //}
    //return rooms;
  }

  Room getRoom(PVector in) {    
    Coord coord = new Coord(
      round(in.x * invDim.x),
      round(in.y * invDim.y));

    // To simplify the example, the dungeon
    // returns its starting room when the
    // coordinate supplied is not in the map.
    return rooms.getOrDefault(coord, start);
  }
}
