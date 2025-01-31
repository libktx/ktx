package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * [World] factory function.
 * @param gravity world's gravity applied to bodies on each step. Defaults to no gravity (0f, 0f).
 * @param allowSleep if true, inactive bodies will not be simulated. Improves performance. Defaults to true.
 * @return a new [World] instance with given parameters.
 */
fun createWorld(
  gravity: Vector2 = Vector2.Zero,
  allowSleep: Boolean = true,
) = World(gravity, allowSleep)

/**
 * Type-safe [Body] building DSL.
 * @param type [BodyType] of the constructed [Body]. Matches libGDX default of [BodyType.StaticBody].
 * @param init inlined. Invoked on a [BodyDefinition] instance, which provides access to [Body] properties, as well as
 *    fixture building DSL. Defaults to no-op.
 * @return a fully constructed [Body] instance with all defined fixtures.
 * @see BodyDefinition
 * @see FixtureDefinition
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun World.body(
  type: BodyType = BodyType.StaticBody,
  init: BodyDefinition.() -> Unit = {},
): Body {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val bodyDefinition = BodyDefinition()
  bodyDefinition.type = type
  bodyDefinition.init()
  return create(bodyDefinition)
}

/**
 * Handles additional building properties provided by [BodyDefinition] and [FixtureDefinition]. Prefer this method
 * over [World.createBody] when using [BodyDefinition] directly.
 * @param bodyDefinition stores [Body] properties and optional [Fixture] definitions.
 * @return a fully constructed [Body] instance with all defined fixtures.
 * @see BodyDefinition
 * @see FixtureDefinition
 * @see body
 */
fun World.create(bodyDefinition: BodyDefinition): Body {
  val body = createBody(bodyDefinition)
  body.userData = bodyDefinition.userData
  for (fixtureDefinition in bodyDefinition.fixtureDefinitions) {
    val fixture = body.createFixture(fixtureDefinition)
    fixture.userData = fixtureDefinition.userData
    fixtureDefinition.creationCallback?.let { it(fixture) }
    if (fixtureDefinition.disposeOfShape) {
      fixtureDefinition.shape.dispose()
    }
  }
  bodyDefinition.creationCallback?.let { it(body) }
  return body
}

/**
 * Roughly matches Earth gravity of 9.80665 m/s^2. Moves bodies down on the Y axis.
 *
 * Note that [Vector2] class is mutable, so this vector can be modified. Use this property in read-only mode.
 *
 * Usage example:
 * val world = createWorld(gravity = earthGravity)
 * @see createWorld
 */
val earthGravity = Vector2(0f, -9.8f)

/**
 * Callback lambda for ray-casts.
 *
 * This lambda is called for each fixture the ray-cast hits.
 *
 * There is no guarantee on the order of the callback is called, e.g. the first call to the lambda
 * is not necessarily the nearest to the start point of the ray.
 *
 * The lambda accepts these parameters:
 * - [Fixture], the fixture hit by the ray.
 * - [Vector2], the point of initial intersection.
 * - [Vector2], the normal vector at the point of intersection.
 * - [Float], the fraction of the distance from `start` to `end` that the intersection point is at.
 *
 * The lambda returns the new length of the ray as a fraction of the distance between the start and
 * end or the ray. Common values are:
 * - `-1f`, ignore this fixture and continue.
 * - `0f`, terminate the ray cast.
 * - A fraction, clip the length of the ray to this point.
 * - `1f`, don't clip the ray and continue.
 *
 * Can be used in place of [com.badlogic.gdx.physics.box2d.RayCastCallback] via Kotlin SAM conversion.
 *
 * @see RayCast
 * @see rayCast
 */
typealias KtxRayCastCallback = (
  fixture: Fixture,
  point: Vector2,
  normal: Vector2,
  fraction: Float,
) -> Float

/**
 * Stores constants that can be returned by [KtxRayCastCallback] to control its behavior.
 * @see rayCast
 */
object RayCast {
  /**
   * Indicates to ignore the hit fixture and continue.
   * @see KtxRayCastCallback
   */
  const val IGNORE = -1f

  /**
   * Indicates to terminate the ray cast.
   * @see KtxRayCastCallback
   */
  const val TERMINATE = 0f

  /**
   * Indicates to not clip the ray and continue.
   * @see KtxRayCastCallback
   */
  const val CONTINUE = 1f
}

/**
 * Ray-cast the world for all fixtures in the path of the ray.
 *
 * The ray-cast ignores shapes that contain the starting point.
 *
 * @param start the ray starting point.
 * @param end the ray ending point.
 * @param callback a user implemented callback called on every fixture hit.
 * @see RayCast
 */
fun World.rayCast(
  start: Vector2,
  end: Vector2,
  callback: KtxRayCastCallback,
) {
  rayCast(callback, start, end)
}

/**
 * Ray-cast the world for all fixtures in the path of the ray.
 *
 * The ray-cast ignores shapes that contain the starting point.
 *
 * @param startX the ray starting point X.
 * @param startY the ray starting point Y.
 * @param endX the ray ending point X.
 * @param endY the ray ending point Y.
 * @param callback a user implemented callback called on every fixture hit.
 * @see RayCast
 */
fun World.rayCast(
  startX: Float,
  startY: Float,
  endX: Float,
  endY: Float,
  callback: KtxRayCastCallback,
) {
  rayCast(callback, startX, startY, endX, endY)
}

/**
 * Query the world for all fixtures that potentially overlap the provided AABB (Axis-Aligned Bounding Box).
 *
 * @param lowerX the x coordinate of the lower left corner
 * @param lowerY the y coordinate of the lower left corner
 * @param upperX the x coordinate of the upper right corner
 * @param upperY the y coordinate of the upper right corner
 * @param callback a user implemented callback that is called for every fixture overlapping the AABB.
 * @see Query
 */
fun World.query(
  lowerX: Float,
  lowerY: Float,
  upperX: Float,
  upperY: Float,
  callback: KtxQueryCallback,
) {
  QueryAABB(callback, lowerX, lowerY, upperX, upperY)
}

/**
 * Stores constants that can be returned by [KtxQueryCallback] to control its behavior.
 * @see query
 */
object Query {
  /**
   * Stop querying the world.
   * @see KtxQueryCallback
   */
  const val STOP = false

  /**
   * Continue querying for the next match.
   * @see KtxQueryCallback
   */
  const val CONTINUE = true
}

/**
 * Callback lambda for querying with an AABB.
 *
 * This lambda is called for each fixture the AABB overlaps.
 *
 * There is no guarantee on the order of the callback is called.
 *
 * The lambda returns whether to terminate the query.
 *
 * Can be used in place of [com.badlogic.gdx.physics.box2d.QueryCallback] via Kotlin SAM conversion.
 *
 * @see Query
 * @see query
 */
typealias KtxQueryCallback = (fixture: Fixture) -> Boolean
