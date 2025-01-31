package ktx.ai

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.branch.Parallel
import com.badlogic.gdx.ai.btree.branch.Parallel.Orchestrator
import com.badlogic.gdx.ai.btree.branch.Parallel.Policy
import com.badlogic.gdx.ai.btree.branch.RandomSelector
import com.badlogic.gdx.ai.btree.branch.RandomSequence
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.ai.btree.decorator.Repeat
import com.badlogic.gdx.ai.btree.decorator.SemaphoreGuard
import com.badlogic.gdx.ai.btree.decorator.UntilFail
import com.badlogic.gdx.ai.btree.decorator.UntilSuccess
import com.badlogic.gdx.ai.btree.leaf.Failure
import com.badlogic.gdx.ai.btree.leaf.Success
import com.badlogic.gdx.ai.btree.leaf.Wait
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.FloatDistribution
import com.badlogic.gdx.ai.utils.random.IntegerDistribution
import com.badlogic.gdx.utils.Array
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Alias for [com.badlogic.gdx.ai.btree.branch.Sequence] to avoid name collisions with the standard library. */
typealias GdxAiSequence<E> = com.badlogic.gdx.ai.btree.branch.Sequence<E>

/** Alias for [com.badlogic.gdx.ai.btree.branch.Selector] to avoid name collisions with the standard library. */
typealias GdxAiSelector<E> = com.badlogic.gdx.ai.btree.branch.Selector<E>

/** Alias for [com.badlogic.gdx.ai.btree.decorator.Random] to avoid name collisions with the standard library. */
typealias GdxAiRandom<E> = com.badlogic.gdx.ai.btree.decorator.Random<E>

/** Should annotate builder methods of gdxAI. */
@DslMarker
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.TYPE_PARAMETER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPEALIAS,
)
annotation class GdxAiDsl

