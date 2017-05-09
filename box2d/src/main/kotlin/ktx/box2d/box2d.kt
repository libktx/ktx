package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import kotlin.annotation.AnnotationTarget.*
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Marks KTX Box2D type-safe builders.
 */
@DslMarker
@Target(CLASS, TYPE_PARAMETER, FUNCTION, TYPE, TYPEALIAS)
annotation class Box2DDsl

/**
 * Box2D building DSL utility class. [BodyDef] extension storing [FixtureDef] instances in [fixtureDefinitions]
 * collection. Provides inlined building methods that construct fixture definitions.
 * @see body
 */
@Box2DDsl
class BodyDefinition : BodyDef() {
  /** If true, all fixture shapes will be disposed of after constructing this body. Defaults to true. If false, shapes
   * will not be disposed after creation of the fixtures, allowing for [Shape] instances reuse among multiple bodies. */
  var disposeOfShapes = true
  /** Stores [FixtureDef] instances of all currently defined fixtures of this body.*/
  val fixtureDefinitions = GdxArray<FixtureDef>(4)

  /**
   * Utility builder method for constructing fixtures of custom shape type.
   * @param shape will be set as [FixtureDef] type.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [shape] as first (`it`) argument.
   */
  inline fun <ShapeType : Shape> fixture(
      shape: ShapeType,
      init: (@Box2DDsl FixtureDef).(ShapeType) -> Unit): FixtureDef {
    val fixture = FixtureDef()
    fixture.shape = shape
    fixture.init(shape)
    fixtureDefinitions.add(fixture)
    return fixture
  }

