# KTX: async

[Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) support and general asynchronous operations
utilities for LibGDX applications.

### Why?

Coroutines-based APIs greatly simplify asynchronous operations and allow to avoid so-called callback hell. Some LibGDX
APIs - like the `Timer` - were not written with functional expressions in mind (often using abstract classes) and they
are tedious to call with vanilla Kotlin. This module aims to hide asynchronous code complexity with coroutines, as well
as improve existing asynchronous APIs to feel more like Kotlin.

### Guide

#### Coroutines

#### Utilities

Other asynchronous operations utilities include:
- `schedule` and `interval` functions were added to simplify LibGDX `Timer` API. Normally it requires the user to extend
an abstract class - these utility functions allow to use idiomatic Kotlin lambdas syntax.

### Usage examples

Scheduling a task executed on the main rendering thread after one second:

```Kotlin
import ktx.async.schedule

val taskCallback = schedule(delaySeconds = 1f) {
  println("Hello!")
}
```

Scheduling a task repeatedly executed on the main rendering thread after one second:

```Kotlin
import ktx.async.interval

val taskCallback = interval(delaySeconds = 1f, intervalSeconds = 1f) {
  println("Hello again!")
}
```

### Alternatives

- Standard Kotlin coroutines libraries might be used along with custom thread pools. They do not offer the same level
of compatibility with existing LibGDX APIs though.

#### Additional documentation

- [LibGDX threading article.](https://github.com/libgdx/libgdx/wiki/Threading)
- [Coroutines language reference.](https://kotlinlang.org/docs/reference/coroutines.html)
- [Coroutines repository.](https://github.com/Kotlin/kotlin-coroutines)