/**
 * Creates a [BehaviorTree].
 *
 * @param E the type of the behavior tree's blackboard.
 * @param rootTask the root task of the [BehaviorTree].
 * @param blackboard the blackboard of the [BehaviorTree].
 * @param init an optional inline block to configure the [BehaviorTree].
 * @return a new [BehaviorTree] instance.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> behaviorTree(
  rootTask: Task<E>? = null,
  blackboard: E? = null,
  init: (@GdxAiDsl BehaviorTree<E>).() -> Unit = {},
): BehaviorTree<E> {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val behaviorTree = BehaviorTree(rootTask, blackboard)
  behaviorTree.init()
  return behaviorTree
}

/**
 * Adds a task to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param T the type of the task which gets added.
 * @param task the task to add.
 * @param init an optional inline block to configure the task.
 * @return the index where the task has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E, T : Task<E>> Task<E>.add(
  task: T,
  init: (@GdxAiDsl T).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  task.init()
  return addChild(task)
}

/**
 * Creates and adds a [DynamicGuardSelector] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param tasks the children of the [DynamicGuardSelector].
 * @param init an optional inline block to configure the [DynamicGuardSelector].
 * @return the index where the [DynamicGuardSelector] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.dynamicGuardSelector(
  tasks: Array<Task<E>> = Array(),
  init: (@GdxAiDsl DynamicGuardSelector<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val dynamicGuardSelector = DynamicGuardSelector(tasks)
  dynamicGuardSelector.init()
  return addChild(dynamicGuardSelector)
}

/**
 * Creates and adds a [Parallel] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param policy the [Parallel.Policy] of the [Parallel].
 * @param orchestrator the [Parallel.Orchestrator] of the [Parallel].
 * @param tasks the children of the [Parallel].
 * @param init an optional inline block to configure the [Parallel].
 * @return the index where the [Parallel] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.parallel(
  policy: Policy = Policy.Sequence,
  orchestrator: Orchestrator = Orchestrator.Resume,
  tasks: Array<Task<E>> = Array(),
  init: (@GdxAiDsl Parallel<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val parallel = Parallel(policy, orchestrator, tasks)
  parallel.init()
  return addChild(parallel)
}

/**
 * Creates and adds a [RandomSelector] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param tasks the children of the [RandomSelector].
 * @param init an optional inline block to configure the [RandomSelector].
 * @return the index where the [RandomSelector] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.randomSelector(
  tasks: Array<Task<E>> = Array(),
  init: (@GdxAiDsl RandomSelector<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val randomSelector = RandomSelector(tasks)
  randomSelector.init()
  return addChild(randomSelector)
}

/**
 * Creates and adds a [RandomSequence] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param tasks the children of the [RandomSequence].
 * @param init an optional inline block to configure the [RandomSequence].
 * @return the index where the [RandomSequence] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.randomSequence(
  tasks: Array<Task<E>> = Array(),
  init: (@GdxAiDsl RandomSequence<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val randomSequence = RandomSequence(tasks)
  randomSequence.init()
  return addChild(randomSequence)
}

/**
 * Creates and adds a [GdxAiSelector] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param tasks the children of the [GdxAiSelector].
 * @param init an optional inline block to configure the [GdxAiSelector].
 * @return the index where the [GdxAiSelector] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.selector(
  tasks: Array<Task<E>> = Array(),
  init: (@GdxAiDsl GdxAiSelector<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val selector = GdxAiSelector(tasks)
  selector.init()
  return addChild(selector)
}

/**
 * Creates and adds a [GdxAiSequence] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param tasks the children of the [GdxAiSequence].
 * @param init an optional inline block to configure the [GdxAiSequence].
 * @return the index where the [GdxAiSequence] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.sequence(
  tasks: Array<Task<E>> = Array(),
  init: (@GdxAiDsl GdxAiSequence<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val sequence = GdxAiSequence(tasks)
  sequence.init()
  return addChild(sequence)
}

/**
 * Creates and adds an [AlwaysFail] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param task the task to add to the [AlwaysFail].
 * @param init an optional inline block to configure the [AlwaysFail].
 * @return the index where the [AlwaysFail] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.alwaysFail(
  task: Task<E>? = null,
  init: (@GdxAiDsl AlwaysFail<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val alwaysFail = AlwaysFail(task)
  alwaysFail.init()
  return addChild(alwaysFail)
}

/**
 * Creates and adds an [AlwaysSucceed] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param task the task to add to the [AlwaysSucceed].
 * @param init an optional inline block to configure the [AlwaysSucceed].
 * @return the index where the [AlwaysSucceed] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.alwaysSucceed(
  task: Task<E>? = null,
  init: (@GdxAiDsl AlwaysSucceed<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val alwaysSucceed = AlwaysSucceed(task)
  alwaysSucceed.init()
  return addChild(alwaysSucceed)
}

/**
 * Creates and adds an [Include] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param subtree the path of the subtree to include.
 * @param lazy indicates whether the subtree should be included at clone-time ({@code false}, the default) or at
 *             run-time ({@code true}).
 * @param init an optional inline block to configure the [Include].
 * @return the index where the [Include] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.include(
  subtree: String? = null,
  lazy: Boolean = false,
  init: (@GdxAiDsl Include<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val include = Include<E>(subtree, lazy)
  include.init()
  return addChild(include)
}

/**
 * Creates and adds an [Invert] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param task the task to add to the [Invert].
 * @param init an optional inline block to configure the [Invert].
 * @return the index where the Invert has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.invert(
  task: Task<E>? = null,
  init: Invert<E>.() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val invert = Invert(task)
  invert.init()
  return addChild(invert)
}

/**
 * Creates and adds a [GdxAiRandom] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param success the probability for the task to succeed.
 * @param task the task to add to the [GdxAiRandom].
 * @param init an optional inline block to configure the [GdxAiRandom].
 * @return the index where the [GdxAiRandom] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.random(
  success: FloatDistribution = ConstantFloatDistribution.ZERO_POINT_FIVE,
  task: Task<E>? = null,
  init: (@GdxAiDsl GdxAiRandom<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val random = GdxAiRandom(success, task)
  random.init()
  return addChild(random)
}

/**
 * Creates and adds a [Repeat] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param times the [IntegerDistribution] specifying how many times the task must be repeated.
 * @param task the task to add to the [Repeat].
 * @param init an optional inline block to configure the [Repeat].
 * @return the index where the [Repeat] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.repeat(
  times: IntegerDistribution = ConstantIntegerDistribution.NEGATIVE_ONE,
  task: Task<E>? = null,
  init: (@GdxAiDsl Repeat<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val repeat = Repeat(times, task)
  repeat.init()
  return addChild(repeat)
}

/**
 * Creates and adds a [SemaphoreGuard] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param name the semaphore name.
 * @param task the task to add to the [SemaphoreGuard].
 * @param init an optional inline block to configure the [SemaphoreGuard].
 * @return the index where the [SemaphoreGuard] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.semaphoreGuard(
  name: String? = null,
  task: Task<E>? = null,
  init: (@GdxAiDsl SemaphoreGuard<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val semaphoreGuard = SemaphoreGuard(name, task)
  semaphoreGuard.init()
  return addChild(semaphoreGuard)
}

/**
 * Creates and adds an [UntilFail] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param task the task to add to the [UntilFail].
 * @param init an optional inline block to configure the [UntilFail].
 * @return the index where the [UntilFail] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.untilFail(
  task: Task<E>? = null,
  init: (@GdxAiDsl UntilFail<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val untilFail = UntilFail(task)
  untilFail.init()
  return addChild(untilFail)
}

/**
 * Creates and adds an [UntilSuccess] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param task the task to add to the [UntilSuccess].
 * @param init an optional inline block to configure the [UntilSuccess].
 * @return the index where the [UntilSuccess] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.untilSuccess(
  task: Task<E>? = null,
  init: (@GdxAiDsl UntilSuccess<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val untilSuccess = UntilSuccess(task)
  untilSuccess.init()
  return addChild(untilSuccess)
}

/**
 * Creates and adds a [Failure] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param init an optional inline block to configure the [Failure].
 * @return the index where the [Failure] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.failureLeaf(init: (@GdxAiDsl Failure<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val failure = Failure<E>()
  failure.init()
  return addChild(failure)
}

/**
 * Creates and adds a [Success] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param init an optional inline block to configure the [Success].
 * @return the index where the [Success] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.successLeaf(init: (@GdxAiDsl Success<E>).() -> Unit = {}): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val success = Success<E>()
  success.init()
  return addChild(success)
}

/**
 * Creates and adds a [Wait] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param seconds the number of seconds to wait for.
 * @param init an optional inline block to configure the [Wait].
 * @return the index where the [Wait] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.waitLeaf(
  seconds: Float,
  init: (@GdxAiDsl Wait<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val wait = Wait<E>(seconds)
  wait.init()
  return addChild(wait)
}

/**
 * Creates and adds a [Wait] to the receiver [Task].
 *
 * @param E the type of the receiving task's blackboard.
 * @param seconds the [FloatDistribution] determining the number of seconds to wait for.
 * @param init an optional inline block to configure the [Wait].
 * @return the index where the [Wait] has been added.
 */
@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun <E> Task<E>.waitLeaf(
  seconds: FloatDistribution = ConstantFloatDistribution.ZERO,
  init: (@GdxAiDsl Wait<E>).() -> Unit = {},
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val wait = Wait<E>(seconds)
  wait.init()
  return addChild(wait)
}
