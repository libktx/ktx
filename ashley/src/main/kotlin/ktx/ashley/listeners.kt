package ktx.ashley

import com.badlogic.ashley.core.*
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
  override fun entityAdded(entity: Entity) = Unit
}

@PublishedApi internal val emptyFamily = Family.all().get()

inline fun Engine.onEntityAdded(crossinline implementation: (entity: Entity) -> Unit, family: Family = emptyFamily, priority: Int = 0): EntityAdditionListener {
  val listener = object : EntityAdditionListener {
    override fun entityAdded(entity: Entity) {
      implementation.invoke(entity)
    }
  }

  addEntityListener(family, priority, listener)
  return listener
}

inline fun Engine.onEntityRemoved(crossinline implementation: (entity: Entity) -> Unit, family: Family = emptyFamily, priority: Int = 0): EntityRemovalListener {
  val listener = object : EntityRemovalListener {
    override fun entityRemoved(entity: Entity) {
      implementation.invoke(entity)
    }
  }

  addEntityListener(family, priority, listener)
  return listener
}

inline fun IteratingSystem.onEntityAdded(crossinline implementation: (entity: Entity) -> Unit, priority: Int = 0): EntityAdditionListener {
  return engine.onEntityAdded(implementation, family, priority)
}

inline fun IteratingSystem.onEntityRemoved(crossinline implementation: (entity: Entity) -> Unit, priority: Int = 0): EntityRemovalListener {
  return engine.onEntityRemoved(implementation, family, priority)
}

inline fun IntervalIteratingSystem.onEntityAdded(crossinline implementation: (entity: Entity) -> Unit, priority: Int = 0): EntityAdditionListener {
  return engine.onEntityAdded(implementation, family, priority)
}

inline fun IntervalIteratingSystem.onEntityRemoved(crossinline implementation: (entity: Entity) -> Unit, priority: Int = 0): EntityRemovalListener {
  return engine.onEntityRemoved(implementation, family, priority)
}

inline fun SortedIteratingSystem.onEntityAdded(crossinline implementation: (entity: Entity) -> Unit, priority: Int = 0): EntityAdditionListener {
  return engine.onEntityAdded(implementation, family, priority)
}

inline fun SortedIteratingSystem.onEntityRemoved(crossinline implementation: (entity: Entity) -> Unit, priority: Int = 0): EntityRemovalListener {
  return engine.onEntityRemoved(implementation, family, priority)
}
