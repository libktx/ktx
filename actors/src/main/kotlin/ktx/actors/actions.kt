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
 * Combines this action and the passed action into a single [SequenceAction].
 *
 * If [action] is a [SequenceAction], its nested actions will be unwrapped and it will be freed to its pool
 * if it has one.
 *
 * @param action will be executed after this action.
 * @return [SequenceAction] storing both actions.
 */
infix fun Action.then(action: Action): SequenceAction {
  val result = Actions.sequence(this)
  result.addUnwrapped(action)
  return result
}

/**
 * Adds [action] to this [SequenceAction].
 *
 * If [action] is a [SequenceAction], its nested actions will be unwrapped and it will be freed to its pool if it has
 * one.
 *
 * @param action will be added to this [SequenceAction]
 * @return [SequenceAction] this [SequenceAction]
 */
infix fun SequenceAction.then(action: Action): SequenceAction {
  addUnwrapped(action)
  return this
}

/**
 * Wraps this action and the passed action in a [SequenceAction].
 *
 * @param action will be executed after [this] action.
 * @return [SequenceAction] storing both actions.
 */
operator fun Action.plus(action: Action): SequenceAction = Actions.sequence(this, action)

private fun SequenceAction.addUnwrapped(action: Action) {
  when (action) {
    is SequenceAction -> {
      action.actions.forEach(::addAction)
      action.pool?.free(action)
    }
    else -> addAction(action)
  }
}

/**
 * Adds another [Action] to this sequence.
 * @param action will be executed during this sequence.
 */
operator fun SequenceAction.plusAssign(action: Action) = addAction(action)

/**
 * Wraps this action and the passed action with a [ParallelAction], executing them both at the same time.
 *
 * If [action] is a [ParallelAction], so long as it is not a [SequenceAction], its nested actions will be unwrapped and
 * it will be freed to its pool if it has one.
 *
 * @param action will be executed at the same time as this action.
 * @return [ParallelAction] storing both actions.
 */
infix fun Action.along(action: Action): ParallelAction {
  val parallel = Actions.parallel(this)
  parallel.addUnwrapped(action)
  return parallel
}

@Deprecated("parallelTo has been replaced with along.", ReplaceWith("this along action"), DeprecationLevel.WARNING)
infix fun Action.parallelTo(action: Action): ParallelAction = along(action)

/**
 * Adds [action] to this [ParallelAction], so long as it isn't a [SequenceAction]. If it is a [SequenceAction], see
 * [Action.along].
 *
 * If [this] or [action] are [ParallelAction]s and not [SequenceAction]s, their nested actions will be unwrapped and
 * they will be freed to their pools
 * if they have them.
 *
 * @param action will be added to this [ParallelAction].
 * @return [ParallelAction] this [ParallelAction], now containing [action]]
 */
infix fun ParallelAction.along(action: Action): ParallelAction {
  if (this is SequenceAction)
    return this as Action along action // workaround for SequenceAction inheritance from ParallelAction
  addUnwrapped(action)
  return this
}

@Deprecated("parallelTo has been replaced with along.", ReplaceWith("this along action"), DeprecationLevel.WARNING)
infix fun ParallelAction.parallelTo(action: Action): ParallelAction = along(action)

private fun ParallelAction.addUnwrapped(action: Action) {
  if (action is ParallelAction && action !is SequenceAction) {
    action.actions.forEach(::addAction)
    action.pool?.free(action)
  } else {
    addAction(action)
  }
}

/**
 * Wraps this action and the passed action with a [ParallelAction].
 *
 * @param action will be executed at the same time as this action.
 * @return [SequenceAction] storing both actions.
 */
operator fun Action.div(action: Action): ParallelAction = Actions.parallel(this, action)

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
