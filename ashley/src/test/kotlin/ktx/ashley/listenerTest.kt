@file:Suppress("ClassName")

package ktx.ashley

import com.badlogic.ashley.core.Entity

@Suppress("unused")
class `should implement EntityAdditionListener with only one method implemented` : EntityAdditionListener {
  // Guarantees EntityAdditionListener's entityRemoved method is always optional to implement

  override fun entityAdded(entity: Entity) = Unit
}

@Suppress("unused")
class `should implement EntityRemovalListener with only one method implemented` : EntityRemovalListener {
  // Guarantees EntityRemovalListener's entityAdded method is always optional to implement

  override fun entityRemoved(entity: Entity) = Unit
}
