[![gdxAI](https://img.shields.io/badge/gdxAI-1.8.2-red.svg)](https://github.com/libgdx/gdx-ai)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-ai.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-ai)

# KTX: gdxAI builders and utilities

Utilities and type-safe builders for the [gdxAI](https://github.com/libgdx/gdx-ai) artificial intelligence framework.

### Why?

Creating behavior trees programmatically with gdxAI is not as intuitive as defining a _btree_ text file. However, _btree_
text files have to be loaded as external resources and are not validated at compilation time.
`ktx-ai` provides an alternative by defining Kotlin type-safe builders for behavior trees.

### Guide

#### Type aliases

Some classes from gdxAI are named similarly to the types in standard library and other common dependencies.
To avoid name clashes, you can use these type aliases instead:

- `GdxAiSequence<E>` for `com.badlogic.gdx.ai.btree.branch.Sequence<E>`
- `GdxAiSelector<E>` for `com.badlogic.gdx.ai.btree.branch.Selector<E>`
- `GdxAiRandom<E>` for `com.badlogic.gdx.ai.btree.decorator.Random<E>`

#### Type-safe builders

`ktx-ai` provides the following extension functions for building behavior trees:

|        Function        | GdxAI class                                             |
|:----------------------:|---------------------------------------------------------|
|         `add`          | `com.badlogic.gdx.ai.btree.Task.addChild`               |
|     `behaviorTree`     | `com.badlogic.gdx.ai.btree.BehaviorTree`                |
| `dynamicGuardSelector` | `com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector` |
|       `parallel`       | `com.badlogic.gdx.ai.btree.branch.Parallel`             |
|    `randomSelector`    | `com.badlogic.gdx.ai.btree.branch.RandomSelector`       |
|    `randomSequence`    | `com.badlogic.gdx.ai.btree.branch.RandomSequence`       |
|       `selector`       | `com.badlogic.gdx.ai.btree.branch.Selector`             |
|       `sequence`       | `com.badlogic.gdx.ai.btree.branch.Sequence`             |
|      `alwaysFail`      | `com.badlogic.gdx.ai.btree.decorator.AlwaysFail`        |
|    `alwaysSucceed`     | `com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed`     |
|       `include`        | `com.badlogic.gdx.ai.btree.decorator.Include`           |
|        `invert`        | `com.badlogic.gdx.ai.btree.decorator.Invert`            |
|        `random`        | `com.badlogic.gdx.ai.btree.decorator.Random`            |
|        `repeat`        | `com.badlogic.gdx.ai.btree.decorator.Repeat`            |
|    `semaphoreGuard`    | `com.badlogic.gdx.ai.btree.decorator.SemaphoreGuard`    |
|      `untilFail`       | `com.badlogic.gdx.ai.btree.decorator.UntilFail`         |
|     `untilSuccess`     | `com.badlogic.gdx.ai.btree.decorator.UntilSuccess`      |
|     `failureLeaf`      | `com.badlogic.gdx.ai.btree.leaf.Failure`                |
|     `successLeaf`      | `com.badlogic.gdx.ai.btree.leaf.Success`                |
|       `waitLeaf`       | `com.badlogic.gdx.ai.btree.leaf.Wait`                   |

### Usage examples

Creating an empty `BehaviorTree` with no `rootTask` or `blackboard`:

```kotlin
import ktx.ai.*

class Cat

val tree = behaviorTree<Cat> {
  // Edit BehaviorTree here.
}
```

Note that `BehaviorTree` requires a single root task and blackboard. So these have to be set manually at some point.

Setting the `blackboard` and `rootTask` in the constructor:

```kotlin
import ktx.ai.*

class Cat

val rootTask = GdxAiSequence<Cat>().apply {
  // Edit the root Task
}

val tree = behaviorTree<Cat>(
  rootTask = rootTask,
  blackboard = Cat()
) {
  // Edit BehaviorTree here.
}
```

Alternatively, you can set the `rootTask` and `blackboard` in the `init` block:

```kotlin

val tree = behaviorTree<Cat> {
    // Set the blackboard:
    `object` = Cat()
  
    // Set the rootTask:
    selector {

    }
}
```

Adding a custom task to a receiver task:

```kotlin
import com.badlogic.gdx.ai.btree.*
import ktx.ai.*

class EatTask : LeafTask<Cat>()

val selector = GdxAiSelector<Cat>()

selector.add(EatTask())
```

Adding nested branches to a `BehaviorTree`:

```kotlin
import com.badlogic.gdx.ai.btree.*
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution
import ktx.ai.*

val tree = behaviorTree<Cat>(blackboard = Cat()) {
  // Add a root task:
  selector {
    // Add a selector with 4 children to the root task:
    selector {
      add(EatTask())
      waitLeaf(2f)
      add(SleepTask())
      add(DrinkTask())
    }
    // Add a sequence with 2 children to the root task:
    sequence {
      repeat(UniformIntegerDistribution(1, 3), FindPrey())
      add(PlayWithPrey())
    }
  }
}
```

Creating a custom builder function for a custom task:

```kotlin
class EatTask : LeafTask<Cat>()

@OptIn(ExperimentalContracts::class)
@GdxAiDsl
inline fun Task<Cat>.eatTask(
  init: (@GdxAiDsl EatTask).() -> Unit = {}
): Int {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  val eatTask = EatTask()
  eatTask.init()
  return addChild(eatTask)
}

val tree = behaviorTree<Cat>(blackboard = Cat()) {
  selector {
    // Using the custom builder function:
    eatTask {
      // Edit EatTask here.
    }
  }
}
```

#### Additional documentation

- [gdxAI repository.](https://github.com/libgdx/gdx-ai)
