package ktx.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction

/**
 * Alias for [Stage.addAction] method. Allows to add global actions to the stage with + operator.
 * @param action will be added to the stage root actor.
 * @return this (for chaining).
 */
operator fun Stage.plus(action: Action): Stage {
  this.addAction(action)
  return this
}

/**
 * Alias for [Stage.root.removeAction] method. Removes global stage actions with - operator.
 * @param action will be removed from the stage root actor.
 * @return this (for chaining).
 */
operator fun Stage.minus(action: Action): Stage {
  this.root.removeAction(action)
  return this
}

/**
 * Action chaining utility. Wraps this action and the passed action with a [SequenceAction].
 * @param action will be executed after this action.
 * @return [SequenceAction] storing both actions.
 */
infix fun Action.then(action: Action): SequenceAction = Actions.sequence(this, action)

/**
 * Action chaining utility. Adds another action to this sequence.
 * @param action will be executed after the last scheduled action.
 * @return this [SequenceAction].
 */
infix fun SequenceAction.then(action: Action): SequenceAction {
  this.addAction(action)
  return this
}
