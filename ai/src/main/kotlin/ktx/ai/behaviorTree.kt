package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.branch.Parallel
import com.badlogic.gdx.ai.btree.branch.RandomSelector
import com.badlogic.gdx.ai.btree.branch.RandomSequence
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Alias for [com.badlogic.gdx.ai.btree.branch.Sequence] avoiding name collision with the standard library. */
typealias GdxSequence<E> = com.badlogic.gdx.ai.btree.branch.Sequence<E>

/** Alias for [com.badlogic.gdx.ai.btree.branch.Selector] avoiding name collision with the standard library. */
typealias GdxSelector<E> = com.badlogic.gdx.ai.btree.branch.Selector<E>

/** Should annotate builder methods of gdxAI [Task]. */
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
  val behaviorTree = BehaviorTree(rootTask, blackboard)
  behaviorTree.init()
  return behaviorTree
}

/**
 * Adds a task to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard.
 * @param task the task to add.
 * @param init an optional inline block to configure the task.
 * @return the index where the task has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E, T : Task<E>> Task<E>.add(task: T, init: (@GdxAiTaskDsl T).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  task.init()
  return addChild(task)
}

/**
 * Creates and adds a DynamicGuardSelector to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard
 * @param init an optional inline block to configure the DynamicGuardSelector
 * @return the index where the DynamicGuardSelector has been added
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> Task<E>.dynamicGuardSelector(init: (@GdxAiTaskDsl DynamicGuardSelector<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}

/**
 * Creates and adds a Parallel to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard
 * @param init an optional inline block to configure the Parallel
 * @return the index where the Parallel has been added
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> Task<E>.parallel(init: (@GdxAiTaskDsl Parallel<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}

/**
 * Creates and adds a RandomSelector to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard
 * @param init an optional inline block to configure the RandomSelector
 * @return the index where the Parallel has been added
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> Task<E>.randomSelector(init: (@GdxAiTaskDsl RandomSelector<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}

/**
 * Creates and adds a RandomSequence to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard
 * @param init an optional inline block to configure the RandomSequence
 * @return the index where the Parallel has been added
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> Task<E>.randomSequence(init: (@GdxAiTaskDsl RandomSequence<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}

/**
 * Creates and adds a GdxSelector to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard
 * @param init an optional inline block to configure the GdxSelector
 * @return the index where the Parallel has been added
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> Task<E>.selector(init: (@GdxAiTaskDsl GdxSelector<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}

/**
 * Creates and adds a GdxSequence to the receiver Task<E>.
 *
 * @param E the type of the receiving task's blackboard
 * @param init an optional inline block to configure the GdxSequence
 * @return the index where the Parallel has been added
 */
@OptIn(ExperimentalContracts::class)
@GdxAiTaskDsl
inline fun <E> Task<E>.sequence(init: (@GdxAiTaskDsl GdxSequence<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  TODO()
}
