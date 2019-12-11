static class Camera2D {
  final PGraphics2D renderer;
  final PVector loc = new PVector(0.0, 0.0);
  final PVector i = new PVector(1.0, 0.0); /* Right Axis */
  final PVector j = new PVector(0.0, 1.0); /* Up Axis */
  
  float rot = 0.0;
  float smoothing = 0.04;
  final PVector scl = new PVector(1.0, 1.0, 1.0);

  // setMatrix feeds into PGraphicsOpenGL's applyMatrixImpl,
  // which uses 3D matrices. These variables are shorthand
  // to the memory addresses of the needed renderer matrices.
  final PMatrix3D pmv; /* Project-Model-View. */
  final PMatrix3D c; /* Camera. */
  final PMatrix3D cInv; /* Camera inverse. */

  Camera2D(PGraphics2D renderer) {
    this.renderer = renderer;
    c = renderer.modelview;
    cInv = renderer.modelviewInv;
    pmv = renderer.projmodelview;
  }

  Camera2D lookAt(float tx, float ty, float rot, float zoomW, float zoomH) {
      
    // Snap to target immediately.
    //loc.set(tx, ty, 0.0);
    //scl.set(zoomW, zoomH, 0.0);
    //this.rot = rot;

    // Ease toward target.
    loc.lerp(tx, ty, 0.0, smoothing);
    scl.lerp(zoomW, zoomH, 0.0, smoothing);
    this.rot = lerpAngle(this.rot, rot, smoothing);
    PVector.fromAngle(this.rot, i);
    j.set(-i.y, i.x);

    c.set(
      1.0, 0.0, 0.0, renderer.width * 0.5, 
      0.0, 1.0, 0.0, renderer.height * 0.5, 
      0.0, 0.0, 1.0, 0.0, 
      0.0, 0.0, 0.0, 1.0);
    c.scale(scl.x, -scl.y); /* Flip y axis. */
    c.rotate(-this.rot);  
    c.translate(-loc.x, -loc.y);

    // Instead of calling setMatrix, which resets and then applies
    // the target matrices, set them directly.
    cInv.set(c);
    cInv.invert();
    pmv.set(c);
    return this;
  }
}
