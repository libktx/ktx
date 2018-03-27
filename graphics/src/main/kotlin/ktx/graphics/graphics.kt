package ktx.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/** Draws a cube using {@link ShapeType#Line} or {@link ShapeType#Filled}. The pos specify the bottom, left, front corner
 * of the rectangle. */
fun ShapeRenderer.box(pos: Vector3, width: Float, height: Float, depth: Float) {
  this.box(pos.x, pos.y, pos.z, width, height, depth)
}

/** Calls {@link #circle(float, float, int)} by estimating the number of segments needed for a smooth circle. */
fun ShapeRenderer.circle(pos: Vector2, radius: Float) {
  this.circle(pos.x, pos.y, radius)
}

/** Draws a circle using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.circle(pos: Vector2, radius: Float, segment: Int) {
  this.circle(pos.x, pos.y, radius, segment)
}

/** Draws a rectangle at the pos plane using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.rect(pos: Vector2, width: Float, height: Float) {
  this.rect(pos.x, pos.y, width, height)
}

/** Draws a rectangle at the pos plane using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.rect(pos: Vector2, size: Vector2) {
  this.rect(pos.x, pos.y, size.x, size.y)
}

/** Draws a line using a rotated rectangle, where with one edge is centered at a and the opposite edge centered at b. */
fun ShapeRenderer.rectLine(a: Vector2, b: Vector2, width: Float, colorA: Color, colorB: Color) {
  this.rectLine(a.x, a.y, b.x, b.y, width, colorA, colorB)
}

/** Draws a cone using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.cone(pos: Vector3, radius: Float, height: Float, segment: Int) {
  this.cone(pos.x, pos.y, pos.z, radius, height, segment)
}

/** Calls {@link #cone(float, float, float, float, float, int)} by estimating the number of segments needed for a smooth
 * circular base. */
fun ShapeRenderer.cone(pos: Vector3, radius: Float, height: Float) {
  this.cone(pos.x, pos.y, pos.z, radius, height)
}

/** Calls {@link #arc(float, float, float, float, float, int)} by estimating the number of segments needed for a smooth arc. */
fun ShapeRenderer.arc(pos: Vector2, radius: Float, start: Float, degrees: Float) {
  this.arc(pos.x, pos.y, radius, start, degrees)
}

/** Draws an arc using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.arc(pos: Vector2, radius: Float, start: Float, degrees: Float, segments: Int) {
  this.arc(pos.x, pos.y, radius, start, degrees, segments)
}

/** Draws an ellipse using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.ellipse(pos: Vector2, width: Float, height: Float, rotation: Float, segments: Int) {
  this.ellipse(pos.x, pos.y, width, height, rotation, segments)
}

/** Calls {@link #ellipse(float, float, float, float, float, int)} by estimating the number of segments needed for a smooth ellipse. */
fun ShapeRenderer.ellipse(pos: Vector2, width: Float, height: Float, rotation: Float) {
  this.ellipse(pos.x, pos.y, width, height, rotation)
}

/** Draws a triangle at 'a' plane using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.triange(a: Vector2, b: Vector2, c: Vector2) {
  this.triangle(a.x, a.y, b.x, b.y, c.x, c.y)
}

/** Draws a triangle in 'a' plane with colored corners using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
fun ShapeRenderer.triange(a: Vector2, b: Vector2, c: Vector2, colorA: Color, colorB: Color, colorC: Color) {
  this.triangle(a.x, a.y, b.x, b.y, c.x, c.y, colorA, colorB, colorC)
}

/** Multiplies the current transformation matrix by a translation matrix. */
fun ShapeRenderer.translate(mul: Vector3) {
  this.translate(mul.x, mul.y, mul.z)
}

/** Multiplies the current transformation matrix by a scale matrix, with z not scaled. */
fun ShapeRenderer.scale(scale: Vector2) {
  this.scale(scale.x, scale.y, 1f)
}

/** Multiplies the current transformation matrix by a scale matrix. */
fun ShapeRenderer.scale(scale: Vector3) {
  this.scale(scale.x, scale.y, scale.z)
}
