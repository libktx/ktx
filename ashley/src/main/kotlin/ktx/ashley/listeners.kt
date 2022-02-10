package ktx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener

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
