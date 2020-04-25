package ktx.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** See [ShapeRenderer.translate]. Multiplies the current transformation matrix by a translation matrix.
 * @param transform supplies x, y and z parameters. */
fun ShapeRenderer.translate(transform: Vector3) {
  translate(transform.x, transform.y, transform.z)
}

/** See [ShapeRenderer.scale]. Multiplies the current transformation matrix by a scale matrix, with z not scaled.
 * @param scale supplies x and y scale. */
fun ShapeRenderer.scale(scale: Vector2) {
  scale(scale.x, scale.y, 1f)
}

/** See [ShapeRenderer.scale]. Multiplies the current transformation matrix by a scale matrix.
 * @param scale supplies x, y and z scale. */
fun ShapeRenderer.scale(scale: Vector3) {
  scale(scale.x, scale.y, scale.z)
}

/** See [ShapeRenderer.rotate]. Multiplies the current transformation matrix by a rotation matrix.
 * @param axis supplies x, y and z parameters.
 * @param degrees rotation angle in degrees. */
fun ShapeRenderer.rotate(axis: Vector3, degrees: Float) {
  rotate(axis.x, axis.y, axis.z, degrees)
}

/** See [ShapeRenderer.box].
 * @param position supplies x, y and z parameters.
 * @param width width of the rendered box.
 * @param height height of the rendered box.
 * @param depth depth of the rendered box. */
fun ShapeRenderer.box(position: Vector3, width: Float, height: Float, depth: Float) {
  box(position.x, position.y, position.z, width, height, depth)
}

/** See [ShapeRenderer.circle]. Estimates the number of segments needed for a smooth circle.
 * @param position supplies x and y parameters.
 * @param radius radius of the rendered circle. */
fun ShapeRenderer.circle(position: Vector2, radius: Float) {
  circle(position.x, position.y, radius)
}

/** See [ShapeRenderer.circle].
 * @param position supplies x and y parameters.
 * @param radius radius of the rendered circle.
 * @param segments amount of segments used to render the circle. */
fun ShapeRenderer.circle(position: Vector2, radius: Float, segments: Int) {
  circle(position.x, position.y, radius, segments)
}

/** See [ShapeRenderer.rect].
 * @param position supplies x and y parameters.
 * @param width width of the rendered rectangle.
 * @param height height of the rendered rectangle. */
fun ShapeRenderer.rect(position: Vector2, width: Float, height: Float) {
  rect(position.x, position.y, width, height)
}

/** See [ShapeRenderer.rect].
 * @param position supplies x and y parameters.
 * @param size supplies width and height parameters. */
fun ShapeRenderer.rect(position: Vector2, size: Vector2) {
  rect(position.x, position.y, size.x, size.y)
}

/** See [ShapeRenderer.rectLine].
 * @param positionA position of edge A.
 * @param positionB position of edge B.
 * @param colorA color of edge A.
 * @param colorB color of edge B*/
fun ShapeRenderer.rectLine(positionA: Vector2, positionB: Vector2, width: Float, colorA: Color, colorB: Color) {
  rectLine(positionA.x, positionA.y, positionB.x, positionB.y, width, colorA, colorB)
}

/** See [ShapeRenderer.cone]. Estimates the number of segments needed for a smooth cone.
 * @param position supplies x, y and z parameters.
 * @param radius radius of the rendered cone.
 * @param height height of the rendered cone. */
fun ShapeRenderer.cone(position: Vector3, radius: Float, height: Float) {
  cone(position.x, position.y, position.z, radius, height)
}

/** See [ShapeRenderer.cone].
 * @param position supplies x, y and z parameters.
 * @param radius radius of the rendered cone.
 * @param height height of the rendered cone.
 * @param segments amount of segments used to render the cone. */
fun ShapeRenderer.cone(position: Vector3, radius: Float, height: Float, segments: Int) {
  cone(position.x, position.y, position.z, radius, height, segments)
}

/** See [ShapeRenderer.arc]. Estimates the number of segments needed for a smooth arc.
 * @param position supplies x and y parameters.
 * @param radius radius of the rendered arc.
 * @param start start of the rendered arc.
 * @param degrees angle of the rendered arc in degrees. */
fun ShapeRenderer.arc(position: Vector2, radius: Float, start: Float, degrees: Float) {
  arc(position.x, position.y, radius, start, degrees)
}

/** See [ShapeRenderer.arc].
 * @param position supplies x and y parameters.
 * @param radius radius of the rendered arc.
 * @param start start of the rendered arc.
 * @param degrees angle of the rendered arc in degrees.
 * @param segments amount of segments used to render the arc. */
fun ShapeRenderer.arc(position: Vector2, radius: Float, start: Float, degrees: Float, segments: Int) {
  arc(position.x, position.y, radius, start, degrees, segments)
}

/** See [ShapeRenderer.ellipse]. Estimates the number of segments needed for a smooth ellipse.
 * @param position supplies x and y parameters.
 * @param width width of the rendered ellipse.
 * @param height height of the rendered ellipse.
 * @param degrees rotation of the rendered ellipse in degrees. */
fun ShapeRenderer.ellipse(position: Vector2, width: Float, height: Float, degrees: Float) {
  ellipse(position.x, position.y, width, height, degrees)
}

/** See [ShapeRenderer.ellipse]
 * @param position supplies x and y parameters.
 * @param width width of the rendered ellipse.
 * @param height height of the rendered ellipse.
 * @param degrees rotation of the rendered ellipse in degrees.
 * @param segments amount of segments used to render the arc. */
fun ShapeRenderer.ellipse(position: Vector2, width: Float, height: Float, degrees: Float, segments: Int) {
  ellipse(position.x, position.y, width, height, degrees, segments)
}

/** See [ShapeRenderer.triangle].
 * @param pointA position of point A.
 * @param pointB position of point B.
 * @param pointC position of point C. */
fun ShapeRenderer.triangle(pointA: Vector2, pointB: Vector2, pointC: Vector2) {
  triangle(pointA.x, pointA.y, pointB.x, pointB.y, pointC.x, pointC.y)
}

/** See [ShapeRenderer.triangle].
 * @param pointA position of point A.
 * @param pointB position of point B.
 * @param pointC position of point C.
 * @param colorA color of A corner.
 * @param colorB color of B corner.
 * @param colorC color of C corner. */
fun ShapeRenderer.triangle(pointA: Vector2, pointB: Vector2, pointC: Vector2,
                           colorA: Color, colorB: Color, colorC: Color) {
  triangle(pointA.x, pointA.y, pointB.x, pointB.y, pointC.x, pointC.y, colorA, colorB, colorC)
}

/**
 * Automatically calls [ShapeRenderer.begin] and [ShapeRenderer.end].
 * @param type specified shape type used to draw the shapes in the [action] block. Can be changed during the rendering
 * with [ShapeRenderer.set].
 * @param action inlined. Executed after [ShapeRenderer.begin] and before [ShapeRenderer.end].
 */
@OptIn(ExperimentalContracts::class)
inline fun <SR: ShapeRenderer> SR.use(type: ShapeType, action: (SR) -> Unit) {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  begin(type)
  action(this)
  end()
}
