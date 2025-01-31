package ktx.ashley

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem

/**
 * An [EntityListener] only requiring an implementation of the [entityAdded] method.
 *
 * An empty implementation is provided for the [entityRemoved] method. By default, the entity removal event is ignored.
 *
 * To handle more types of events, consider implementing [EntityListener] directly.
 */
interface EntityAdditionListener : EntityListener {
  /**
   * Invoked when a matching [Entity] is added to the [Engine].
   *
   * See [EntityListener.entityAdded].
   */
  override fun entityAdded(entity: Entity)

  override fun entityRemoved(entity: Entity) = Unit
}

/**
 * An [EntityListener] only requiring an implementation of the [entityRemoved] method.
 *
 * An empty implementation is provided for the [entityAdded] method. By default, the entity addition event is ignored.
 *
 * To handle more types of events, consider implementing [EntityListener] directly.
 */
interface EntityRemovalListener : EntityListener {
  /**
   * Invoked when a matching [Entity] is removed from the [Engine].
   *
   * See [EntityListener.entityRemoved].
   */
  override fun entityRemoved(entity: Entity)

  override fun entityAdded(entity: Entity) = Unit
}

// A family that matches any entity in the engine. Used to make Family an optional parameter in the API.
@PublishedApi internal val anyFamily = Family.all().get()

/**
 * Adds an [EntityAdditionListener] to this [Engine] and returns a reference to the new listener instance.
 * The listener calls [onAdded] lambda every time an entity addition event is triggered.
 *
 * The [family] parameter allows targeting a specific subset of entities. By default, [anyFamily] is used,
 * matching every entity within the engine.
 *
 * The [priority] argument affects the order in which the listeners are triggered. Lower value means higher
 * priority.
 *
 * To remove this [EntityListener] from the [Engine] afterwards, retain the reference returned by
 * this method and pass it to [Engine.removeEntityListener].
 */
inline fun Engine.onEntityAdded(
  family: Family = anyFamily,
  priority: Int = 0,
  crossinline onAdded: (entity: Entity) -> Unit,
): EntityAdditionListener {
  val listener =
    object : EntityAdditionListener {
      override fun entityAdded(entity: Entity) {
        onAdded.invoke(entity)
      }
    }

  addEntityListener(family, priority, listener)
  return listener
}

/**
 * Adds an [EntityRemovalListener] to this [Engine] and returns a reference to the new listener instance.
 * The listener calls [onRemoved] lambda every time an entity addition event is triggered.
 *
 * The [family] parameter allows targeting a specific subset of entities. By default, [anyFamily] is used,
 * matching every entity within the engine.
 *
 * The [priority] argument affects the order in which the listeners are triggered. Lower value means higher
 * priority.
 *
 * To remove this [EntityListener] from the [Engine] afterwards, retain the reference returned by this method
 * and pass it to [Engine.removeEntityListener].
 */
inline fun Engine.onEntityRemoved(
  family: Family = anyFamily,
  priority: Int = 0,
  crossinline onRemoved: (entity: Entity) -> Unit,
): EntityRemovalListener {
  val listener =
    object : EntityRemovalListener {
      override fun entityRemoved(entity: Entity) {
        onRemoved.invoke(entity)
      }
    }

  addEntityListener(family, priority, listener)
  return listener
}

/**
 * A wrapper for [Engine.onEntityAdded] that uses this [IteratingSystem]'s [Family]
 * as a filter for the [EntityAdditionListener].
 */
inline fun IteratingSystem.onEntityAdded(
  priority: Int = 0,
  crossinline onAdded: (entity: Entity) -> Unit,
): EntityAdditionListener = engine.onEntityAdded(family, priority, onAdded)

/**
 * A wrapper for [Engine.onEntityRemoved] that uses this [IteratingSystem]'s [Family]
 * as a filter for the [EntityRemovalListener].
 */
inline fun IteratingSystem.onEntityRemoved(
  priority: Int = 0,
  crossinline onRemoved: (entity: Entity) -> Unit,
): EntityRemovalListener = engine.onEntityRemoved(family, priority, onRemoved)

/**
 * A wrapper for [Engine.onEntityAdded] that uses this [IntervalIteratingSystem]'s [Family]
 * as a filter for the [EntityAdditionListener].
 */
inline fun IntervalIteratingSystem.onEntityAdded(
  priority: Int = 0,
  crossinline onAdded: (entity: Entity) -> Unit,
): EntityAdditionListener = engine.onEntityAdded(family, priority, onAdded)

/**
 * A wrapper for [Engine.onEntityRemoved] that uses this [IntervalIteratingSystem]'s [Family]
 * as a filter for the [EntityRemovalListener].
 */
inline fun IntervalIteratingSystem.onEntityRemoved(
  priority: Int = 0,
  crossinline onRemoved: (entity: Entity) -> Unit,
): EntityRemovalListener = engine.onEntityRemoved(family, priority, onRemoved)

/**
 * A wrapper for [Engine.onEntityAdded] that uses this [SortedIteratingSystem]'s [Family]
 * as a filter for the [EntityAdditionListener].
 */
inline fun SortedIteratingSystem.onEntityAdded(
  priority: Int = 0,
  crossinline onAdded: (entity: Entity) -> Unit,
): EntityAdditionListener = engine.onEntityAdded(family, priority, onAdded)

/**
 * A wrapper for [Engine.onEntityRemoved] that uses this [SortedIteratingSystem]'s [Family]
 * as a filter for the [EntityRemovalListener].
 */
inline fun SortedIteratingSystem.onEntityRemoved(
  priority: Int = 0,
  crossinline onRemoved: (entity: Entity) -> Unit,
): EntityRemovalListener = engine.onEntityRemoved(family, priority, onRemoved)
