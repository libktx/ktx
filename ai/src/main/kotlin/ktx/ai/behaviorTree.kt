package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Alias for [com.badlogic.gdx.ai.btree.branch.Sequence] avoiding name collision with the standard library. */
typealias GdxSequence<E> = com.badlogic.gdx.ai.btree.branch.Sequence<E>

/** Should annotate builder methods of gdxAI [BehaviorTree]. */
@DslMarker
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.TYPE_PARAMETER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPEALIAS
)
annotation class GdxAiTaskDsl

/**
 * Creates a BehaviorTree<E>.
 *
 * @param E the type of the behavior tree's blackboard.
 * @param rootTask the root task of the behavior tree.
 * @param blackboard the blackboard of the behavior tree.
 * @param init an optional inline block to configure the behavior tree.
 * @return a new [BehaviorTree<E>] instance.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> behaviorTree(
  rootTask: Task<E>? = null,
  blackboard: E? = null,
  init: (@GdxAiTaskDsl BehaviorTree<E>).() -> Unit = {}
): BehaviorTree<E> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}

/**
 * Adds a task to the BehaviorTree<E>.
 *
 * @param E the type of the behavior tree's blackboard.
 * @param task the task to add.
 * @param init an optional inline block to configure the task.
 * @return the index where the task has been created.
 */
@OptIn(ExperimentalContracts::class)
inline fun <E> BehaviorTree<E>.add(task: Task<E>, init: (@GdxAiTaskDsl Task<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}
