static class DungeonCam extends Camera2D {
  float minZoom = 0.125;
  float maxZoom = 3.0;
  float defZoom = 1.0;
  float zoomIncr = 0.01;
  float zoom = defZoom;

  DungeonCam(PGraphics2D renderer) {
    super(renderer);
  }

  DungeonCam lookAt(Dungeon dungeon, PVector loc, boolean[] pressed, int zoomIn, int zoomOut) {
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
    lookAt(room.loc.x, room.loc.y, 0.0, zoom, zoom);
    //Camera2D lookAt(float tx, float ty, float rot, float zoomW, float zoomH)
    
    
    //// Snap to target immediately.
    ////loc.set(tx, ty, 0.0);
    ////scl.set(zoomW, zoomH, 0.0);
    ////this.rot = rot;

    //// Ease toward target.
    //loc.lerp(room.loc.x, room.loc.y, 0.0, smoothing);
    //scl.lerp(zoom, zoom, 0.0, smoothing);
    //this.rot = lerpAngle(this.rot, rot, smoothing);
    //PVector.fromAngle(this.rot, i);
    //j.set(-i.y, i.x);

    //c.set(
    //  1.0, 0.0, 0.0, renderer.width * 0.5, 
    //  0.0, 1.0, 0.0, renderer.height * 0.5, 
    //  0.0, 0.0, 1.0, 0.0, 
    //  0.0, 0.0, 0.0, 1.0);
    //c.scale(scl.x, -scl.y); /* Flip y axis. */
    //c.rotate(-this.rot);  
    //c.translate(-loc.x, -loc.y);

    //// Instead of calling setMatrix, which resets and then applies
    //// the target matrices, set them directly.
    //cInv.set(c);
    //cInv.invert();
    //pmv.set(c);
    
    return this;
  }
}
