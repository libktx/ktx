package ktx.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction

/**
 * Alias for [Stage.addAction] method. Allows to add global actions to the stage with += operator.
 * @param action will be added to the stage root actor.
 */
operator fun Stage.plusAssign(action: Action) = addAction(action)

/**
 * Alias for [Stage.root].removeAction method. Removes global stage actions with -= operator.
 * @param action will be removed from the stage root actor.
 */
operator fun Stage.minusAssign(action: Action) = root.removeAction(action)

/**
 * Alias for [Actor.addAction] method. Allows to add actions to the actor with += operator.
 * @param action will be added to this actor.
 */
operator fun Actor.plusAssign(action: Action) = addAction(action)

/**
 * Alias for [Actor.removeAction] method. Allows to remove actions from the actor with -= operator.
 * @param action will be removed from this actor.
 */
operator fun Actor.minusAssign(action: Action) = removeAction(action)

/**
 * Action chaining utility. Wraps this action and the passed action with a [SequenceAction].
 * @param action will be executed after this action.
 * @return [SequenceAction] storing both actions.
 */
infix fun Action.then(action: Action): SequenceAction = Actions.sequence(this, action)

/**
 * Action chaining utility. Adds another action to this sequence.
 * @param action will be executed after the last scheduled action.
 * @return this [SequenceAction] for further chaining.
 */
infix fun SequenceAction.then(action: Action): SequenceAction {
  this.addAction(action)
  return this
}

/**
 * Actions utility. Wraps this action and the passed action with a [ParallelAction], executing them both at the same time.
 * @param action will be executed at the same time as this action.
 * @return [ParallelAction] storing both actions.
 */
infix fun Action.parallelTo(action: Action): ParallelAction = Actions.parallel(this, action)

/**
 * Actions utility. Adds another action to this [ParallelAction].
 * @param action will be executed at the same time as the other scheduled actions.
 * @return this [ParallelAction] for further chaining.
 */
infix fun ParallelAction.parallelTo(action: Action): ParallelAction {
  this.addAction(action)
  return this
}

/**
 * This action will be wrapped with a [RepeatAction] that repeats itself "forever" (until the action is removed or the
 * actor is no longer updated on the stage).
 * @return [RepeatAction] with no repetitions limit.
 * @see RepeatAction.FOREVER
 */
fun Action.repeatForever(): RepeatAction = Actions.forever(this)
