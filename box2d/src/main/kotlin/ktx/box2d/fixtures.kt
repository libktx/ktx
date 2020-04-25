package ktx.box2d

import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Box2D building DSL utility class. [FixtureDef] extension exposing new properties. Note that when using fixture
 * builders from [BodyDefinition], [FixtureDefinition.shape] field should not be modified - fixture's shape of the
 * chosen type will already be set in the [FixtureDefinition] instance.
 * @see BodyDefinition
 */
@Box2DDsl
class FixtureDefinition : FixtureDef() {
  /** Custom data object assigned to [Fixture.getUserData]. Allows to store additional data about the [Fixture] without
   * having to override the class. Defaults to null. */
  var userData: Any? = null

  /** Invoked after the [Fixture] is fully constructed.
   * @see onCreate */
  var creationCallback: ((Fixture) -> Unit)? = null

  /** If true, will dispose of [FixtureDef.shape] right after [Fixture] construction. */
  var disposeOfShape: Boolean = true

  /**
   * @param callback will be invoked after the [Fixture] defined by this object will be fully constructed.
   * @see creationCallback
   */
  fun onCreate(callback: (Fixture) -> Unit) {
    creationCallback = callback
  }
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

/**
 * Inlined utility extension method for setting up of [FixtureDef.filter]. Exposes [Filter] properties under `this`.
 * @param init inlined. Uses [FixtureDef.filter] as `this`.
 */
@OptIn(ExperimentalContracts::class)
inline fun FixtureDef.filter(init: (@Box2DDsl Filter).() -> Unit) {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  filter.init()
}
