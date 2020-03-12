static float lerpAngle(float a, float b, float step) {
  
  // Prefer shortest distance,
  float delta = b - a;
  if (delta == 0.0) {
  
    // Problem case: where angles are 180 degrees
    // apart and neither clockwise nor counter-clockwise
    // are shorter than the other.
    return lerpAngle(a + EPSILON, b, step);
  } else if (delta < -PI) {
    b += TWO_PI;
  } else if (delta > PI) {
    a += TWO_PI;
  }
  return (1.0 - step) * a + step * b;
}