  /**
   * Utility builder method for constructing fixtures with [CircleShape].
   * @param radius radius of the [CircleShape]. Defaults to 1f.
   * @param x X offset of the fixture. Defaults to 0f.
   * @param y Y offset of the fixture. Defaults to 0f.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [CircleShape] as first (`it`) argument.
   */
  inline fun circle(
      radius: Float = 1f,
      x: Float = 0f,
      y: Float = 0f,
      init: (@Box2DDsl FixtureDef).(CircleShape) -> Unit): FixtureDef {
    val shape = CircleShape()
    shape.radius = radius
    shape.position = Vector2(x, y)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with [PolygonShape] set as box. Note that - contrary to
   * [PolygonShape.setAsBox] methods - this method consumes actual _not halved_ box width and height sizes.
   * @param width width of the box shape. Defaults to 1f.
   * @param height height of the box shape. Defaults to 1f
   * @param center offset of the fixture. Defaults to (0f, 0f).
   * @param angle angle of the box in radians. Defaults to 0f.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
   * @see PolygonShape.setAsBox
   */
  inline fun box(
      width: Float = 1f,
      height: Float = 1f,
      center: Vector2 = Vector2.Zero,
      angle: Float = 0f,
      init: (@Box2DDsl FixtureDef).(PolygonShape) -> Unit): FixtureDef {
    val shape = PolygonShape()
    shape.setAsBox(width / 2f, height / 2f, center, angle)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with [PolygonShape]. Note that this method consumes a [FloatArray]
   * instead of array of [Vector2] instances, which might be less readable, but creates slightly less garbage. This
   * method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
   * @param vertices optional. If given, will be converted to [PolygonShape] points.  Each two adjacent numbers
   *    represent a point's X and Y.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
   * @see PolygonShape.set
   */
  inline fun polygon(
      vertices: FloatArray? = null,
      init: (@Box2DDsl FixtureDef).(PolygonShape) -> Unit): FixtureDef {
    val shape = PolygonShape()
    if (vertices != null) shape.set(vertices)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with [PolygonShape].
   * @param vertices will be converted to [PolygonShape] points.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
   * @see PolygonShape.set
   */
  inline fun polygon(
      vararg vertices: Vector2,
      init: (@Box2DDsl FixtureDef).(PolygonShape) -> Unit): FixtureDef {
    val shape = PolygonShape()
    shape.set(vertices)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with [ChainShape].
   * @param vertices will be converted to [ChainShape] points.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
   * @see ChainShape.createChain
   */
  inline fun chain(
      vararg vertices: Vector2,
      init: (@Box2DDsl FixtureDef).(ChainShape) -> Unit): FixtureDef {
    val shape = ChainShape()
    shape.createChain(vertices)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with [ChainShape]. Note that this method consumes a [FloatArray]
   * instead of array of [Vector2] instances, which might be less readable, but creates slightly less garbage. This
   * method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
   * @param vertices will be converted to [ChainShape] points. Each two adjacent numbers represent a point's X and Y.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
   * @see ChainShape.createChain
   */
  inline fun chain(
      vertices: FloatArray,
      init: (@Box2DDsl FixtureDef).(ChainShape) -> Unit): FixtureDef {
    val shape = ChainShape()
    shape.createChain(vertices)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with looped [ChainShape].
   * @param vertices will be converted to [ChainShape] points. Will be looped.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
   * @see ChainShape.createLoop
   */
  inline fun loop(
      vararg vertices: Vector2,
      init: (@Box2DDsl FixtureDef).(ChainShape) -> Unit): FixtureDef {
    val shape = ChainShape()
    shape.createLoop(vertices)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with looped [ChainShape]. Note that this method consumes
   * a [FloatArray] instead of array of [Vector2] instances, which might be less readable, but creates slightly less
   * garbage. This method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
   * @param vertices will be converted to [ChainShape] points. Will be looped. Each two adjacent numbers represent
   *    a point's X and Y.
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
   * @see ChainShape.createLoop
   */
  inline fun loop(
      vertices: FloatArray,
      init: (@Box2DDsl FixtureDef).(ChainShape) -> Unit): FixtureDef {
    val shape = ChainShape()
    shape.createLoop(vertices)
    return fixture(shape, init)
  }

  /**
   * Utility builder method for constructing fixtures with [EdgeShape].
   * @param from first point of the [EdgeShape]. See [EdgeShape.getVertex1].
   * @param to second point of the [EdgeShape]. See [EdgeShape.getVertex2].
   * @param init inlined. Allows to modify [FixtureDef] properties. Receives [EdgeShape] as first (`it`) argument.
   * @see EdgeShape.set
   */
  inline fun edge(
      from: Vector2,
      to: Vector2,
      init: (@Box2DDsl FixtureDef).(EdgeShape) -> Unit): FixtureDef {
    val shape = EdgeShape()
    shape.set(from, to)
    return fixture(shape, init)
  }
}

/**
 * [World] factory function.
 * @param gravity world's gravity applied to bodies on each step. Defaults to no gravity (0f, 0f).
 * @param allowSleep if true, inactive bodies will not be simulated. Improves performance. Defaults to true.
 * @return a new [World] instance with given parameters.
 */
fun createWorld(gravity: Vector2 = Vector2.Zero, allowSleep: Boolean = true) = World(gravity, allowSleep)

/**
 * Roughly matches Earth gravity of 9.80665 m/s^2. Moves bodies down on the Y axis.
 *
 * Note that [Vector2] class is mutable, so this vector can be modified. Use this property as read-only.
 *
 * Usage example:
 * val world = createWorld(gravity = earthGravity)
 * @see createWorld
 */
val earthGravity = Vector2(0f, -9.8f)

/**
 * Type-safe [Body] building DSl.
 * @param init inlined. Invoked on a [BodyDefinition] instance, which provides access to [Body] properties, as well as
 *    fixture building DSL.
 * @return a fully constructed [Body] instance with all defined fixtures.
 * @see BodyDefinition
 */
inline fun World.body(init: BodyDefinition.() -> Unit): Body {
  val bodyDefinition = BodyDefinition()
  bodyDefinition.init()
  val body = createBody(bodyDefinition)
  val dispose = bodyDefinition.disposeOfShapes
  for (fixture in bodyDefinition.fixtureDefinitions) {
    body.createFixture(fixture)
    if (dispose) fixture.shape.dispose()
  }
  return body
}

/**
 * Utility extension method for setting up of [FixtureDef.filter].
 * @param category filter category bits. See [Filter.categoryBits].
 * @param mask filter mask bits. See [Filter.maskBits].
 * @param group filter group index. See [Filter.groupIndex].
 * @return [FixtureDef.filter] of this [FixtureDef] instance.
 * @see Filter
 */
fun FixtureDef.filter(
    category: Short = filter.categoryBits,
    mask: Short = filter.maskBits,
    group: Short = filter.groupIndex): Filter {
  val filter: Filter = filter
  filter.categoryBits = category
  filter.maskBits = mask
  filter.groupIndex = group
  return filter
}

/**
 * Utility extension method for setting up of [FixtureDef.filter]. Allows to copy an existing [Filter] instance to avoid
 * copying each property manually.
 * @param filter its properties will be copied.
 * @return [FixtureDef.filter] of this [FixtureDef] instance.
 * @see Filter
 */
fun FixtureDef.filter(filter: Filter): Filter {
  val fixtureFilter = this.filter
  fixtureFilter.categoryBits = filter.categoryBits
  fixtureFilter.maskBits = filter.maskBits
  fixtureFilter.groupIndex = filter.groupIndex
  return fixtureFilter
}
