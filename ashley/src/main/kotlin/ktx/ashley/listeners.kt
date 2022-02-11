package ktx.ashley

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem

/**
 * An [EntityListener] only listening to the [entityAdded] event.
 *
 * An empty implementation is provided for the [entityRemoved] event, as it is ignored.
 *
 * If you need both events from the [EntityListener], implement the regular [EntityListener] from Ashley.
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
 * An [EntityListener] only listening to the [entityRemoved] event.
 *
 * An empty implementation is provided for the [entityAdded] event, as it is ignored.
 *
 * If you need both events from the [EntityListener], implement the regular [EntityListener] from Ashley.
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
 * Adds an [EntityAdditionListener] to this [Engine] that is backed by the given lambda and returns it.
 *
 * If you want to remove this [EntityAdditionListener] from the [Engine] afterwards, retain the reference returned by
 * this method and call [Engine.removeEntityListener] with it.
 *
 * @param family The [Family] targeted by this [EntityAdditionListener]. An empty family (see [anyFamily] by default)
 * @param priority The priority, with which this [EntityAdditionListener] will be executed. Lower value means higher
 * priority. 0 (the highest priority) by default
 * @param onAdded The lambda implementation of this [EntityAdditionListener] that must match the
 * [EntityAdditionListener.entityAdded] method.
 */
inline fun Engine.onEntityAdded(family: Family = anyFamily, priority: Int = 0, crossinline onAdded: (entity: Entity) -> Unit): EntityAdditionListener {
  val listener = object : EntityAdditionListener {
    override fun entityAdded(entity: Entity) {
      onAdded.invoke(entity)
    }
  }

  addEntityListener(family, priority, listener)
  return listener
}

/**
 * Adds an [EntityRemovalListener] to this [Engine] that is backed by the given lambda and returns it.
 *
 * If you want to remove this [EntityRemovalListener] from the [Engine] afterwards, retain the reference returned by
 * this method and call [Engine.removeEntityListener] with it.
 *
 * @param family The [Family] targeted by this [EntityRemovalListener]. An empty family (see [anyFamily] by default)
 * @param priority The priority, with which this [EntityRemovalListener] will be executed. Lower value means higher
 * priority. 0 (the highest priority) by default
 * @param onRemoved The lambda implementation of this [EntityRemovalListener] that must match the
 * [EntityRemovalListener.entityRemoved] method.
 */
inline fun Engine.onEntityRemoved(family: Family = anyFamily, priority: Int = 0, crossinline onRemoved: (entity: Entity) -> Unit): EntityRemovalListener {
  val listener = object : EntityRemovalListener {
    override fun entityRemoved(entity: Entity) {
      onRemoved.invoke(entity)
    }
  }

  addEntityListener(family, priority, listener)
  return listener
}

/**
 * A wrapper for [Engine.onEntityAdded] that uses this [IteratingSystem]'s [Family] as a filter for the [EntityAdditionListener].
 */
inline fun IteratingSystem.onEntityAdded(priority: Int = 0, crossinline onAdded: (entity: Entity) -> Unit): EntityAdditionListener {
  return engine.onEntityAdded(family, priority, onAdded)
}

/**
 * A wrapper for [Engine.onEntityRemoved] that uses this [IteratingSystem]'s [Family] as a filter for the [EntityRemovalListener].
 */
inline fun IteratingSystem.onEntityRemoved(priority: Int = 0, crossinline onRemoved: (entity: Entity) -> Unit): EntityRemovalListener {
  return engine.onEntityRemoved(family, priority, onRemoved)
}

/**
 * A wrapper for [Engine.onEntityAdded] that uses this [IntervalIteratingSystem]'s [Family] as a filter for the [EntityAdditionListener].
 */
inline fun IntervalIteratingSystem.onEntityAdded(priority: Int = 0, crossinline onAdded: (entity: Entity) -> Unit): EntityAdditionListener {
  return engine.onEntityAdded(family, priority, onAdded)
}

/**
 * A wrapper for [Engine.onEntityRemoved] that uses this [IntervalIteratingSystem]'s [Family] as a filter for the [EntityRemovalListener].
 */
inline fun IntervalIteratingSystem.onEntityRemoved(priority: Int = 0, crossinline onRemoved: (entity: Entity) -> Unit): EntityRemovalListener {
  return engine.onEntityRemoved(family, priority, onRemoved)
}

/**
 * A wrapper for [Engine.onEntityAdded] that uses this [SortedIteratingSystem]'s [Family] as a filter for the [EntityAdditionListener].
 */
inline fun SortedIteratingSystem.onEntityAdded(priority: Int = 0, crossinline onAdded: (entity: Entity) -> Unit): EntityAdditionListener {
  return engine.onEntityAdded(family, priority, onAdded)
}

/**
 * A wrapper for [Engine.onEntityRemoved] that uses this [SortedIteratingSystem]'s [Family] as a filter for the [EntityRemovalListener].
 */
inline fun SortedIteratingSystem.onEntityRemoved(priority: Int = 0, crossinline onRemoved: (entity: Entity) -> Unit): EntityRemovalListener {
  return engine.onEntityRemoved(family, priority, onRemoved)
}
