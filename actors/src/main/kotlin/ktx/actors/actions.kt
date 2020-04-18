package ktx.actors

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import java.lang.Integer.max

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
 * Wraps this action and the passed action in a [SequenceAction].
 *
 * @param action will be executed after this action.
 * @return [SequenceAction] storing both actions.
 */
infix fun Action.then(action: Action): SequenceAction = Actions.sequence(this, action)

/**
 * Adds [action] to this [SequenceAction].
 *
 * @param action will be added to this [SequenceAction]
 * @return this [SequenceAction].
 * @see plus for a non-mutating alternative.
 */
infix fun SequenceAction.then(action: Action): SequenceAction = apply {
  addAction(action)
}

/**
 * Wraps this action and the passed action in a [SequenceAction].
 *
 * @param action will be executed after [this] action.
 * @return [SequenceAction] storing both actions.
 */
operator fun Action.plus(action: Action): SequenceAction = Actions.sequence(this, action)

/**
 * Adds [action] to this [SequenceAction].
 *
 * @param action will be executed during this sequence.
 */
operator fun SequenceAction.plusAssign(action: Action) = addAction(action)

/**
 * Wraps this action and the passed action with a [ParallelAction], executing them both at the same time.
 *
 * @param action will be executed at the same time as this action.
 * @return [ParallelAction] storing both actions.
 */
infix fun Action.along(action: Action): ParallelAction = Actions.parallel(this, action)

@Deprecated("parallelTo has been replaced with along.", ReplaceWith("this along action"), DeprecationLevel.WARNING)
infix fun Action.parallelTo(action: Action): ParallelAction = along(action)

/**
 * Adds [action] to this [ParallelAction], so long as this isn't a [SequenceAction]. If it is a [SequenceAction],
 * it creates a new [ParallelAction] executing both actions in parallel.
 *
 * @param action will be added to this [ParallelAction].
 * @return this [ParallelAction], now containing [action], or a new [ParallelAction] if this is a [SequenceAction].
 * @see div for a non-mutating alternative.
 */
infix fun ParallelAction.along(action: Action): ParallelAction {
  if (this is SequenceAction) {
    // Workaround for SequenceAction inheritance from ParallelAction:
    return Actions.parallel(this, action)
  }
  addAction(action)
  return this
}

@Deprecated("parallelTo has been replaced with along.", ReplaceWith("this along action"), DeprecationLevel.WARNING)
infix fun ParallelAction.parallelTo(action: Action): ParallelAction = along(action)

/**
 * Wraps this action and the passed action with a [ParallelAction].
 *
 * @param action will be executed at the same time as this action.
 * @return [SequenceAction] storing both actions.
 */
operator fun Action.div(action: Action): ParallelAction = Actions.parallel(this, action)

/**
 * Adds another [Action] to this [ParallelAction].
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

/**
 * This action will be wrapped wit ha [RepeatAction] that repeats itself for the given amount of [times].
 * @param times amount of repetitions to perform. If 0 or negative, the action will not be executed.
 * @return [RepeatAction] with [times] repetitions limit.
 * @see RepeatAction
 * @see repeatForever
 */
fun Action.repeat(times: Int): RepeatAction = Actions.repeat(max(0, times), this)
