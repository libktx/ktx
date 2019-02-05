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
 * Wraps this action and the passed action with a [SequenceAction].
 *
 * If [this] or [action] are [SequenceAction]s, their nested actions will be unwrapped.
 *
 * @param action will be executed after this action.
 * @return [SequenceAction] storing both actions.
 */
infix fun Action.then(action: Action): SequenceAction {
  val result = Actions.sequence()
  result.addUnwrapped(this)
  result.addUnwrapped(action)
  return result
}

private fun SequenceAction.addUnwrapped(action: Action) {
  when (action) {
    is SequenceAction -> action.actions.forEach(::addAction)
    else -> addAction(action)
  }
}

/**
 * Wraps this action and the passed action with a [SequenceAction].
 * @param action will be executed after this action.
 * @return [SequenceAction] storing both actions.
 */
operator fun Action.plus(action: Action): SequenceAction = then(action)

/**
 * Adds another [Action] to this sequence.
 * @param action will be executed during this sequence.
 */
operator fun SequenceAction.plusAssign(action: Action) = addAction(action)

/**
 * Actions utility. Wraps this action and the passed action with a [ParallelAction], executing them both
 * at the same time.
 *
 * If [this] or [action] are [ParallelAction]s, their nested actions will be unwrapped.
 *
 * @param action will be executed at the same time as this action.
 * @return [ParallelAction] storing both actions.
 */
infix fun Action.parallelTo(action: Action): ParallelAction {
  val parallel = Actions.parallel()
  parallel.addUnwrapped(this)
  parallel.addUnwrapped(action)
  return parallel
}

private fun ParallelAction.addUnwrapped(action: Action) {
  when (action) {
    is ParallelAction -> action.actions.forEach(::addAction)
    else -> addAction(action)
  }
}

/**
 * Adds another [Action] to this action group.
 * @param action will be executed in parallel to the other actions of this [ParallelAction].
 */
operator fun ParallelAction.plusAssign(action: Action) = addAction(action)

/**
 * This action will be wrapped with a [RepeatAction] that repeats itself "forever" (until the action is removed or the
 * actor is no longer updated on the stage).
 * @return [RepeatAction] with no repetitions limit.
 * @see RepeatAction.FOREVER
 */
fun Action.repeatForever(): RepeatAction = Actions.forever(this)
