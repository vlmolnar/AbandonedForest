static class DungeonCam extends Camera2D {
  float minZoom = 0.125;
  float maxZoom = 5.0;
  float defZoom = 1.0;
  float zoomIncr = 0.01;
  float zoom = defZoom;

  DungeonCam(PGraphics2D renderer) {
    super(renderer);
  }

  DungeonCam lookAt(Dungeon dungeon, PVector loc, boolean[] pressed, int zoomIn, int zoomOut, boolean snap) {
    boolean inPress = pressed[zoomIn];
    boolean outPress = pressed[zoomOut];
    if (inPress || outPress) {
      if (inPress) {
        zoom = constrain(zoom + zoomIncr,
          minZoom, maxZoom);
      }
      if (outPress) {
        zoom = constrain(zoom - zoomIncr,
          minZoom, maxZoom);
      }
    } else {
      zoom = defZoom;
    }
    Room room = dungeon.getRoom(loc);
    lookAt(room.loc.x, room.loc.y, 0.0, zoom, zoom, snap);
    
    return this;
  }
}
