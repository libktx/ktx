package ktx.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Box2D building DSL utility class. [BodyDef] extension storing [FixtureDef] instances in [fixtureDefinitions]
 * collection. Provides inlined building methods that construct fixture definitions.
 * @see body
 * @see FixtureDefinition
 */
@Box2DDsl
class BodyDefinition : BodyDef() {
  /** Custom data object assigned to [Body.getUserData]. Allows to store additional data about the [Body] without having
   * to override the class. Defaults to null. */
  var userData: Any? = null

  /** Invoked after the [Body] is fully constructed.
   * @see onCreate */
  var creationCallback: ((Body) -> Unit)? = null

  /** Stores [FixtureDefinition] instances of all currently defined fixtures of this body. Should not be modified
   * manually - instead, use [fixture] or one of building methods for fixtures of a specific shape. */
  val fixtureDefinitions = GdxArray<FixtureDefinition>(4)

  /**
   * @param callback will be invoked after the [Body] defined by this object will be fully constructed.
   * @see creationCallback
   */
  fun onCreate(callback: (Body) -> Unit) {
    creationCallback = callback
  }
}

/**
 * Utility builder method for constructing fixtures of custom shape type.
 * @param shape will be set as [FixtureDef] shape type.
 * @param disposeOfShape whether to call [Shape.dispose] immediately after fixture creation. `false` by default.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [shape] as first (`it`) argument.
 * @see circle
 * @see box
 * @see chain
 * @see loop
 * @see edge
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun <ShapeType : Shape> BodyDefinition.fixture(
  shape: ShapeType,
  disposeOfShape: Boolean = false,
  init: FixtureDefinition.(ShapeType) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val fixtureDefinition = FixtureDefinition()
  fixtureDefinition.disposeOfShape = disposeOfShape
  fixtureDefinition.shape = shape
  fixtureDefinition.init(shape)
  fixtureDefinitions.add(fixtureDefinition)
  return fixtureDefinition
}

/**
 * Utility builder method for constructing fixtures with [CircleShape].
 * @param radius radius of the [CircleShape]. Defaults to 1f.
 * @param position offset of the circle position in relation to the body center. Defaults to (0f, 0f).
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [CircleShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.circle(
  radius: Float = 1f,
  position: Vector2 = Vector2.Zero,
  init: FixtureDefinition.(CircleShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = CircleShape()
  shape.radius = radius
  shape.position = position
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [PolygonShape] set as box. Note that - contrary to
 * [PolygonShape.setAsBox] methods - this method consumes actual _not halved_ box width and height sizes.
 * @param width width of the box shape. Defaults to 1f.
 * @param height height of the box shape. Defaults to 1f
 * @param position offset of the box position in relation to body center. Defaults to (0f, 0f).
 * @param angle angle of the box in radians. Defaults to 0f.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see PolygonShape.setAsBox
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.box(
  width: Float = 1f,
  height: Float = 1f,
  position: Vector2 = Vector2.Zero,
  angle: Float = 0f,
  init: FixtureDefinition.(PolygonShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = PolygonShape()
  shape.setAsBox(width / 2f, height / 2f, position, angle)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [PolygonShape]. Note that this method consumes a [FloatArray]
 * instead of array of [Vector2] instances, which might be less readable, but creates slightly less garbage. This
 * method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param vertices optional. If given, will be converted to [PolygonShape] points.  Each two adjacent numbers
 *    represent a point's X and Y.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see PolygonShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.polygon(
  vertices: FloatArray? = null,
  init: FixtureDefinition.(PolygonShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = PolygonShape()
  if (vertices != null) shape.set(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [PolygonShape].
 * @param vertices will be converted to [PolygonShape] points.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see PolygonShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.polygon(
  vararg vertices: Vector2,
  init: FixtureDefinition.(PolygonShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = PolygonShape()
  shape.set(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [ChainShape].
 * @param vertices will be converted to [ChainShape] points.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see ChainShape.createChain
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.chain(
  vararg vertices: Vector2,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createChain(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [ChainShape]. Note that this method consumes a [FloatArray]
 * instead of array of [Vector2] instances, which might be less readable, but creates slightly less garbage. This
 * method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param vertices will be converted to [ChainShape] points. Each two adjacent numbers represent a point's X and Y.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see ChainShape.createChain
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.chain(
  vertices: FloatArray,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createChain(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with looped [ChainShape].
 * @param vertices will be converted to [ChainShape] points. Will be looped.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see ChainShape.createLoop
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.loop(
  vararg vertices: Vector2,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createLoop(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with looped [ChainShape]. Note that this method consumes
 * a [FloatArray] instead of array of [Vector2] instances, which might be less readable, but creates slightly less
 * garbage. This method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param vertices will be converted to [ChainShape] points. Will be looped. Each two adjacent numbers represent
 *    a point's X and Y.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see ChainShape.createLoop
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.loop(
  vertices: FloatArray,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createLoop(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [EdgeShape].
 * @param from first point of the [EdgeShape]. See [EdgeShape.getVertex1].
 * @param to second point of the [EdgeShape]. See [EdgeShape.getVertex2].
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [EdgeShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see EdgeShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.edge(
  from: Vector2,
  to: Vector2,
  init: FixtureDefinition.(EdgeShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = EdgeShape()
  shape.set(from, to)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [EdgeShape]. Note that this method consumes floats instead of
 * [Vector2] instances, which might be less concise, but also creates slightly less garbage. This method is advised to
 * be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param fromX position of first point of the [EdgeShape] on X axis. See [EdgeShape.getVertex1].
 * @param fromY position of first point of the [EdgeShape] on Y axis. See [EdgeShape.getVertex1].
 * @param toX position of second point of the [EdgeShape] on X axis. See [EdgeShape.getVertex2].
 * @param toY position of second point of the [EdgeShape] on Y axis. See [EdgeShape.getVertex2].
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [EdgeShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @see EdgeShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun BodyDefinition.edge(
  fromX: Float,
  fromY: Float,
  toX: Float,
  toY: Float,
  init: FixtureDefinition.(EdgeShape) -> Unit = {},
): FixtureDefinition {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = EdgeShape()
  shape.set(fromX, fromY, toX, toY)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures of custom shape type.
 * @param shape will be set as [FixtureDef] shape type.
 * @param disposeOfShape whether to call [Shape.dispose] immediately after fixture creation. `false` by default.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [shape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with properties from the defined [FixtureDef].
 * @see circle
 * @see box
 * @see chain
 * @see loop
 * @see edge
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun <S : Shape> Body.fixture(
  shape: S,
  disposeOfShape: Boolean = false,
  init: FixtureDefinition.(S) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val fixtureDefinition = FixtureDefinition()
  fixtureDefinition.disposeOfShape = disposeOfShape
  fixtureDefinition.shape = shape
  fixtureDefinition.init(shape)
  val fixture = createFixture(fixtureDefinition)
  fixture.userData = fixtureDefinition.userData
  fixtureDefinition.creationCallback?.let { it(fixture) }
  if (disposeOfShape) shape.dispose()
  return fixture
}

/**
 * Utility builder method for constructing fixtures with [CircleShape].
 * @param radius radius of the [CircleShape]. Defaults to 1f.
 * @param position offset of the circle position in relation to the body center. Defaults to (0f, 0f).
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [CircleShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with [CircleShape].
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.circle(
  radius: Float = 1f,
  position: Vector2 = Vector2.Zero,
  init: FixtureDefinition.(CircleShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = CircleShape()
  shape.radius = radius
  shape.position = position
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [PolygonShape] set as box. Note that - contrary to
 * [PolygonShape.setAsBox] methods - this method consumes actual _not halved_ box width and height sizes.
 * @param width width of the box shape. Defaults to 1f.
 * @param height height of the box shape. Defaults to 1f
 * @param position offset of the box position in relation to body center. Defaults to (0f, 0f).
 * @param angle angle of the box in radians. Defaults to 0f.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with box [PolygonShape].
 * @see PolygonShape.setAsBox
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.box(
  width: Float = 1f,
  height: Float = 1f,
  position: Vector2 = Vector2.Zero,
  angle: Float = 0f,
  init: FixtureDefinition.(PolygonShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = PolygonShape()
  shape.setAsBox(width / 2f, height / 2f, position, angle)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [PolygonShape]. Note that this method consumes a [FloatArray]
 * instead of array of [Vector2] instances, which might be less readable, but creates slightly less garbage. This
 * method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param vertices optional. If given, will be converted to [PolygonShape] points.  Each two adjacent numbers
 *    represent a point's X and Y.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with [PolygonShape].
 * @see PolygonShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.polygon(
  vertices: FloatArray? = null,
  init: FixtureDefinition.(PolygonShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = PolygonShape()
  if (vertices != null) shape.set(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [PolygonShape].
 * @param vertices will be converted to [PolygonShape] points.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [PolygonShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with [PolygonShape].
 * @see PolygonShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.polygon(
  vararg vertices: Vector2,
  init: FixtureDefinition.(PolygonShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = PolygonShape()
  shape.set(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [ChainShape].
 * @param vertices will be converted to [ChainShape] points.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return [FixtureDefinition] with [Fixture] properties.
 * @return a fully constructed [Fixture] with [ChainShape].
 * @see ChainShape.createChain
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.chain(
  vararg vertices: Vector2,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createChain(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [ChainShape]. Note that this method consumes a [FloatArray]
 * instead of array of [Vector2] instances, which might be less readable, but creates slightly less garbage. This
 * method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param vertices will be converted to [ChainShape] points. Each two adjacent numbers represent a point's X and Y.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with [ChainShape].
 * @see ChainShape.createChain
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.chain(
  vertices: FloatArray,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createChain(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with looped [ChainShape].
 * @param vertices will be converted to [ChainShape] points. Will be looped.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with looped [ChainShape].
 * @see ChainShape.createLoop
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.loop(
  vararg vertices: Vector2,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createLoop(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with looped [ChainShape]. Note that this method consumes
 * a [FloatArray] instead of array of [Vector2] instances, which might be less readable, but creates slightly less
 * garbage. This method is advised to be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param vertices will be converted to [ChainShape] points. Will be looped. Each two adjacent numbers represent
 *    a point's X and Y.
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [ChainShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with looped [ChainShape].
 * @see ChainShape.createLoop
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.loop(
  vertices: FloatArray,
  init: FixtureDefinition.(ChainShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = ChainShape()
  shape.createLoop(vertices)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [EdgeShape].
 * @param from first point of the [EdgeShape]. See [EdgeShape.getVertex1].
 * @param to second point of the [EdgeShape]. See [EdgeShape.getVertex2].
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [EdgeShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with [EdgeShape].
 * @see EdgeShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.edge(
  from: Vector2,
  to: Vector2,
  init: FixtureDefinition.(EdgeShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = EdgeShape()
  shape.set(from, to)
  return fixture(shape, disposeOfShape = true, init = init)
}

/**
 * Utility builder method for constructing fixtures with [EdgeShape]. Note that this method consumes floats instead of
 * [Vector2] instances, which might be less concise, but also creates slightly less garbage. This method is advised to
 * be used instead of the [Vector2]-consuming variant on mobile devices.
 * @param fromX position of first point of the [EdgeShape] on X axis. See [EdgeShape.getVertex1].
 * @param fromY position of first point of the [EdgeShape] on Y axis. See [EdgeShape.getVertex1].
 * @param toX position of second point of the [EdgeShape] on X axis. See [EdgeShape.getVertex2].
 * @param toY position of second point of the [EdgeShape] on Y axis. See [EdgeShape.getVertex2].
 * @param init inlined. Allows to modify [FixtureDef] properties. Receives [EdgeShape] as first (`it`) argument.
 * @return a fully constructed [Fixture] with [EdgeShape].
 * @see EdgeShape.set
 */
@Box2DDsl
@OptIn(ExperimentalContracts::class)
inline fun Body.edge(
  fromX: Float,
  fromY: Float,
  toX: Float,
  toY: Float,
  init: FixtureDefinition.(EdgeShape) -> Unit = {},
): Fixture {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val shape = EdgeShape()
  shape.set(fromX, fromY, toX, toY)
  return fixture(shape, disposeOfShape = true, init = init)
}
