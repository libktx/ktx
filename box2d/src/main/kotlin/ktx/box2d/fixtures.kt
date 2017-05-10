package ktx.box2d

import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.FixtureDef

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
inline fun FixtureDef.filter(init: (@Box2DDsl Filter).() -> Unit) = filter.init()

/**
 * Box2D building DSL utility class. [FixtureDef] extension exposing new properties.
 * @see BodyDefinition
 */
@Box2DDsl
class FixtureDefinition: FixtureDef()
